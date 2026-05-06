# Admin Postman Tests

## Objectif
Valider les endpoints ADMIN du backend SmartBudgetPro :
- GET /api/admin/stats
- GET /api/admin/users
- GET /api/admin/users/{id}
- PUT /api/admin/users/{id}/role
- DELETE /api/admin/users/{id}
- GET /api/admin/expenses

---

## Pré-requis
1. Backend lancé sur `http://localhost:8081`.
2. PostgreSQL accessible.
3. Un compte ADMIN existe en base (colonne `role = 'ADMIN'`).
4. Postman avec un environnement configuré.

---

## Variables d'environnement Postman
Créer ces variables :
- `baseUrl` = `http://localhost:8081`
- `adminEmail` = `admin@ex.com`
- `adminPassword` = `admin123`
- `adminToken` = *(vide au départ)*
- `targetUserId` = *(id utilisateur à tester)*
- `tempUserEmail` = `temp.user.admin.test@ex.com`
- `tempUserPassword` = `Test1234!`
- `tempUserId` = *(vide au départ)*

---

## 0) Login ADMIN (récupérer token)
### Requête
- Method: `POST`
- URL: `{{baseUrl}}/auth/login`
- Headers:
  - `Content-Type: application/json`
- Body:
```json
{
  "email": "{{adminEmail}}",
  "password": "{{adminPassword}}"
}
```

### Résultat attendu
- Status: `200 OK`
- Body contient au minimum :
```json
{
  "token": "<jwt>"
}
```

### Action Postman
Copier le token dans `adminToken`.

---

## 1) GET /api/admin/stats
### Requête
- Method: `GET`
- URL: `{{baseUrl}}/api/admin/stats`
- Headers:
  - `Authorization: Bearer {{adminToken}}`

### Résultat attendu
- Status: `200 OK`
- Structure du body :
```json
{
    "totalUsers": 2,
    "totalExpensesAmount": 400.00,
    "totalCategories": 1
}
```

---

## 2) GET /api/admin/users
### Requête
- Method: `GET`
- URL: `{{baseUrl}}/api/admin/users`
- Headers:
  - `Authorization: Bearer {{adminToken}}`

### Résultat attendu
- Status: `200 OK`
- Body: tableau d'utilisateurs
```json
[
  {
    "id": 1,
    "email": "user@example.com",
    "role": "USER",
    "totalExpensesAmount": 0
  },
  {
    "id": 2,
    "email": "admin@ex.com",
    "role": "ADMIN",
    "totalExpensesAmount": 0
  }
]
```

### Action Postman
Récupérer un id de user et stocker dans `targetUserId`.

---

## 3) GET /api/admin/users/{id}
### Requête
- Method: `GET`
- URL: `{{baseUrl}}/api/admin/users/{{targetUserId}}`
- Headers:
  - `Authorization: Bearer {{adminToken}}`

### Résultat attendu
- Status: `200 OK`
- Structure du body :
```json
{
    "id": 1,
    "email": "user@example.com",
    "role": "USER",
    "totalExpensesCount": 2,
    "totalBudgets": 1,
    "totalCategories": 1,
    "totalExpensesAmount": 400.00
}
```

### Cas erreur attendu
- Si id inexistant: `404 Not Found`.

---

## 4) PUT /api/admin/users/{id}/role
### Option recommandée: utiliser un user temporaire

#### 4.1 Créer user temporaire
- Method: `POST`
- URL: `{{baseUrl}}/auth/register`
- Headers:
  - `Content-Type: application/json`
- Body:
```json
{
  "email": "{{tempUserEmail}}",
  "password": "{{tempUserPassword}}"
}
```
- Attendu: `201 Created`

#### 4.2 Récupérer son id
- Refaire `GET {{baseUrl}}/api/admin/users`
- Identifier `{{tempUserEmail}}`
- Stocker son id dans `tempUserId`

#### 4.3 Changer son rôle
- Method: `PUT`
- URL: `{{baseUrl}}/api/admin/users/{{tempUserId}}/role`
- Headers:
  - `Authorization: Bearer {{adminToken}}`
  - `Content-Type: application/json`
- Body:
```json
{
  "role": "ADMIN"
}
```

### Résultat attendu
- Status: `204 No Content`
- Vérification: `GET /api/admin/users/{{tempUserId}}` retourne `"role": "ADMIN"`.

### Cas erreur attendu
- Body invalide (sans role): `400 Bad Request`.
- Role invalide: `400 Bad Request`.

---

## 5) GET /api/admin/expenses
### Requête
- Method: `GET`
- URL: `{{baseUrl}}/api/admin/expenses`
- Headers:
  - `Authorization: Bearer {{adminToken}}`

### Résultat attendu
- Status: `200 OK`
- Body: tableau (vide ou rempli selon données)
```json
[
    {
        "id": 1,
        "amount": 100.00,
        "description": "Restaurant",
        "date": "2026-03-15",
        "userId": 1,
        "userEmail": "user@example.com",
        "categoryId": 1,
        "categoryName": "Restaurant",
        "budgetId": 1
    },
    {
        "id": 2,
        "amount": 300.00,
        "description": "Macdo",
        "date": "2026-03-20",
        "userId": 1,
        "userEmail": "user@example.com",
        "categoryId": 1,
        "categoryName": "Restaurant",
        "budgetId": 1
    }
]
```

---

## 6) DELETE /api/admin/users/{id}
⚠️ Test potentiellement destructif. Utiliser `tempUserId`.

### Requête
- Method: `DELETE`
- URL: `{{baseUrl}}/api/admin/users/{{tempUserId}}`
- Headers:
  - `Authorization: Bearer {{adminToken}}`

### Résultat attendu
- Status: `204 No Content`
- Vérification:
  - `GET /api/admin/users` ne contient plus l'utilisateur
  - `GET /api/admin/users/{{tempUserId}}` retourne `404 Not Found`

---

## Tests de sécurité (obligatoires)

### A) Sans token sur endpoint ADMIN
- Exemple: `GET {{baseUrl}}/api/admin/stats`
- Attendu: `401 Unauthorized`

### B) Avec token USER sur endpoint ADMIN
- Login avec un compte USER
- Appeler `GET {{baseUrl}}/api/admin/stats`
- Attendu: `403 Forbidden`

### C) Token invalide/mal formé
- Header Authorization avec faux token
- Attendu: `401 Unauthorized`

---

## Critères de validation finale
Tous les points ci-dessous doivent être vrais :
1. Les 6 endpoints ADMIN répondent avec les statuts attendus avec un token ADMIN.
2. Les endpoints ADMIN sont refusés sans token (`401`).
3. Les endpoints ADMIN sont refusés avec token USER (`403`).
4. Le changement de rôle est effectif après vérification de lecture.
5. La suppression utilisateur est effective et vérifiée.

---

## Note
Si `ECONNREFUSED 127.0.0.1:8081`, le backend n'est pas en écoute. Relancer depuis le dossier backend :
```bash
cd /Users/sariakasmac/smartBudgetPro/backend
./mvnw spring-boot:run
```
