-- SQL Script to add GESTION_STOCK module to the permissions table CHECK constraint
-- Run this script on your SQL Server database to allow the new GESTION_STOCK module

-- First, drop the existing CHECK constraint
ALTER TABLE permissions DROP CONSTRAINT CK__permissio__modul__01142BA1;

-- Add the new CHECK constraint that includes GESTION_STOCK
ALTER TABLE permissions ADD CONSTRAINT CK__permissio__modul__01142BA1 
CHECK (module IN (
    'GESTION_UTILISATEURS',
    'GESTION_CLIENTS', 
    'GESTION_FOURNISSEURS',
    'GESTION_PROJETS',
    'GESTION_RH',
    'GESTION_FORMATIONS',
    'GESTION_VEHICULES',
    'GESTION_STOCK',
    'SYSTEME'
));

-- Optionally, update any existing CATEGORIE permissions to use the new module
-- (This is safe to run even if no such permissions exist)
UPDATE permissions 
SET module = 'GESTION_STOCK' 
WHERE ressource LIKE '%CATEGORIE%' 
   OR ressource LIKE '%ARTICLE%' 
   OR ressource LIKE '%STOCK%';

PRINT 'Successfully updated permissions table constraint to include GESTION_STOCK module';