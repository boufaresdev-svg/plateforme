import { Formation } from "./formation.model";
import { PlanFormation } from "./PlanFormation.model";

export interface Formateur {
  idFormateur?: number;           
  nom: string;                   
  prenom?: string;                
  specialite?: string;            
  contact?: string;               
  experience?: string;            
  documentUrl?: string; 
  

  formationsAnimees?: Formation[];          
  formationsResponsables?: PlanFormation[]; 
}
