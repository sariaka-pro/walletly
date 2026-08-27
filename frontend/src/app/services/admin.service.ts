import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AdminBudget,
  AdminExpense,
  AdminGlobalStats,
  AdminSavingsGoal,
  AdminUserDetails,
  AdminUserSummary,
  CreateAdminUserPayload,
  UpdateAdminUserPayload,
} from '../models/admin.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private apiUrl = 'http://localhost:8081/api/admin';

  constructor(private http: HttpClient) {}

  // Statistiques globales (tous utilisateurs)
  getGlobalStats(): Observable<AdminGlobalStats> {
    return this.http.get<AdminGlobalStats>(`${this.apiUrl}/stats`);
  }

  // Liste de tous les utilisateurs
  getAllUsers(): Observable<AdminUserSummary[]> {
    return this.http.get<AdminUserSummary[]>(`${this.apiUrl}/users`);
  }

  createUser(payload: CreateAdminUserPayload): Observable<AdminUserSummary> {
    return this.http.post<AdminUserSummary>(`${this.apiUrl}/users`, payload);
  }

  updateUser(id: number, payload: UpdateAdminUserPayload): Observable<AdminUserSummary> {
    return this.http.put<AdminUserSummary>(`${this.apiUrl}/users/${id}`, payload);
  }

  // Détail d'un utilisateur spécifique
  getUserDetails(id: number): Observable<AdminUserDetails> {
    return this.http.get<AdminUserDetails>(`${this.apiUrl}/users/${id}`);
  }

  // Changer le rôle d'un utilisateur
  changeUserRole(id: number, role: 'USER' | 'ADMIN'): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/users/${id}/role`, { role });
  }

  // Supprimer un utilisateur
  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${id}`);
  }

  // Toutes les dépenses de tous les utilisateurs
  getAllExpenses(): Observable<AdminExpense[]> {
    return this.http.get<AdminExpense[]>(`${this.apiUrl}/expenses`);
  }

  // Tous les budgets de tous les utilisateurs
  getAllBudgets(): Observable<AdminBudget[]> {
    return this.http.get<AdminBudget[]>(`${this.apiUrl}/budgets`);
  }

  // Tous les savings goals de tous les utilisateurs
  getAllSavingsGoals(): Observable<AdminSavingsGoal[]> {
    return this.http.get<AdminSavingsGoal[]>(`${this.apiUrl}/savings-goals`);
  }
}
