import { Apprenant } from "./Apprenant.model";
import { Examen } from "./Examen.model";

export interface Certificat {
  idCertificat?: number;       
  titre: string;               
  description?: string;       
  niveau?: string;             
  apprenant?: Apprenant;       
  examen?: Examen; 
  idApprenant?: number;
  idExamen?: number;
            
}

