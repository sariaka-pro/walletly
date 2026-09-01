import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { tap } from "rxjs";
import { AuthResponse, LoginDto, RegisterDto, User } from "../models/user.model";
import { HttpClient } from "@angular/common/http";
import { API_ENDPOINTS } from "../config/api.config";

@Injectable({
    providedIn: 'root'
})

export class AuthService {

    private readonly apiUrl = API_ENDPOINTS.auth;

    // Token qui permet stopper l'auth partout dès logout 
    private tokenSubject = new BehaviorSubject<string | null>(null);
    public token$ = this.tokenSubject.asObservable(); 

    // constructor 
    constructor(private http: HttpClient) {
        const token = localStorage.getItem('token');
        if (token && !this.isTokenExpired(token)) {
            this.tokenSubject.next(token);
        } else if (token) {
            this.clearSession();
        }
    }

    login(credentials: LoginDto): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials)
        .pipe(tap(response => {
            localStorage.setItem('token', response.token); 
            if (response.id !== undefined) {
                localStorage.setItem('userId', response.id.toString());
            }
            if (response.email) {
                localStorage.setItem('email', response.email);
            }
            if (response.firstName && response.firstName.trim()) {
                localStorage.setItem('firstName', response.firstName.trim());
            } else {
                localStorage.removeItem('firstName');
            }
            if (response.lastName && response.lastName.trim()) {
                localStorage.setItem('lastName', response.lastName.trim());
            } else {
                localStorage.removeItem('lastName');
            }
            this.tokenSubject.next(response.token); 
            })
        ); 
    } 

    register(payload: RegisterDto): Observable<string> {
        return this.http.post(`${this.apiUrl}/register`, payload, { responseType: 'text' });
    }

    logout(): void {
        this.clearSession();
    }

    private clearSession(): void {
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        localStorage.removeItem('email');
        localStorage.removeItem('firstName');
        localStorage.removeItem('lastName');
        this.tokenSubject.next(null);
    }

    getToken(): string | null {
        return localStorage.getItem('token');
    }

    isAuthenticated(): boolean {
        const token = this.getToken();
        if (!token) return false;

        if (this.isTokenExpired(token)) {
            this.clearSession();
            return false;
        }

        return true;
    } 

    isTokenExpired(token: string): boolean {
        const payload = this.getTokenPayload(token);
        const expiration = payload?.['exp'];

        // Un JWT mal formé ou sans date d'expiration n'est jamais accepté.
        if (typeof expiration !== 'number') return true;

        return expiration * 1000 <= Date.now();
    }

    getCurrentUserId(): number | null {
        if (!this.isAuthenticated()) return null;
        const userId = localStorage.getItem('userId');
        return userId ? parseInt(userId) : null; 
    }

    getCurrentEmail(): string | null {
        if (!this.isAuthenticated()) return null;
        return localStorage.getItem('email');
    }

    getCurrentFirstName(): string | null {
        if (!this.isAuthenticated()) return null;
        const fromStorage = localStorage.getItem('firstName');
        if (fromStorage && fromStorage.trim()) {
            return fromStorage.trim();
        }

        const token = this.getToken();
        if (!token) return null;

        const payload = this.getTokenPayload(token);
        const rawFirstName = payload?.['firstName'];
        const firstName = typeof rawFirstName === 'string' ? rawFirstName.trim() : '';
        return firstName.length > 0 ? firstName : null;
    }

    private getTokenPayload(token: string): Record<string, any> | null {
        try {
            const parts = token.split('.');
            if (parts.length !== 3) return null;

            const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
            const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');
            return JSON.parse(atob(padded));
        } catch {
            return null;
        }
    }

    getRoleFromToken(token: string): User['role'] | null {
        try {
            const payload = this.getTokenPayload(token);
            if (!payload) return null;

            // On lit le rôle : Spring peut l'envoyer sous "role" (string) ou "roles" (tableau)
            const raw: string = payload?.['role'] ?? payload?.['roles']?.[0] ?? '';

            // Spring préfixe les rôles avec "ROLE_" (ex: "ROLE_ADMIN") → on enlève ce préfixe
            const normalized = raw.replace('ROLE_', '');

            // On vérifie que le rôle est bien l'un des deux valeurs attendues avant de le retourner
            if (normalized === 'ADMIN' || normalized === 'USER') {
                return normalized as User['role'];
            }

            // Rôle inconnu → on retourne null
            return null;
        } catch {
            // Si le token est malformé ou le JSON invalide, on retourne null sans planter
            return null;
        }
    }

    isAdmin(): boolean {
        // On récupère le token stocké dans localStorage
        const token = this.getToken();

        // Pas de token = pas connecté = pas admin
        if (!token || !this.isAuthenticated()) return false;

        // On parse le token et on compare le rôle à 'ADMIN'
        return this.getRoleFromToken(token) === 'ADMIN';
    }
}
