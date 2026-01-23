-- ==============================================
-- PORTLAND BREWPUBS DATABASE SCHEMA
-- ==============================================

-- ========== USERS TABLE ==========
-- Stores registered users with hashed passwords
CREATE TABLE IF NOT EXISTS USERS (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    salt VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50)
    );

-- ========== BREWERIES TABLE ==========

-- for clean restart during development,
-- not required for H2 or MySQL, but useful for other DBMS
-- DROP TABLE IF EXISTS BREWERIES;

CREATE TABLE IF NOT EXISTS BREWERIES (
    brewery_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200) NOT NULL,
    signature_beer VARCHAR(100)
);

-- ==============================================
-- PRACTICE FEATURE TABLES
-- (Same structure as SuperDuperDrive for learning)
-- ==============================================

-- ========== NOTES TABLE ==========
-- Practice: User-created text notes
CREATE TABLE IF NOT EXISTS NOTES (
        note_id INT AUTO_INCREMENT PRIMARY KEY,
        title VARCHAR(20) NOT NULL,
        description VARCHAR(1000) NOT NULL,
        user_id INT NOT NULL,
        FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
    );

-- ========== FILES TABLE ==========
-- Practice: File upload/download
CREATE TABLE IF NOT EXISTS FILES (
        file_id INT AUTO_INCREMENT PRIMARY KEY,
        filename VARCHAR(255) NOT NULL,
        content_type VARCHAR(255),
        file_size VARCHAR(255),
        file_data BLOB,
        user_id INT NOT NULL,
        FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
    );

-- ========== CREDENTIALS TABLE ==========
-- Practice: Encrypted credential storage
CREATE TABLE IF NOT EXISTS CREDENTIALS (
        credential_id INT AUTO_INCREMENT PRIMARY KEY,
        url VARCHAR(100) NOT NULL,
        username VARCHAR(30) NOT NULL,
        encryption_key VARCHAR(255),           -- Encryption key
        password VARCHAR(255),       -- Encrypted password
        user_id INT NOT NULL,
        FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
    );
