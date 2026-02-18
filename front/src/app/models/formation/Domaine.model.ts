import {  TypeFormations } from "./TypeFormations.model";

export interface Domaine {
  idDomaine?: number;         
  nom: string;                
  description?: string;      
  types?: TypeFormations[];             
}
