import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Budget, CreateBudgetDto } from '../models/budget.model';

@Injectable({
  providedIn: 'root'
})
export class BudgetService {
  
  private apiUrl = 'http://localhost:8081/api/budgets'; 

  constructor(private http: HttpClient) {}

  /// Méthode CRUD 
  // Récupère tous les budgets 
  getAllBudgets(): Observable<Budget[]> {
    return this.http.get<Budget[]>(this.apiUrl);
  }

  // récipère les budgets par ID 
  getBudgetById(id: number): Observable<Budget> {
    return this.http.get<Budget>(`${this.apiUrl}/${id}`);
  }

  // Créer un nouveau budget 
  createBudget(budget: CreateBudgetDto): Observable<Budget> {
    return this.http.post<Budget>(this.apiUrl, budget); 
  }

  /// modifier un budget
  updateBudget(id: number, budget: Partial<CreateBudgetDto>): Observable<Budget> {
    return this.http.put<Budget>(`${this.apiUrl}/${id}`, budget);
  }

  // supprimer un budget 
  deleteBudget(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
