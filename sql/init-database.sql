-- ================================================================
-- E-COMMERCE DATABASE INITIALIZATION
-- Single database with multiple schemas for microservices
-- ================================================================

-- Create schemas for each microservice
CREATE SCHEMA IF NOT EXISTS users_schema;
CREATE SCHEMA IF NOT EXISTS catalog_schema;
CREATE SCHEMA IF NOT EXISTS payment_schema;
CREATE SCHEMA IF NOT EXISTS notification_schema;

-- ================================================================
-- USERS SCHEMA (Login Service)
-- ================================================================

-- Users table
CREATE TABLE IF NOT EXISTS users_schema.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Insert default users with BCrypt encrypted passwords
-- Password: "password123" for all test users
INSERT INTO users_schema.users (username, password, email) VALUES
('admin', '$2a$10$e4wBgUjHhIK2YGshD.gGKOZsLTdA1FJiGCZHw7b3L8xqz9hX/6.EC', 'admin@example.com'),
('user1', '$2a$10$e4wBgUjHhIK2YGshD.gGKOZsLTdA1FJiGCZHw7b3L8xqz9hX/6.EC', 'user1@example.com'),
('user2', '$2a$10$e4wBgUjHhIK2YGshD.gGKOZsLTdA1FJiGCZHw7b3L8xqz9hX/6.EC', 'user2@example.com'),
('testuser', '$2a$10$e4wBgUjHhIK2YGshD.gGKOZsLTdA1FJiGCZHw7b3L8xqz9hX/6.EC', 'test@example.com'),
('demo', '$2a$10$e4wBgUjHhIK2YGshD.gGKOZsLTdA1FJiGCZHw7b3L8xqz9hX/6.EC', 'demo@example.com')
ON CONFLICT (username) DO NOTHING;

-- Indexes for users schema
CREATE INDEX IF NOT EXISTS idx_users_username ON users_schema.users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users_schema.users(email);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users_schema.users(created_at);

-- ================================================================
-- CATALOG SCHEMA (Catalog Service)
-- ================================================================

-- Products table
CREATE TABLE IF NOT EXISTS catalog_schema.products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50),
    image_url VARCHAR(500),
    stock_quantity INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample products
INSERT INTO catalog_schema.products (name, description, price, category, image_url, stock_quantity) VALUES
('Laptop', 'High-performance laptop for work and gaming', 999.99, 'Electronics', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=300', 50),
('Smartphone', 'Latest smartphone with advanced camera', 699.99, 'Electronics', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=300', 100),
('Headphones', 'Wireless noise-canceling headphones', 199.99, 'Electronics', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300', 75),
('Coffee Maker', 'Automatic coffee maker with timer', 149.99, 'Home & Kitchen', 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=300', 30),
('Running Shoes', 'Comfortable running shoes for all terrains', 89.99, 'Sports & Outdoors', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=300', 120),
('Backpack', 'Durable travel backpack with multiple compartments', 59.99, 'Travel', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=300', 80),
('Desk Chair', 'Ergonomic office chair with lumbar support', 249.99, 'Furniture', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=300', 25),
('Water Bottle', 'Insulated stainless steel water bottle', 24.99, 'Sports & Outdoors', 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=300', 200)
ON CONFLICT DO NOTHING;

-- Indexes for catalog schema
CREATE INDEX IF NOT EXISTS idx_products_category ON catalog_schema.products(category);
CREATE INDEX IF NOT EXISTS idx_products_price ON catalog_schema.products(price);
CREATE INDEX IF NOT EXISTS idx_products_stock ON catalog_schema.products(stock_quantity);

-- ================================================================
-- PAYMENT SCHEMA (Payment Service)
-- ================================================================

-- Transactions table
CREATE TABLE IF NOT EXISTS payment_schema.transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    product_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('SUCCESS', 'FAILED', 'PENDING')),
    transaction_id VARCHAR(100) UNIQUE,
    payment_method VARCHAR(50),
    product_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for payment schema
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON payment_schema.transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_status ON payment_schema.transactions(status);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON payment_schema.transactions(created_at);
CREATE INDEX IF NOT EXISTS idx_transactions_transaction_id ON payment_schema.transactions(transaction_id);

-- ================================================================
-- NOTIFICATION SCHEMA (Notification Service)
-- ================================================================

-- Notifications table
CREATE TABLE IF NOT EXISTS notification_schema.notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'general',
    is_read BOOLEAN DEFAULT FALSE,
    notification_id VARCHAR(100) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for notification schema
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notification_schema.notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notification_schema.notifications(type);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notification_schema.notifications(created_at);
CREATE INDEX IF NOT EXISTS idx_notifications_is_read ON notification_schema.notifications(is_read);

-- ================================================================
-- DATABASE VIEWS FOR ANALYTICS (OPTIONAL)
-- ================================================================

-- Cross-schema view for user analytics
CREATE OR REPLACE VIEW public.user_analytics AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.created_at as registration_date,
    u.last_login,
    COUNT(DISTINCT t.id) as total_purchases,
    COALESCE(SUM(t.amount), 0) as total_spent,
    COUNT(DISTINCT n.id) as total_notifications
FROM users_schema.users u
LEFT JOIN payment_schema.transactions t ON u.username = t.user_id AND t.status = 'SUCCESS'
LEFT JOIN notification_schema.notifications n ON u.username = n.user_id
GROUP BY u.id, u.username, u.email, u.created_at, u.last_login;

-- Product sales analytics view
CREATE OR REPLACE VIEW public.product_analytics AS
SELECT 
    p.id,
    p.name,
    p.category,
    p.price,
    p.stock_quantity,
    COUNT(t.id) as times_purchased,
    COALESCE(SUM(t.amount), 0) as total_revenue
FROM catalog_schema.products p
LEFT JOIN payment_schema.transactions t ON p.id = t.product_id AND t.status = 'SUCCESS'
GROUP BY p.id, p.name, p.category, p.price, p.stock_quantity;

-- ================================================================
-- GRANT PERMISSIONS
-- ================================================================

-- Grant permissions to postgres user (you can create separate users later)
GRANT ALL PRIVILEGES ON SCHEMA users_schema TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA catalog_schema TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA payment_schema TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA notification_schema TO postgres;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA users_schema TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA catalog_schema TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA payment_schema TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA notification_schema TO postgres;

GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA users_schema TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA catalog_schema TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA payment_schema TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA notification_schema TO postgres;

-- ================================================================
-- INITIAL DATA SUMMARY
-- ================================================================

-- Log initialization completion
DO $$
BEGIN
    RAISE NOTICE 'E-commerce database initialized successfully!';
    RAISE NOTICE 'Schemas created: users_schema, catalog_schema, payment_schema, notification_schema';
    RAISE NOTICE 'Sample data inserted for testing';
    RAISE NOTICE 'Default login: admin / password123';
END $$;
