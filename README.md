# 📚 Library Management System

A full-stack **Java Spring Boot 3.x** web application for managing a library — built with **Thymeleaf**, **Spring Data JPA (Hibernate 6)**, **Spring Security**, **raw JDBC batch operations**, **Java sockets chat**, and a **scheduled overdue reminder**.

---

## 🛠 Tech Stack

| Layer        | Technology                              |
|--------------|-----------------------------------------|
| Backend      | Spring Boot 3.2.5, Java 17+            |
| ORM          | Hibernate 6 (via Spring Data JPA)       |
| Database     | MySQL 8.x                              |
| Security     | Spring Security 6 (BCrypt, role-based)  |
| Frontend     | Thymeleaf + Bootstrap 5                 |
| Chat         | Multi-threaded Java TCP Socket Server   |
| Scheduler    | Spring @Scheduled + @Async              |
| Batch JDBC   | JdbcTemplate with PreparedStatement     |

---

## 📁 Project Structure

```
Library/
├── pom.xml
├── README.md
├── src/main/java/com/library/
│   ├── LibraryManagementApplication.java   # Entry point
│   ├── config/
│   │   ├── SecurityConfig.java             # Spring Security config
│   │   └── DataInitializer.java            # Seeds roles, admin, categories
│   ├── controller/
│   │   ├── AuthController.java             # Login / Register
│   │   ├── DashboardController.java        # Admin & User dashboards
│   │   ├── BookController.java             # Book CRUD + search + borrow
│   │   ├── BorrowController.java           # Borrow / return flow
│   │   ├── UserManagementController.java   # Admin: manage users
│   │   ├── CategoryController.java         # Admin: manage categories
│   │   └── ChatController.java             # Chat page
│   ├── dto/
│   │   ├── UserRegistrationDto.java
│   │   ├── BookDto.java
│   │   └── DashboardStatsDto.java
│   ├── entity/
│   │   ├── User.java                       # @ManyToMany → Role
│   │   ├── Role.java
│   │   ├── Category.java                   # @OneToMany → Book
│   │   ├── Book.java                       # @ManyToOne → Category
│   │   └── BorrowRecord.java               # @ManyToOne → User, Book
│   ├── exception/
│   │   ├── ResourceNotFoundException.java
│   │   ├── LibraryException.java
│   │   └── GlobalExceptionHandler.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── RoleRepository.java
│   │   ├── BookRepository.java
│   │   ├── CategoryRepository.java
│   │   └── BorrowRecordRepository.java
│   ├── service/
│   │   ├── UserService.java                # UserDetailsService impl
│   │   ├── BookService.java
│   │   ├── BorrowService.java
│   │   └── CategoryService.java
│   ├── jdbc/
│   │   └── BookBatchRepository.java        # Raw JDBC batch operations
│   ├── scheduler/
│   │   └── OverdueReminderScheduler.java   # @Scheduled daily + @Async
│   └── socket/
│       ├── ChatServer.java                 # Multi-threaded TCP server
│       └── ClientHandler.java              # Per-client thread handler
├── src/main/resources/
│   ├── application.properties
│   ├── schema.sql                          # MySQL DB creation script
│   ├── static/
│   │   ├── css/style.css                   # Custom styles
│   │   └── js/app.js                       # Client-side JS
│   └── templates/
│       ├── layout/base.html                # Shared navbar layout
│       ├── auth/login.html
│       ├── auth/register.html
│       ├── admin/dashboard.html
│       ├── admin/users.html
│       ├── admin/categories.html
│       ├── user/dashboard.html
│       ├── books/list.html
│       ├── books/detail.html
│       ├── books/form.html
│       ├── borrows/my-borrows.html
│       ├── borrows/all-borrows.html
│       ├── borrows/overdue.html
│       ├── chat/chat.html
│       └── error/{404,error,access-denied}.html
```

---

## 🚀 How to Run in IntelliJ IDEA

### Prerequisites

1. **Java 17+** installed (check: `java -version`)
2. **MySQL 8.x** installed and running
3. **IntelliJ IDEA** (Community or Ultimate)
4. **Maven** (bundled with IntelliJ)

### Step 1: Create the MySQL Database

Open MySQL terminal or Workbench and run:

```sql
CREATE DATABASE IF NOT EXISTS library_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

Or run the provided `src/main/resources/schema.sql` file.

### Step 2: Configure Database Credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root     # ← change to YOUR MySQL password
```

### Step 3: Open in IntelliJ IDEA

1. **File → Open** → Select the `Library` project folder
2. IntelliJ will auto-detect the Maven project
3. Wait for Maven to download all dependencies (status bar at bottom)
4. If prompted, enable **annotation processing** for Lombok:
   - **Settings → Build → Compiler → Annotation Processors → Enable**

### Step 4: Run the Application

- Navigate to `LibraryManagementApplication.java`
- Click the green ▶ **Run** button next to `main()`
- Or right-click the file → **Run 'LibraryManagementApplication'**

Alternatively, from terminal:
```bash
mvn spring-boot:run
```

### Step 5: Access the Application

| URL                          | Description              |
|------------------------------|--------------------------|
| http://localhost:8080         | Redirects to login       |
| http://localhost:8080/login   | Login page               |
| http://localhost:8080/register| Registration page        |

### Default Admin Credentials

| Username | Password   |
|----------|-----------|
| `admin`  | `admin123` |

---

## 👤 User Roles

| Role       | Capabilities                                            |
|------------|--------------------------------------------------------|
| **ADMIN**  | Add/edit/delete books, manage users, view all borrows, manage categories, dashboard stats |
| **USER**   | Register, login, search books, borrow/return books, view own borrow history |

---

## 💬 Chat System

A **multi-threaded TCP socket server** starts automatically on port `9090`.

### Connect via Terminal:

**Windows** (enable Telnet first in Windows Features):
```
telnet localhost 9090
```

**Mac/Linux**:
```
nc localhost 9090
```

1. First line you type = your **username**
2. Subsequent lines = **messages** broadcast to all connected users

---

## ⏰ Scheduled Overdue Reminder

Runs **every day at 8:00 AM** server time:
- Scans all `BORROWED` records where `due_date < today`
- Marks them as `OVERDUE` in the database
- Logs a reminder (replace with email sending in production)

---

## 📊 Database Schema (5+ Normalized Tables)

1. **users** — id, username, password, email, full_name, enabled
2. **roles** — id, name (ROLE_ADMIN, ROLE_USER)
3. **user_roles** — user_id, role_id (join table for @ManyToMany)
4. **categories** — id, name, description
5. **books** — id, title, author, isbn, total_copies, available_copies, description, published_year, category_id (FK)
6. **borrow_records** — id, user_id (FK), book_id (FK), borrow_date, due_date, return_date, status

### Entity Relationships

- **@ManyToMany**: User ↔ Role
- **@OneToMany / @ManyToOne**: Category → Book
- **@OneToMany / @ManyToOne**: User → BorrowRecord
- **@OneToMany / @ManyToOne**: Book → BorrowRecord

---

## 🔧 Troubleshooting

| Issue                        | Solution                                         |
|------------------------------|--------------------------------------------------|
| MySQL connection refused     | Ensure MySQL is running on port 3306             |
| Access denied for DB         | Check username/password in application.properties|
| Lombok errors in IDE         | Install Lombok plugin + enable annotation processing |
| Port 8080 already in use     | Change `server.port` in application.properties   |
| Port 9090 (chat) in use      | Change `library.socket.port` in application.properties |
