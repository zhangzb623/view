# Database Initialization Scripts

This directory contains database initialization scripts for the Spring Cloud Learning System.

## Scripts

### init.sql
Main database initialization script that creates:
- **learning_system**: Main application database
  - user_db: User data (t_user_info, t_user_address)
  - product_db: Product data (t_product, t_category)
  - order_db: Order data with 4 sharding tables (t_order_0, t_order_1, t_order_2, t_order_3)
  - payment_db: Payment data (t_payment, t_refund)
  - Test data inserted (users, products, addresses, payments)

### xxl-job.sql
xxl-job task scheduling database:
- xxl_job_info: Task definitions
- xxl_job_log: Task execution logs
- xxl_job_logglue: GLUE script logs
- xxl_job_registry: Executor registration
- xxl_job_group: Executor management
- xxl_job_user: User accounts (admin/123456)

### seata.sql
Seata distributed transaction database:
- global_table: Global transaction records
- branch_table: Branch transaction records
- lock_table: Global lock table

## Usage

### 1. Create Databases

```bash
# Method 1: Using MySQL command line
mysql -h localhost -u root -proot < scripts/mysql/init.sql
mysql -h localhost -u root -proot < scripts/mysql/xxl-job.sql
mysql -h localhost -u root -proot < scripts/mysql/seata.sql

# Method 2: Using Source command
mysql -h localhost -u root -proot -e "source scripts/mysql/init.sql"
```

### 2. Verify Databases

```bash
# List databases
mysql -h localhost -u root -proot -e "SHOW DATABASES;"

# Verify tables
mysql -h localhost -u root -proot -e "USE learning_system; SHOW TABLES;"
mysql -h localhost -u root -proot -e "USE order_db; SHOW TABLES;"
```

### 3. Test Data

Test user accounts:
- admin / password123
- user1 / password123
- user2 / password123
- user3 / password123

Test orders and payments are inserted.

## Database Schema Summary

### learning_system (Main Database)
- user_db: User information and addresses
- product_db: Products and categories
- order_db: Orders with ShardingJDBC (4 shards)
- payment_db: Payments and refunds

### xxl-job
- Centralized task scheduling management
- Execution log tracking
- Admin interface at http://localhost:8088/xxl-job-admin

### seata
- Distributed transaction management
- Global transaction records
- Branch transaction management

## Note

- All databases use UTF8MB4 character set for full emoji support
- All tables are using InnoDB engine for ACID compliance
- Test data is included for development and testing
