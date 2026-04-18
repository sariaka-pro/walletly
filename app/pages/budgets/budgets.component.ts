import { Component } from '@angular/core';
import { SidenavComponent } from '../../components/sidenav/sidenav.component';

@Component({
  selector: 'app-budgets',
  standalone: true,
  imports: [SidenavComponent],
  templateUrl: './budgets.component.html',
  styleUrl: './budgets.component.css',
})
export class BudgetsComponent {
  // À implémenter
}
