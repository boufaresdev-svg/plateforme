import { TypeFormations } from "./TypeFormations.model";

export interface Categorie {
  idCategorie?: number;            
  nom: string;                      
  description?: string;            
  type?: TypeFormations;                     
}
