import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { TransactionsComponent } from './pages/transactions/transactions.component';
import { BudgetsComponent } from './pages/budgets/budgets.component';
import { SavingsGoalsPageComponent } from './pages/savings-goals/savings-goals.component';
import { AdminComponent } from './pages/admin/admin.component';
import { LoginComponent } from './components/login/login.component';
import { AppShellComponent } from './layout/app-shell/app-shell.component';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: LoginComponent },
  {
    path: '',
    component: AppShellComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent,
        data: {
          breadcrumbs: [
            { label: 'breadcrumbs.home', url: '/dashboard' },
            { label: 'breadcrumbs.dashboard' },
          ],
          tabs: ['Overview', 'Details'],
        },
      },
      {
        path: 'transactions',
        component: TransactionsComponent,
        data: {
          breadcrumbs: [
            { label: 'breadcrumbs.home', url: '/dashboard' },
            { label: 'breadcrumbs.transactions' },
          ],
          tabs: ['All', 'Expenses'],
        },
      },
      {
        path: 'budgets',
        component: BudgetsComponent,
        data: {
          breadcrumbs: [
            { label: 'breadcrumbs.home', url: '/dashboard' },
            { label: 'breadcrumbs.budgets' },
          ],
          tabs: ['Monthly', 'Annual'],
        },
      },
      {
        path: 'savings-goals',
        component: SavingsGoalsPageComponent,
        data: {
          breadcrumbs: [
            { label: 'breadcrumbs.home', url: '/dashboard' },
            { label: 'breadcrumbs.savingsGoals' },
          ],
          tabs: ['Active', 'Completed'],
        },
      },
      {
        path: 'admin',
        component: AdminComponent,
        canActivate: [adminGuard],
        data: {
          breadcrumbs: [
            { label: 'breadcrumbs.home', url: '/dashboard' },
            { label: 'breadcrumbs.admin' },
          ],
          tabs: ['Users', 'Expenses', 'Stats'],
        },
      },
    ],
  },
];

