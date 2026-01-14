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
