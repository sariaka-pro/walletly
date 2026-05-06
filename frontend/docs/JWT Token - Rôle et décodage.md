# JWT Token — Rôle et décodage côté frontend

## Ce qu'est un JWT

Un JWT (JSON Web Token) est une **string encodée** envoyée par le backend après un login réussi.
Elle ressemble à ça :

```
eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJlbWFpbCI6ImFkbWluQGV4LmNvbSJ9.abc123
```

Ce n'est pas un objet JavaScript — c'est une string avec **3 blocs séparés par des points `.`** :

```
HEADER . PAYLOAD . SIGNATURE
```

| Partie | Contenu | Utilité |
|---|---|---|
| **Header** | Algorithme de chiffrement (ex: HS256) | Indique comment vérifier la signature |
| **Payload** | Les données : rôle, email, expiration... | Ce que le frontend lit |
| **Signature** | Hash cryptographique | Garantit que le token n'a pas été falsifié |

---

## Ce que le backend envoie

Au moment du login, le backend génère un token et y **inscrit le rôle** de l'utilisateur dans le payload.

Une fois décodé, le payload ressemble à :

```json
{
  "role": "ADMIN",
  "email": "admin@ex.com",
  "exp": 1234567890
}
```

Le frontend reçoit ce token en réponse au `/auth/login` et le stocke dans le `localStorage`.

---

## Pourquoi on doit "découper" le token

Le payload est encodé en **base64**, pas en JSON brut. On ne peut pas le lire directement.

Pour accéder au rôle, il faut :

1. **Découper** la string en 3 parties (`token.split('.')`)
2. **Prendre la 2ème partie** (index 1) qui contient le payload
3. **Corriger le format base64url → base64** standard (remplacer `-` par `+` et `_` par `/`)
4. **Compléter avec des `=`** si la longueur n'est pas un multiple de 4 (obligation du format base64)
5. **Décoder** avec `atob()` → on obtient une string JSON
6. **Parser** avec `JSON.parse()` → on obtient un objet avec `role`, `email`, etc.

```ts
const parts = token.split('.');           // ["header", "payload", "signature"]
const base64 = parts[1]
  .replace(/-/g, '+')
  .replace(/_/g, '/');                    // base64url → base64 standard
const padded = base64.padEnd(
  Math.ceil(base64.length / 4) * 4, '=') // longueur multiple de 4
const payload = JSON.parse(atob(padded)); // { role: "ADMIN", email: "...", exp: ... }
```

---

## Ce que le frontend fait avec le rôle

Une fois le rôle lu, le frontend peut :

- **Afficher ou cacher** des éléments d'UI (menu admin, pages réservées)
- **Protéger des routes** avec un `adminGuard`
- **Conditionner** des appels API selon le rôle

```ts
getRoleFromToken(token: string): User['role'] | null { ... } // lit le rôle dans le token
isAdmin(): boolean { ... }                                   // retourne true si rôle = ADMIN
```

---

## Flux complet

```
1. Utilisateur se connecte → POST /auth/login
2. Backend vérifie les credentials → génère un JWT avec { role: "ADMIN", ... }
3. Frontend reçoit le token → le stocke dans localStorage
4. Frontend décode le payload → lit le rôle
5. Frontend affiche le menu admin si rôle = ADMIN
6. Utilisateur clique sur une route admin → adminGuard vérifie isAdmin()
7. Utilisateur fait une action admin → appel API avec le token en header
8. Backend vérifie la signature du token + @PreAuthorize("hasRole('ADMIN')") → autorise ou rejette
```

---

## Sécurité : frontend vs backend

> Le décodage côté frontend sert **uniquement à l'UI**. La vraie sécurité reste côté backend.

Un utilisateur malveillant pourrait modifier manuellement son token dans le localStorage pour mettre `"role": "ADMIN"`. Mais :

- La **signature** du token ne correspondrait plus
- Le backend rejetterait toutes ses requêtes avec une erreur **401/403**
- `@PreAuthorize("hasRole('ADMIN')")` sur chaque endpoint admin empêche tout accès non autorisé

Le frontend fait confiance au token pour l'UI, le backend ne fait confiance qu'à la signature cryptographique.
