import { Categorie } from "./Categorie.model";
import { Domaine } from "./Domaine.model";
import { Formateur } from "./Formateur.model";
import { PlanFormation } from "./PlanFormation.model";
import { SousCategorie } from "./SousCategorie.model";
import { TypeFormations } from "./TypeFormations.model";



export interface Formation {
  idFormation?: number;
  theme: string;
  descriptionTheme?: string;
  objectifGlobal?: string;
  objectifSpecifiqueGlobal?: string;
  nombreHeures?: number;
  prix?: number;
  nombreMax?: number;
  populationCible?: string;
  imageUrl?: string;
  typeFormation?: TypeFormation;  
  niveau?: NiveauFormation;        
  domaine?: Domaine;
  type?: TypeFormations;
  categorie?: Categorie;
  sousCategorie?: SousCategorie;
  planFormations?: PlanFormation[]; 
  formateurs?: Formateur[];  
  programmesDetailes?: ProgrammeDetaile[];
  contenusFormation?: ContenuFormation[];  // Array to store attached content
  contenusWithJours?: ContenuWithJour[];  // Contenus with jour mapping for PDF
  objectifsGlobaux?: ObjectifGlobal[];  // Objectives from backend
  objectifsSpecifiques?: any[];  // Specific objectives with contenus
  statut?: StatutFormation; // Draft or Published status
  
  idDomaine?: number;
  nomDomaine?: string;
  idType?: number;
  idCategorie?: number;
  idSousCategorie?: number;

}

export interface ProgrammeDetaile {
  idProgramme?: number;
  titre: string;
  jours: JourFormation[];
}

export interface JourFormation {
  idJour?: number;
  numeroJour: number;
  contenus: ContenuDetaille[];
}

export interface ContenuDetaille {
  idContenuDetaille?: number;
  contenusCles: string[];
  methodesPedagogiques: string;
  dureeTheorique: number;
  dureePratique: number;
}
export interface ObjectifGlobal {
  idObjectifGlobal?: number;
  libelle: string;
  description?: string;
  tags?: string;
}

export interface ContenuFormation {
  id: number;
  title: string;
  description: string;
  type: string;
  size: string;
  uploadDate: string;
  fileName: string;
  fileUrl?: string;
  videoUrl?: string;
}

// New interface for contenus with jour mapping (for PDF generation)
export interface ContenuWithJour {
  idContenuDetaille: number;
  titre: string;
  methodesPedagogiques?: string;
  numeroJour: number;  // From ContenuJourFormation
  files: {
    fileName: string;
    filePath: string;
    fileType: string;
    fileSize: number;
    levelNumber: number;
  }[];
}

export enum TypeFormation {
  Inter_Entreprise = 'Inter_Entreprise',
  Intra_Entreprise = 'Intra_Entreprise',
  En_Ligne = 'En_Ligne'
}

export enum NiveauFormation {
  Debutant = 'Débutant',
  Intermediaire = 'Intermédiaire',
  Avance = 'Avancé'
}

export enum StatutFormation {
  Brouillon = 'Brouillon',
  Publie = 'Publié'
}

// Statistics interface for dashboard
export interface FormationStatistics {
  totalFormations: number;
  formationsEnCours: number;
  formationsPlanifiees: number;
  formationsTerminees: number;
  formationsAnnulees: number;
  formationsReportees: number;
  totalParticipants: number;
  participantsActifs: number;
  certificationsDelivrees: number;
  totalContenus: number;
  totalFormateurs: number;
  formationsByType: { [key: string]: number };
  formationsByLevel: { [key: string]: number };
  formationsThisMonth: number;
  certificationsThisMonth: number;
  totalRevenue: number;
  averageFormationPrice: number;
  totalDomaines: number;
  totalCategories: number;
  totalSousCategories: number;
  totalPlans: number;
  activePlans: number;
}