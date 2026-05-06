# SmartBudgetPro — Journal des ajouts

> Session du **4 mai 2026**  
> Périmètre : Frontend Angular 17+ · Backend Spring Boot · PostgreSQL

---

## 1. Intercepteur JWT (`auth.interceptor.ts`)

### Fichier ajouté
`frontend/src/app/interceptors/auth.interceptor.ts`

### Ce que ça fait
Attache automatiquement l'en-tête `Authorization: Bearer <token>` à **toutes** les requêtes HTTP sortantes, sauf celles à destination des routes `/auth/` (login, register).

### Pourquoi
Sans intercepteur, chaque appel API renvoyait une erreur **401 Unauthorized** — le token JWT stocké dans le `localStorage` n'était jamais transmis. Aucune page ne pouvait charger de données réelles.

### Fichier modifié en parallèle
`frontend/src/app/app.config.ts` — enregistrement de l'intercepteur via `provideHttpClient(withInterceptors([authInterceptor]))`.

---

## 2. Backend — SavingsGoal (entité complète)

### Fichiers créés
| Fichier | Rôle |
|---|---|
| `backend/.../model/SavingsGoal.java` | Entité JPA `savings_goals`, champs : `name`, `targetAmount`, `currentAmount`, `deadline`, `user` |
| `backend/.../repository/SavingsGoalRepository.java` | `findByUser_Id(Long userId)` |
| `backend/.../service/SavingsGoalService.java` | CRUD + vérification de propriété (un user ne peut modifier que ses propres goals) |
| `backend/.../controller/SavingsGoalController.java` | `POST/GET/GET{id}/PUT{id}/DELETE{id}` sur `/api/savings-goals` |

### Fichier modifié
`backend/.../exception/ErrorMessages.java` — ajout des constantes `SAVINGS_GOAL_NOT_FOUND`, `SAVINGS_GOAL_NAME_REQUIRED`, `SAVINGS_GOAL_TARGET_REQUIRED`, `SAVINGS_GOAL_TARGET_INVALID`.

### Pourquoi
L'entité `SavingsGoal` n'existait **pas du tout** côté backend. Le frontend avait déjà un modèle et un service pour cette feature, mais tous les appels retournaient 404. La création de la stack complète (entité → repo → service → controller) était le prérequis pour que la page savings-goals soit fonctionnelle.

---

## 3. Frontend — Modèle & service SavingsGoal

### Fichiers modifiés
| Fichier | Changement |
|---|---|
| `frontend/.../models/savings-goal.model.ts` | Stub remplacé par : `SavingsGoal { id, name, targetAmount, currentAmount, deadline }` + `CreateSavingsGoalDto` |
| `frontend/.../services/savings-goal.service.ts` | Stub remplacé par les appels CRUD réels vers `http://localhost:8081/api/savings-goals` |

### Pourquoi
Les fichiers existaient mais ne contenaient que des placeholders vides. Sans eux, le composant savings-goals ne pouvait ni typer ses données ni appeler le backend.

---

## 4. Pages — Données réelles (Transactions, Budgets, Savings Goals)

### Fichiers modifiés (`.ts`)
| Composant | Service injecté | Mapping |
|---|---|---|
| `transactions.component.ts` | `ExpenseService` (user) / `AdminService` (admin) | `description → label`, `category.name → category`, `amount → négatif` |
| `budgets.component.ts` | `BudgetService` | `spendingLimit → allocated`, `currentSpent → spent` |
| `savings-goals.component.ts` | `SavingsGoalService` | `currentAmount → savedAmount` |

### Fichiers modifiés (`.html`)
Chaque template a été mis à jour pour utiliser les **signals Angular** (`signal<T[]>`) avec 3 états :
- `@if (loading())` — indicateur de chargement
- `@if (error())` — message d'erreur
- État normal — affichage des données réelles

### Pourquoi
Les trois pages affichaient uniquement des **données fictives codées en dur**. L'objectif était de les brancher sur les vraies APIs du backend tout en offrant un feedback visuel correct à l'utilisateur (chargement, erreur, liste vide).

---

## 5. Dashboard — Données réelles

### Fichiers modifiés
`frontend/.../pages/dashboard/dashboard.component.ts` et `.html`

### Ce que ça fait
Le dashboard détecte automatiquement le rôle de l'utilisateur connecté au chargement :

**Utilisateur (USER)**
- Appel `GET /api/expenses`
- KPIs calculés localement : total de toutes les dépenses, total du mois courant avec comparaison mois précédent, nombre de transactions ce mois
- 5 dernières transactions triées par date

**Administrateur (ADMIN)**
- Appel `GET /api/admin/stats`
- KPIs : nombre total d'utilisateurs, montant total de toutes les dépenses, nombre de catégories

### Pourquoi
Le dashboard affichait des chiffres fictifs codés en dur. Le brancher sur les vraies APIs rend l'application opérationnelle. La distinction USER/ADMIN est nécessaire car un admin supervise l'ensemble de la plateforme et n'a pas les mêmes métriques qu'un utilisateur ordinaire.

---

## 6. Modèle & service Admin

### Fichiers créés
| Fichier | Contenu |
|---|---|
| `frontend/.../models/admin.model.ts` | `AdminUserSummary`, `AdminUserDetails`, `AdminGlobalStats`, `AdminExpense` |
| `frontend/.../services/admin.service.ts` | `getGlobalStats()`, `getAllUsers()`, `getUserDetails(id)`, `changeUserRole(id, role)`, `deleteUser(id)`, `getAllExpenses()` |

### Pourquoi
Sans ces fichiers, les composants admin n'avaient ni types partagés ni service centralisé pour appeler les endpoints `/api/admin/*`. Les créer séparément respecte le principe de **séparation des responsabilités** et facilite la réutilisation (le dashboard et la page transactions les consomment tous les deux).

---

## 7. Page Admin — Implémentation complète

### Fichiers modifiés
`frontend/.../pages/admin/admin.component.ts`, `.html`, `.css`

### Fonctionnalités
| Feature | Détail |
|---|---|
| **Stats globales** | 3 cards en haut : nb users, total dépenses, nb catégories |
| **Liste des utilisateurs** | Tableau : id, email, rôle, total dépenses |
| **Édition de rôle** | Clic sur ✏️ → `<select>` inline (USER / ADMIN) → `PUT /api/admin/users/{id}/role` |
| **Suppression** | Clic sur 🗑️ → modal de confirmation → `DELETE /api/admin/users/{id}` |
| **États** | Loading, erreur, liste vide gérés |

### Pourquoi
La page admin n'était qu'un **scaffold vide** avec du texte placeholder. L'implémentation complète est le cœur du rôle ADMIN : pouvoir voir, modifier et supprimer des utilisateurs directement depuis l'interface.

---

## 8. Vue globale ADMIN — Transactions

### Fichier modifié
`frontend/.../pages/transactions/transactions.component.ts` + `.html`

### Ce que ça fait
- Si `isAdmin()` → appel `GET /api/admin/expenses` (toutes les dépenses de tous les utilisateurs)
- Si `isUser()` → appel `GET /api/expenses` (ses propres dépenses uniquement)
- La colonne **"User"** (email) apparaît dans le tableau uniquement pour l'admin

### Pourquoi
Un admin doit pouvoir consulter l'ensemble des transactions de la plateforme, pas seulement les siennes. La distinction est faite dynamiquement au chargement sans changer de route ni de composant.

---

## Résumé des commits à effectuer

```
feat(back): add savings goal CRUD endpoints
feat(front): add JWT auth interceptor for Bearer token
feat(front): add savings goal model and service
feat(front): connect transactions, budgets, savings-goals to backend API
feat(front): connect dashboard to real API (user KPIs + admin global stats)
feat(front): add admin model and service
feat(front): implement admin page with users list, role edit and delete modal
feat(front): ADMIN global view on transactions and dashboard
```
