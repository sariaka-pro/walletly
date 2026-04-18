import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.css',
})
export class TransactionsComponent {
  readonly transactions = [
    { id: 1, date: '2025-04-15', label: 'Grocery Store', category: 'Food', amount: -85.50, type: 'expense' },
    { id: 2, date: '2025-04-14', label: 'Monthly Salary', category: 'Income', amount: 3200.00, type: 'income' },
    { id: 3, date: '2025-04-13', label: 'Netflix', category: 'Entertainment', amount: -15.99, type: 'expense' },
    { id: 4, date: '2025-04-12', label: 'Electric Bill', category: 'Utilities', amount: -120.00, type: 'expense' },
    { id: 5, date: '2025-04-10', label: 'Freelance Payment', category: 'Income', amount: 500.00, type: 'income' },
    { id: 6, date: '2025-04-09', label: 'Restaurant', category: 'Food', amount: -42.00, type: 'expense' },
  ];
}
