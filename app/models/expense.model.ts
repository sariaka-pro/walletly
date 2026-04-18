import { Budget } from "./budget.model";

export interface Expense {
  id: number;
  amount: number;
  description: string;
  date: string;            
  category: Category;
  budget: Budget;
  user?: any;
}

export interface CreateExpenseDto {
  amount: number;
  description: string;
  date: string;
  category: { id: number };
  budget: { id: number };
}

export interface Category {
  id: number;
  name: string;
  color?: string;
}
