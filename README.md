# 📚 Library Management System (Java – Console Based)

A robust, menu-driven Library Management System built using Core Java, OOP principles, and File Handling (Serialization).  
This project supports Admin & User roles, book issue/return with due dates, fine calculation, and persistent storage.

---

## 🚀 Features

### 🔐 Authentication & Roles
- Admin login
- User (student) registration & login
- Role-based access control

### 📖 Book Management (Admin)
- Add new books
- View all books
- Search books (by title / author)
- Update book details
- Delete books
- View detailed book & borrower information

### 👤 Borrower Management
- Auto-create borrower using 3-digit ID
- Persistent borrower storage
- Borrower history tracking

### 🔄 Issue & Return System
- 14-day borrowing period
- Prevent issue if copies unavailable
- Automatic fine calculation
- Fine: ₹5 per day after due date

### 📊 Borrow Tracking
- View all books borrowed by a borrower
- View issue & due dates per book

### 💾 Persistent Storage
- Java Serialization (`.dat` files)
- Data remains after program restart

---

## 🧠 Concepts Used

- Object-Oriented Programming (OOP)
  - Encapsulation
  - Abstraction
  - Composition
- Java Collections (ArrayList, HashMap)
- File Handling (ObjectInputStream, ObjectOutputStream)
- Java Date & Time API (LocalDate, ChronoUnit)
- Input validation & error handling
- Role-based menu system

---

## 🏗️ Project Structure

LibraryManagementSystem/
├── LibraryApp.java
├── User.java
├── UserManager.java
├── Book.java
├── BorrowRecord.java
├── Borrower.java
├── BorrowerManager.java
├── library.dat
├── users.dat
├── borrowers.dat
└── README.md

---

## 🧑‍💻 Default Credentials

Admin Login:
Username: admin  
Password: admin123

User:
Register via menu (role defaults to USER)

---

## 📋 Menu Overview

### 👨‍💼 Admin Menu
1. Add Book
2. View All Books
3. Search Books
4. View Book Details
5. Issue Book
6. Return Book
7. View Borrower's Books
8. Update Book
9. Delete Book
0. Logout
99. Exit Program

### 👤 User Menu
1. View All Books
2. Search Books
3. View Book Details
4. Borrow Book
5. Return Book
6. My Borrowed Books
0. Logout
99. Exit Program

---

## 🧾 Borrowing Rules

- Borrower ID must be exactly 3 digits  
  Examples: 101, 007
- Borrow duration: 14 days
- Late fine: ₹5 per day

---

## ▶️ How to Run

Prerequisites:
- Java JDK 8 or higher

Commands:
javac LibraryApp.java  
java LibraryApp

---

## 📌 Sample Output

→ Issued successfully. Due: 2026-03-03  
→ Book returned LATE by 3 day(s). Fine: ₹15.0

---

## 🎯 Why This Project Stands Out

- Real-world library workflow
- Clean OOP architecture
- Persistent data storage
- Role-based access control
- Interview-ready logic & structure

---

## 🔮 Future Enhancements

- Password hashing
- GUI (JavaFX / Swing)
- MySQL database integration
- Spring Boot REST API
- Book categories & ISBN
- Reports & analytics

---

## 🏆 Author

Jeethendra 



Java Full Stack 
