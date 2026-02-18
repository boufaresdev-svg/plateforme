export interface Charge {
  id: string;
  tacheId: string;
  employeId: string;  
  nom:string;
  description?: string;
  montant:number ;
  categorie?: string;
  sousCategorie?: string;
  createdAt: Date;
  updatedAt: Date;
}