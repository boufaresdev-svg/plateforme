import { ObjectifSpecifique } from "./ObjectifSpecifique.model";

export interface ContenuJourFormation {
  idContenuJour?: number;                
  contenu: string;                       
  moyenPedagogique?: string;             
  supportPedagogique?: string;           
  nbHeuresTheoriques?: number;           
  nbHeuresPratiques?: number;            
  objectifSpecifique?: ObjectifSpecifique; 
  idPlanFormation?: number;

}
