import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SavingsGoalService } from '../../services/savings-goal.service';

interface GoalRow {
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
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SavingsGoalsPageComponent implements OnInit {

  goals = signal<GoalRow[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor(private savingsGoalService: SavingsGoalService) {}

  ngOnInit(): void {
    this.savingsGoalService.getAllSavingsGoals().subscribe({
      next: (data) => {
        this.goals.set(
          data.map(g => ({
            id: g.id,
            name: g.name,
            targetAmount: Number(g.targetAmount),
            savedAmount: Number(g.currentAmount),
            deadline: g.deadline ?? '-',
            icon: 'savings', // icône générique
          }))
        );
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les savings goals.');
        this.loading.set(false);
      }
    });
  }

  getProgressPercent(goal: GoalRow): number {
    return Math.min((goal.savedAmount / goal.targetAmount) * 100, 100);
  }

  isCompleted(goal: GoalRow): boolean {
    return goal.savedAmount >= goal.targetAmount;
  }
}
