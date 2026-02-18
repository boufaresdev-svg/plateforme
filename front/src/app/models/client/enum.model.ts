
export enum TypeClient {
  PARTICULIER = 'Particulier',
  ENTREPRISE = 'Entreprise',
  ADMINISTRATION = 'Administration',
  ASSOCIATION = 'Association'
}

export enum SecteurClient {
  INFORMATIQUE = 'Informatique',
  SANTE = 'Santé',
  EDUCATION = 'Éducation',
  FINANCE = 'Finance',
  INDUSTRIE = 'Industrie',
  COMMERCE = 'Commerce',
  SERVICES = 'Services',
  AUTRE = 'Autre'
}

export enum StatutClient {
  PROSPECT = 'Prospect',
  ACTIF = 'Actif',
  INACTIF = 'Inactif',
  PERDU = 'Perdu'
}

export enum PrioriteClient {
  BASSE = 'Basse',
  NORMALE = 'Normale',
  HAUTE = 'Haute',
  VIP = 'VIP'
}

export enum StatutProjetClient {
  DEVIS = 'Devis',
  ACCEPTE = 'Accepté',
  EN_COURS = 'En Cours',
  TERMINE = 'Terminé',
  ANNULE = 'Annulé'
}

export enum StatutFacture {
  BROUILLON = 'Brouillon',
  ENVOYEE = 'Envoyée',
  PAYEE = 'Payée',
  EN_RETARD = 'En Retard',
  ANNULEE = 'Annulée'
}

export enum MethodePaiement {
  VIREMENT = 'Virement',
  CHEQUE = 'Chèque',
  CARTE = 'Carte',
  ESPECES = 'Espèces',
  AUTRE = 'Autre'
}

export enum TypeInteraction {
  APPEL = 'Appel',
  EMAIL = 'Email',
  REUNION = 'Réunion',
  VISITE = 'Visite',
  SUPPORT = 'Support',
  AUTRE = 'Autre'
}

export enum CanalCommunication {
  EMAIL = 'Email',
  TELEPHONE = 'Téléphone',
  COURRIER = 'Courrier',
  SMS = 'SMS'
}

export enum FrequenceContact {
  HEBDOMADAIRE = 'Hebdomadaire',
  MENSUELLE = 'Mensuelle',
  TRIMESTRIELLE = 'Trimestrielle',
  ANNUELLE = 'Annuelle'
}