# SmartBudgetPro 💰

Application de gestion budgétaire complète avec **Angular** (frontend) et **Spring Boot** (backend).

---

## 📋 **Architecture**

**Monorepo** avec structure :
```
smartBudgetPro/
├── frontend/        # Angular 18+ (port 4200)
├── backend/         # Spring Boot (port 8080)
├── docker-compose.yml
├── Dockerfile       # Frontend
├── backend/Dockerfile
└── README.md
```

---

## 🚀 **Quick Start**

### **Option 1 : Docker Compose (Recommandé)** 🐳

```bash
# Lancer tout (frontend + backend + database)
docker-compose up -d

# Arrêter tout
docker-compose down

# Voir les logs
docker-compose logs -f
```

**Accès :**
- 🌐 Frontend: http://localhost:4200
- 🔌 Backend API: http://localhost:8080/api
- 🗄️ MySQL: localhost:3306

---

### **Option 2 : Local Development** 💻

#### **Frontend**

```bash
cd frontend
npm install
npm start
```

Accès: http://localhost:4200

#### **Backend**

```bash
cd backend
./mvnw spring-boot:run
```

Accès: http://localhost:8080

---

## 🛠️ **Technologies**

### **Frontend**
- ⚛️ **Angular 18+**
- 🎨 **SCSS / CSS3**
- 📦 **TypeScript**
- 🔌 **RxJS**

### **Backend**
- ☕ **Spring Boot 3.x**
- 🗄️ **JPA / Hibernate**
- 🔐 **Spring Security**
- 📦 **Maven**

### **Database**
- 🗄️ **MySQL 8.0**

### **DevOps**
- 🐳 **Docker & Docker Compose**
- 🌐 **Nginx**

---

## 📦 **Installation & Configuration**

### **Prérequis**

- Docker & Docker Compose (recommandé)
- Ou : Node.js 20+, Java 21+, Maven 3.9+

### **Configuration d'environnement**

Créer un fichier `.env` à la racine :

```bash
# Frontend
ANGULAR_API_URL=http://localhost:8080/api

# Backend
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/smartbudget
SPRING_DATASOURCE_USERNAME=smartbudget_user
SPRING_DATASOURCE_PASSWORD=smartbudget_password
```

---

## 📚 **Documentation**

- [Frontend README](./frontend/README.md)
- [Backend README](./backend/README.md)

---

## 🔄 **Git Workflow**

### **Configuration initiale**

```bash
# Initialiser le repo
git init
git add .
git commit -m "Initial commit: SmartBudgetPro monorepo"
```

### **Branches recommandées**

```bash
main          # Production
develop       # Développement
feature/*     # Nouvelles features
fix/*         # Bugfixes
```

### **Commit convention**

```bash
git commit -m "feat(frontend): add expense filter component"
git commit -m "fix(backend): resolve user authentication issue"
git commit -m "docs(readme): update installation guide"
```

---

## 🚢 **Déploiement**

### **Production avec Docker**

```bash
# Build les images
docker-compose build

# Lancer en production
docker-compose -f docker-compose.yml up -d

# Vérifier le statut
docker-compose ps
```

### **Logs & Debugging**

```bash
# Logs du frontend
docker-compose logs -f frontend

# Logs du backend
docker-compose logs -f backend

# Logs de la database
docker-compose logs -f mysql

# Arrêter un service spécifique
docker-compose stop frontend
```

---

## 🧪 **Testing**

### **Frontend**

```bash
cd frontend
npm run test
npm run e2e
```

### **Backend**

```bash
cd backend
./mvnw test
./mvnw verify
```

---

## 📊 **Monitoring & Health Checks**

```bash
# Vérifier la santé des services
curl http://localhost:8080/actuator/health
```

---

## 🤝 **Contributing**

1. Fork le repo
2. Créer une branch (`git checkout -b feature/amazing-feature`)
3. Commit tes changements (`git commit -m 'Add amazing feature'`)
4. Push vers la branch (`git push origin feature/amazing-feature`)
5. Ouvre une Pull Request

---

## 📄 **License**

MIT License - voir [LICENSE](LICENSE)

---

## 📞 **Support**

Pour les questions ou problèmes :
- 📧 Email: support@smartbudgetpro.com
- 🐛 GitHub Issues: [Issues](https://github.com/smartbudgetpro/issues)

---

**Fait avec ❤️ par SmartBudgetPro Team**
