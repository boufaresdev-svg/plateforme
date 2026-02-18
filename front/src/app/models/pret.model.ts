export interface Pret {
  id: string;
  emprunteur: string;
  emailEmprunteur: string;
  telephoneEmprunteur: string;
  departement: string;
  articles: ArticlePret[];
  datePret: Date;
  dateRetourPrevue: Date;
  dateRetourEffective?: Date;
  statut: StatutPret;
  motif: string;
  observations?: string;
  validePar: string;
  dateValidation: Date;
}

export interface ArticlePret {
  id: string;
  stockId: string;
  nomArticle: string;
  quantitePretee: number;
  quantiteRetournee: number;
  etatDepart: EtatArticle;
  etatRetour?: EtatArticle;
  observations?: string;
}

export enum StatutPret {
  EN_COURS = 'En Cours',
  RETOURNE = 'Retourné',
  RETARD = 'En Retard',
  PARTIELLEMENT_RETOURNE = 'Partiellement Retourné',
  ANNULE = 'Annulé'
}

export enum EtatArticle {
  NEUF = 'Neuf',
  BON_ETAT = 'Bon État',
  USAGE = 'Usagé',
  ENDOMMAGE = 'Endommagé',
  DEFECTUEUX = 'Défectueux'
}

export interface PretStats {
  totalPrets: number;
  pretsEnCours: number;
  pretsEnRetard: number;
  articlesPretes: number;
  tauxRetour: number;
  pretsParMois: number;
}