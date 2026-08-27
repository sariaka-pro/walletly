export interface AdminUserSummary {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  email: string;
  role: 'USER' | 'ADMIN';
  totalExpensesAmount: number;
}

export interface AdminUserDetails {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
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

export interface AdminBudget {
  id: number;
  name: string;
  spendingLimit: number;
  currentSpent: number;
  yearMonth: string;
  userId: number;
  userEmail: string;
}

export interface AdminSavingsGoal {
  id: number;
  name: string;
  targetAmount: number;
  currentAmount: number;
  deadline: string | null;
  userId: number;
  userEmail: string;
}

export interface CreateAdminUserPayload {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role: 'USER' | 'ADMIN';
}

export interface UpdateAdminUserPayload {
  firstName: string;
  lastName: string;
  email: string;
  password?: string;
  role: 'USER' | 'ADMIN';
}
