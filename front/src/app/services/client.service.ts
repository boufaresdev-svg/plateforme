import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { 
  Client, 
  
} from '../models/client/client.model';
import { ProjetClient } from '../models/client/ProjetClient.model';
import { FactureClient } from '../models/client/FactureClient.model';
import { PaiementClient } from '../models/client/PaiementClient.model';
import { InteractionClient } from '../models/client/InteractionClient.model';
import { CanalCommunication, FrequenceContact, MethodePaiement, PrioriteClient, SecteurClient, StatutClient, StatutFacture, StatutProjetClient, TypeClient, TypeInteraction } from '../models/client/enum.model';
import { ClientStats } from '../models/client/ClientStats.model';

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private clientsSubject = new BehaviorSubject<Client[]>([]);
  public clients$ = this.clientsSubject.asObservable();

  private projetsSubject = new BehaviorSubject<ProjetClient[]>([]);
  public projets$ = this.projetsSubject.asObservable();

  private facturesSubject = new BehaviorSubject<FactureClient[]>([]);
  public factures$ = this.facturesSubject.asObservable();

  private paiementsSubject = new BehaviorSubject<PaiementClient[]>([]);
  public paiements$ = this.paiementsSubject.asObservable();

  private interactionsSubject = new BehaviorSubject<InteractionClient[]>([]);
  public interactions$ = this.interactionsSubject.asObservable();

  constructor() {
    this.loadMockData();
  }

  private loadMockData(): void {
    const mockClients: Client[] = [
      {
        id: '1',
        nom: 'TechCorp',
        raisonSociale: 'TechCorp Solutions SARL',
        type: TypeClient.ENTREPRISE,
        secteur: SecteurClient.INFORMATIQUE,
        statut: StatutClient.ACTIF,
        priorite: PrioriteClient.HAUTE,
        siret: '12345678901234',
        adresse: '123 Avenue des Technologies',
        ville: 'Tunis',
        codePostal: '1000',
        pays: 'Tunisie',
        localisation: '123 Avenue des Technologies, Tunis 1000',
        identifiantFiscal: '1234567A',
        rib: '08 006 0123456789012345 67',
        pointsFidelite: 150,
        telephone: '+216 71 123 456',
        email: 'contact@techcorp.tn',
        siteWeb: 'https://techcorp.tn',
        contactPrincipal: {
          id: '1',
          nom: 'Gharbi',
          prenom: 'Mohamed',
          poste: 'Directeur IT',
          telephone: '+216 20 123 456',
          email: 'mohamed.gharbi@techcorp.tn',
          principal: true
        },
        contacts: [],
        projets: [],
        factures: [],
        paiements: [],
        interactions: [],
        preferences: {
          canalCommunication: CanalCommunication.EMAIL,
          frequenceContact: FrequenceContact.MENSUELLE,
          newsletter: true,
          promotions: true
        },
        documents: ['contrat_cadre.pdf', 'kbis.pdf'],
        tags: ['informatique', 'prioritaire', 'grand_compte'],
        dateCreation: new Date('2023-03-15'),
        derniereInteraction: new Date('2024-01-20'),
        chiffreAffaires: 45000,
        createdAt: new Date('2023-03-15'),
        updatedAt: new Date('2024-01-20')
      },
      {
        id: '2',
        nom: 'Ben Ahmed',
        prenom: 'Fatma',
        type: TypeClient.PARTICULIER,
        secteur: SecteurClient.SERVICES,
        statut: StatutClient.ACTIF,
        priorite: PrioriteClient.NORMALE,
        adresse: '456 Rue de la République',
        ville: 'Sfax',
        codePostal: '3000',
        pays: 'Tunisie',
        localisation: '456 Rue de la République, Sfax 3000',
        identifiantFiscal: '9876543B',
        rib: '08 006 9876543210987654 32',
        pointsFidelite: 75,
        telephone: '+216 74 987 654',
        email: 'fatma.benahmed@email.com',
        contacts: [],
        projets: [],
        factures: [],
        paiements: [],
        interactions: [],
        preferences: {
          canalCommunication: CanalCommunication.TELEPHONE,
          frequenceContact: FrequenceContact.TRIMESTRIELLE,
          newsletter: false,
          promotions: true
        },
        documents: ['cin.pdf'],
        tags: ['particulier', 'fidele'],
        dateCreation: new Date('2023-08-20'),
        derniereInteraction: new Date('2024-01-15'),
        chiffreAffaires: 3500,
        createdAt: new Date('2023-08-20'),
        updatedAt: new Date('2024-01-15')
      }
    ];

    const mockProjets: ProjetClient[] = [
      {
        id: '1',
        clientId: '1',
        nom: 'Refonte Site Web',
        description: 'Refonte complète du site web corporate',
        montant: 25000,
        dateDebut: new Date('2024-01-15'),
        dateFin: new Date('2024-03-15'),
        statut: StatutProjetClient.EN_COURS
      },
      {
        id: '2',
        clientId: '1',
        nom: 'Formation équipe',
        description: 'Formation Angular pour l\'équipe développement',
        montant: 8000,
        dateDebut: new Date('2024-02-01'),
        statut: StatutProjetClient.DEVIS
      }
    ];

    const mockFactures: FactureClient[] = [
      {
        id: '1',
        clientId: '1',
        numero: 'FACT-2024-001',
        montant: 12500,
        dateEmission: new Date('2024-01-15'),
        dateEcheance: new Date('2024-02-15'),
        statut: StatutFacture.PAYEE,
        projetId: '1'
      },
      {
        id: '2',
        clientId: '2',
        numero: 'FACT-2024-002',
        montant: 3500,
        dateEmission: new Date('2024-01-20'),
        dateEcheance: new Date('2024-02-20'),
        statut: StatutFacture.ENVOYEE
      }
    ];

    const mockPaiements: PaiementClient[] = [
      {
        id: '1',
        clientId: '1',
        factureId: '1',
        montant: 12500,
        datePaiement: new Date('2024-02-10'),
        methode: MethodePaiement.VIREMENT
      }
    ];

    const mockInteractions: InteractionClient[] = [
      {
        id: '1',
        clientId: '1',
        type: TypeInteraction.REUNION,
        sujet: 'Réunion de lancement projet',
        description: 'Définition des besoins et planning',
        date: new Date('2024-01-10'),
        responsable: 'Mohamed Ben Ahmed',
        suiviRequis: true,
        dateSuivi: new Date('2024-01-20')
      },
      {
        id: '2',
        clientId: '2',
        type: TypeInteraction.APPEL,
        sujet: 'Suivi satisfaction',
        description: 'Appel de suivi post-livraison',
        date: new Date('2024-01-15'),
        responsable: 'Fatma Trabelsi',
        suiviRequis: false
      }
    ];

    this.clientsSubject.next(mockClients);
    this.projetsSubject.next(mockProjets);
    this.facturesSubject.next(mockFactures);
    this.paiementsSubject.next(mockPaiements);
    this.interactionsSubject.next(mockInteractions);
  }

  getClients(): Observable<Client[]> {
    return this.clients$;
  }

  getClientById(id: string): Client | undefined {
    return this.clientsSubject.value.find(c => c.id === id);
  }

  addClient(client: Omit<Client, 'id' | 'createdAt' | 'updatedAt' | 'contacts' | 'projets' | 'factures' | 'paiements' | 'interactions'>): void {
    const newClient: Client = {
      ...client,
      id: Date.now().toString(),
      adresse: client.localisation,
      ville: this.extractCityFromLocation(client.localisation),
      codePostal: '1000',
      pays: 'Tunisie',
      chiffreAffaires: 0,
      contacts: [],
      projets: [],
      factures: [],
      paiements: [],
      interactions: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const currentClients = this.clientsSubject.value;
    this.clientsSubject.next([...currentClients, newClient]);
  }

  private extractCityFromLocation(location: string): string {
    const parts = location.split(',');
    return parts.length > 1 ? parts[parts.length - 1].trim() : 'Tunis';
  }

  updateClient(id: string, updates: Partial<Client>): void {
    const currentClients = this.clientsSubject.value;
    const updatedClients = currentClients.map(client => 
      client.id === id 
        ? { ...client, ...updates, updatedAt: new Date() }
        : client
    );
    this.clientsSubject.next(updatedClients);
  }

  deleteClient(id: string): void {
    const currentClients = this.clientsSubject.value;
    const filteredClients = currentClients.filter(c => c.id !== id);
    this.clientsSubject.next(filteredClients);

    // Supprimer les éléments associés
    const currentProjets = this.projetsSubject.value;
    const filteredProjets = currentProjets.filter(p => p.clientId !== id);
    this.projetsSubject.next(filteredProjets);

    const currentFactures = this.facturesSubject.value;
    const filteredFactures = currentFactures.filter(f => f.clientId !== id);
    this.facturesSubject.next(filteredFactures);

    const currentPaiements = this.paiementsSubject.value;
    const filteredPaiements = currentPaiements.filter(p => p.clientId !== id);
    this.paiementsSubject.next(filteredPaiements);

    const currentInteractions = this.interactionsSubject.value;
    const filteredInteractions = currentInteractions.filter(i => i.clientId !== id);
    this.interactionsSubject.next(filteredInteractions);
  }

  getProjetsByClientId(clientId: string): ProjetClient[] {
    return this.projetsSubject.value.filter(p => p.clientId === clientId);
  }

  addProjet(projet: Omit<ProjetClient, 'id'>): void {
    const newProjet: ProjetClient = {
      ...projet,
      id: Date.now().toString()
    };

    const currentProjets = this.projetsSubject.value;
    this.projetsSubject.next([...currentProjets, newProjet]);
  }

  deleteProjet(id: string): void {
    const currentProjets = this.projetsSubject.value;
    const filteredProjets = currentProjets.filter(p => p.id !== id);
    this.projetsSubject.next(filteredProjets);
  }

  getFacturesByClientId(clientId: string): FactureClient[] {
    return this.facturesSubject.value.filter(f => f.clientId === clientId);
  }

  addFacture(facture: Omit<FactureClient, 'id'>): void {
    const newFacture: FactureClient = {
      ...facture,
      id: Date.now().toString()
    };

    const currentFactures = this.facturesSubject.value;
    this.facturesSubject.next([...currentFactures, newFacture]);
  }

  updateFacture(id: string, updates: Partial<FactureClient>): void {
    const currentFactures = this.facturesSubject.value;
    const updatedFactures = currentFactures.map(facture => 
      facture.id === id ? { ...facture, ...updates } : facture
    );
    this.facturesSubject.next(updatedFactures);
  }

  deleteFacture(id: string): void {
    const currentFactures = this.facturesSubject.value;
    const filteredFactures = currentFactures.filter(f => f.id !== id);
    this.facturesSubject.next(filteredFactures);

    // Supprimer les paiements associés à cette facture
    const currentPaiements = this.paiementsSubject.value;
    const filteredPaiements = currentPaiements.filter(p => p.factureId !== id);
    this.paiementsSubject.next(filteredPaiements);
  }

  getPaiementsByClientId(clientId: string): PaiementClient[] {
    return this.paiementsSubject.value.filter(p => p.clientId === clientId);
  }

  addPaiement(paiement: Omit<PaiementClient, 'id'>): void {
    const newPaiement: PaiementClient = {
      ...paiement,
      id: Date.now().toString()
    };

    const currentPaiements = this.paiementsSubject.value;
    this.paiementsSubject.next([...currentPaiements, newPaiement]);
  }

  getInteractionsByClientId(clientId: string): InteractionClient[] {
    return this.interactionsSubject.value.filter(i => i.clientId === clientId);
  }

  addInteraction(interaction: Omit<InteractionClient, 'id'>): void {
    const newInteraction: InteractionClient = {
      ...interaction,
      id: Date.now().toString()
    };

    const currentInteractions = this.interactionsSubject.value;
    this.interactionsSubject.next([...currentInteractions, newInteraction]);

    // Mettre à jour la dernière interaction du client
    this.updateClient(interaction.clientId, {
      derniereInteraction: interaction.date
    });
  }

  getClientStats(): ClientStats {
    const clients = this.clientsSubject.value;
    const projets = this.projetsSubject.value;
    const factures = this.facturesSubject.value;
    const interactions = this.interactionsSubject.value;

    const clientsActifs = clients.filter(c => c.statut === StatutClient.ACTIF).length;
    const prospects = clients.filter(c => c.statut === StatutClient.PROSPECT).length;

    const chiffreAffairesTotal = clients.reduce((total, c) => total + c.chiffreAffaires, 0);
    const facturesEnAttente = factures.filter(f => 
      f.statut === StatutFacture.ENVOYEE || f.statut === StatutFacture.EN_RETARD
    ).length;

    const projetsConverts = projets.filter(p => 
      p.statut === StatutProjetClient.ACCEPTE || p.statut === StatutProjetClient.EN_COURS
    ).length;
    const totalProjets = projets.length;
    const tauxConversion = totalProjets > 0 ? (projetsConverts / totalProjets) * 100 : 0;

    const valeurMoyenneClient = clients.length > 0 ? chiffreAffairesTotal / clients.length : 0;

    const lastMonth = new Date();
    lastMonth.setMonth(lastMonth.getMonth() - 1);
    const interactionsRecentes = interactions.filter(i => i.date >= lastMonth).length;

    return {
      totalClients: clients.length,
      clientsActifs,
      prospects,
      chiffreAffairesTotal,
      facturenEnAttente: facturesEnAttente,
      tauxConversion: Math.round(tauxConversion),
      valeurMoyenneClient: Math.round(valeurMoyenneClient),
      interactionsRecentes
    };
  }
}