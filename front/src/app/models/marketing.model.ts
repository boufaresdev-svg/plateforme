export interface CampagneMarketing {
  id: string;
  nom: string;
  description: string;
  type: TypeCampagne;
  canal: CanalMarketing;
  statut: StatutCampagne;
  budget: number;
  coutReel?: number;
  dateDebut: Date;
  dateFin: Date;
  cible: string;
  objectifs: string[];
  kpis: KPICampagne[];
  contenu: ContenuMarketing[];
  resultats: ResultatCampagne[];
  responsable: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface KPICampagne {
  id: string;
  nom: string;
  objectif: number;
  actuel: number;
  unite: string;
}

export interface ContenuMarketing {
  id: string;
  campagneId: string;
  titre: string;
  type: TypeContenu;
  url?: string;
  datePublication: Date;
  vues: number;
  interactions: number;
}

export interface ResultatCampagne {
  id: string;
  campagneId: string;
  date: Date;
  impressions: number;
  clics: number;
  conversions: number;
  cout: number;
  revenus: number;
}

export enum TypeCampagne {
  AWARENESS = 'Notoriété',
  LEAD_GENERATION = 'Génération de leads',
  CONVERSION = 'Conversion',
  RETENTION = 'Fidélisation',
  PROMOTION = 'Promotion'
}

export enum CanalMarketing {
  FACEBOOK = 'Facebook',
  INSTAGRAM = 'Instagram',
  LINKEDIN = 'LinkedIn',
  GOOGLE_ADS = 'Google Ads',
  EMAIL = 'Email',
  SEO = 'SEO',
  BLOG = 'Blog',
  AUTRE = 'Autre'
}

export enum StatutCampagne {
  PLANIFIEE = 'Planifiée',
  EN_COURS = 'En Cours',
  TERMINEE = 'Terminée',
  SUSPENDUE = 'Suspendue',
  ANNULEE = 'Annulée'
}

export enum TypeContenu {
  IMAGE = 'Image',
  VIDEO = 'Vidéo',
  ARTICLE = 'Article',
  INFOGRAPHIE = 'Infographie',
  PODCAST = 'Podcast',
  AUTRE = 'Autre'
}

export interface MarketingStats {
  totalCampagnes: number;
  campagnesActives: number;
  budgetTotal: number;
  impressionsTotal: number;
  tauxConversion: number;
  roiMoyen: number;
  leadsGeneres: number;
  coutParLead: number;
}