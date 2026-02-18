import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { 
  BienImmobilier, 
  ContratLocation, 
  PaiementLoyer, 
  MaintenanceImmobilier, 
  TypeBien, 
  StatutBien, 
  StatutContrat, 
  StatutPaiement, 
  TypeMaintenance, 
  StatutMaintenance, 
  ImmobilierStats 
} from '../models/immobilier.model';

@Injectable({
  providedIn: 'root'
})
export class ImmobilierService {
  private biensSubject = new BehaviorSubject<BienImmobilier[]>([]);
  public biens$ = this.biensSubject.asObservable();

  private contratsSubject = new BehaviorSubject<ContratLocation[]>([]);
  public contrats$ = this.contratsSubject.asObservable();

  private paiementsSubject = new BehaviorSubject<PaiementLoyer[]>([]);
  public paiements$ = this.paiementsSubject.asObservable();

  private maintenancesSubject = new BehaviorSubject<MaintenanceImmobilier[]>([]);
  public maintenances$ = this.maintenancesSubject.asObservable();

  constructor() {
    this.loadMockData();
  }

  private loadMockData(): void {
    const mockBiens: BienImmobilier[] = [
      {
        id: '1',
        reference: 'IMM-001',
        nom: 'Appartement Centre Ville',
        type: TypeBien.APPARTEMENT,
        statut: StatutBien.LOUE,
        adresse: '123 Avenue Habib Bourguiba',
        ville: 'Tunis',
        codePostal: '1000',
        surface: 85,
        nombrePieces: 3,
        etage: 2,
        ascenseur: true,
        parking: true,
        prixAchat: 120000,
        valeurActuelle: 135000,
        loyerMensuel: 800,
        charges: 50,
        taxeFonciere: 200,
        assurance: 150,
        dateAcquisition: new Date('2022-03-15'),
        contrats: [],
        maintenances: [],
        documents: ['acte_propriete.pdf', 'diagnostic.pdf'],
        photos: ['photo1.jpg', 'photo2.jpg'],
        createdAt: new Date('2022-03-15'),
        updatedAt: new Date('2024-01-10')
      },
      {
        id: '2',
        reference: 'IMM-002',
        nom: 'Bureau Commercial',
        type: TypeBien.BUREAU,
        statut: StatutBien.LIBRE,
        adresse: '456 Rue de la République',
        ville: 'Sfax',
        codePostal: '3000',
        surface: 120,
        nombrePieces: 5,
        etage: 1,
        ascenseur: false,
        parking: true,
        prixAchat: 80000,
        valeurActuelle: 85000,
        loyerMensuel: 600,
        charges: 80,
        taxeFonciere: 150,
        assurance: 120,
        dateAcquisition: new Date('2023-06-20'),
        contrats: [],
        maintenances: [],
        documents: ['acte_propriete.pdf'],
        photos: ['bureau1.jpg'],
        createdAt: new Date('2023-06-20'),
        updatedAt: new Date('2024-01-05')
      }
    ];

    const mockContrats: ContratLocation[] = [
      {
        id: '1',
        bienId: '1',
        locataire: 'Mohamed Gharbi',
        emailLocataire: 'mohamed.gharbi@email.com',
        telephoneLocataire: '+216 20 555 123',
        dateDebut: new Date('2023-09-01'),
        dateFin: new Date('2024-08-31'),
        loyerMensuel: 800,
        depot: 1600,
        statut: StatutContrat.ACTIF,
        paiements: []
      }
    ];

    const mockPaiements: PaiementLoyer[] = [
      {
        id: '1',
        contratId: '1',
        mois: '2024-01',
        montant: 800,
        datePaiement: new Date('2024-01-05'),
        statut: StatutPaiement.PAYE
      },
      {
        id: '2',
        contratId: '1',
        mois: '2024-02',
        montant: 800,
        statut: StatutPaiement.EN_ATTENTE
      }
    ];

    const mockMaintenances: MaintenanceImmobilier[] = [
      {
        id: '1',
        bienId: '1',
        type: TypeMaintenance.PREVENTIVE,
        description: 'Révision annuelle chaudière',
        cout: 150,
        date: new Date('2024-01-15'),
        prestataire: 'Chauffage Plus',
        statut: StatutMaintenance.TERMINEE
      },
      {
        id: '2',
        bienId: '2',
        type: TypeMaintenance.CORRECTIVE,
        description: 'Réparation fuite robinet',
        cout: 80,
        date: new Date('2024-01-20'),
        prestataire: 'Plomberie Express',
        statut: StatutMaintenance.PLANIFIEE
      }
    ];

    this.biensSubject.next(mockBiens);
    this.contratsSubject.next(mockContrats);
    this.paiementsSubject.next(mockPaiements);
    this.maintenancesSubject.next(mockMaintenances);
  }

  getBiens(): Observable<BienImmobilier[]> {
    return this.biens$;
  }

  getBienById(id: string): BienImmobilier | undefined {
    return this.biensSubject.value.find(b => b.id === id);
  }

  addBien(bien: Omit<BienImmobilier, 'id' | 'createdAt' | 'updatedAt' | 'contrats' | 'maintenances'>): void {
    const newBien: BienImmobilier = {
      ...bien,
      id: Date.now().toString(),
      contrats: [],
      maintenances: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const currentBiens = this.biensSubject.value;
    this.biensSubject.next([...currentBiens, newBien]);
  }

  updateBien(id: string, updates: Partial<BienImmobilier>): void {
    const currentBiens = this.biensSubject.value;
    const updatedBiens = currentBiens.map(bien => 
      bien.id === id 
        ? { ...bien, ...updates, updatedAt: new Date() }
        : bien
    );
    this.biensSubject.next(updatedBiens);
  }

  deleteBien(id: string): void {
    const currentBiens = this.biensSubject.value;
    const filteredBiens = currentBiens.filter(b => b.id !== id);
    this.biensSubject.next(filteredBiens);

    // Supprimer les contrats, paiements et maintenances associés
    const currentContrats = this.contratsSubject.value;
    const contratsToDelete = currentContrats.filter(c => c.bienId === id);
    const filteredContrats = currentContrats.filter(c => c.bienId !== id);
    this.contratsSubject.next(filteredContrats);

    // Supprimer les paiements des contrats supprimés
    const currentPaiements = this.paiementsSubject.value;
    const contratIds = contratsToDelete.map(c => c.id);
    const filteredPaiements = currentPaiements.filter(p => !contratIds.includes(p.contratId));
    this.paiementsSubject.next(filteredPaiements);

    const currentMaintenances = this.maintenancesSubject.value;
    const filteredMaintenances = currentMaintenances.filter(m => m.bienId !== id);
    this.maintenancesSubject.next(filteredMaintenances);
  }

  getContratsByBienId(bienId: string): ContratLocation[] {
    return this.contratsSubject.value.filter(c => c.bienId === bienId);
  }

  addContrat(contrat: Omit<ContratLocation, 'id' | 'paiements'>): void {
    const newContrat: ContratLocation = {
      ...contrat,
      id: Date.now().toString(),
      paiements: []
    };

    const currentContrats = this.contratsSubject.value;
    this.contratsSubject.next([...currentContrats, newContrat]);

    // Mettre à jour le statut du bien
    this.updateBien(contrat.bienId, { statut: StatutBien.LOUE });
  }

  getPaiementsByContratId(contratId: string): PaiementLoyer[] {
    return this.paiementsSubject.value.filter(p => p.contratId === contratId);
  }

  addPaiement(paiement: Omit<PaiementLoyer, 'id'>): void {
    const newPaiement: PaiementLoyer = {
      ...paiement,
      id: Date.now().toString()
    };

    const currentPaiements = this.paiementsSubject.value;
    this.paiementsSubject.next([...currentPaiements, newPaiement]);
  }

  getMaintenancesByBienId(bienId: string): MaintenanceImmobilier[] {
    return this.maintenancesSubject.value.filter(m => m.bienId === bienId);
  }

  addMaintenance(maintenance: Omit<MaintenanceImmobilier, 'id'>): void {
    const newMaintenance: MaintenanceImmobilier = {
      ...maintenance,
      id: Date.now().toString()
    };

    const currentMaintenances = this.maintenancesSubject.value;
    this.maintenancesSubject.next([...currentMaintenances, newMaintenance]);
  }

  getImmobilierStats(): ImmobilierStats {
    const biens = this.biensSubject.value;
    const contrats = this.contratsSubject.value;
    const paiements = this.paiementsSubject.value;
    const maintenances = this.maintenancesSubject.value;

    const biensLoues = biens.filter(b => b.statut === StatutBien.LOUE).length;
    const biensLibres = biens.filter(b => b.statut === StatutBien.LIBRE).length;

    const valeurPortefeuille = biens.reduce((total, b) => total + b.valeurActuelle, 0);
    
    const contratsActifs = contrats.filter(c => c.statut === StatutContrat.ACTIF);
    const revenus = contratsActifs.reduce((total, c) => total + c.loyerMensuel, 0) * 12;
    
    const charges = biens.reduce((total, b) => total + b.charges + b.taxeFonciere + b.assurance, 0);
    const coutMaintenances = maintenances.reduce((total, m) => total + m.cout, 0);
    const chargesTotal = charges + coutMaintenances;

    const rentabilite = valeurPortefeuille > 0 ? ((revenus - chargesTotal) / valeurPortefeuille) * 100 : 0;
    const tauxOccupation = biens.length > 0 ? (biensLoues / biens.length) * 100 : 0;

    return {
      totalBiens: biens.length,
      biensLoues,
      biensLibres,
      valeurPortefeuille,
      revenus,
      charges: chargesTotal,
      rentabilite: Math.round(rentabilite * 100) / 100,
      tauxOccupation: Math.round(tauxOccupation)
    };
  }

  generateReference(): string {
    const biens = this.biensSubject.value;
    const lastRef = biens.length > 0 
      ? Math.max(...biens.map(b => parseInt(b.reference.replace('IMM-', '')))) 
      : 0;
    return `IMM-${String(lastRef + 1).padStart(3, '0')}`;
  }
}