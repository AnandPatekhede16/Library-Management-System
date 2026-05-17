# 📚 Library Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-21%2B-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-green?style=for-the-badge)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

### 🔴 LIVE DEMO: [https://library-management-system-0v3v.onrender.com](https://library-management-system-0v3v.onrender.com)

**A fully featured, full-stack Library Management System built with Spring Boot 3, Hibernate, Spring Security, Thymeleaf, and MySQL. Supports role-based access control, email notifications, book borrowing, overdue tracking, fine calculation, and a real-time chat system.**

</div>


---

## 📸 Features at a Glance

| Feature | Description |
|---------|-------------|
| 🔐 **Authentication** | Secure login & registration with Spring Security |
| 👥 **Role-Based Access** | Separate ADMIN and USER dashboards |
| 📖 **Book Management** | Add, edit, delete, search books with categories |
| 🔄 **Borrow & Return** | Full borrowing lifecycle with due date tracking |
| 📧 **Email Notifications** | Instant confirmation + 3-day reminder + overdue fine alerts |
| ⏰ **Overdue Tracking** | Daily scheduled checks with ₹20/day fine calculation |
| ⭐ **Reviews & Ratings** | Users can rate and review books |
| 💬 **Real-Time Chat** | Built-in socket-based live chat |
| 🤖 **Chatbot** | AI-powered library assistant |
| 📊 **Admin Dashboard** | Stats for books, users, borrows, overdue records |

---

## 🛠️ Technology Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 21+ |
| **Framework** | Spring Boot 3.4.4 |
| **ORM** | Hibernate 6 / Spring Data JPA |
| **Security** | Spring Security 6 |
| **Template Engine** | Thymeleaf 3 |
| **Database** | MySQL 8.0 |
| **Email** | Spring Boot Mail (JavaMail + Gmail SMTP) |
| **Scheduling** | Spring Scheduler (`@Scheduled`) |
| **Real-time Chat** | Java Raw Sockets |
| **Build Tool** | Maven (with Maven Wrapper) |
| **CSS** | Vanilla CSS (custom design system) |
| **JavaScript** | Vanilla JS |

---

## 📁 Complete Project Structure

```
Library/
├── src/
│   └── main/
│       ├── java/com/library/
│       │   ├── LibraryManagementApplication.java   # Main entry point
│       │   │
│       │   ├── config/                             # Configuration classes
│       │   │   ├── SecurityConfig.java             # Spring Security rules & routes
│       │   │   ├── PasswordEncoderConfig.java      # BCrypt password encoder bean
│       │   │   ├── DataInitializer.java            # Seeds default roles & admin user
│       │   │   ├── BookSeeder.java                 # Seeds sample books on startup
│       │   │   └── DebugRunner.java                # Dev-only user verification runner
│       │   │
│       │   ├── controller/                         # HTTP request handlers (MVC)
│       │   │   ├── AuthController.java             # /register, /login, /logout
│       │   │   ├── BookController.java             # /books – list, detail, add, edit, delete
│       │   │   ├── BorrowController.java           # /borrows – borrow, return, overdue
│       │   │   ├── CategoryController.java         # /admin/categories – CRUD
│       │   │   ├── ChatController.java             # /chat – real-time chat page
│       │   │   ├── ChatbotController.java          # /chatbot – AI chatbot API
│       │   │   ├── DashboardController.java        # /dashboard – admin stats
│       │   │   ├── PublicCategoryController.java   # /categories – public category browse
│       │   │   └── UserManagementController.java   # /admin/users – manage users
│       │   │
│       │   ├── entity/                             # JPA Database entities
│       │   │   ├── Book.java                       # Books table (title, author, ISBN, copies)
│       │   │   ├── BorrowRecord.java               # Borrow history (user, book, dates, status)
│       │   │   ├── Category.java                   # Book categories
│       │   │   ├── Review.java                     # Book reviews & ratings
│       │   │   ├── Role.java                       # User roles (ROLE_ADMIN, ROLE_USER)
│       │   │   └── User.java                       # Registered users (email, username, etc.)
│       │   │
│       │   ├── dto/                                # Data Transfer Objects
│       │   │   ├── BookDto.java                    # Book form input
│       │   │   ├── DashboardStatsDto.java          # Admin dashboard stats
│       │   │   └── UserRegistrationDto.java        # Registration form input
│       │   │
│       │   ├── repository/                         # Spring Data JPA repositories
│       │   │   └── BorrowRecordRepository.java     # Custom queries for borrows & overdue
│       │   │
│       │   ├── service/                            # Business logic layer
│       │   │   ├── BookService.java                # Book CRUD, copy management
│       │   │   ├── BorrowService.java              # Borrow/return logic + email trigger
│       │   │   ├── CategoryService.java            # Category management
│       │   │   ├── ChatbotService.java             # Chatbot response generation
│       │   │   ├── EmailService.java               # HTML email sending (3 types)
│       │   │   └── UserService.java                # User registration, lookup
│       │   │
│       │   ├── scheduler/
│       │   │   └── OverdueReminderScheduler.java   # Daily jobs: pre-due + overdue emails
│       │   │
│       │   ├── security/
│       │   │   └── CustomAuthenticationProvider.java # Custom login validation logic
│       │   │
│       │   ├── exception/                          # Error handling
│       │   │   ├── GlobalExceptionHandler.java     # Catches all unhandled exceptions
│       │   │   ├── LibraryException.java           # Business logic exceptions
│       │   │   └── ResourceNotFoundException.java  # 404-type exceptions
│       │   │
│       │   ├── socket/                             # Real-time chat via raw sockets
│       │   │   ├── ChatServer.java                 # TCP socket server (port 9090)
│       │   │   └── ClientHandler.java              # Handles individual chat clients
│       │   │
│       │   └── jdbc/
│       │       └── BookBatchRepository.java        # Raw JDBC batch insert for books
│       │
│       └── resources/
│           ├── application.properties              # All app configuration
│           ├── static/
│           │   ├── css/
│           │   │   ├── style.css                   # Main stylesheet (dark theme, animations)
│           │   │   └── chatbot.css                 # Chatbot widget styles
│           │   └── js/
│           │       ├── app.js                      # General UI interactions
│           │       └── chatbot.js                  # Chatbot widget logic
│           └── templates/                          # Thymeleaf HTML templates
│               ├── layout/                         # Shared layout fragments
│               ├── auth/                           # login.html, register.html
│               ├── books/                          # list.html, detail.html, form.html
│               ├── borrows/                        # my-borrows.html, all-borrows.html, overdue.html
│               ├── categories/                     # list.html, form.html
│               ├── admin/                          # dashboard.html, users.html
│               ├── user/                           # profile page
│               ├── chat/                           # chat.html
│               └── error/                          # access-denied.html, error pages
│
├── pom.xml                                         # Maven dependencies & build config
└── mvnw.cmd                                        # Maven wrapper (no Maven install needed)
```

---

## 📧 Email Notification System

The system sends **3 types of automated emails**:

| Email Type | When Triggered | Content |
|-----------|---------------|---------|
| 📚 **Borrow Confirmation** | Instantly when user borrows a book | Book title, borrow date, due date |
| ⏰ **Pre-Due Reminder** | Daily at 7:00 AM — 3 days before due | Return warning + ₹20/day fine notice |
| 🚨 **Overdue Notice** | Daily at 8:00 AM — for overdue books | Days late + total fine accrued so far |

---

## 🗃️ Database Schema

```
users ──────────────────────────────────────┐
  id, username, email, password,            │
  full_name, enabled                        │
                                            │
user_roles (junction table)                 │
  user_id → users.id                        │
  role_id → roles.id                        │
                                            │
roles                                       │
  id, name (ROLE_ADMIN / ROLE_USER)         │
                                            │
categories                                  │
  id, name                                  │
                                            │
books                                       │
  id, title, author, isbn,                  │
  total_copies, available_copies,           │
  description, published_year,              │
  category_id → categories.id              │
                                            │
borrow_records                              │
  id, user_id → users.id,                  │
  book_id → books.id,                      │
  borrow_date, due_date, return_date,       │
  status (BORROWED / RETURNED / OVERDUE)    │
                                            │
reviews                                     │
  id, book_id → books.id,                  │
  user_id → users.id,                      │
  rating (1-5), comment, created_at         │
```

---

## 🚀 How to Run This Project Locally

### ✅ Prerequisites

Make sure you have the following installed:

| Tool | Version | Download |
|------|---------|----------|
| **Java JDK** | 21 or higher | [https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/) |
| **MySQL** | 8.0+ | [https://dev.mysql.com/downloads/](https://dev.mysql.com/downloads/) |
| **Git** | Latest | [https://git-scm.com/downloads](https://git-scm.com/downloads) |

> **No Maven installation needed** — this project includes `mvnw.cmd` (Maven Wrapper).

---

### Step 1 — Clone the Repository

```bash
git clone https://github.com/AnandPatekhede16/Library-Management-System.git
cd Library-Management-System
```

---

### Step 2 — Create the MySQL Database

Open MySQL Workbench or MySQL command line and run:

```sql
CREATE DATABASE library_db;
```

> The tables will be created automatically by Hibernate on first startup.

---

### Step 3 — Configure `application.properties`

Open the file:
```
src/main/resources/application.properties
```

Update the following values:

```properties
# ── MySQL ──────────────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD     ← Change this

# ── Gmail SMTP (for email notifications) ───
spring.mail.username=YOUR_GMAIL@gmail.com          ← Change this
spring.mail.password=YOUR_GMAIL_APP_PASSWORD       ← Change this (NOT your regular password)
```

#### 📌 How to get Gmail App Password:
1. Go to [https://myaccount.google.com](https://myaccount.google.com) → Security
2. Enable **2-Step Verification**
3. Go to [https://myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)
4. Create an App Password named `Library System`
5. Copy the 16-character password and paste it in `spring.mail.password`

---

### Step 4 — Run the Application

**On Windows:**
```bash
.\mvnw.cmd spring-boot:run
```

**On Mac/Linux:**
```bash
./mvnw spring-boot:run
```

Wait until you see:
```
Started LibraryManagementApplication in X seconds
```

---

### Step 5 — Open in Browser

```
http://localhost:8085
```

---

### Step 6 — Default Login Credentials

The system automatically creates a default admin user on first startup:

| Role | Username | Password |
|------|----------|----------|
| **Admin** | `admin` | `admin@123` |

> You can register new users via the `/register` page.

---

## 👥 User Roles & Permissions

### 🔴 ADMIN can:
- View admin dashboard with statistics
- Add, edit, delete books
- Manage book categories
- View ALL borrow records
- View & manage overdue records
- Process returns on behalf of users
- Manage all registered users

### 🟢 USER can:
- Browse & search all books
- View book details & reviews
- Borrow available books
- Return borrowed books
- View their own borrow history
- Write reviews & ratings for books
- Use the live chat & chatbot

---

## 🔧 Configuration Reference (`application.properties`)

```properties
# Server
server.port=8085

# MySQL Database
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update       # auto-creates/updates tables
spring.jpa.show-sql=true

# Email (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_GMAIL
spring.mail.password=YOUR_APP_PASSWORD

# Fine per day for overdue books (in Rupees)
library.fine.per-day=20

# Real-time chat socket port
library.socket.port=9090
```

---

## 🌐 Deployment Guide (Docker + Railway + Render)

This project is fully configured for automated containerized deployment using Docker.

### 1. Set up Cloud Database (Railway.app)
1. Go to [https://railway.app](https://railway.app) and create a new project → **Deploy MySQL**.
2. Go to the MySQL service → **Connect** tab → **Enable Public Networking**.
3. Note down your Public Host, Port, User, and Password.

### 2. Deploy Web App (Render.com)
1. Go to [https://render.com](https://render.com) → **New Web Service** → Connect your GitHub repository.
2. Render will automatically detect the `render.yaml` and `Dockerfile` in the repository and set the runtime to **Docker**.
3. Add the following **Environment Variables** in the Render dashboard:
   ```properties
   DB_URL        = jdbc:mysql://<railway_public_host>:<railway_port>/railway?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   DB_USERNAME   = root
   DB_PASSWORD   = <railway_db_password>
   MAIL_USERNAME = <your_gmail@gmail.com>
   MAIL_PASSWORD = <your_16_char_app_password>
   FINE_PER_DAY  = 20
   ```
4. Click **Create Web Service**. Render will build the Docker container and deploy your live app!


---

## 📌 Troubleshooting

| Problem | Solution |
|---------|---------|
| `Access Denied` on MySQL | Check username/password in `application.properties` |
| App starts but emails not sending | Verify Gmail App Password; ensure 2-Step Verification is ON |
| Port 8085 already in use | Change `server.port` in `application.properties` to e.g. `8086` |
| `Table not found` error | Make sure `spring.jpa.hibernate.ddl-auto=update` is set |
| Chat not connecting | Ensure port `9090` is not blocked by firewall |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m "Add your feature"`
4. Push to branch: `git push origin feature/your-feature-name`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** — feel free to use it, modify it, and share it.

---

## 👨‍💻 Author

**Anand Patekhede**
- GitHub: [@AnandPatekhede16](https://github.com/AnandPatekhede16)

---

<div align="center">
  <strong>⭐ If you found this project helpful, please give it a star on GitHub! ⭐</strong>
</div>
