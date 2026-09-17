
export interface Budget {
  id: number; 
  name: string; 
  spendingLimit: number; 
  currentSpent: number; 
  remaining: number; 
  percentageSpent: number; 
  yearMonth: string; 
  period: 'MONTHLY';
  user?: any; 
}

export interface CreateBudgetDto {
  name: string;
  spendingLimit: number;
  yearMonth: string;
}
