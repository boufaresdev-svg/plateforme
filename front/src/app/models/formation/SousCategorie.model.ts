import { Categorie } from "./Categorie.model";

export interface SousCategorie {
  id?: number;                // Backend uses 'id'
  idSousCategorie?: number;   // Alternative property name
  nom: string;                
  description?: string;       
  categorie?: Categorie;
  categorieNom?: string;      // Backend includes this
}
