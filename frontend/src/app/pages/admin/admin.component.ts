import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin.service';
import {
  AdminGlobalStats,
  AdminUserSummary,
  CreateAdminUserPayload,
  UpdateAdminUserPayload,
} from '../../models/admin.model';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslatePipe],
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

  // --- Edit user modal ---
  showEditUserModal = signal(false);
  userToEdit = signal<AdminUserSummary | null>(null);
  editUserFirstName = '';
  editUserLastName = '';
  editUserEmail = '';
  editUserPassword = '';
  editUserRole = signal<'USER' | 'ADMIN'>('USER');
  editUserError = signal<string | null>(null);

  // --- Create user ---
  showCreateUserModal = signal(false);
  newUserFirstName = '';
  newUserLastName = '';
  newUserEmail = '';
  newUserPassword = '';
  newUserRole = signal<'USER' | 'ADMIN'>('USER');
  createUserError = signal<string | null>(null);

  constructor(
    private adminService: AdminService,
    private translate: TranslateService
  ) {}

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
        this.error.set(this.translate.instant('admin.errors.loadUsersFailed'));
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
      error: () => this.error.set(this.translate.instant('admin.errors.deleteFailed'))
    });
  }

  // --- Edit user ---
  openEditUserModal(user: AdminUserSummary): void {
    this.userToEdit.set(user);
    this.editUserFirstName = user.firstName ?? '';
    this.editUserLastName = user.lastName ?? '';
    this.editUserEmail = user.email;
    this.editUserPassword = '';
    this.editUserRole.set(user.role);
    this.editUserError.set(null);
    this.showEditUserModal.set(true);
  }

  closeEditUserModal(): void {
    this.showEditUserModal.set(false);
    this.userToEdit.set(null);
    this.editUserError.set(null);
  }

  saveUserEdit(): void {
    const user = this.userToEdit();
    if (!user) return;

    if (!this.editUserEmail.trim()) {
      this.editUserError.set(this.translate.instant('admin.errors.emailRequired'));
      return;
    }

    if (!this.editUserFirstName.trim() || !this.editUserLastName.trim()) {
      this.editUserError.set(this.translate.instant('admin.errors.firstLastRequired'));
      return;
    }

    const payload: UpdateAdminUserPayload = {
      firstName: this.editUserFirstName.trim(),
      lastName: this.editUserLastName.trim(),
      email: this.editUserEmail.trim(),
      role: this.editUserRole(),
    };

    if (this.editUserPassword.trim()) {
      payload.password = this.editUserPassword;
    }

    this.adminService.updateUser(user.id, payload).subscribe({
      next: (updatedUser) => {
        this.users.update((currentUsers) =>
          currentUsers.map((currentUser) => (currentUser.id === updatedUser.id ? updatedUser : currentUser))
        );
        this.closeEditUserModal();
        this.loadData();
      },
      error: () => this.editUserError.set(this.translate.instant('admin.errors.updateFailed'))
    });
  }

  openCreateUserModal(): void {
    this.createUserError.set(null);
    this.newUserFirstName = '';
    this.newUserLastName = '';
    this.newUserEmail = '';
    this.newUserPassword = '';
    this.newUserRole.set('USER');
    this.showCreateUserModal.set(true);
  }

  closeCreateUserModal(): void {
    this.showCreateUserModal.set(false);
  }

  createUser(): void {
    if (!this.newUserFirstName.trim() || !this.newUserLastName.trim() || !this.newUserEmail.trim() || !this.newUserPassword.trim()) {
      this.createUserError.set(this.translate.instant('admin.errors.createRequired'));
      return;
    }

    this.createUserError.set(null);

    const payload: CreateAdminUserPayload = {
      firstName: this.newUserFirstName.trim(),
      lastName: this.newUserLastName.trim(),
      email: this.newUserEmail.trim(),
      password: this.newUserPassword,
      role: this.newUserRole(),
    };

    this.adminService.createUser(payload).subscribe({
      next: () => {
        this.newUserFirstName = '';
        this.newUserLastName = '';
        this.newUserEmail = '';
        this.newUserPassword = '';
        this.newUserRole.set('USER');
        this.showCreateUserModal.set(false);
        this.loadData();
      },
      error: () => {
        this.createUserError.set(this.translate.instant('admin.errors.createFailed'));
      }
    });
  }
}
