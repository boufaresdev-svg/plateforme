import { StatutFacture } from "./enum.model";

export interface FactureClient {
  id: string;
  clientId: string;
  numero: string;
  montant: number;
  dateEmission: Date;
  dateEcheance: Date;
  statut: StatutFacture;
  projetId?: string;
  description?: string;
}