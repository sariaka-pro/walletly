import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ExpenseService } from '../../services/expense.service';
import { AdminService } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';
import { Expense } from '../../models/expense.model';

interface KpiItem {
  label: string;
  value: string;
  change: string;
  positive: boolean;
  icon: string;
}

interface TxRow {
  label: string;
  category: string;
  date: string;
  amount: number;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent implements OnInit {

  kpis = signal<KpiItem[]>([]);
  recentTransactions = signal<TxRow[]>([]);
  loading = signal(true);

  constructor(
    private expenseService: ExpenseService,
    private adminService: AdminService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    if (this.authService.isAdmin()) {
      this.loadAdminDashboard();
    } else {
      this.loadUserDashboard();
    }
  }

  private loadAdminDashboard(): void {
    this.adminService.getGlobalStats().subscribe({
      next: (stats) => {
        this.kpis.set([
          { label: 'Total Users', value: `${stats.totalUsers}`, change: '-', positive: true, icon: 'group' },
          { label: 'Total Expenses', value: `€ ${stats.totalExpensesAmount.toFixed(2)}`, change: '-', positive: false, icon: 'euro' },
          { label: 'Total Categories', value: `${stats.totalCategories}`, change: '-', positive: true, icon: 'category' },
        ]);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  private loadUserDashboard(): void {
    this.expenseService.getAllExpenses().subscribe({
      next: (expenses: Expense[]) => {
        this.kpis.set(this.buildKpis(expenses));
        this.recentTransactions.set(
          [...expenses]
            .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
            .slice(0, 5)
            .map(e => ({
              label: e.description,
              category: e.category?.name ?? '-',
              date: e.date,
              amount: -Math.abs(e.amount),
            }))
        );
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  private buildKpis(expenses: Expense[]): KpiItem[] {
    const now = new Date();
    const currentMonth = now.getMonth();
    const currentYear = now.getFullYear();

    const monthExpenses = expenses.filter(e => {
      const d = new Date(e.date);
      return d.getMonth() === currentMonth && d.getFullYear() === currentYear;
    });

    const prevMonth = currentMonth === 0 ? 11 : currentMonth - 1;
    const prevYear = currentMonth === 0 ? currentYear - 1 : currentYear;
    const prevMonthExpenses = expenses.filter(e => {
      const d = new Date(e.date);
      return d.getMonth() === prevMonth && d.getFullYear() === prevYear;
    });

    const totalMonthly = monthExpenses.reduce((s, e) => s + e.amount, 0);
    const totalPrev = prevMonthExpenses.reduce((s, e) => s + e.amount, 0);
    const totalAll = expenses.reduce((s, e) => s + e.amount, 0);

    const changePct = totalPrev > 0
      ? (((totalMonthly - totalPrev) / totalPrev) * 100).toFixed(1)
      : '0';

    return [
      { label: 'Total Expenses', value: `€ ${totalAll.toFixed(2)}`, change: '-', positive: false, icon: 'account_balance' },
      { label: 'Monthly Expenses', value: `€ ${totalMonthly.toFixed(2)}`, change: `${changePct}%`, positive: totalMonthly <= totalPrev, icon: 'trending_down' },
      { label: 'Transactions this month', value: `${monthExpenses.length}`, change: '-', positive: true, icon: 'swap_horiz' },
    ];
  }
}
