-- Archivo: src/main/resources/db/migration/V1__init_auth_and_profiles.sql

-- ============================================================================
-- 1. TABLA MAESTRA DE PERSONAS (Identidad real: Abogados, Staff y Clientes)
-- ============================================================================
CREATE TABLE persons (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         person_type VARCHAR(20) NOT NULL DEFAULT 'FISICA',
                         first_name VARCHAR(100),
                         last_name VARCHAR(100),
                         business_name VARCHAR(200),                  -- Para clientes empresas (ej. "Metalúrgica SRL")
                         tax_id VARCHAR(20) UNIQUE,                   -- DNI / CUIT / CUIL
                         email VARCHAR(150),
                         phone VARCHAR(50),
                         address TEXT,
                         created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                         CONSTRAINT chk_person_type CHECK (person_type IN ('FISICA', 'JURIDICA'))
);

-- ============================================================================
-- 2. TABLA DE USUARIOS (Credenciales de acceso y Seguridad)
-- ============================================================================
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       person_id UUID NOT NULL UNIQUE REFERENCES persons(id) ON DELETE RESTRICT,
                       email VARCHAR(150) NOT NULL UNIQUE,          -- Usado como username en /login
                       password_hash VARCHAR(255) NOT NULL,         -- Hash BCrypt ($2a$10$...)
                       role VARCHAR(30) NOT NULL,                   -- Rol principal en Spring Security

    -- Seguridad de la cuenta (UserDetails de Spring Security)
                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                       is_locked BOOLEAN NOT NULL DEFAULT FALSE,
                       failed_login_attempts INT NOT NULL DEFAULT 0,
                       last_login_at TIMESTAMPTZ,

                       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                       CONSTRAINT chk_user_role CHECK (role IN ('ROLE_ADMIN', 'ROLE_LAWYER', 'ROLE_PARALEGAL', 'ROLE_CLIENT'))
);

CREATE INDEX idx_users_email ON users(email);

-- ============================================================================
-- 3. TABLA DE TOKENS DE REFRESCO (Control de sesiones activas y Logout real)
-- ============================================================================
CREATE TABLE refresh_tokens (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                token_hash VARCHAR(255) NOT NULL UNIQUE,     -- Token opaco o identificador del refresh
                                device_info VARCHAR(255),                    -- User-Agent / IP para auditoría
                                expires_at TIMESTAMPTZ NOT NULL,
                                revoked BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id) WHERE revoked = FALSE;

-- ============================================================================
-- 4. PERFIL PROFESIONAL DEL ABOGADO (Datos del Estudio)
-- ============================================================================
CREATE TABLE lawyer_profiles (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 person_id UUID NOT NULL UNIQUE REFERENCES persons(id) ON DELETE CASCADE,
                                 bar_license_provincial VARCHAR(50),          -- Ej: "M.P. 1-42890" (público, usado en escritos)
                                 bar_license_federal VARCHAR(50),             -- Ej: "T° 510 F° 120"
                                 specialty VARCHAR(100),                      -- Civil, Laboral, Familia, etc.
                                 is_partner BOOLEAN NOT NULL DEFAULT FALSE,   -- Socio del estudio vs Asociado/Colaborador
                                 is_active_staff BOOLEAN NOT NULL DEFAULT TRUE,
                                 created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                 updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 5. PERFIL DE CLIENTE (Permite guardar clientes hoy sin darles usuario aún)
-- ============================================================================
CREATE SEQUENCE client_code_seq START 1;

CREATE TABLE client_profiles (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 person_id UUID NOT NULL UNIQUE REFERENCES persons(id) ON DELETE CASCADE,
                                 client_code VARCHAR(20) NOT NULL UNIQUE DEFAULT ('CLI-' || LPAD(nextval('client_code_seq')::TEXT, 4, '0')),
                                 tax_condition VARCHAR(50),                   -- Consumidor Final, Resp. Inscripto, Monotributo
                                 internal_notes TEXT,                         -- Observaciones privadas de los abogados
                                 created_by_user_id UUID REFERENCES users(id),
                                 created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                 updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);