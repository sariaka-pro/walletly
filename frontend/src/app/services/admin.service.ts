import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminExpense, AdminGlobalStats, AdminUserDetails, AdminUserSummary } from '../models/admin.model';

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
}
