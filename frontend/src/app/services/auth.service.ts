import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { tap } from "rxjs";
import { AuthResponse, LoginDto, User } from "../models/user.model";
import { HttpClient } from "@angular/common/http";

@Injectable({
    providedIn: 'root'
})

export class AuthService {

    // Url connectée au backend
    private apiUrl = 'http://localhost:8081/auth'; 

    // Token qui permet stopper l'auth partout dès logout 
    private tokenSubject = new BehaviorSubject<String | null>(this.getToken()); 
    public token$ = this.tokenSubject.asObservable(); 

    // constructor 
    constructor(private http: HttpClient) {}

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

    getRoleFromToken(token: string): User['role'] | null {
        try {
            // Un JWT est composé de 3 parties séparées par des points : header.payload.signature
            const parts = token.split('.');

            // Si le token n'a pas exactement 3 parties, il est invalide
            if (parts.length !== 3) return null;

            // La 2ème partie (index 1) contient le payload encodé en base64url
            // base64url utilise '-' et '_' à la place de '+' et '/' → on les remet au format base64 standard
            const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');

            // base64 doit avoir une longueur multiple de 4, on ajoute des '=' si nécessaire
            const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');

            // On décode le base64 en string JSON puis on le parse en objet JavaScript
            const payload = JSON.parse(atob(padded));

            // On lit le rôle : Spring peut l'envoyer sous "role" (string) ou "roles" (tableau)
            const raw: string = payload?.role ?? payload?.roles?.[0] ?? '';

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
        if (!token) return false;

        // On parse le token et on compare le rôle à 'ADMIN'
        return this.getRoleFromToken(token) === 'ADMIN';
    }
}