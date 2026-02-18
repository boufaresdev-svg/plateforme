import { CarteGazoil } from "./carte-gazoil.model";
import { TypeCarburant } from "./enum.model";
import { Vehicle } from "./vehicle.model";

export interface TransactionCarburant {
  id: string;
  carteId: string;
  carte:CarteGazoil;
  vehiculeId: string;
  vehicule:Vehicle;
  date: Date;
  station: string;
  adresseStation: string;
  quantite: number; // en litres
  prixLitre: number;
  montantTotal: number;
  kilometrage: number;
  ancienkilometrage: number;
  typeCarburant: TypeCarburant;
  conducteur?: string;
  consommation:number;
  carteTelepeageId:string;
  montantTelepeage:number;
}

