import { TypeReparation } from "./enum.model";
import { Vehicle } from "./vehicle.model";

export interface Reparation {
  id: string;
  vehicleId: string;
  type: TypeReparation;
  prix: number;
  date: Date;
  description?: string; 
  url?: string;
  vehicule?: Vehicle
}

