import { MethodePaiement } from "./enum.model";

export interface PaiementClient {
  id: string;
  clientId: string;
  factureId: string;
  montant: number;
  datePaiement: Date;
  methode: MethodePaiement;
}
