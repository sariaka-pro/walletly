import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ExpenseService } from '../../services/expense.service';
import { AdminService } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';
import { Expense } from '../../models/expense.model';
import { TranslatePipe } from '@ngx-translate/core';

interface KpiItem {
  labelKey: string;
  value: string;
  icon: string;
  tone: 'positive' | 'negative' | 'neutral';
}

interface ActionCard {
  titleKey: string;
  descriptionKey: string;
  icon: string;
  tone: 'success' | 'danger' | 'info';
}

interface CategorySlice {
  label: string;
  amount: number;
  percentage: number;
  color: string;
}

type DashboardPeriod = 'this-month' | 'last-month' | 'this-year' | 'last-12-months';

interface TxRow {
  label: string;
  category: string;
  date: string;
  amount: number;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, TranslatePipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent implements OnInit {

  periodTabs: { labelKey: string; value: DashboardPeriod }[] = [
    { labelKey: 'dashboard.period.thisMonth', value: 'this-month' },
    { labelKey: 'dashboard.period.lastMonth', value: 'last-month' },
    { labelKey: 'dashboard.period.thisYear', value: 'this-year' },
    { labelKey: 'dashboard.period.last12Months', value: 'last-12-months' },
  ];
  activePeriod = signal<DashboardPeriod>('this-month');
  displayName = signal<string>('');
  actionCards: ActionCard[] = [
    {
      titleKey: 'dashboard.actions.addExpense.title',
      descriptionKey: 'dashboard.actions.addExpense.description',
      icon: 'remove_circle',
      tone: 'danger'
    },
    {
      titleKey: 'dashboard.actions.transfer.title',
      descriptionKey: 'dashboard.actions.transfer.description',
      icon: 'swap_horiz',
      tone: 'info'
    },
  ];

  kpis = signal<KpiItem[]>([]);
  recentTransactions = signal<TxRow[]>([]);
  loading = signal(true);
  totalExpenses = signal(0);
  monthlyExpenses = signal(0);
  monthlyTransactions = signal(0);
  topCategory = signal('Category');
  topCategoryPercent = signal(0);
  heroValue = signal('€0.00');
  heroCaptionKey = signal('dashboard.period.thisMonth');
  categorySlices = signal<CategorySlice[]>([]);
  chartStyle = signal('conic-gradient(#e7e0d5 0% 100%)');

  private allExpenses: Expense[] = [];
  private readonly sliceColors = ['#1b2232', '#f4c94f', '#ff7b6b', '#7b9762', '#6c8bd8', '#9b7bff'];

  constructor(
    private expenseService: ExpenseService,
    private adminService: AdminService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.displayName.set(
      this.buildDisplayName(this.authService.getCurrentFirstName(), this.authService.getCurrentEmail())
    );
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
          { labelKey: 'dashboard.kpis.users', value: `${stats.totalUsers}`, icon: 'group', tone: 'positive' },
          { labelKey: 'dashboard.kpis.expenses', value: `€ ${Math.abs(stats.totalExpensesAmount).toFixed(2)}`, icon: 'euro', tone: 'negative' },
          { labelKey: 'dashboard.kpis.categories', value: `${stats.totalCategories}`, icon: 'category', tone: 'positive' },
        ]);
        this.totalExpenses.set(Math.abs(stats.totalExpensesAmount));
        this.monthlyExpenses.set(Math.abs(stats.totalExpensesAmount));
        this.monthlyTransactions.set(stats.totalUsers);
        this.topCategory.set('All users');
        this.topCategoryPercent.set(68);
        this.heroValue.set(`€ ${Math.abs(stats.totalExpensesAmount).toFixed(2)}`);
        this.heroCaptionKey.set('dashboard.globalExpenses');
        this.categorySlices.set([
          { label: 'Utilisateurs', amount: stats.totalUsers, percentage: 40, color: this.sliceColors[0] },
          { label: 'Depenses', amount: stats.totalExpensesAmount, percentage: 38, color: this.sliceColors[1] },
          { label: 'Categories', amount: stats.totalCategories, percentage: 22, color: this.sliceColors[2] },
        ]);
        this.chartStyle.set('conic-gradient(#1b2232 0% 40%, #f4c94f 40% 78%, #ff7b6b 78% 100%)');
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  private loadUserDashboard(): void {
    this.expenseService.getAllExpenses().subscribe({
      next: (expenses: Expense[]) => {
        this.allExpenses = expenses;
        this.kpis.set(this.buildKpis(expenses));
        this.refreshVisualData();
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  setPeriod(period: DashboardPeriod): void {
    this.activePeriod.set(period);
    this.refreshVisualData();
  }

  private buildKpis(expenses: Expense[]): KpiItem[] {
    const monthExpenses = this.getCurrentMonthExpenses(expenses);

    const totalMonthly = monthExpenses.reduce((s, e) => s + Number(e.amount), 0);
    const totalAll = expenses.reduce((s, e) => s + Number(e.amount), 0);

    this.totalExpenses.set(Math.abs(totalAll));
    this.monthlyExpenses.set(Math.abs(totalMonthly));
    this.monthlyTransactions.set(monthExpenses.length);

    return [
      { labelKey: 'dashboard.kpis.balance', value: `€ ${Math.abs(totalAll).toFixed(2)}`, icon: 'account_balance_wallet', tone: 'neutral' },
      { labelKey: 'dashboard.kpis.thisMonth', value: `€ ${Math.abs(totalMonthly).toFixed(2)}`, icon: 'trending_down', tone: 'negative' },
      { labelKey: 'dashboard.kpis.transactions', value: `${monthExpenses.length}`, icon: 'swap_horiz', tone: 'positive' },
    ];
  }

  private refreshVisualData(): void {
    const periodExpenses = this.getPeriodExpenses();
    const sorted = [...periodExpenses].sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());

    this.recentTransactions.set(
      sorted.slice(0, 6).map((expense) => ({
        label: expense.description,
        category: expense.category?.name ?? '-',
        date: expense.date,
        amount: -Math.abs(Number(expense.amount)),
      }))
    );

    const categoryBreakdown = this.buildCategoryBreakdown(periodExpenses);
    const totalSpent = periodExpenses.reduce((sum, expense) => sum + Math.abs(Number(expense.amount)), 0);
    const top = categoryBreakdown[0];

    this.heroValue.set(`€ ${totalSpent.toFixed(2)}`);
    this.heroCaptionKey.set(this.getPeriodLabelKey(this.activePeriod()));
    this.categorySlices.set(categoryBreakdown);
    this.chartStyle.set(this.buildChartStyle(categoryBreakdown));
    this.topCategory.set(top?.label ?? 'No category');
    this.topCategoryPercent.set(top ? Math.round(top.percentage) : 0);
    this.monthlyExpenses.set(totalSpent);
    this.monthlyTransactions.set(periodExpenses.length);
  }

  private getCurrentMonthExpenses(expenses: Expense[]): Expense[] {
    const now = new Date();
    const currentMonth = now.getMonth();
    const currentYear = now.getFullYear();

    return expenses.filter((expense) => {
      const date = new Date(expense.date);
      return date.getMonth() === currentMonth && date.getFullYear() === currentYear;
    });
  }

  private getPeriodExpenses(): Expense[] {
    const now = new Date();
    const currentMonth = now.getMonth();
    const currentYear = now.getFullYear();

    return this.allExpenses.filter((expense) => {
      const date = new Date(expense.date);

      switch (this.activePeriod()) {
        case 'last-month': {
          const previous = new Date(currentYear, currentMonth - 1, 1);
          return date.getMonth() === previous.getMonth() && date.getFullYear() === previous.getFullYear();
        }
        case 'this-year':
          return date.getFullYear() === currentYear;
        case 'last-12-months': {
          const limit = new Date(currentYear, currentMonth - 11, 1);
          return date >= limit;
        }
        case 'this-month':
        default:
          return date.getMonth() === currentMonth && date.getFullYear() === currentYear;
      }
    });
  }

  private buildCategoryBreakdown(expenses: Expense[]): CategorySlice[] {
    const totals = new Map<string, number>();

    for (const expense of expenses) {
      const name = expense.category?.name?.trim() || 'Uncategorized';
      totals.set(name, (totals.get(name) ?? 0) + Math.abs(Number(expense.amount)));
    }

    const total = [...totals.values()].reduce((sum, amount) => sum + amount, 0);

    return [...totals.entries()]
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5)
      .map(([label, amount], index) => ({
        label,
        amount,
        percentage: total > 0 ? (amount / total) * 100 : 0,
        color: this.sliceColors[index % this.sliceColors.length],
      }));
  }

  private buildChartStyle(slices: CategorySlice[]): string {
    if (slices.length === 0) {
      return 'conic-gradient(#e7e0d5 0% 100%)';
    }

    let cursor = 0;
    const segments = slices.map((slice) => {
      const start = cursor;
      cursor += slice.percentage;
      return `${slice.color} ${start}% ${cursor}%`;
    });

    if (cursor < 100) {
      segments.push(`#e7e0d5 ${cursor}% 100%`);
    }

    return `conic-gradient(${segments.join(', ')})`;
  }

  private getPeriodLabelKey(period: DashboardPeriod): string {
    switch (period) {
      case 'last-month': return 'dashboard.period.lastMonth';
      case 'this-year': return 'dashboard.period.thisYear';
      case 'last-12-months': return 'dashboard.period.last12Months';
      case 'this-month':
      default:
        return 'dashboard.period.thisMonth';
    }
  }

  private buildDisplayName(firstName: string | null, email: string | null): string {
    if (firstName && firstName.trim()) {
      const normalized = firstName.trim();
      return normalized.charAt(0).toUpperCase() + normalized.slice(1);
    }

    if (!email) {
      return 'Utilisateur';
    }

    const localPart = email.split('@')[0] ?? email;
    return localPart
      .split(/[._-]/)
      .filter(Boolean)
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  }
}
