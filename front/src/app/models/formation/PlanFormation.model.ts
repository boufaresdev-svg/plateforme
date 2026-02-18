import { Apprenant } from "./Apprenant.model";
import { Evaluation } from "./Evaluation.model";
import { Formateur } from "./Formateur.model";
import { Formation } from "./formation.model";




export interface PlanFormation {
  idPlanFormation?: number;       
  titre: string;                  
  description?: string;          
  dateLancement?: string;       
  dateFinReel?: string;           
  dateDebut?: string;             
  dateFin?: string;               
  statusFormation?: StatutFormation;  

  formation?: Formation;          
  formateur?: Formateur;     

  idFormation?: number;  
  idFormateur?: number;

  apprenants?: Apprenant[];       
  evaluations?: Evaluation[];     
  nombreJours?: number;
}

export enum StatutFormation {
  PLANIFIEE = 'PLANIFIEE',
  EN_COURS = 'EN_COURS',
  TERMINEE = 'TERMINEE',
  ANNULEE = 'ANNULEE',
  REPORTEE = 'REPORTEE'
}
