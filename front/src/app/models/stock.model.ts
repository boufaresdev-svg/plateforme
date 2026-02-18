// Interface pour Catégorie
export interface CategorieArticle {
  id: string;
  nom: string;
  description: string;
  estActif: boolean;
  nombreProduits?: number;
  createdAt: Date;
  updatedAt: Date;
}

// Interface pour les commandes de catégorie
export interface AddCategoryCommand {
  nom: string;
  description?: string;
  estActif?: boolean;
}

export interface UpdateCategoryCommand {
  id: string;
  nom: string;
  description?: string;
  estActif?: boolean;
}

export interface CategoryQuery {
  id?: string;
  nom?: string;
  description?: string;
  estActif?: boolean;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
}

// Interface pour Contact Marque
export interface ContactMarque {
  nom: string;
  email?: string;
  telephone?: string;
  poste?: string;
}

// Interface pour Marque (with French field names)
export interface Marque {
  id: string;
  nom: string;
  codeMarque?: string;
  description?: string;
  pays?: string;
  siteWeb?: string;
  urlLogo?: string;
  nomContact?: string;
  email?: string;
  telephone?: string;
  poste?: string;
  estActif: boolean;
  nombreProduits?: number;
  createdAt: Date;
  updatedAt: Date;
}

// Interface pour les commandes de marque
export interface AddMarqueCommand {
  nom: string;
  codeMarque?: string;
  description?: string;
  pays?: string;
  siteWeb?: string;
  urlLogo?: string;
  nomContact?: string;
  email?: string;
  telephone?: string;
  poste?: string;
  estActif?: boolean;
}

export interface UpdateMarqueCommand {
  id: string;
  nom: string;
  codeMarque?: string;
  description?: string;
  pays?: string;
  siteWeb?: string;
  urlLogo?: string;
  nomContact?: string;
  email?: string;
  telephone?: string;
  poste?: string;
  estActif?: boolean;
}

export interface MarqueQuery {
  id?: string;
  nom?: string;
  codeMarque?: string;
  pays?: string;
  estActif?: boolean;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
}

// Interface pour les commandes d'article
export interface AddArticleCommand {
  codebare?: string;
  nom: string;
  description?: string;
  categorieId?: string;
  imageUrl?: string;
  unitesDeMesure?: string;
  prixAchat?: number;
  prixVente?: number;
  tauxTaxe?: number;
  marqueId?: string;
  prixVenteHT?: number;
  stockMinimum?: number;
  stockMaximum?: number;
  estStockBasee?: boolean;
  estStockElever?: boolean;
  estActif?: boolean;
}

export interface UpdateArticleCommand {
  id: string;
  sku?: string;
  codebare?: string;
  nom: string;
  description?: string;
  categorieId?: string;
  imageUrl?: string;
  unitesDeMesure?: string;
  prixAchat?: number;
  prixVente?: number;
  tauxTaxe?: number;
  marqueId?: string;
  prixVenteHT?: number;
  stockMinimum?: number;
  stockMaximum?: number;
  estStockBasee?: boolean;
  estStockElever?: boolean;
  estActif?: boolean;
}

export interface ArticleQuery {
  id?: string;
  searchTerm?: string;
  nom?: string;
  sku?: string;
  categorie?: string;
  marque?: string;
  estActif?: boolean;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
}

// Interface pour Entrepôt (with French field names)
export interface Entrepot {
  id: string;
  nom: string;
  description?: string;
  adresse: string;
  ville: string;
  codePostal?: string;
  telephone?: string;
  email?: string;
  responsable?: string;
  superficie?: number;
  capaciteMaximale?: number;
  capaciteUtilisee?: number;
  statut?: string;
  estActif: boolean;
  nombreProduits?: number;
  createdAt: Date;
  updatedAt: Date;
}

// Interface pour les commandes d'entrepôt
export interface AddEntrepotCommand {
  nom: string;
  description?: string;
  adresse: string;
  ville: string;
  codePostal?: string;
  telephone?: string;
  email?: string;
  responsable?: string;
  superficie?: number;
  capaciteMaximale?: number;
  statut?: string;
  estActif?: boolean;
}

export interface UpdateEntrepotCommand {
  id: string;
  nom: string;
  description?: string;
  adresse: string;
  ville: string;
  codePostal?: string;
  telephone?: string;
  email?: string;
  responsable?: string;
  superficie?: number;
  capaciteMaximale?: number;
  statut?: string;
  estActif?: boolean;
}

export interface EntrepotQuery {
  id?: string;
  searchTerm?: string;
  ville?: string;
  statut?: string;
  estActif?: boolean;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
}

// Legacy interfaces for backward compatibility
export interface EntrepotLegacy {
  id: string;
  nom: string;
  description: string;
  adresse: string;
  ville: string;
  codePostal: string;
  telephone?: string;
  email?: string;
  responsable?: string;
  superficie?: number;
  capaciteMaximale?: number;
  capaciteUtilisee: number;
  statut: StatutEntrepot;
  zones: ZoneEntrepot[];
  dateCreation: Date;
  dateModification: Date;
}

// Interface pour Zone d'Entrepôt
export interface ZoneEntrepot {
  id: string;
  nom: string;
  description: string;
  entrepotId: string;
  typeZone: TypeZone;
  capacite: number;
  capaciteUtilisee: number;
  emplacements: EmplacementStock[];
}

// Interface pour Emplacement
export interface EmplacementStock {
  id: string;
  nom: string;
  description: string;
  zoneId: string;
  rangee?: string;
  etagere?: string;
  niveau?: string;
  capacite: number;
  capaciteUtilisee: number;
  actif: boolean;
}

// Interface pour Stock dans un Entrepôt
export interface StockEntrepot {
  id: string;
  articleId: string;
  article?: Article;
  entrepotId: string;
  entrepot?: Entrepot;
  emplacementId?: string;
  emplacement?: EmplacementStock;
  zone?: ZoneEntrepot;
  quantite: number;
  quantiteStock?: number;
  quantiteReservee?: number;
  quantiteDisponible?: number;
  stockMinimum: number;
  stockMaximum: number;
  statut?: StatutStock;
  dateDerniereEntree?: Date;
  dateDerniereSortie?: Date;
  dateCreation: Date;
  dateModification: Date;
  mouvements?: MouvementStock[];
}

// Interface Stock alignée avec le backend
export interface Stock {
  id: string;
  article_id?: string; // For direct API calls
  articleId?: string; // For compatibility with existing code
  article?: Article;
  entrepot_id?: string; // For direct API calls
  entrepotId?: string; // For compatibility with existing code
  entrepot?: Entrepot;
  fournisseur_id?: string; // For direct API calls
  fournisseurId?: string; // For compatibility with existing code
  fournisseur?: {
    id: string;
    nom: string;
    infoContact?: string;
  };
  fournisseurNom?: string;
  quantite: number;
  date_dexpiration?: Date;
  dateDexpiration?: Date; // Camel case alias
  created_at?: Date;
  createdAt?: Date; // Camel case alias
  updated_at?: Date;
  updatedAt?: Date; // Camel case alias
  created_by?: string;
  createdBy?: string; // Camel case alias
  updated_by?: string;
  updatedBy?: string; // Camel case alias
  // Lot-based pricing fields (calculated from active lots)
  prixUnitaireMoyenAchat?: number;  // Weighted average purchase price from lots
  prixUnitaireMoyenVente?: number;  // Weighted average selling price from lots
  valeurTotaleStock?: number;       // Total value based on lot prices (quantity * purchase price)
  nombreLots?: number;              // Number of active lots
}

// Interface Stock originale pour compatibilité avec les anciens composants
export interface StockLegacy {
  id: string;
  nom: string;
  description: string;
  categorie: CategorieStock;
  quantiteStock: number;
  quantiteMinimale: number;
  quantiteMaximale: number;
  prixUnitaire: number;
  fournisseur?: string | {
    id: string;
    nom: string;
    infoContact?: string;
  };
  dateAjout: Date;
  dateModification: Date;
  statut: StatutStock;
  emplacement: string;
  codeBarres?: string;
  mouvements: MouvementStock[];
}

export interface MouvementStock {
  id: string;
  typeMouvement: TypeMouvement;
  articleId: string;
  article?: Article;
  articleNom?: string;
  articleSku?: string;
  sourceEntrepotId?: string;
  sourceEntrepot?: Entrepot;
  sourceEntrepotNom?: string;
  destinationEntrepotId?: string;
  destinationEntrepot?: Entrepot;
  destinationEntrepotNom?: string;
  quantite: number;
  dateMouvement: Date;
  utilisateurId: string;
  utilisateur?: string;
  utilisateurNom?: string;
  reference?: string;
  statut: StatutMouvement;
  numeroBonReception?: string;
  referenceBonCommande?: string;
  typeEntree?: TypeEntree;
  numeroBonSortie?: string;
  typeSortie?: TypeSortie;
  createdAt?: Date;
  updatedAt?: Date;
  createdBy?: string;
  updatedBy?: string;
  
  // Legacy fields for backward compatibility
  entrepotId?: string;
  entrepot?: Entrepot;
  type?: TypeMouvement;
  motif?: string;
  commentaire?: string;
  prixUnitaire?: number;
  notes?: string;
  stockId?: string;
  date?: Date;
}

export enum StatutMouvement {
  EN_ATTENTE = 'EN_ATTENTE',
  VALIDE = 'VALIDE',
  ANNULE = 'ANNULE'
}

export enum TypeEntree {
  ACHAT = 'ACHAT',
  RETOUR_CLIENT = 'RETOUR_CLIENT',
  INVENTAIRE_POSITIF = 'INVENTAIRE_POSITIF'
}

export enum TypeSortie {
  VENTE = 'VENTE',
  RETOUR_FOURNISSEUR = 'RETOUR_FOURNISSEUR',
  INVENTAIRE_NEGATIF = 'INVENTAIRE_NEGATIF'
}

export enum CategorieStock {
  MATERIEL_INFORMATIQUE = 'Matériel Informatique',
  FOURNITURES_BUREAU = 'Fournitures de Bureau',
  EQUIPEMENT_SECURITE = 'Équipement de Sécurité',
  PIECES_DETACHEES = 'Pièces Détachées',
  CONSOMMABLES = 'Consommables',
  AUTRE = 'Autre'
}

export enum StatutStock {
  DISPONIBLE = 'Disponible',
  RUPTURE = 'Rupture de Stock',
  COMMANDE = 'En Commande',
  INDISPONIBLE = 'Indisponible'
}

export enum TypeMouvement {
  ENTREE = 'ENTREE',
  SORTIE = 'SORTIE',
  RETOUR_ENTREE = 'RETOUR_ENTREE',
  RETOUR_SORTIE = 'RETOUR_SORTIE',
  AJUSTEMENT = 'AJUSTEMENT',
  TRANSFERT = 'TRANSFERT',  // Keep for backward compatibility
  INVENTAIRE = 'INVENTAIRE',  // Keep for backward compatibility
  RETOUR = 'RETOUR'  // Keep for backward compatibility
}

// Nouveaux enums pour les nouvelles interfaces
export enum StatutEntrepot {
  ACTIF = 'Actif',
  INACTIF = 'Inactif',
  MAINTENANCE = 'En Maintenance',
  FERME = 'Fermé'
}

export enum TypeZone {
  STOCKAGE = 'Zone de Stockage',
  RECEPTION = 'Zone de Réception',
  EXPEDITION = 'Zone d\'Expédition',
  QUARANTAINE = 'Quarantaine',
  RETOURS = 'Zone de Retours',
  PREPARATION = 'Zone de Préparation'
}

export enum UniteStock {
  PIECE = 'Pièce',
  BOITE = 'Boîte',
  PAQUET = 'Paquet',
  CARTON = 'Carton',
  PALETTE = 'Palette',
  METRE = 'Mètre',
  LITRE = 'Litre',
  KILOGRAMME = 'Kilogramme',
  LOT = 'Lot'
}

// Interface pour les statistiques étendues
export interface StockStats {
  totalArticles: number;
  articlesDisponibles: number;
  articlesRupture: number;
  valeurTotaleStock: number;
  mouvementsRecents: number;
  alertesStock: number;
  totalEntrepots: number;
  totalCategories: number;
  totalMarques: number;
  articlesParCategorie: { [key: string]: number };
  articlesParMarque: { [key: string]: number };
  stockParEntrepot: { [key: string]: number };
}

// Interface pour les filtres de recherche
export interface FiltreStock {
  categorieId?: string;
  marqueId?: string;
  entrepotId?: string;
  statut?: StatutStock;
  recherche?: string;
  prixMin?: number;
  prixMax?: number;
  quantiteMin?: number;
  quantiteMax?: number;
  dateCreationDebut?: Date;
  dateCreationFin?: Date;
}

// Interface pour les rapports
export interface RapportStock {
  id: string;
  nom: string;
  type: TypeRapport;
  parametres: any;
  dateGeneration: Date;
  donnees: any[];
}

export enum TypeRapport {
  INVENTAIRE = 'Inventaire',
  VALORISATION = 'Valorisation',
  MOUVEMENTS = 'Mouvements',
  ALERTES = 'Alertes',
  PERFORMANCE = 'Performance'
}

// Interface pour les demandes de stock
export interface DemandeStock {
  id: string;
  articleId: string;
  article?: Article;
  entrepotSource?: string;
  entrepotDestination?: string;
  quantiteDemandee: number;
  quantiteValidee?: number;
  motif: string;
  statut: StatutDemande;
  demandePar: string;
  dateCreation: Date;
  dateValidation?: Date;
  validePar?: string;
  commentaires?: string;
}

export enum StatutDemande {
  EN_ATTENTE = 'En Attente',
  VALIDEE = 'Validée',
  REJETEE = 'Rejetée',
  TRAITEE = 'Traitée',
  ANNULEE = 'Annulée'
}

// Fournisseur interface (minimal for relationships)
export interface Fournisseur {
  id: string;
  nom: string;
}

// Interface pour Article
export interface Article {
  id: string;
  sku?: string;
  codebare?: string;
  nom: string;
  description?: string;
  categorie?: string | CategorieArticle;
  categorieId?: string; // For compatibility with stock components
  categorieNom?: string; // Display name for category
  imageUrl?: string;
  image?: string; // Alias for imageUrl for compatibility
  unitesDeMesure?: string;
  unite?: string; // Alias for unitesDeMesure for compatibility
  prixAchat?: number;
  prixVente?: number;
  prixUnitaire?: number; // Alias for prixVente for compatibility
  tauxTaxe?: number;
  marque?: string | Marque;
  marqueId?: string; // For compatibility with stock components
  prixVenteHT?: number;
  stockMinimum?: number;
  stockMaximum?: number;
  stockActuel?: number; // Current stock quantity
  quantiteDisponible?: number; // Available quantity from stock
  quantiteMinimale?: number; // Alias for stockMinimum
  quantiteMaximale?: number; // Alias for stockMaximum
  fournisseur?: string | Fournisseur;
  fournisseurId?: string; // For form binding
  fournisseurPrincipal?: string; // Alias for fournisseur
  code?: string; // Alias for SKU for compatibility
  codeBarres?: string; // Alias for codebare
  estStockBasee?: boolean;
  estStockElever?: boolean;
  estActif?: boolean;
  actif?: boolean; // Alias for estActif
  createdAt?: Date;
  updatedAt?: Date;
  dateCreation?: Date; // Alias for createdAt
  dateModification?: Date; // Alias for updatedAt
}

// Commands pour Stock (alignées avec le backend)
export interface AddStockCommand {
  articleId: string;
  entrepotId: string;
  fournisseurId?: string;
  quantite: number;
  dateDexpiration?: Date;
  createdBy?: string;
  // Lot/Batch information for initial stock entry
  prixAchatUnitaire?: number;
  prixVenteUnitaire?: number;
  dateAchat?: string; // format: 'yyyy-MM-dd'
  factureUrl?: string;
  numeroFacture?: string;
  dateExpiration?: string; // format: 'yyyy-MM-dd'
}

export interface UpdateStockCommand {
  id?: string;
  fournisseurId?: string;
  quantite?: number;
  dateDexpiration?: Date;
  updatedBy?: string;
}

export interface DeleteStockCommand {
  id: string;
}

// Queries pour Stock
export interface StockQuery {
  id?: string;
  articleId?: string;
  entrepotId?: string;
  categorieId?: string;
  marqueId?: string;
  fournisseurId?: string;
  statut?: string;
  searchTerm?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
}

// Responses du backend
export interface StockResponse {
  id: string;
  articleId: string;
  articleNom?: string;
  articleDescription?: string;
  articleCodebare?: string;
  articlePrixVente?: number;
  articlePrixAchat?: number;
  articleStockMinimum?: number;
  articleStockMaximum?: number;
  categorieId?: string;
  categorieNom?: string;
  marqueId?: string;
  marqueNom?: string;
  entrepotId: string;
  entrepotNom?: string;
  entrepotAdresse?: string;
  fournisseurId?: string;
  fournisseurNom?: string;
  quantite: number;
  dateDexpiration?: Date;
  createdBy?: string;
  updatedBy?: string;
  createdAt: Date;
  updatedAt: Date;
  // Lot-based pricing fields (calculated from active lots)
  prixUnitaireMoyenAchat?: number;  // Weighted average purchase price from lots
  prixUnitaireMoyenVente?: number;  // Weighted average selling price from lots
  valeurTotaleStock?: number;       // Total value based on lot prices (quantity * purchase price)
  nombreLots?: number;              // Number of active lots
}

// Ajustement de Stock - Stock Adjustment
export interface AjustementStock {
  id: string;
  articleId: string;
  articleNom?: string;
  articleSku?: string;
  entrepotId?: string;
  entrepotNom?: string;
  quantiteAvant: number;
  quantiteApres: number;
  ajustement: number;
  dateAjustement: Date;
  utilisateurId?: string;
  utilisateurNom?: string;
  raison?: string;
  notes?: string;
}

export interface AddAjustementStockCommand {
  articleId: string;
  entrepotId?: string;
  quantiteAvant: number;
  quantiteApres: number;
  dateAjustement?: Date;
  raison?: string;
  notes?: string;
}

export interface UpdateAjustementStockCommand {
  id: string;
  articleId: string;
  entrepotId?: string;
  quantiteAvant: number;
  quantiteApres: number;
  dateAjustement?: Date;
  raison?: string;
  notes?: string;
}

export interface AjustementStockQuery {
  id?: string;
  articleId?: string;
  utilisateurId?: string;
  categorieId?: string;
  marqueId?: string;
  fournisseurId?: string;
  entrepotId?: string;
  startDate?: Date;
  endDate?: Date;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

export interface CommandResponse {
  id: string;
  message: string;
}

// Mouvement Stock Commands
export interface EntreeStockCommand {
  articleId: string;
  destinationEntrepotId: string;
  quantite: number;
  typeEntree: TypeEntree;
  reference?: string;
  numeroBonReception?: string;
  referenceBonCommande?: string;
  dateMouvement?: string;
  statut?: StatutMouvement;
  notes?: string;
  // Batch/Lot information - TODO: Make these required and update UI forms
  prixAchatUnitaire?: number; // SHOULD BE REQUIRED
  prixVenteUnitaire?: number; // SHOULD BE REQUIRED
  dateAchat?: string;
  factureUrl?: string;
  numeroFacture?: string;
  dateExpiration?: string;
  numeroLot?: string; // Optional: auto-generated if not provided
  stockLotId?: string; // Optional: specify to add quantity to existing lot instead of creating new one
}

export interface SortieStockCommand {
  articleId: string;
  sourceEntrepotId: string;
  quantite: number;
  typeSortie: TypeSortie;
  reference?: string;
  numeroBonLivraison?: string;
  referenceCommandeClient?: string;
  destinataire?: string;
  motif?: string;
  dateMouvement?: string;
  statut?: StatutMouvement;
  notes?: string;
  // Batch selection - TODO: Make required and add batch selection UI
  stockLotId?: string; // SHOULD BE REQUIRED - User must specify which batch to take from
}

export interface TransfertStockCommand {
  articleId: string;
  sourceEntrepotId: string;
  destinationEntrepotId: string;
  quantite: number;
  reference?: string;
  motif?: string;
  dateMouvement?: string;
  statut?: StatutMouvement;
  notes?: string;
  // Batch selection - REQUIRED for stock transfer
  stockLotId: string; // User must specify which batch to transfer from
}

export interface RetourStockCommand {
  articleId: string;
  sourceEntrepotId?: string;
  destinationEntrepotId?: string;
  quantite: number;
  typeRetour: 'RETOUR_ENTREE' | 'RETOUR_SORTIE';
  reference?: string;
  dateMouvement?: Date;
  statut?: StatutMouvement;
  utilisateurId: string;
}

export interface MouvementStockQuery {
  id?: string;
  articleId?: string;
  entrepotId?: string;
  utilisateurId?: string;
  categorieId?: string;
  marqueId?: string;
  typeMouvement?: TypeMouvement;
  statut?: StatutMouvement;
  reference?: string;
  startDate?: Date;
  endDate?: Date;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

export interface MouvementStockResponse {
  id: string;
  typeMouvement: TypeMouvement;
  articleId: string;
  articleNom?: string;
  articleSku?: string;
  sourceEntrepotId?: string;
  sourceEntrepotNom?: string;
  destinationEntrepotId?: string;
  destinationEntrepotNom?: string;
  quantite: number;
  dateMouvement: Date;
  utilisateurId: string;
  utilisateurNom?: string;
  reference?: string;
  statut: StatutMouvement;
  numeroBonReception?: string;
  referenceBonCommande?: string;
  typeEntree?: TypeEntree;
  numeroBonSortie?: string;
  typeSortie?: TypeSortie;
  motif?: string;
  notes?: string;
  createdAt: Date;
  updatedAt: Date;
  createdBy?: string;
  updatedBy?: string;
  stockLotId?: string; // For exits, indicates which batch was used
}

// Stock Batch (Lot) Management
export interface StockLot {
  id: string;
  numeroLot: string;
  articleId: string;
  articleNom?: string;
  articleSku?: string;
  entrepotId: string;
  entrepotNom?: string;
  quantiteInitiale: number;
  quantiteActuelle: number;
  quantiteReservee: number;
  quantiteDisponible: number;
  dateAchat: Date;
  prixAchatUnitaire: number;
  prixVenteUnitaire: number;
  valeurTotale: number;
  dateExpiration?: Date;
  factureUrl?: string;
  numeroFacture?: string;
  referenceFournisseur?: string;
  notes?: string;
  estActif: boolean;
  createdAt?: Date;
  updatedAt?: Date;
  createdBy?: string;
  updatedBy?: string;
}

export interface StockLotMouvement {
  id: string;
  stockLotId: string;
  numeroLot?: string;
  mouvementStockId: string;
  typeMouvement: TypeMouvement;
  quantite: number;
  quantiteAvant: number;
  quantiteApres: number;
  prixUnitaire: number;
  valeurTotale: number;
  dateMouvement: Date;
  utilisateurId?: string;
  utilisateurNom?: string;
  reference?: string;
  commentaire?: string;
  articleId?: string;
  articleNom?: string;
  entrepotId?: string;
  entrepotNom?: string;
}

export interface StockLotQuery {
  id?: string;
  articleId?: string;
  entrepotId?: string;
  estActif?: boolean;
  availableOnly?: boolean;
  expiringBefore?: Date;
}

export interface StockLotMouvementQuery {
  id?: string;
  stockLotId?: string;
  articleId?: string;
}
