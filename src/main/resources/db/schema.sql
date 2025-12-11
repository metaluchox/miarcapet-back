-- ============================================
-- SCRIPT DE CREACIÓN DE TABLAS - MIARCAPET
-- ============================================
-- Base de datos: PostgreSQL
-- Descripción: Script para crear todas las tablas necesarias para la aplicación Mi Arcapet
-- Autor: Sistema Mi Arcapet
-- Fecha: 2025-12-09
--
-- INSTRUCCIONES DE USO:
-- 1. Conectarse a PostgreSQL: psql -U postgres
-- 2. Crear la base de datos: CREATE DATABASE miarcapet;
-- 3. Conectarse a la base de datos: \c miarcapet
-- 4. Ejecutar este script: \i /ruta/a/schema.sql
--    O desde línea de comandos: psql -U postgres -d miarcapet -f schema.sql
-- ============================================

-- ============================================
-- ELIMINAR TABLAS EXISTENTES (SI EXISTEN)
-- ============================================
-- Descomenta estas líneas si necesitas recrear las tablas desde cero
-- ADVERTENCIA: Esto eliminará todos los datos existentes

-- DROP TABLE IF EXISTS users CASCADE;

-- ============================================
-- TABLA: users
-- ============================================
-- Descripción: Almacena información de usuarios del sistema
-- Incluye campos para autenticación JWT y roles
CREATE TABLE IF NOT EXISTS users (
    -- Clave primaria
    id BIGSERIAL PRIMARY KEY,

    -- Información de autenticación
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    -- Información del usuario
    name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT true,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- ÍNDICES
-- ============================================
-- Índice en email para búsquedas rápidas (login)
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Índice en role para consultas de usuarios por rol
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Índice en enabled para filtrar usuarios activos
CREATE INDEX IF NOT EXISTS idx_users_enabled ON users(enabled);

-- ============================================
-- COMENTARIOS EN COLUMNAS
-- ============================================
COMMENT ON TABLE users IS 'Tabla de usuarios del sistema Mi Arcapet';
COMMENT ON COLUMN users.id IS 'Identificador único del usuario';
COMMENT ON COLUMN users.email IS 'Email del usuario (usado como username para autenticación)';
COMMENT ON COLUMN users.password IS 'Contraseña encriptada con BCrypt';
COMMENT ON COLUMN users.name IS 'Nombre completo del usuario';
COMMENT ON COLUMN users.role IS 'Rol del usuario (USER, ADMIN, etc.)';
COMMENT ON COLUMN users.enabled IS 'Indica si el usuario está activo';
COMMENT ON COLUMN users.created_at IS 'Fecha y hora de creación del usuario';
COMMENT ON COLUMN users.updated_at IS 'Fecha y hora de última actualización';

-- ============================================
-- FUNCIÓN PARA ACTUALIZAR updated_at AUTOMÁTICAMENTE
-- ============================================
-- Esta función actualiza automáticamente el campo updated_at cuando se modifica un registro
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- TRIGGER PARA ACTUALIZAR updated_at
-- ============================================
-- Este trigger ejecuta la función anterior en cada UPDATE
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- DATOS DE PRUEBA (OPCIONAL)
-- ============================================
-- Descomentar si deseas insertar un usuario de prueba
-- Contraseña sin encriptar: "admin123"
-- Contraseña encriptada con BCrypt: $2a$10$ejemplo...

/*
INSERT INTO users (email, name, password, role, enabled)
VALUES (
    'admin@miarcapet.cl',
    'Administrador',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye/IKGJqDSp99wvweT.DkWeLG5LKK6Qmm', -- password: admin123
    'ADMIN',
    true
) ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, name, password, role, enabled)
VALUES (
    'user@miarcapet.cl',
    'Usuario de Prueba',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye/IKGJqDSp99wvweT.DkWeLG5LKK6Qmm', -- password: admin123
    'USER',
    true
) ON CONFLICT (email) DO NOTHING;
*/

-- ============================================
-- VERIFICACIÓN
-- ============================================
-- Consultas para verificar la creación correcta

-- Ver todas las tablas
-- \dt

-- Ver estructura de la tabla users
-- \d users

-- Contar registros en users
-- SELECT COUNT(*) FROM users;

-- ============================================
-- NOTAS IMPORTANTES
-- ============================================
-- 1. La contraseña en la aplicación se encripta con BCrypt antes de guardarse
-- 2. El campo email debe ser único y se usa como username
-- 3. Los roles típicos son: USER, ADMIN, MODERATOR, etc.
-- 4. El campo enabled permite desactivar usuarios sin eliminarlos
-- 5. Los timestamps se manejan automáticamente con triggers
-- 6. Se recomienda hacer backup antes de ejecutar DROP TABLE

-- ============================================
-- PERMISOS (OPCIONAL)
-- ============================================
-- Si necesitas crear un usuario específico para la aplicación:
/*
CREATE USER miarcapet_app WITH PASSWORD 'tu_password_seguro';
GRANT CONNECT ON DATABASE miarcapet TO miarcapet_app;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO miarcapet_app;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO miarcapet_app;
*/
