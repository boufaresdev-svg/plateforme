
export enum TypeProjet {
  DEVELOPPEMENT = 'DEVELOPPEMENT',
  INFRASTRUCTURE = 'INFRASTRUCTURE',
  FORMATION = 'FORMATION',
  CONSEIL = 'CONSEIL',
  MAINTENANCE = 'MAINTENANCE',
  RECHERCHE = 'RECHERCHE',
  AUTRE = 'AUTRE'
}

export enum StatutProjet {
  PLANIFIE = 'PLANIFIE',
  EN_COURS = 'EN_COURS',
  EN_PAUSE = 'EN_PAUSE',
  TERMINE = 'TERMINE',
  ANNULE = 'ANNULE',
  ARCHIVE = 'ARCHIVE'
}

export enum PrioriteProjet {
  BASSE = 'BASSE',
  NORMALE = 'NORMALE',
  HAUTE = 'HAUTE',
  CRITIQUE = 'CRITIQUE'
}

 

export interface ProjetStats {
  totalProjets: number;
  projetsEnCours: number;
  projetsPlanifies: number;
  projetsTermines: number;
  budgetTotal: number;
  progressionMoyenne: number;
  projetsEnRetard: number;
  tachesEnCours: number;
}

export enum StatutMission {
  PLANIFIEE = 'PLANIFIEE',
  EN_COURS = 'EN_COURS',
  EN_PAUSE = 'EN_PAUSE',
  TERMINEE = 'TERMINEE',
  ANNULEE = 'ANNULEE'
}

export enum PrioriteMission {
  BASSE = 'BASSE',
  NORMALE = 'NORMALE',
  HAUTE = 'HAUTE',
  CRITIQUE = 'CRITIQUE'
}

export enum StatutTache {
  A_FAIRE = 'A_FAIRE',
  EN_COURS = 'EN_COURS',
  EN_REVISION = 'EN_REVISION',
  TERMINEE = 'TERMINEE',
  BLOQUEE = 'BLOQUEE',
  ANNULEE = 'ANNULEE'
}

export enum PrioriteTache {
  BASSE = 'BASSE',
  NORMALE = 'NORMALE',
  HAUTE = 'HAUTE',
  URGENTE = 'URGENTE'
}

export enum RoleProjet {
  CHEF_PROJET = 'CHEF_PROJET',
  ARCHITECTE = 'ARCHITECTE',
  DEVELOPPEUR = 'DEVELOPPEUR',
  TESTEUR = 'TESTEUR',
  ANALYSTE = 'ANALYSTE',
  DESIGNER = 'DESIGNER',
  CONSULTANT = 'CONSULTANT',
  AUTRE = 'AUTRE'
}

export enum StatutAffectation {
  ACTIF = 'ACTIF',
  INACTIF = 'INACTIF',
  SUSPENDU = 'SUSPENDU',
  TERMINE = 'TERMINE'
}

export enum StatutPhase {
  PLANIFIEE = 'Planifiée',
  EN_COURS = 'En Cours',
  EN_PAUSE = 'En Pause',
  TERMINEE = 'Terminée',
  ANNULEE = 'Annulée'
}

export enum TypeCommentaire {
  GENERAL = 'GENERAL',
  PROBLEME = 'PROBLEME',
  SOLUTION = 'SOLUTION',
  QUESTION = 'QUESTION',
  INFORMATION = 'INFORMATION'
}

export interface ProjetStats {
  totalProjets: number;
  projetsEnCours: number;
  projetsPlanifies: number;
  projetsTermines: number;
  budgetTotal: number;
  progressionMoyenne: number;
  projetsEnRetard: number;
  totalMissions: number;
  totalTaches: number;
  tachesEnCours: number;
  employesAffectes: number;
  heuresAllouees: number;
  heuresRealisees: number;
}