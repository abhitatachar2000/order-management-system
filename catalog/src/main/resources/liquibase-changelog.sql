-- Liquibase formatted SQL

-- changeset abhiTatachar2000:create-sequence
CREATE SEQUENCE catelog_id START WITH 1 INCREMENT BY 1;

-- changeset abhiTatachar2000:create-table
CREATE TABLE catelog_items (
    id INTEGER PRIMARY KEY DEFAULT nextval('catelog_id'),
    name VARCHAR(255) NOT NULL,
    price_per_unit DECIMAL NOT NULL,
    category VARCHAR(255) NOT NULL,
    available_stock INTEGER NOT NULL
);
