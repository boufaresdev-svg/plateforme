import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { 
  Dossier, 
  DocumentDossier, 
  AccesDossier, 
  CategorieDossier, 
  StatutDossier, 
  TypeDocument, 
  TypeAcces, 
  DossierStats 
} from '../models/dossier.model';

@Injectable({
  providedIn: 'root'
})
export class DossierService {
  private dossiersSubject = new BehaviorSubject<Dossier[]>([]);
  public dossiers$ = this.dossiersSubject.asObservable();

  private documentsSubject = new BehaviorSubject<DocumentDossier[]>([]);
  public documents$ = this.documentsSubject.asObservable();

  private accesSubject = new BehaviorSubject<AccesDossier[]>([]);
  public acces$ = this.accesSubject.asObservable();

  constructor() {
    this.loadMockData();
  }

  private loadMockData(): void {
    const mockDossiers: Dossier[] = [
      {
        id: '1',
        nom: 'Contrats Clients 2024',
        description: 'Dossier contenant tous les contrats clients pour l\'année 2024',
        categorie: CategorieDossier.COMMERCIAL,
        statut: StatutDossier.ACTIF,
        proprietaire: 'admin',
        departement: 'Commercial',
        dateCreation: new Date('2024-01-01'),
        dateModification: new Date('2024-01-20'),
        tags: ['contrats', 'clients', '2024'],
        documents: [],
        acces: [],
        taille: 25.6
      },
      {
        id: '2',
        nom: 'Procédures RH',
        description: 'Documentation des procédures de ressources humaines',
        categorie: CategorieDossier.RH,
        statut: StatutDossier.ACTIF,
        proprietaire: 'admin',
        departement: 'RH',
        dateCreation: new Date('2023-12-15'),
        dateModification: new Date('2024-01-15'),
        tags: ['procédures', 'rh', 'documentation'],
        documents: [],
        acces: [],
        taille: 12.3
      },
      {
        id: '3',
        nom: 'Archives Comptables 2023',
        description: 'Archives des documents comptables de l\'année 2023',
        categorie: CategorieDossier.FINANCIER,
        statut: StatutDossier.ARCHIVE,
        proprietaire: 'admin',
        departement: 'Finance',
        dateCreation: new Date('2023-01-01'),
        dateModification: new Date('2023-12-31'),
        dateArchivage: new Date('2024-01-01'),
        tags: ['archives', 'comptabilité', '2023'],
        documents: [],
        acces: [],
        taille: 156.8
      }
    ];

    const mockDocuments: DocumentDossier[] = [
      {
        id: '1',
        dossierId: '1',
        nom: 'Contrat_Client_ABC_2024.pdf',
        type: TypeDocument.PDF,
        taille: 2.5,
        chemin: '/dossiers/1/contrat_abc.pdf',
        version: '1.0',
        dateAjout: new Date('2024-01-15'),
        ajoutePar: 'admin',
        description: 'Contrat de service avec le client ABC',
        estConfidentiel: false
      },
      {
        id: '2',
        dossierId: '2',
        nom: 'Procedure_Recrutement.docx',
        type: TypeDocument.WORD,
        taille: 1.2,
        chemin: '/dossiers/2/procedure_recrutement.docx',
        version: '2.1',
        dateAjout: new Date('2024-01-10'),
        ajoutePar: 'admin',
        description: 'Procédure de recrutement mise à jour',
        estConfidentiel: true
      }
    ];

    const mockAcces: AccesDossier[] = [
      {
        id: '1',
        dossierId: '1',
        utilisateur: 'mohamed.benahmed@sms2i.com',
        typeAcces: TypeAcces.LECTURE,
        dateAccorde: new Date('2024-01-15'),
        accordePar: 'admin'
      },
      {
        id: '2',
        dossierId: '2',
        utilisateur: 'fatma.trabelsi@sms2i.com',
        typeAcces: TypeAcces.ECRITURE,
        dateAccorde: new Date('2024-01-10'),
        accordePar: 'admin'
      }
    ];

    this.dossiersSubject.next(mockDossiers);
    this.documentsSubject.next(mockDocuments);
    this.accesSubject.next(mockAcces);
  }

  getDossiers(): Observable<Dossier[]> {
    return this.dossiers$;
  }

  getDossierById(id: string): Dossier | undefined {
    return this.dossiersSubject.value.find(d => d.id === id);
  }

  addDossier(dossier: Omit<Dossier, 'id' | 'dateCreation' | 'dateModification' | 'documents' | 'acces' | 'taille'>): void {
    const newDossier: Dossier = {
      ...dossier,
      id: Date.now().toString(),
      dateCreation: new Date(),
      dateModification: new Date(),
      documents: [],
      acces: [],
      taille: 0
    };

    const currentDossiers = this.dossiersSubject.value;
    this.dossiersSubject.next([...currentDossiers, newDossier]);
  }

  updateDossier(id: string, updates: Partial<Dossier>): void {
    const currentDossiers = this.dossiersSubject.value;
    const updatedDossiers = currentDossiers.map(dossier => 
      dossier.id === id 
        ? { ...dossier, ...updates, dateModification: new Date() }
        : dossier
    );
    this.dossiersSubject.next(updatedDossiers);
  }

  deleteDossier(id: string): void {
    const currentDossiers = this.dossiersSubject.value;
    const filteredDossiers = currentDossiers.filter(d => d.id !== id);
    this.dossiersSubject.next(filteredDossiers);

    // Supprimer les documents et accès associés
    const currentDocuments = this.documentsSubject.value;
    const filteredDocuments = currentDocuments.filter(doc => doc.dossierId !== id);
    this.documentsSubject.next(filteredDocuments);

    const currentAcces = this.accesSubject.value;
    const filteredAcces = currentAcces.filter(acc => acc.dossierId !== id);
    this.accesSubject.next(filteredAcces);
  }

  addDocument(document: Omit<DocumentDossier, 'id'>): void {
    const newDocument: DocumentDossier = {
      ...document,
      id: Date.now().toString()
    };

    const currentDocuments = this.documentsSubject.value;
    this.documentsSubject.next([...currentDocuments, newDocument]);

    // Mettre à jour la taille du dossier
    const dossier = this.getDossierById(document.dossierId);
    if (dossier) {
      this.updateDossier(document.dossierId, {
        taille: dossier.taille + document.taille
      });
    }
  }

  getDocumentsByDossierId(dossierId: string): DocumentDossier[] {
    return this.documentsSubject.value.filter(doc => doc.dossierId === dossierId);
  }

  addAcces(acces: Omit<AccesDossier, 'id'>): void {
    const newAcces: AccesDossier = {
      ...acces,
      id: Date.now().toString()
    };

    const currentAcces = this.accesSubject.value;
    this.accesSubject.next([...currentAcces, newAcces]);
  }

  getAccesByDossierId(dossierId: string): AccesDossier[] {
    return this.accesSubject.value.filter(acc => acc.dossierId === dossierId);
  }

  archiverDossier(id: string): void {
    this.updateDossier(id, {
      statut: StatutDossier.ARCHIVE,
      dateArchivage: new Date()
    });
  }

  getDossierStats(): DossierStats {
    const dossiers = this.dossiersSubject.value;
    const documents = this.documentsSubject.value;

    const dossiersActifs = dossiers.filter(d => d.statut === StatutDossier.ACTIF).length;
    const dossiersArchives = dossiers.filter(d => d.statut === StatutDossier.ARCHIVE).length;
    const tailleTotal = dossiers.reduce((total, d) => total + d.taille, 0);

    const dossiersParCategorie: { [key: string]: number } = {};
    Object.values(CategorieDossier).forEach(categorie => {
      dossiersParCategorie[categorie] = dossiers.filter(d => d.categorie === categorie).length;
    });

    return {
      totalDossiers: dossiers.length,
      dossiersActifs,
      dossiersArchives,
      totalDocuments: documents.length,
      tailleTotal: Math.round(tailleTotal * 10) / 10,
      dossiersParCategorie
    };
  }
}