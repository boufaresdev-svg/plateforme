import { FournisseurCarburant, StatutCarte } from "./enum.model";
import { TransactionCarburant } from "./transaction.model";
 

export interface CarteGazoil {
  id: string;
  numero: string;
  dateEmission: Date;
  solde: number;
  consomation: number;
  fournisseur: FournisseurCarburant;
  transactions: TransactionCarburant[];

}

