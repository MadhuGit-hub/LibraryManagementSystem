Library Management System - Complete Setup Guide
1. Required Installations
Java Development Kit (JDK)
Download JDK 8 or later from Oracle's website

Or use OpenJDK from AdoptOpenJDK

MySQL Database
Download MySQL Community Server from MySQL website

Install MySQL with default settings

Note down the root password during installation

MySQL Connector/J
Download MySQL JDBC driver from MySQL website

Select Platform Independent version

Extract the downloaded archive to get the JAR file

IDE (Optional but recommended)
Eclipse: https://www.eclipse.org/downloads/

IntelliJ IDEA: https://www.jetbrains.com/idea/download/

NetBeans: https://netbeans.apache.org/download/index.html

2. Environment Setup
Set JAVA_HOME environment variable:
Windows:

text
Setx JAVA_HOME "C:\Program Files\Java\jdk-version"
Linux/Mac:

bash
export JAVA_HOME=/usr/lib/jvm/java-version
echo 'export JAVA_HOME=/usr/lib/jvm/java-version' >> ~/.bashrc
Add Java to PATH:
Windows: Add %JAVA_HOME%\bin to your PATH

Linux/Mac: Add $JAVA_HOME/bin to your PATH

3. Database Setup
Create MySQL Database:
Start MySQL service

Open MySQL command line or MySQL Workbench

Execute the following SQL commands:

sql
-- Create database
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Create tables
CREATE TABLE books (
    book_id VARCHAR(20) PRIMARY KEY,
    book_name VARCHAR(100) NOT NULL
);

CREATE TABLE users (
    user_id VARCHAR(20) PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL
);

CREATE TABLE issued_books (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id VARCHAR(20) NOT NULL,
    user_id VARCHAR(20) NOT NULL,
    issue_date DATE NOT NULL,
    return_date DATE NULL,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Insert sample data
INSERT INTO books (book_id, book_name) VALUES
('B001', 'Java Programming'),
('B002', 'Database Systems'),
('B003', 'Data Structures'),
('B004', 'Algorithms'),
('B005', 'Computer Networks');

INSERT INTO users (user_id, user_name) VALUES
('U001', 'Alice Smith'),
('U002', 'Bob Johnson'),
('U003', 'Charlie Brown'),
('U004', 'Diana Wilson'),
('U005', 'Eva Davis');
4. Project Setup and Code
Create a new Java project:
Create a new directory for your project

Create lib folder and place the MySQL connector JAR file there

Create src folder for your Java source files

Directory Structure:
text
LibraryManagementSystem/
├── lib/
│   └── mysql-connector-java-8.0.x.jar
├── src/
│   └── LibraryManagementSystem.java
└── ba4eee915fdd441cb62be5dc85492187.jpg (optional background image)
