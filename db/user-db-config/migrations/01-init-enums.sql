-- Создание типа role
CREATE TYPE user_role AS ENUM ('hr', 'admin', 'user');

-- Создание типа status
CREATE TYPE user_status AS ENUM ('active', 'blocked');
