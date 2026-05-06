export interface AdminUserSummary {
  id: number;
  email: string;
  role: 'USER' | 'ADMIN';
  totalExpensesAmount: number;
}

export interface AdminUserDetails {
  id: number;
  email: string;
  role: 'USER' | 'ADMIN';
  totalExpensesCount: number;
  totalBudgets: number;
  totalCategories: number;
  totalExpensesAmount: number;
}

export interface AdminGlobalStats {
  totalUsers: number;
  totalExpensesAmount: number;
  totalCategories: number;
}

export interface AdminExpense {
  id: number;
  amount: number;
  description: string;
  date: string;
  userId: number;
  userEmail: string;
  categoryId: number;
  categoryName: string;
  budgetId: number;
}
