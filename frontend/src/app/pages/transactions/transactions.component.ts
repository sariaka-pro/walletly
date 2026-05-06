import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ExpenseService } from '../../services/expense.service';
import { AdminService } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';
import { CategoryService } from '../../services/category.service';
import { BudgetService } from '../../services/budget.service';
import { AdminExpense } from '../../models/admin.model';
import { Expense, Category } from '../../models/expense.model';
import { Budget } from '../../models/budget.model';

interface TxRow {
  id: number;
  date: string;
  label: string;
  category: string;
  amount: number;
  userEmail?: string;
  type: 'expense' | 'income';
}

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule],
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
  formCategoryId: number | null = null;
  formBudgetId: number | null = null;
  formError = signal<string | null>(null);
  formSaving = signal(false);
  categories = signal<Category[]>([]);
  budgets = signal<Budget[]>([]);

  constructor(
    private expenseService: ExpenseService,
    private adminService: AdminService,
    private authService: AuthService,
    private categoryService: CategoryService,
    private budgetService: BudgetService,
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
              type: 'expense',
            }))
          );
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossible de charger les transactions.');
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
              type: 'expense',
            }))
          );
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossible de charger les transactions.');
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
      error: () => this.formError.set('Impossible de charger les catégories / budgets.')
    });
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }

  saveTransaction(): void {
    if (!this.formDescription.trim() || !this.formAmount || this.formAmount <= 0) {
      this.formError.set('Description et montant sont obligatoires.');
      return;
    }
    if (!this.formCategoryId) {
      this.formError.set('Sélectionnez une catégorie.');
      return;
    }
    if (!this.formBudgetId) {
      this.formError.set('Sélectionnez un budget.');
      return;
    }
    this.formSaving.set(true);
    this.expenseService.createExpense({
      description: this.formDescription.trim(),
      amount: this.formAmount,
      date: this.formDate,
      category: { id: this.formCategoryId },
      budget: { id: this.formBudgetId },
    }).subscribe({
      next: () => {
        this.formSaving.set(false);
        this.closeModal();
        this.loadTransactions();
      },
      error: () => {
        this.formError.set('Erreur lors de la création.');
        this.formSaving.set(false);
      }
    });
  }
}
