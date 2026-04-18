import { ChangeDetectionStrategy, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

interface KpiItem {
  label: string;
  value: string;
  change: string;
  positive: boolean;
  icon: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent {
  readonly kpis: KpiItem[] = [
    { label: 'Total Balance', value: '€ 12,450', change: '+2.5%', positive: true, icon: 'account_balance' },
    { label: 'Monthly Income', value: '€ 3,200', change: '+0%', positive: true, icon: 'trending_up' },
    { label: 'Monthly Expenses', value: '€ 2,653', change: '+8.1%', positive: false, icon: 'trending_down' },
    { label: 'Net Savings', value: '€ 547', change: '-12%', positive: false, icon: 'savings' },
  ];

  readonly recentTransactions = [
    { label: 'Grocery Store', category: 'Food', amount: -85.50, date: '2025-04-15' },
    { label: 'Monthly Salary', category: 'Income', amount: 3200.00, date: '2025-04-14' },
    { label: 'Netflix', category: 'Entertainment', amount: -15.99, date: '2025-04-13' },
    { label: 'Electric Bill', category: 'Utilities', amount: -120.00, date: '2025-04-12' },
    { label: 'Freelance', category: 'Income', amount: 500.00, date: '2025-04-10' },
  ];
}
