-- Flyway Database Migration: Initial Schema
-- This file is auto-applied when Flyway is enabled

-- Create audit metadata table for tracking
CREATE TABLE IF NOT EXISTS audit_metadata (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type VARCHAR(100) NOT NULL,
    entity_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL,
    changed_by VARCHAR(255),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    old_value JSONB,
    new_value JSONB
);

-- Create index for audit queries
CREATE INDEX idx_audit_entity ON audit_metadata(entity_type, entity_id);
CREATE INDEX idx_audit_changed_at ON audit_metadata(changed_at);

-- Application version tracking
CREATE TABLE IF NOT EXISTS app_version (
    version VARCHAR(50) PRIMARY KEY,
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(500)
);

INSERT INTO app_version (version, description) VALUES ('1.0.0', 'Initial release');