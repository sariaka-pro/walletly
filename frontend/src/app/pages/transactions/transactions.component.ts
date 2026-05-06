import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExpenseService } from '../../services/expense.service';
import { AdminService } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';
import { AdminExpense } from '../../models/admin.model';
import { Expense } from '../../models/expense.model';

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
  imports: [CommonModule],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransactionsComponent implements OnInit {

  transactions = signal<TxRow[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  isAdmin = false;

  constructor(
    private expenseService: ExpenseService,
    private adminService: AdminService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
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
}
