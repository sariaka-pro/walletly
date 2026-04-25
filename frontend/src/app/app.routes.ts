import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { TransactionsComponent } from './pages/transactions/transactions.component';
import { BudgetsComponent } from './pages/budgets/budgets.component';
import { SavingsGoalsPageComponent } from './pages/savings-goals/savings-goals.component';
import { AnalyticsComponent } from './pages/analytics/analytics.component';
import { LoginComponent } from './components/login/login.component';
import { AppShellComponent } from './layout/app-shell/app-shell.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: AppShellComponent,
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent,
        data: {
          breadcrumbs: [
            { label: 'Home', url: '/dashboard' },
            { label: 'Dashboard' },
          ],
          tabs: ['Overview', 'Details', 'Analytics'],
        },
      },
      {
        path: 'transactions',
        component: TransactionsComponent,
        data: {
          breadcrumbs: [
            { label: 'Home', url: '/dashboard' },
            { label: 'Transactions' },
          ],
          tabs: ['All', 'Income', 'Expenses'],
        },
      },
      {
        path: 'budgets',
        component: BudgetsComponent,
        data: {
          breadcrumbs: [
            { label: 'Home', url: '/dashboard' },
            { label: 'Budgets' },
          ],
          tabs: ['Monthly', 'Annual'],
        },
      },
      {
        path: 'savings-goals',
        component: SavingsGoalsPageComponent,
        data: {
          breadcrumbs: [
            { label: 'Home', url: '/dashboard' },
            { label: 'Savings Goals' },
          ],
          tabs: ['Active', 'Completed'],
        },
      },
      {
        path: 'analytics',
        component: AnalyticsComponent,
        data: {
          breadcrumbs: [
            { label: 'Home', url: '/dashboard' },
            { label: 'Analytics' },
          ],
          tabs: ['Charts', 'Reports', 'Trends'],
        },
      },
    ],
  },
];

