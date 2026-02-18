import { TelepeageTransaction } from "./TelepeageTransaction.model";

export interface CarteTelepeage {
  id?: string;               
  numero: string;           
  fournisseur: string;      
  solde: number;            
  dateEmission: Date;       
  consomation?: number;     
  transactions?: TelepeageTransaction[];  
}