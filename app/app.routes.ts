import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { TransactionsComponent } from './pages/transactions/transactions.component';
import { BudgetsComponent } from './pages/budgets/budgets.component';
import { SavingsGoalsPageComponent } from './pages/savings-goals/savings-goals.component';
import { AnalyticsComponent } from './pages/analytics/analytics.component';
import { LoginComponent } from './components/login/login.component';

export const routes: Routes = [
    { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
    { path: 'login', component: LoginComponent},
    { path: 'dashboard', component: DashboardComponent },
    { path: 'transactions', component: TransactionsComponent },
    { path: 'budgets', component: BudgetsComponent },
    { path: 'savings-goals', component: SavingsGoalsPageComponent },
    { path: 'analytics', component: AnalyticsComponent },
];

