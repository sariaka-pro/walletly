import { Component } from '@angular/core';
import { SidenavComponent } from '../../components/sidenav/sidenav.component';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [SidenavComponent],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.css',
})
export class TransactionsComponent {
  // À implémenter
}
