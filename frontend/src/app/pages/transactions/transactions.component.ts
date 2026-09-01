import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, switchMap } from 'rxjs';
import { ExpenseService } from '../../services/expense.service';
import { AdminService } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';
import { CategoryService } from '../../services/category.service';
import { BudgetService } from '../../services/budget.service';
import { AdminExpense } from '../../models/admin.model';
import { Expense, Category } from '../../models/expense.model';
import { Budget } from '../../models/budget.model';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

interface TxRow {
  id: number;
  date: string;
  label: string;
  category: string;
  amount: number;
  userEmail?: string;
}

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslatePipe],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransactionsComponent implements OnInit {

  transactions = signal<TxRow[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  isAdmin = false;

  // --- Modal Add Transaction ---
  showModal = signal(false);
  formDescription = '';
  formAmount: number | null = null;
  formDate: string = '';
  formCategoryId: number | null = null;   // -1 = "nouvelle catégorie"
  formNewCategoryName = '';
  formBudgetId: number | null = null;
  formError = signal<string | null>(null);
  formSaving = signal(false);
  categories = signal<Category[]>([]);
  budgets = signal<Budget[]>([]);

  // --- Modal Delete Transaction ---
  showDeleteModal = signal(false);
  transactionToDelete = signal<TxRow | null>(null);
  deleteSaving = signal(false);
  deleteError = signal<string | null>(null);

  constructor(
    private expenseService: ExpenseService,
    private adminService: AdminService,
    private authService: AuthService,
    private categoryService: CategoryService,
    private budgetService: BudgetService,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.loadTransactions();
  }

  loadTransactions(): void {
    this.loading.set(true);
    if (this.isAdmin) {
      this.adminService.getAllExpenses().subscribe({
        next: (expenses: AdminExpense[]) => {
          this.transactions.set(
            expenses.map(e => ({
              id: e.id,
              date: e.date,
              label: e.description,
              category: e.categoryName ?? '-',
              amount: -Math.abs(e.amount),
              userEmail: e.userEmail,
            }))
          );
          this.loading.set(false);
        },
        error: () => {
          this.error.set(this.translate.instant('transactions.errors.loadFailed'));
          this.loading.set(false);
        }
      });
    } else {
      this.expenseService.getAllExpenses().subscribe({
        next: (expenses: Expense[]) => {
          this.transactions.set(
            expenses.map(e => ({
              id: e.id,
              date: e.date,
              label: e.description,
              category: e.category?.name ?? '-',
              amount: -Math.abs(e.amount),
            }))
          );
          this.loading.set(false);
        },
        error: () => {
          this.error.set(this.translate.instant('transactions.errors.loadFailed'));
          this.loading.set(false);
        }
      });
    }
  }

  openModal(): void {
    this.formDescription = '';
    this.formAmount = null;
    this.formDate = new Date().toISOString().split('T')[0];
    this.formCategoryId = null;
    this.formNewCategoryName = '';
    this.formBudgetId = null;
    this.formError.set(null);
    // Charger catégories et budgets en parallèle
    forkJoin({
      categories: this.categoryService.getAllCategories(),
      budgets: this.budgetService.getAllBudgets(),
    }).subscribe({
      next: ({ categories, budgets }) => {
        this.categories.set(categories);
        this.budgets.set(budgets);
      },
      error: () => this.formError.set(this.translate.instant('transactions.errors.loadCategoriesBudgetsFailed'))
    });
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }

  openDeleteModal(transaction: TxRow): void {
    this.transactionToDelete.set(transaction);
    this.deleteError.set(null);
    this.showDeleteModal.set(true);
  }

  closeDeleteModal(): void {
    if (this.deleteSaving()) return;
    this.showDeleteModal.set(false);
    this.transactionToDelete.set(null);
    this.deleteError.set(null);
  }

  confirmDelete(): void {
    const transaction = this.transactionToDelete();
    if (!transaction || this.deleteSaving()) return;

    this.deleteSaving.set(true);
    this.deleteError.set(null);
    this.expenseService.deleteExpense(transaction.id).subscribe({
      next: () => {
        this.deleteSaving.set(false);
        this.showDeleteModal.set(false);
        this.transactionToDelete.set(null);
        this.loadTransactions();
      },
      error: () => {
        this.deleteError.set(this.translate.instant('transactions.errors.deleteFailed'));
        this.deleteSaving.set(false);
      }
    });
  }

  getDeleteAmount(): number {
    return Math.abs(this.transactionToDelete()?.amount ?? 0);
  }

  saveTransaction(): void {
    if (!this.formDescription.trim() || !this.formAmount || this.formAmount <= 0) {
      this.formError.set(this.translate.instant('transactions.errors.descriptionAmountRequired'));
      return;
    }
    if (!this.formCategoryId) {
      this.formError.set(this.translate.instant('transactions.errors.categoryRequired'));
      return;
    }
    if (this.formCategoryId === -1 && !this.formNewCategoryName.trim()) {
      this.formError.set(this.translate.instant('transactions.errors.newCategoryNameRequired'));
      return;
    }
    if (!this.formBudgetId) {
      this.formError.set(this.translate.instant('transactions.errors.budgetRequired'));
      return;
    }
    this.formSaving.set(true);

    const doSave = (categoryId: number) =>
      this.expenseService.createExpense({
        description: this.formDescription.trim(),
        amount: this.formAmount!,
        date: this.formDate,
        category: { id: categoryId },
        budget: { id: this.formBudgetId! },
      });

    if (this.formCategoryId === -1) {
      this.categoryService.createCategory(this.formNewCategoryName.trim()).pipe(
        switchMap(newCat => doSave(newCat.id))
      ).subscribe({
        next: () => { this.formSaving.set(false); this.closeModal(); this.loadTransactions(); },
        error: () => { this.formError.set(this.translate.instant('transactions.errors.createFailed')); this.formSaving.set(false); }
      });
    } else {
      doSave(this.formCategoryId).subscribe({
        next: () => { this.formSaving.set(false); this.closeModal(); this.loadTransactions(); },
        error: () => { this.formError.set(this.translate.instant('transactions.errors.createFailed')); this.formSaving.set(false); }
      });
    }
  }
}
