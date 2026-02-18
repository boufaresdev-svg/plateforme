
export interface VehicleStats {
  totalVehicles: number;
  vehiculesEnPanne: number;
  prochainVidangeKm: number;
  visiteTechniqueExpire: number;
  assuranceExpire: number;
  taxeExpire: number;
  totalReparations: number;
  coutTotalReparations: number;
}

export enum TypeCarburant {
  ESSENCE = 'ESSENCE',
  GASOIL = 'GASOIL',
  GASOIL_50 = 'GASOIL_50',
  GPL = 'GPL',

}

export enum TypeReparation {
  VIDANGE = 'VIDANGE',
  PNEU_AVANT_DROIT = 'PNEU_AVANT_DROIT',
  PNEU_AVANT_GAUCHE = 'PNEU_AVANT_GAUCHE',
  PNEU_ARRIERE_DROIT = 'PNEU_ARRIERE_DROIT',
  PNEU_ARRIERE_GAUCHE = 'PNEU_ARRIERE_GAUCHE',
  FREINS = 'FREINS',
  BATTERIE = 'BATTERIE',
  CHAINE_DISTRIBUTION = 'CHAINE_DISTRIBUTION',
  ACCEDEMENT = 'ACCEDEMENT',
  AUTRE = 'AUTRE',
  REPARATION_GENERALE = 'REPARATION_GENERALE'
}


export enum StatutCarte {
  ACTIVE = 'ACTIVE',
  SUSPENDUE = 'SUSPENDUE',
  EXPIREE = 'EXPIREE',
  BLOQUEE = 'BLOQUEE',
  PERDUE = 'PERDUE'
}

export enum FournisseurCarburant {
  AGIL = 'AGIL',
  TOTAL = 'TOTAL',
  SHELL = 'SHELL',
  ESSO = 'ESSO',
  ESPACE = 'ESPACE'
}

export enum FournisseurTelepeage {
  SAT = 'SAT',
  TUNISIA_TOLL = 'TUNISIA_TOLL',
  T_CARD = 'T_CARD',
  ESPACE = 'ESPACE'
}

export interface CarteGazoilStats {
  totalCartes: number;
  cartesActives: number;
  cartesExpirees: number;
  consommationTotale: number;
  coutTotalCarburant: number;
  moyenneConsommation: number;
  plafondTotalMensuel: number;
  transactionsRecentes: number;
}