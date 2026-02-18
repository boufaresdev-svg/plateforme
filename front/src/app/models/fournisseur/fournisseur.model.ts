export interface Fournisseur {
  id: string;
  nom: string;
  infoContact: string;
  adresse: string;
  telephone: string;
  matriculeFiscale: string;
  nombreDettes?: number;
  totalDettes?: {
    source: string;
    parsedValue: number;
  };
  dettes?: DettesFournisseur[];
  createdAt?: Date;
  updatedAt?: Date;
  // New calculated fields from backend
  soldeTotal?: number;      // Sum of all debt amounts (montantTotal) from all DettesFournisseur
  montantAPayer?: number;   // Sum of remaining amounts to pay from all unpaid debts
}

export interface DettesFournisseur {
  id: string;
  numeroFacture?: string;
  titre?: string;
  description?: string;
  montantTotal: number;
  montantDu: number;
  estPaye: boolean;
  type?: string;
  datePaiementPrevue: Date;
  datePaiementReelle?: Date | string | null;
  fournisseurId: string;
  fournisseurNom?: string;
  soldeRestant?: number;
  soldeTotalFournisseur?: number; // Sum of all soldeRestant for this fournisseur
  enRetard?: boolean;
  fournisseur?: Fournisseur;
  tranches?: TranchePaiement[];
}

export interface TranchePaiement {
  id?: string;
  montant: number;
  dateEcheance: Date | string;
  datePaiement?: Date | string | null;
  estPaye?: boolean;
  dettesFournisseurId?: string;
}

// Query interface for searching/filtering tranches
export interface TranchePaiementQuery {
  id?: string;
  dettesFournisseurId?: string;
  fournisseurId?: string;
  estPaye?: boolean;
  enRetard?: boolean;
  dateEcheanceDebut?: Date | string;
  dateEcheanceFin?: Date | string;
}

export interface DetteUpdateRequest {
  numeroFacture?: string;
  titre?: string;
  description?: string;
  montantTotal?: number;
  estPaye?: boolean;
  type?: string;
  datePaiementReelle?: string;
  montantDu?: number;
  datePaiementPrevue?: string;
  fournisseurId?: string;
}

export interface FournisseurStats {
  totalFournisseurs: number;
  fournisseursActifs: number;
  totalDettes: number;
  dettesNonPayees: number;
  dettesEnRetard: number;
  montantDettesNonPayees: number;
}

// Pagination interfaces
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  empty: boolean;
  sortBy?: string;
  sortDirection?: string;
}

export interface PaginationParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}