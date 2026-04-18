# ── BUILD STAGE ──
FROM node:20-alpine AS builder

WORKDIR /app

# Copier les fichiers de dépendances
COPY frontend/package*.json ./

# Installer les dépendances
RUN npm ci

# Copier le code source
COPY frontend/ ./

# Builder l'app Angular
RUN npm run build

# ── PRODUCTION STAGE ──
FROM nginx:alpine

# Copier la config nginx
COPY frontend/nginx.conf /etc/nginx/nginx.conf

# Copier les fichiers buildés
COPY --from=builder /app/dist/frontend /usr/share/nginx/html

# Exposer le port
EXPOSE 4200

# Commande de démarrage
CMD ["nginx", "-g", "daemon off;"]
