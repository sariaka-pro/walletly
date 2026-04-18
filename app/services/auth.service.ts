import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { tap } from "rxjs";
import { AuthResponse, LoginDto } from "../models/user.model";
import { HttpClient } from "@angular/common/http";

@Injectable({
    providedIn: 'root'
})

export class AuthService {

    // Url connectée au backend
    private apiUrl = 'http://localhost:8081/api/auth'; 

    // Token qui permet stopper l'auth partout dès logout 
    private tokenSubject = new BehaviorSubject<String | null>(this.getToken()); 
    public token$ = this.tokenSubject.asObservable(); 

    // constructor 
    constructor(private http: HttpClient) {}

    login(credentials: LoginDto): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials)
        .pipe(tap(response => {
            localStorage.setItem('token', response.token); 
            localStorage.setItem('userId', response.id.toString());
            localStorage.setItem('email', response.email); 
            this.tokenSubject.next(response.token); 
            })
        ); 
    } 

    register(email: string, password: string): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/register`, {email, password})
        .pipe(tap(response => {
            localStorage.setItem('token', response.token);
            this.tokenSubject.next(response.token);
            })
        ); 
    }

    logout(): void {
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        localStorage.removeItem('email');
        this.tokenSubject.next(null);
    }

    getToken(): string | null {
        return localStorage.getItem('token');
    }

    isAuthenticated(): boolean {
        return !!this.getToken();
    } 

    getCurrentUserId(): number | null {
        const userId = localStorage.getItem('userId');
        return userId ? parseInt(userId) : null; 
    }
}