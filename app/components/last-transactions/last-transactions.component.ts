import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-last-transactions',
  imports: [CommonModule],
  standalone: true,
  templateUrl: './last-transactions.component.html',
  styleUrl: './last-transactions.component.css',
})
export class LastTransactionsComponent {
  // À implémenter avec les données des transactions
  
  constructor() {
  }

  ngOnInit() {
  }
}
