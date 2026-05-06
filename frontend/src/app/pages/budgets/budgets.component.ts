import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
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
  imports: [CommonModule],
  templateUrl: './budgets.component.html',
  styleUrl: './budgets.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BudgetsComponent implements OnInit {

  budgets = signal<BudgetRow[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor(private budgetService: BudgetService) {}

  ngOnInit(): void {
    this.budgetService.getAllBudgets().subscribe({
      next: (data) => {
        // On mappe les champs backend vers la shape attendue par le template
        this.budgets.set(
          data.map(b => ({
            id: b.id,
            category: b.name,
            allocated: Number(b.spendingLimit),
            spent: Number(b.currentSpent),
            icon: 'account_balance_wallet', // icône générique, pas stockée en DB
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
