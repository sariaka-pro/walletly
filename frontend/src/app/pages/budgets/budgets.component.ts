import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Budget {
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
})

export class BudgetsComponent {
  readonly budgets: Budget[] = [
    { id: 1, category: 'Food & Groceries', allocated: 500, spent: 342, icon: 'restaurant' },
    { id: 2, category: 'Transport', allocated: 200, spent: 180, icon: 'directions_car' },
    { id: 3, category: 'Entertainment', allocated: 150, spent: 95, icon: 'movie' },
    { id: 4, category: 'Utilities', allocated: 300, spent: 260, icon: 'bolt' },
    { id: 5, category: 'Health', allocated: 100, spent: 35, icon: 'health_and_safety' },
    { id: 6, category: 'Shopping', allocated: 250, spent: 310, icon: 'shopping_bag' },
  ];

  getProgressPercent(budget: Budget): number {
    return Math.min((budget.spent / budget.allocated) * 100, 100);
  }

  getProgressClass(budget: Budget): string {
    const pct = (budget.spent / budget.allocated) * 100;
    if (pct >= 100) return 'progress-bar--danger';
    if (pct >= 80) return 'progress-bar--warning';
    return 'progress-bar--normal';
  }
}
