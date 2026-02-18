import { ContenuGlobal } from "./ContenuGlobal.model";

export interface ObjectifSpecifique {
  idObjectifSpec?: number;                   
  titre: string;                              
  description?: string;                      
  idObjectifGlobal?: number;  // Link to parent global objective
  contenus?: any[];  // List of content/ContenuJourFormation items
  contenuGlobal?: ContenuGlobal;         

}
