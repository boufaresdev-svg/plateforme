import { Formation } from "./formation.model";

export interface ContenuGlobal {
  idContenuGlobal?: number;              
  titre: string;                         
  description?: string;                 
  formation?: Formation;                 
}
