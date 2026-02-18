/**
 * Application Resources/Entities
 * These represent all the main entities in the system that can have permissions
 */
export const APP_RESOURCES = {
  CLIENT: 'CLIENT',
  FOURNISSEUR: 'FOURNISSEUR',
  PROJET: 'PROJET',
  RH: 'RH',
  VEHICULE: 'VEHICULE',
  FORMATION: 'FORMATION',
  USER: 'USER',
  ROLE: 'ROLE',
  PERMISSION: 'PERMISSION',
  STOCK: 'STOCK',
  DOSSIER: 'DOSSIER',
  PRET: 'PRET',
  SOP: 'SOP',
  SOUS_TRAITANCE: 'SOUS_TRAITANCE'
} as const;

/**
 * Resource display names in French
 */
export const RESOURCE_DISPLAY_NAMES: Record<string, string> = {
  CLIENT: 'Client',
  FOURNISSEUR: 'Fournisseur',
  PROJET: 'Projet',
  RH: 'Ressources Humaines',
  VEHICULE: 'Véhicule',
  FORMATION: 'Formation',
  USER: 'Utilisateur',
  ROLE: 'Rôle',
  PERMISSION: 'Permission',
  STOCK: 'Stock',
  DOSSIER: 'Dossier',
  PRET: 'Prêt',
  SOP: 'SOP',
  SOUS_TRAITANCE: 'Sous-traitance'
};

/**
 * Permission action display names in French
 */
export const ACTION_DISPLAY_NAMES: Record<string, string> = {
  CREATE: 'Créer',
  READ: 'Consulter',
  UPDATE: 'Modifier',
  DELETE: 'Supprimer',
  EXPORT: 'Exporter',
  IMPORT: 'Importer'
};

/**
 * Get the list of all available resources
 */
export function getAllResources(): string[] {
  return Object.values(APP_RESOURCES);
}

/**
 * Get display name for a resource
 */
export function getResourceDisplayName(resource: string): string {
  return RESOURCE_DISPLAY_NAMES[resource] || resource;
}

/**
 * Get display name for an action
 */
export function getActionDisplayName(action: string): string {
  return ACTION_DISPLAY_NAMES[action] || action;
}

/**
 * Generate permission display string
 */
export function getPermissionDisplay(resource: string, action: string): string {
  return `${getActionDisplayName(action)} ${getResourceDisplayName(resource)}`;
}
