-- ==============================================
-- PORTLAND BREWPUBS DATABASE SCHEMA
-- ==============================================

-- Drop table if exists (for clean restart during development)
DROP TABLE IF EXISTS BREWERIES;

-- Create BREWERIES table
CREATE TABLE BREWERIES (
                           brewery_id INT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(100) NOT NULL,
                           address VARCHAR(200) NOT NULL,
                           signature_beer VARCHAR(100)
);
