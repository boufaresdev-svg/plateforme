export interface ProblemeTache {
  id: string;
  tacheId: string;         // ID de la tâche associée
  nom: string;             // Nom du problème
  description?: string;    // Description optionnelle
  dateDetection?: Date;    // Date de détection du problème
}