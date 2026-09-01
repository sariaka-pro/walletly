import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BudgetService } from '../../services/budget.service';
import { AdminService } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';
import { AdminBudget } from '../../models/admin.model';
import { Budget } from '../../models/budget.model';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

interface BudgetRow {
  id: number;
  category: string;
  allocated: number;
  spent: number;
  yearMonth: string;
  icon: string;
  userEmail?: string;
}

@Component({
  selector: 'app-budgets',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslatePipe],
  templateUrl: './budgets.component.html',
  styleUrl: './budgets.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BudgetsComponent implements OnInit {

  budgets = signal<BudgetRow[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  isAdmin = false;

  // --- Modal New Budget ---
  showModal = signal(false);
  formName = '';
  formSpendingLimit: number | null = null;
  formYearMonth: string = '';
  formError = signal<string | null>(null);
  formSaving = signal(false);

  // --- Modal Edit Budget ---
  showEditModal = signal(false);
  budgetToEdit = signal<BudgetRow | null>(null);
  editName = '';
  editSpendingLimit: number | null = null;
  editError = signal<string | null>(null);
  editSaving = signal(false);

  // --- Modal Delete Budget ---
  showDeleteModal = signal(false);
  budgetToDelete = signal<BudgetRow | null>(null);
  deleteError = signal<string | null>(null);
  deleteSaving = signal(false);

  constructor(
    private budgetService: BudgetService,
    private adminService: AdminService,
    private authService: AuthService,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.loadBudgets();
  }

  loadBudgets(): void {
    this.loading.set(true);
    if (this.isAdmin) {
      this.adminService.getAllBudgets().subscribe({
        next: (budgets: AdminBudget[]) => {
          this.budgets.set(
            budgets.map(b => ({
              id: b.id,
              category: b.name,
              allocated: Number(b.spendingLimit),
              spent: Number(b.currentSpent),
              yearMonth: b.yearMonth,
              icon: 'account_balance_wallet',
              userEmail: b.userEmail,
            }))
          );
          this.loading.set(false);
        },
        error: () => {
          this.error.set(this.translate.instant('budgets.errors.loadFailed'));
          this.loading.set(false);
        }
      });
      return;
    }

    this.budgetService.getAllBudgets().subscribe({
      next: (budgets: Budget[]) => {
        this.budgets.set(
          budgets.map(b => ({
            id: b.id,
            category: b.name,
            allocated: Number(b.spendingLimit),
            spent: Number(b.currentSpent),
            yearMonth: b.yearMonth,
            icon: 'account_balance_wallet',
          }))
        );
        this.loading.set(false);
      },
      error: () => {
        this.error.set(this.translate.instant('budgets.errors.loadFailed'));
        this.loading.set(false);
      }
    });
  }

  openModal(): void {
    this.formName = '';
    this.formSpendingLimit = null;
    // Pré-remplir avec le mois courant au format YYYY-MM
    const now = new Date();
    this.formYearMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
    this.formError.set(null);
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }

  saveBudget(): void {
    if (!this.formName.trim() || !this.formSpendingLimit || this.formSpendingLimit <= 0) {
      this.formError.set(this.translate.instant('budgets.errors.nameLimitRequired'));
      return;
    }
    if (!this.formYearMonth) {
      this.formError.set(this.translate.instant('budgets.errors.periodRequired'));
      return;
    }
    this.formSaving.set(true);
    this.budgetService.createBudget({
      name: this.formName.trim(),
      spendingLimit: this.formSpendingLimit,
      yearMonth: this.formYearMonth,
    }).subscribe({
      next: () => {
        this.formSaving.set(false);
        this.closeModal();
        this.loadBudgets();
      },
      error: () => {
        this.formError.set(this.translate.instant('budgets.errors.createFailed'));
        this.formSaving.set(false);
      }
    });
  }

  openEditModal(budget: BudgetRow): void {
    this.budgetToEdit.set(budget);
    this.editName = budget.category;
    this.editSpendingLimit = budget.allocated;
    this.editError.set(null);
    this.showEditModal.set(true);
  }

  closeEditModal(): void {
    if (this.editSaving()) return;
    this.showEditModal.set(false);
    this.budgetToEdit.set(null);
    this.editError.set(null);
  }

  saveBudgetEdit(): void {
    const budget = this.budgetToEdit();
    if (!budget || this.editSaving()) return;
    if (!this.editName.trim() || !this.editSpendingLimit || this.editSpendingLimit <= 0) {
      this.editError.set(this.translate.instant('budgets.errors.nameLimitRequired'));
      return;
    }

    this.editSaving.set(true);
    this.editError.set(null);
    this.budgetService.updateBudget(budget.id, {
      name: this.editName.trim(),
      spendingLimit: this.editSpendingLimit,
      yearMonth: budget.yearMonth,
    }).subscribe({
      next: () => {
        this.editSaving.set(false);
        this.showEditModal.set(false);
        this.budgetToEdit.set(null);
        this.loadBudgets();
      },
      error: () => {
        this.editError.set(this.translate.instant('budgets.errors.updateFailed'));
        this.editSaving.set(false);
      }
    });
  }

  openDeleteModal(budget: BudgetRow): void {
    this.budgetToDelete.set(budget);
    this.deleteError.set(null);
    this.showDeleteModal.set(true);
  }

  closeDeleteModal(): void {
    if (this.deleteSaving()) return;
    this.showDeleteModal.set(false);
    this.budgetToDelete.set(null);
    this.deleteError.set(null);
  }

  confirmDelete(): void {
    const budget = this.budgetToDelete();
    if (!budget || this.deleteSaving()) return;

    this.deleteSaving.set(true);
    this.deleteError.set(null);
    this.budgetService.deleteBudget(budget.id).subscribe({
      next: () => {
        this.deleteSaving.set(false);
        this.showDeleteModal.set(false);
        this.budgetToDelete.set(null);
        this.loadBudgets();
      },
      error: () => {
        this.deleteError.set(this.translate.instant('budgets.errors.deleteFailed'));
        this.deleteSaving.set(false);
      }
    });
  }

  getProgressPercent(budget: BudgetRow): number {
    return Math.min((budget.spent / budget.allocated) * 100, 100);
  }

  getProgressClass(budget: BudgetRow): string {
    const pct = (budget.spent / budget.allocated) * 100;
    if (pct >= 100) return 'progress-bar--danger';
    if (pct >= 80) return 'progress-bar--warning';
    return 'progress-bar--normal';
  }
}
