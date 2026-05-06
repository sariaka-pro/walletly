# Auth Configuration

## Le concept : protéger les ressources privées

Une application web sans auth laisse n'importe qui accéder à n'importe quelle page. L'authentification répond à la question : **"Qui es-tu ?"** avant d'autoriser l'accès.

Dans cette app, les pages comme `/dashboard`, `/transactions`, `/budgets` contiennent des données **personnelles et sensibles** — elles ne doivent être accessibles qu'à un utilisateur connecté.

---

## Le token JWT — la "clé d'accès"

Quand le backend valide tes identifiants, il renvoie un **token JWT** (JSON Web Token). C'est une chaîne encodée qui contient :
- ton identité (userId, email)
- une date d'expiration
- une signature cryptographique (seul le backend peut la vérifier)

On le stocke dans le `localStorage` du navigateur. Tant qu'il est là, tu es "connecté".

```
POST /api/auth/login → { token: "eyJhbGci...", id: 1, email: "user@mail.com" }
                              ↓
                    localStorage.setItem('token', ...)
```

---

## Les 4 cas du flux

### 1. Arrivée sur `/` → redirect `/login`

```ts
{ path: '', redirectTo: '/login', pathMatch: 'full' }
```
L'app n'a pas de "page d'accueil publique". La racine redirige immédiatement vers le login. C'est la porte d'entrée.

---

### 2. Login réussi → redirect `/dashboard`

```ts
// login.component.ts
this.authService.login({ email, password }).subscribe({
  next: () => this.router.navigate(['/dashboard'])
});
```
Après que le backend confirme les identifiants et retourne le token, on le sauvegarde et on envoie l'utilisateur vers l'app. C'est le seul chemin légitime pour entrer.

---

### 3. Accès direct à `/dashboard` sans token → redirect `/login`

C'est le rôle du **`AuthGuard`** :

```ts
// auth.guard.ts
export const authGuard: CanActivateFn = () => {
  if (authService.isAuthenticated()) return true;    // token présent → OK
  return router.createUrlTree(['/login']);            // pas de token → bloqué
};
```

Angular exécute ce guard **avant** d'afficher la page. Sans lui, n'importe qui pourrait taper `localhost:4200/dashboard` dans la barre d'adresse et y accéder directement. Le guard est posé sur `AppShellComponent` (le layout parent), donc il protège **toutes les pages enfants** en une seule règle.

---

### 4. Logout → `authService.logout()` + redirect `/login`

```ts
// auth.service.ts
logout(): void {
  localStorage.removeItem('token');   // supprime la clé d'accès
  localStorage.removeItem('userId');
  localStorage.removeItem('email');
  this.tokenSubject.next(null);       // notifie tous les abonnés
}
```

Supprimer le token = déconnecter l'utilisateur. Si il essaie ensuite d'aller sur `/dashboard`, le guard ne trouvera plus de token et le renverra au login.

---

## Pourquoi `BehaviorSubject` dans `AuthService` ?

```ts
private tokenSubject = new BehaviorSubject<String | null>(this.getToken());
public token$ = this.tokenSubject.asObservable();
```

C'est un **observable réactif** : si demain tu veux afficher "Connecté en tant que user@mail.com" dans le sidenav, ou griser un bouton selon l'état de connexion, tu t'abonnes à `token$` et ton composant se met à jour automatiquement au login/logout, sans polling ni vérification manuelle.

---

## Résumé visuel

```
Navigateur                 Angular                    Backend
    |                         |                           |
    |-- / ----------------→   | redirectTo /login         |
    |-- /login -----------→   | LoginComponent            |
    |   [form submit] -----→  | authService.login() ----→ POST /api/auth/login
    |                         |                    ←----- { token, id, email }
    |                         | localStorage.setItem(token)
    |                         | router.navigate(/dashboard)
    |                         |
    |-- /dashboard -------→   | authGuard.canActivate()
    |                         |   isAuthenticated() → true ✅
    |                         | DashboardComponent affiché
    |                         |
    |-- [Sign out] -------→   | authService.logout()
    |                         | localStorage.removeItem(token)
    |                         | router.navigate(/login)
```

---

## Fichiers concernés

| Fichier | Rôle |
|---|---|
| `src/app/guards/auth.guard.ts` | Guard qui vérifie le token avant chaque route protégée |
| `src/app/services/auth.service.ts` | Login, logout, stockage du token, état de connexion |
| `src/app/components/login/login.component.ts` | Formulaire de login, appel à `authService.login()` |
| `src/app/app.routes.ts` | Définition des routes + application du guard sur `AppShellComponent` |
| `src/app/components/sidenav/sidenav.component.ts` | Bouton "Sign out" câblé sur `authService.logout()` |

---

## Ce qui manque encore (pour une auth production)

| Manquant | Pourquoi important |
|---|---|
| Token expiration check | Un token JWT expire — il faut gérer le cas "token présent mais expiré" |
| HTTP interceptor | Envoyer automatiquement `Authorization: Bearer <token>` à chaque requête API |
| Refresh token | Renouveler le token sans reforcer le login |
| Guard pour `/login` si déjà connecté | Éviter qu'un utilisateur connecté voie le formulaire de login |
