import { Reparation } from "./reparation.model";

export interface Vehicle {
  id: string;
  serie: string; // Format: xxx tn xxxx
  marque: string;
  dateVisiteTechnique: Date;
  dateAssurance: Date;
  dateTaxe: Date;
  prochainVidangeKm: number;
  kmActuel: number;
  reparations: Reparation[];
  createdAt: Date;
  updatedAt: Date;
  prochaineChaineKm: number;
  dateChangementBatterie: Date,
  consommation100km: number;
}

