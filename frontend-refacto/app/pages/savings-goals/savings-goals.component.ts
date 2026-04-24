import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface SavingsGoal {
  id: number;
  name: string;
  targetAmount: number;
  savedAmount: number;
  deadline: string;
  icon: string;
}

@Component({
  selector: 'app-savings-goals-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './savings-goals.component.html',
  styleUrl: './savings-goals.component.css',
})
export class SavingsGoalsPageComponent {
  readonly goals: SavingsGoal[] = [
    { id: 1, name: 'Emergency Fund', targetAmount: 10000, savedAmount: 6500, deadline: '2025-12-31', icon: 'shield' },
    { id: 2, name: 'Vacation to Japan', targetAmount: 3000, savedAmount: 1200, deadline: '2025-08-01', icon: 'flight' },
    { id: 3, name: 'New Laptop', targetAmount: 1500, savedAmount: 1500, deadline: '2025-03-01', icon: 'laptop' },
    { id: 4, name: 'Home Down Payment', targetAmount: 50000, savedAmount: 12000, deadline: '2027-01-01', icon: 'home' },
  ];

  getProgressPercent(goal: SavingsGoal): number {
    return Math.min((goal.savedAmount / goal.targetAmount) * 100, 100);
  }

  isCompleted(goal: SavingsGoal): boolean {
    return goal.savedAmount >= goal.targetAmount;
  }
}
