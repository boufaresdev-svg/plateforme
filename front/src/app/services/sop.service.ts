import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { 
  SOP, 
  EtapeSOP, 
  DocumentSOP, 
  CategorieSOP, 
  StatutSOP, 
  TypeDocument, 
  SOPStats 
} from '../models/sop.model';

@Injectable({
  providedIn: 'root'
})
export class SOPService {
  private sopsSubject = new BehaviorSubject<SOP[]>([]);
  public sops$ = this.sopsSubject.asObservable();

  private etapesSubject = new BehaviorSubject<EtapeSOP[]>([]);
  public etapes$ = this.etapesSubject.asObservable();

  private documentsSubject = new BehaviorSubject<DocumentSOP[]>([]);
  public documents$ = this.documentsSubject.asObservable();

  constructor() {
    this.loadMockData();
  }

  private loadMockData(): void {
    const mockSOPs: SOP[] = [
      {
        id: '1',
        titre: 'Procédure de sauvegarde des données',
        description: 'Procédure standard pour la sauvegarde quotidienne des données',
        code: 'SOP-001',
        version: '2.1',
        categorie: CategorieSOP.INFORMATIQUE,
        departement: 'IT',
        processus: 'Sauvegarde',
        statut: StatutSOP.ACTIF,
        auteur: 'Mohamed Ben Ahmed',
        approbateur: 'Directeur IT',
        dateCreation: new Date('2023-06-15'),
        dateApprobation: new Date('2023-06-20'),
        dateRevision: new Date('2024-01-15'),
        prochainRevision: new Date('2024-06-15'),
        etapes: [],
        documents: [],
        formations: ['Formation Sauvegarde'],
        tags: ['sauvegarde', 'données', 'sécurité'],
        createdAt: new Date('2023-06-15'),
        updatedAt: new Date('2024-01-15')
      },
      {
        id: '2',
        titre: 'Procédure d\'accueil nouvel employé',
        description: 'Processus d\'intégration des nouveaux employés',
        code: 'SOP-002',
        version: '1.3',
        categorie: CategorieSOP.RH,
        departement: 'RH',
        processus: 'Recrutement',
        statut: StatutSOP.ACTIF,
        auteur: 'Fatma Trabelsi',
        approbateur: 'DRH',
        dateCreation: new Date('2023-09-01'),
        dateApprobation: new Date('2023-09-05'),
        prochainRevision: new Date('2024-09-01'),
        etapes: [],
        documents: [],
        formations: ['Formation RH'],
        tags: ['accueil', 'intégration', 'rh'],
        createdAt: new Date('2023-09-01'),
        updatedAt: new Date('2023-09-01')
      }
    ];

    const mockEtapes: EtapeSOP[] = [
      {
        id: '1',
        sopId: '1',
        numero: 1,
        titre: 'Vérification des systèmes',
        description: 'Vérifier que tous les systèmes sont opérationnels',
        responsable: 'Administrateur système',
        dureeEstimee: 15,
        outils: ['Monitoring', 'Dashboard'],
        controles: ['État des serveurs', 'Espace disque'],
        risques: ['Système indisponible']
      },
      {
        id: '2',
        sopId: '1',
        numero: 2,
        titre: 'Lancement de la sauvegarde',
        description: 'Démarrer le processus de sauvegarde automatique',
        responsable: 'Administrateur système',
        dureeEstimee: 5,
        outils: ['Script de sauvegarde'],
        controles: ['Démarrage du processus'],
        risques: ['Échec du script']
      }
    ];

    const mockDocuments: DocumentSOP[] = [
      {
        id: '1',
        sopId: '1',
        nom: 'Checklist sauvegarde',
        type: TypeDocument.CHECKLIST,
        url: '/documents/checklist-sauvegarde.pdf',
        version: '2.1',
        dateAjout: new Date('2024-01-15')
      }
    ];

    this.sopsSubject.next(mockSOPs);
    this.etapesSubject.next(mockEtapes);
    this.documentsSubject.next(mockDocuments);
  }

  getSOPs(): Observable<SOP[]> {
    return this.sops$;
  }

  getSOPById(id: string): SOP | undefined {
    return this.sopsSubject.value.find(s => s.id === id);
  }

  addSOP(sop: Omit<SOP, 'id' | 'createdAt' | 'updatedAt' | 'etapes' | 'documents'>): void {
    const newSOP: SOP = {
      ...sop,
      id: Date.now().toString(),
      etapes: [],
      documents: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const currentSOPs = this.sopsSubject.value;
    this.sopsSubject.next([...currentSOPs, newSOP]);
  }

  updateSOP(id: string, updates: Partial<SOP>): void {
    const currentSOPs = this.sopsSubject.value;
    const updatedSOPs = currentSOPs.map(sop => 
      sop.id === id 
        ? { ...sop, ...updates, updatedAt: new Date() }
        : sop
    );
    this.sopsSubject.next(updatedSOPs);
  }

  deleteSOP(id: string): void {
    const currentSOPs = this.sopsSubject.value;
    const filteredSOPs = currentSOPs.filter(s => s.id !== id);
    this.sopsSubject.next(filteredSOPs);

    // Supprimer les étapes et documents associés
    const currentEtapes = this.etapesSubject.value;
    const filteredEtapes = currentEtapes.filter(e => e.sopId !== id);
    this.etapesSubject.next(filteredEtapes);

    const currentDocuments = this.documentsSubject.value;
    const filteredDocuments = currentDocuments.filter(d => d.sopId !== id);
    this.documentsSubject.next(filteredDocuments);
  }

  getEtapesBySOPId(sopId: string): EtapeSOP[] {
    return this.etapesSubject.value.filter(e => e.sopId === sopId);
  }

  addEtape(etape: Omit<EtapeSOP, 'id'>): void {
    const newEtape: EtapeSOP = {
      ...etape,
      id: Date.now().toString()
    };

    const currentEtapes = this.etapesSubject.value;
    this.etapesSubject.next([...currentEtapes, newEtape]);
  }

  getDocumentsBySOPId(sopId: string): DocumentSOP[] {
    return this.documentsSubject.value.filter(d => d.sopId === sopId);
  }

  addDocument(document: Omit<DocumentSOP, 'id'>): void {
    const newDocument: DocumentSOP = {
      ...document,
      id: Date.now().toString()
    };

    const currentDocuments = this.documentsSubject.value;
    this.documentsSubject.next([...currentDocuments, newDocument]);
  }

  getSOPStats(): SOPStats {
    const sops = this.sopsSubject.value;
    const documents = this.documentsSubject.value;

    const sopsActifs = sops.filter(s => s.statut === StatutSOP.ACTIF).length;
    const sopsEnRevision = sops.filter(s => s.statut === StatutSOP.EN_REVISION).length;
    const sopsObsoletes = sops.filter(s => s.statut === StatutSOP.OBSOLETE).length;

    const formationsLiees = sops.reduce((total, s) => total + s.formations.length, 0);
    const tauxConformite = 95; // À calculer selon la logique métier

    const today = new Date();
    const revisionsEnRetard = sops.filter(s => s.prochainRevision < today).length;

    return {
      totalSOPs: sops.length,
      sopsActifs,
      sopsEnRevision,
      sopsObsoletes,
      formationsLiees,
      tauxConformite,
      revisionsEnRetard,
      documentsAttaches: documents.length
    };
  }

  generateSOPCode(): string {
    const sops = this.sopsSubject.value;
    const lastCode = sops.length > 0 
      ? Math.max(...sops.map(s => parseInt(s.code.replace('SOP-', '')))) 
      : 0;
    return `SOP-${String(lastCode + 1).padStart(3, '0')}`;
  }
}