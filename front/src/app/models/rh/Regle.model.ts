import { Prime } from "./Prime.model";
import { Retenue } from "./Retenue.model";

export interface Regle {
  id: string;
  libelle: string;
  description: string;
  minAnciennete: number; // en années
  maxAnciennete: number; // en années
  montantFixe?: number; // montant fixe en DT (optionnel)
  pourcentageSalaire?: number; // pourcentage du salaire (ex: 0.05 = 5%)
  nombreJours?: number; // nombre de jours (congés, etc.)
  nombrePoints?: number; // nombre de points
  typeRegle: TypeRegle;
  statut: StatutRegle;
  dateCreation: Date;
  dateModification: Date;
  prime?: Prime; // lien vers Prime si applicable
  retenue?: Retenue; // lien vers Retenue si applicable
  conditions: string; // conditions d'application
  automatique: boolean; // application automatique ou manuelle
  createdAt: Date;
  updatedAt: Date;
}

export enum TypeRegle {
  PRIME = 'PRIME',
  RETENUE = 'RETENUE'
}

export enum StatutRegle {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDUE = 'SUSPENDUE'
}

export interface RegleStats {
  totalRegles: number;
  reglesActives: number;
  reglesInactives: number;
  primes: number;
  retenues: number;
  reglesAutomatiques: number;
  employesConcernes: number;
  montantTotalPrimes: number;
  montantTotalRetenues: number;
}