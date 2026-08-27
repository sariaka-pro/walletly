import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SavingsGoalService } from '../../services/savings-goal.service';
import { AdminService } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';
import { AdminSavingsGoal } from '../../models/admin.model';
import { SavingsGoal } from '../../models/savings-goal.model';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

interface GoalRow {
  id: number;
  name: string;
  targetAmount: number;
  savedAmount: number;
  deadline: string;
  icon: string;
  userEmail?: string;
}

@Component({
  selector: 'app-savings-goals-page',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslatePipe],
  templateUrl: './savings-goals.component.html',
  styleUrl: './savings-goals.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SavingsGoalsPageComponent implements OnInit {

  goals = signal<GoalRow[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  isAdmin = false;

  // --- Modal New Goal ---
  showModal = signal(false);
  formName = '';
  formTargetAmount: number | null = null;
  formCurrentAmount: number = 0;
  formDeadline: string = '';
  formError = signal<string | null>(null);
  formSaving = signal(false);

  constructor(
    private savingsGoalService: SavingsGoalService,
    private adminService: AdminService,
    private authService: AuthService,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.loadGoals();
  }

  loadGoals(): void {
    this.loading.set(true);
    const request$ = this.isAdmin
      ? this.adminService.getAllSavingsGoals()
      : this.savingsGoalService.getAllSavingsGoals();

    request$.subscribe({
      next: (data) => {
        const goals = data as (SavingsGoal | AdminSavingsGoal)[];
        this.goals.set(
          goals.map(g => ({
            id: g.id,
            name: g.name,
            targetAmount: Number(g.targetAmount),
            savedAmount: Number(g.currentAmount),
            deadline: g.deadline ?? '-',
            icon: 'savings',
            userEmail: 'userEmail' in g ? g.userEmail : undefined,
          }))
        );
        this.loading.set(false);
      },
      error: () => {
        this.error.set(this.translate.instant('savingsGoals.errors.loadFailed'));
        this.loading.set(false);
      }
    });
  }

  openModal(): void {
    this.formName = '';
    this.formTargetAmount = null;
    this.formCurrentAmount = 0;
    this.formDeadline = '';
    this.formError.set(null);
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }

  saveGoal(): void {
    if (!this.formName.trim() || !this.formTargetAmount || this.formTargetAmount <= 0) {
      this.formError.set(this.translate.instant('savingsGoals.errors.nameTargetRequired'));
      return;
    }
    this.formSaving.set(true);
    this.savingsGoalService.createSavingsGoal({
      name: this.formName.trim(),
      targetAmount: this.formTargetAmount,
      currentAmount: this.formCurrentAmount || 0,
      deadline: this.formDeadline || null,
    }).subscribe({
      next: () => {
        this.formSaving.set(false);
        this.closeModal();
        this.loadGoals();
      },
      error: () => {
        this.formError.set(this.translate.instant('savingsGoals.errors.createFailed'));
        this.formSaving.set(false);
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
