-- Drop Database if exists to clear out old tables and their constraints
DROP DATABASE IF EXISTS library_db;

-- Create Database
CREATE DATABASE library_db;
USE library_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    role ENUM('STUDENT', 'LIBRARIAN') NOT NULL
);

-- 2. Students Table
CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    department VARCHAR(100),
    enrollment_year INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. Librarians Table
CREATE TABLE IF NOT EXISTS librarians (
    librarian_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 4. Books Table
CREATE TABLE IF NOT EXISTS books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publisher VARCHAR(255),
    publication_year INT,
    total_copies INT NOT NULL DEFAULT 1,
    available_copies INT NOT NULL DEFAULT 1
);

-- 5. Issues Table (to track Borrowed Books)
CREATE TABLE IF NOT EXISTS issues (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    user_id INT NOT NULL,
    issue_date DATE NOT NULL,
    return_date DATE,
    status ENUM('ISSUED', 'RETURNED') NOT NULL DEFAULT 'ISSUED',
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 6. Fines Table
CREATE TABLE IF NOT EXISTS fines (
    fine_id INT AUTO_INCREMENT PRIMARY KEY,
    issue_id INT NOT NULL,
    user_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status ENUM('UNPAID', 'PAID') NOT NULL DEFAULT 'UNPAID',
    FOREIGN KEY (issue_id) REFERENCES issues(issue_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 7. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    message TEXT NOT NULL,
    type ENUM('GENERAL', 'OVERDUE', 'DUE_REMINDER') NOT NULL DEFAULT 'GENERAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
