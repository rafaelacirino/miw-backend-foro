INSERT INTO users (id, first_name, last_name, user_name, email, password, role, registered_date)
SELECT 0, 'Unknown', 'User', 'unknown_user', 'unknown@unknown.com',
       'N/A', 'UNKNOWN', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'unknown_user');
