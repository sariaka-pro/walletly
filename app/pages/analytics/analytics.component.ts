import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface ExpenseCategory {
  label: string;
  amount: number;
  percent: number;
  color: string;
}

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics.component.html',
  styleUrl: './analytics.component.css',
})
export class AnalyticsComponent {
  readonly monthlySummary = [
    { month: 'Jan', income: 3200, expense: 2400 },
    { month: 'Feb', income: 3200, expense: 2100 },
    { month: 'Mar', income: 3700, expense: 2800 },
    { month: 'Apr', income: 3200, expense: 2650 },
  ];

  readonly expenseCategories: ExpenseCategory[] = [
    { label: 'Food', amount: 342, percent: 34, color: '#3b82f6' },
    { label: 'Transport', amount: 180, percent: 18, color: '#8b5cf6' },
    { label: 'Utilities', amount: 260, percent: 26, color: '#f59e0b' },
    { label: 'Entertainment', amount: 95, percent: 9, color: '#ec4899' },
    { label: 'Health', amount: 35, percent: 3, color: '#10b981' },
    { label: 'Other', amount: 90, percent: 9, color: '#6b7280' },
  ];

  get totalIncome(): number {
    return this.monthlySummary.reduce((s, m) => s + m.income, 0);
  }

  get totalExpense(): number {
    return this.monthlySummary.reduce((s, m) => s + m.expense, 0);
  }

  get netSavings(): number {
    return this.totalIncome - this.totalExpense;
  }
}
