import { TypeCommentaire } from "./enum.model";

export interface CommentaireTache {
  id: string;
  tacheId: string;
  auteur: string;
  contenu: string;
  date: Date;
  type: TypeCommentaire;
}