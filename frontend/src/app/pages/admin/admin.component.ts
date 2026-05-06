import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin.service';
import { AdminGlobalStats, AdminUserSummary } from '../../models/admin.model';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminComponent implements OnInit {

  users = signal<AdminUserSummary[]>([]);
  stats = signal<AdminGlobalStats | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);

  // --- Delete modal ---
  showDeleteModal = signal(false);
  userToDelete = signal<AdminUserSummary | null>(null);

  // --- Edit role ---
  editingUserId = signal<number | null>(null);
  editRole = signal<'USER' | 'ADMIN'>('USER');

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading.set(true);
    this.adminService.getGlobalStats().subscribe({
      next: (s) => this.stats.set(s),
      error: () => {}
    });
    this.adminService.getAllUsers().subscribe({
      next: (u) => {
        this.users.set(u);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les utilisateurs.');
        this.loading.set(false);
      }
    });
  }

  // --- Delete ---
  openDeleteModal(user: AdminUserSummary): void {
    this.userToDelete.set(user);
    this.showDeleteModal.set(true);
  }

  closeDeleteModal(): void {
    this.showDeleteModal.set(false);
    this.userToDelete.set(null);
  }

  confirmDelete(): void {
    const user = this.userToDelete();
    if (!user) return;
    this.adminService.deleteUser(user.id).subscribe({
      next: () => {
        this.closeDeleteModal();
        this.loadData();
      },
      error: () => this.error.set('Erreur lors de la suppression.')
    });
  }

  // --- Edit role ---
  startEdit(user: AdminUserSummary): void {
    this.editingUserId.set(user.id);
    this.editRole.set(user.role);
  }

  cancelEdit(): void {
    this.editingUserId.set(null);
  }

  saveRole(userId: number): void {
    this.adminService.changeUserRole(userId, this.editRole()).subscribe({
      next: () => {
        this.editingUserId.set(null);
        this.loadData();
      },
      error: () => this.error.set('Erreur lors du changement de rôle.')
    });
  }
}
