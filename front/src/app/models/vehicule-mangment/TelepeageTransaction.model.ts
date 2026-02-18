export interface TelepeageTransaction {
  id?: string;
  date: Date;
  montant: number;
  conducteur?: string;
  description?: string;     // Optionnel : description ou station
}