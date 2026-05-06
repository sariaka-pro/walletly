import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateSavingsGoalDto, SavingsGoal } from '../models/savings-goal.model';

@Injectable({
  providedIn: 'root'
})
export class SavingsGoalService {

  private apiUrl = 'http://localhost:8081/api/savings-goals';

  constructor(private http: HttpClient) {}

  // Récupérer tous les savings goals de l'utilisateur connecté
  getAllSavingsGoals(): Observable<SavingsGoal[]> {
    return this.http.get<SavingsGoal[]>(this.apiUrl);
  }

  // Récupérer un savings goal par ID
  getSavingsGoalById(id: number): Observable<SavingsGoal> {
    return this.http.get<SavingsGoal>(`${this.apiUrl}/${id}`);
  }

  // Créer un nouveau savings goal
  createSavingsGoal(goal: CreateSavingsGoalDto): Observable<SavingsGoal> {
    return this.http.post<SavingsGoal>(this.apiUrl, goal);
  }

  // Modifier un savings goal
  updateSavingsGoal(id: number, goal: Partial<CreateSavingsGoalDto>): Observable<SavingsGoal> {
    return this.http.put<SavingsGoal>(`${this.apiUrl}/${id}`, goal);
  }

  // Supprimer un savings goal
  deleteSavingsGoal(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
