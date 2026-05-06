import { ChangeDetectionStrategy, Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExpenseService } from '../../services/expense.service';
import { Expense } from '../../models/expense.model';

interface MonthlyStat { month: string; expense: number; }
interface CategoryStat { label: string; amount: number; percent: number; color: string; }

const PALETTE = ['#3b82f6','#8b5cf6','#f59e0b','#ec4899','#10b981','#ef4444','#06b6d4','#6b7280'];

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics.component.html',
  styleUrl: './analytics.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnalyticsComponent implements OnInit {

  private expenses = signal<Expense[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  monthlySummary = computed<MonthlyStat[]>(() => {
    const map = new Map<string, number>();
    for (const e of this.expenses()) {
      const key = e.date?.slice(0, 7) ?? 'unknown';
      map.set(key, (map.get(key) ?? 0) + e.amount);
    }
    return [...map.entries()]
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([key, expense]) => ({
        month: new Date(key + '-01').toLocaleString('en', { month: 'short', year: '2-digit' }),
        expense,
      }));
  });

  expenseCategories = computed<CategoryStat[]>(() => {
    const map = new Map<string, number>();
    for (const e of this.expenses()) {
      const cat = e.category?.name ?? 'Other';
      map.set(cat, (map.get(cat) ?? 0) + e.amount);
    }
    const total = [...map.values()].reduce((s, v) => s + v, 0);
    return [...map.entries()]
      .sort(([, a], [, b]) => b - a)
      .map(([label, amount], i) => ({
        label,
        amount,
        percent: total > 0 ? Math.round((amount / total) * 100) : 0,
        color: PALETTE[i % PALETTE.length],
      }));
  });

  totalExpense = computed(() => this.expenses().reduce((s, e) => s + e.amount, 0));

  thisMonthExpense = computed(() => {
    const ym = new Date().toISOString().slice(0, 7);
    return this.expenses()
      .filter(e => e.date?.startsWith(ym))
      .reduce((s, e) => s + e.amount, 0);
  });

  maxMonthlyExpense = computed(() =>
    Math.max(...this.monthlySummary().map(m => m.expense), 1)
  );

  constructor(private expenseService: ExpenseService) {}

  ngOnInit(): void {
    this.expenseService.getAllExpenses().subscribe({
      next: (data) => { this.expenses.set(data); this.loading.set(false); },
      error: () => { this.error.set('Impossible de charger les données.'); this.loading.set(false); },
    });
  }
}
