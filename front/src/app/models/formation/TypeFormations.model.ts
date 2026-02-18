import { Domaine } from "./Domaine.model";

export interface TypeFormations {
  idType?: number;            
  nom: string;                
  description?: string;       
  domaine?: Domaine;          
}
