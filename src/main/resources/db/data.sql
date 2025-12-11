-- ============================================
-- SCRIPT DE DATOS INICIALES - MIARCAPET
-- ============================================
-- Base de datos: PostgreSQL
-- Descripción: Script para insertar datos iniciales y de prueba
-- Autor: Sistema Mi Arcapet
-- Fecha: 2025-12-09
--
-- INSTRUCCIONES DE USO:
-- 1. Asegúrate de haber ejecutado schema.sql primero
-- 2. Ejecutar desde psql: \i /ruta/a/data.sql
--    O desde línea de comandos: psql -U postgres -d miarcapet -f data.sql
-- ============================================

-- ============================================
-- USUARIOS DE PRUEBA
-- ============================================
-- Nota: Estas son contraseñas de EJEMPLO para desarrollo local
-- IMPORTANTE: NO usar en producción

-- Usuario Administrador
-- Email: admin@miarcapet.cl
-- Password: admin123
-- Hash BCrypt de "admin123"
INSERT INTO users (email, name, password, role, enabled, created_at)
VALUES (
    'admin@miarcapet.cl',
    'Administrador Mi Arcapet',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye/IKGJqDSp99wvweT.DkWeLG5LKK6Qmm',
    'ADMIN',
    true,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;

-- Usuario Regular de Prueba
-- Email: usuario@miarcapet.cl
-- Password: user123
-- Hash BCrypt de "user123"
INSERT INTO users (email, name, password, role, enabled, created_at)
VALUES (
    'usuario@miarcapet.cl',
    'Usuario de Prueba',
    '$2a$10$VEjxo0jq2YT5ZEO.NMy5D.g1.KZNqfH9uxJBJpVLNhFhJXAzIYzKW',
    'USER',
    true,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;

-- Usuario de Desarrollo
-- Email: dev@miarcapet.cl
-- Password: dev123
-- Hash BCrypt de "dev123"
INSERT INTO users (email, name, password, role, enabled, created_at)
VALUES (
    'dev@miarcapet.cl',
    'Desarrollador',
    '$2a$10$hLXjk9zN6oBKHxH0KYsN2OgKKNX/9N5qGJtH5nGx5pZb4cHV.H8tW',
    'USER',
    true,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;

-- ============================================
-- VERIFICACIÓN DE DATOS INSERTADOS
-- ============================================
-- Consulta para ver todos los usuarios creados
-- SELECT id, email, name, role, enabled, created_at FROM users;

-- ============================================
-- NOTAS IMPORTANTES
-- ============================================
-- 1. Estos usuarios son SOLO para desarrollo/pruebas
-- 2. Las contraseñas están hasheadas con BCrypt
-- 3. Para generar nuevos hashes BCrypt, puedes usar:
--    - Online: https://bcrypt-generator.com/
--    - Desde la aplicación Spring Boot
--    - Comando Java: BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
--                     String hash = encoder.encode("tu_password");
--
-- 4. SEGURIDAD EN PRODUCCIÓN:
--    - NO incluir este archivo en despliegues de producción
--    - Crear usuarios manualmente o mediante endpoints seguros
--    - Usar contraseñas fuertes y únicas
--    - Cambiar las contraseñas inmediatamente después del primer login
--
-- 5. Para eliminar todos los usuarios de prueba:
--    DELETE FROM users WHERE email IN ('admin@miarcapet.cl', 'usuario@miarcapet.cl', 'dev@miarcapet.cl');
