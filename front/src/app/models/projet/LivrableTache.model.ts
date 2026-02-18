export interface LivrableTache {
  id: string;
  tacheId: string;         // ID de la tâche associée
  nom: string;             // Nom du livrable
  description?: string;    // Description optionnelle
  dateLivraison?: Date;    // Date de livraison prévue ou effective
}
