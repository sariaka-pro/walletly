import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateExpenseDto, Expense } from '../models/expense.model';
import { API_ENDPOINTS } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class ExpenseService {

  private readonly apiUrl = API_ENDPOINTS.expenses;

  constructor(private http: HttpClient) {}

  // méthode CRUD pour les dépenses 

  // Récupérer toutes les dépenses
  getAllExpenses(): Observable<Expense[]> {
    return this.http.get<Expense[]>(this.apiUrl);
  }

  // Récupérer une dépense par ID
  getExpenseById(id: number): Observable<Expense> {
    return this.http.get<Expense>(`${this.apiUrl}/${id}`);
  }

  // Créer une nouvelle dépense
  createExpense(expense: CreateExpenseDto): Observable<Expense> {
    return this.http.post<Expense>(this.apiUrl, expense);
  }

  // Modifier une dépense
  updateExpense(id: number, expense: Partial<CreateExpenseDto>): Observable<Expense> {
    return this.http.put<Expense>(`${this.apiUrl}/${id}`, expense);
  }

  // Supprimer une dépense
  deleteExpense(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Récupérer les dépenses d'un budget
  getExpensesByBudget(budgetId: number): Observable<Expense[]> {
    return this.http.get<Expense[]>(`${this.apiUrl}?budgetId=${budgetId}`); 
  }


}
