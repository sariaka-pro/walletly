import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BudgetService } from '../../services/budget.service';

interface BudgetRow {
  id: number;
  category: string;
  allocated: number;
  spent: number;
  icon: string;
}

@Component({
  selector: 'app-budgets',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './budgets.component.html',
  styleUrl: './budgets.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BudgetsComponent implements OnInit {

  budgets = signal<BudgetRow[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  // --- Modal New Budget ---
  showModal = signal(false);
  formName = '';
  formSpendingLimit: number | null = null;
  formYearMonth: string = '';
  formError = signal<string | null>(null);
  formSaving = signal(false);

  constructor(private budgetService: BudgetService) {}

  ngOnInit(): void {
    this.loadBudgets();
  }

  loadBudgets(): void {
    this.loading.set(true);
    this.budgetService.getAllBudgets().subscribe({
      next: (data) => {
        this.budgets.set(
          data.map(b => ({
            id: b.id,
            category: b.name,
            allocated: Number(b.spendingLimit),
            spent: Number(b.currentSpent),
            icon: 'account_balance_wallet',
          }))
        );
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les budgets.');
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
      this.formError.set('Nom et limite de dépense sont obligatoires.');
      return;
    }
    if (!this.formYearMonth) {
      this.formError.set('La période est obligatoire.');
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
        this.formError.set('Erreur lors de la création.');
        this.formSaving.set(false);
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
