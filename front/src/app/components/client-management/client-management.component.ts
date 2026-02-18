import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Client } from '../../models/client/client.model';
import { ProjetClient } from '../../models/client/ProjetClient.model';
import { FactureClient } from '../../models/client/FactureClient.model';
import { PaiementClient } from '../../models/client/PaiementClient.model';
import { InteractionClient } from '../../models/client/InteractionClient.model';
import { ClientStats } from '../../models/client/ClientStats.model';
import { CanalCommunication, FrequenceContact, PrioriteClient, SecteurClient, StatutClient, StatutFacture, StatutProjetClient, TypeClient, TypeInteraction } from '../../models/client/enum.model';
import { ClientService } from '../../services/client_managment/client-service';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';

@Component({
  selector: 'app-client-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './client-management.component.html',
  styleUrls: ['./client-management.component.css']
})
export class ClientManagementComponent implements OnInit {
  clients: Client[] = [];
  filteredClients: Client[] = [];
  projets: ProjetClient[] = [];
  factures: FactureClient[] = [];
  paiements: PaiementClient[] = [];
  interactions: InteractionClient[] = [];
  stats: ClientStats = {
    totalClients: 0,
    clientsActifs: 0,
    prospects: 0,
    chiffreAffairesTotal: 0,
    facturenEnAttente: 0,
    tauxConversion: 0,
    valeurMoyenneClient: 0,
    interactionsRecentes: 0
  };

  currentView: 'dashboard' | 'clients' | 'projets' | 'factures' | 'details' | 'form' = 'dashboard';
  selectedClient: Client | null = null;
  isEditMode = false;
  searchTerm = '';
  searchTermProjet = '';
  searchTermFacture = '';
  filterClientId = '';
  filterStatutProjet = '';
  filterClientIdFacture = '';
  filterStatutFacture = '';
  filterMontantMin: number | null = null;
  filterMontantMax: number | null = null;
  filteredProjets: ProjetClient[] = [];
  filteredFactures: FactureClient[] = [];
  showClientForm = false;
  showInteractionForm = false;
  showFactureForm = false;
  activeTab: 'general' | 'projets' | 'factures' = 'general';
  isEditModeFacture = false;
  selectedFacture: FactureClient | null = null;

  // Propriétés pour les options des formulaires
  typesClient = Object.values(TypeClient);
  secteursClient = Object.values(SecteurClient);
  statutsClient = Object.values(StatutClient);
  prioritesClient = Object.values(PrioriteClient);
  statutsProjet = Object.values(StatutProjetClient);
  statutsFacture = Object.values(StatutFacture);
  typesInteraction = Object.values(TypeInteraction);
  canauxCommunication = Object.values(CanalCommunication);
  frequencesContact = Object.values(FrequenceContact);
  clientForm!: FormGroup;
  submitted: boolean = false
  constructor(
    private clientService: ClientService,
    private fb: FormBuilder,
    private router: Router
  ) { }

  goBackToMenu(): void {
    this.router.navigate(['/menu']);
  }

  closeClientForm(): void {
    this.showClientForm = false;
    this.isEditMode = false;
    this.selectedClient = null;
    this.clientFormData = {};
  }

  // Formulaires
  clientFormData: any = {};
  interactionFormData: any = {};
  factureFormData: any = {};

  ngOnInit(): void {
    this.initFormClient()
    this.loadClient();
    this.clientService.getClients().subscribe(clients => {
      this.clients = clients;
      this.filteredClients = clients;
      //this.stats = this.clientService.getClientStats();
    });

    // this.clientService.projets$.subscribe(projets => {
    //   this.projets = projets;
    // });

    // this.clientService.factures$.subscribe(factures => {
    //   this.factures = factures;
    // });

    // this.clientService.paiements$.subscribe(paiements => {
    //   this.paiements = paiements;
    // });

    // this.clientService.interactions$.subscribe(interactions => {
    //   this.interactions = interactions;
    // });

    // // Initialiser les listes filtrées
    // this.applyProjetFilters();
    // this.applyFactureFilters();
  }

  loadClient() {
    this.clientService.getClients().subscribe(clients => {
      this.clients = clients;
      this.filteredClients = clients;
      //this.stats = this.clientService.getClientStats();
    })
  }

  private initFormClient(): void {

    this.clientForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(2)]],
      prenom: [''],
      raisonSociale: [''],
      type: [TypeClient.PARTICULIER, Validators.required],
      secteur: [SecteurClient.SERVICES, Validators.required],
      statut: [StatutClient.PROSPECT, Validators.required],
      localisation: [''],
      contact: ['', Validators.required],
      identifiantFiscal: ['', [Validators.pattern(/^\d{6,9}[A-Za-z]{3}\d{3}$/) ]],
      rib: ['', [Validators.pattern(/^[0-9]{10,30}$/)]],
      pointsFidelite: [0, [Validators.min(0)]],
      adresse: [''],
      ville: [''],
      codePostal: ['', [Validators.pattern(/^[0-9]{4,10}$/)]],
      pays: ['Tunisie', Validators.required],
      telephone: ['', [Validators.pattern(/^[0-9]{8,15}$/)]],
      email: ['', [Validators.email]],
      siteWeb: ['', [Validators.pattern(/^(https?:\/\/)?([\w\-])+(\.[\w\-]+)+[/#?]?.*$/)]],
      chiffreAffaires: [0, [Validators.min(0)]],
      documents: this.fb.array([]),
      tags: this.fb.array([]),
      derniereInteraction: [new Date()],
    });
  }

  get searchTerm_() {
    return this.searchTerm;
  }

  set searchTerm_(value: string) {
    this.searchTerm = value;
    this.filterClients();
  }

  filterClients(): void {
    if (!this.searchTerm) {
      this.filteredClients = this.clients;
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredClients = this.clients.filter(client =>
        client.nom.toLowerCase().includes(term) ||
        (client.prenom && client.prenom.toLowerCase().includes(term)) ||
        (client.raisonSociale && client.raisonSociale.toLowerCase().includes(term)) ||
        client.email.toLowerCase().includes(term) ||
        client.secteur.toLowerCase().includes(term)
      );
    }
  }

  setView(view: 'dashboard' | 'clients' | 'projets' | 'factures' | 'details' | 'form'): void {
    this.currentView = view;
  }

  goBack(): void {
    this.currentView = 'clients';
    this.selectedClient = null;
  }

  viewClientDetails(client: Client): void {
    this.selectedClient = client;
    this.currentView = 'details';
  }

  showAddClientForm(): void {
    this.isEditMode = false;
    this.showClientForm = true;
    this.activeTab = 'general';
    this.clientFormData = {
      nom: '',
      prenom: '',
      raisonSociale: '',
      type: TypeClient.PARTICULIER,
      secteur: SecteurClient.SERVICES,
      statut: StatutClient.PROSPECT,
      priorite: PrioriteClient.NORMALE,
      localisation: '',
      contact: '',
      identifiantFiscal: '',
      rib: '',
      pointsFidelite: 0,
      siret: '',
      adresse: '',
      ville: '',
      codePostal: '',
      pays: 'Tunisie',
      telephone: '',
      email: '',
      siteWeb: '',
      chiffreAffaires: 0,
      projets: [],
      factures: [],
      preferences: {
        canalCommunication: CanalCommunication.EMAIL,
        frequenceContact: FrequenceContact.MENSUELLE,
        newsletter: false,
        promotions: false
      },
      documents: [],
      tags: [],
      dateCreation: new Date(),
      derniereInteraction: new Date()
    };
  }

  editClient(client: Client): void {
    this.isEditMode = true;
    this.selectedClient = client;
    this.showClientForm = true;
    this.activeTab = 'general';
    this.clientForm.patchValue({
      nom: client.nom || '',
      prenom: client.prenom || '',
      raisonSociale: client.raisonSociale || '',
      type: client.type || TypeClient.PARTICULIER,
      secteur: client.secteur || SecteurClient.SERVICES,
      statut: client.statut || StatutClient.PROSPECT,
      localisation: client.localisation || '',
      contact: client.contacts || '',
      identifiantFiscal: client.identifiantFiscal || '',
      rib: client.rib || '',
      pointsFidelite: client.pointsFidelite ?? 0,
      siret: client.siret || '',
      adresse: client.adresse || '',
      ville: client.ville || '',
      codePostal: client.codePostal || '',
      pays: client.pays || 'Tunisie',
      telephone: client.telephone || '',
      email: client.email || '',
      siteWeb: client.siteWeb || '',
      chiffreAffaires: client.chiffreAffaires ?? 0,
      dateCreation: this.formatDate(client.dateCreation),
      derniereInteraction: this.formatDate(client.derniereInteraction),
    });

    // 🔹 Si tu as des FormArray pour documents et tags, les remplir
    // this.documents.clear();
    // client.documents?.forEach(doc => this.documents.push(this.fb.control(doc)));

    // this.tags.clear();
    // client.tags?.forEach(tag => this.tags.push(this.fb.control(tag)));
  }

  // 🔹 Méthode utilitaire pour formater la date en string si besoin
  formatDate(date: Date | string | null): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toISOString().substring(0, 10); // format yyyy-MM-dd
  }

  // saveClient(): void {
  //   if (this.isEditMode && this.selectedClient) {
  //     this.clientService.updateClient(this.selectedClient.id, this.clientFormData);
  //   } else {
  //     this.clientService.addClient(this.clientFormData);
  //   }

  //   this.closeClientForm();
  // }

  saveClient(): void {
    this.submitted = true;

    // Validation du formulaire
    if (this.clientForm.invalid) {
      this.clientForm.markAllAsTouched();
      return;
    }

    // Récupérer les valeurs du formulaire
    const clientData: Partial<Client> = this.clientForm.getRawValue();

    if (this.isEditMode && this.selectedClient?.id) {
      // --- UPDATE ---
      this.clientService.updateClient(this.selectedClient.id, clientData).subscribe({
        next: () => {
          this.showToast('Client modifié avec succès', 'success');
          this.closeClientForm();
          this.loadClient();
          this.initFormClient();
          this.submitted = false;
        },
        error: () => this.showToast('Erreur lors de la modification', 'error')
      });
    } else {
      // --- ADD ---
      this.clientService.addClient(clientData).subscribe({
        next: () => {
          debugger
          this.showToast('Client ajouté avec succès', 'success');
          this.closeClientForm();
          this.loadClient();
          this.initFormClient();
          this.submitted = false;
        },
        error: () => this.showToast('Erreur lors de l\'ajout', 'error')
      });
    }
  }

  private showToast(message: string, icon: 'success' | 'error' | 'info' = 'info') {
    Swal.fire({
      toast: true,
      position: 'top-end',
      icon: icon,
      title: message,
      showConfirmButton: false,
      timer: 2000,
      timerProgressBar: true
    });
  }

  cancelForm(): void {
    this.closeClientForm();
  }

  deleteClient(id: string): void {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: "Cette action est irréversible !",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Oui, supprimer !',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        // 🔹 On s'abonne à l'Observable pour consommer le service
        this.clientService.deleteClient(id).subscribe({
          next: () => {
            Swal.fire(
              'Supprimé !',
              'Le client a été supprimé.',
              'success'
            );
            // 🔹 Ici, tu peux rafraîchir la liste si nécessaire
            this.loadClient();
          },
          error: (err) => {
            Swal.fire(
              'Erreur !',
              'Impossible de supprimer le client.',
              'error'
            );
            console.error(err);
          }
        });
      }
    });
  }


  addInteraction(client: Client): void {
    this.selectedClient = client;
    this.showAddInteractionForm();
  }

  onTypeChange(): void {
    // Réinitialiser certains champs selon le type
    if (this.clientFormData.type === TypeClient.PARTICULIER) {
      this.clientFormData.raisonSociale = '';
      this.clientFormData.siret = '';
    } else {
      this.clientFormData.prenom = '';
    }
  }

  // Méthodes utilitaires
  getClientDisplayName(client: Client): string {
    if (client.type === TypeClient.PARTICULIER) {
      return `${client.prenom || ''} ${client.nom}`.trim();
    } else {
      return client.raisonSociale || client.nom;
    }
  }

  getClientInitials(client: Client): string {
    if (client.type === TypeClient.PARTICULIER && client.prenom) {
      return `${client.prenom.charAt(0)}${client.nom.charAt(0)}`.toUpperCase();
    } else {
      const name = client.raisonSociale || client.nom;
      const words = name.split(' ');
      if (words.length >= 2) {
        return `${words[0].charAt(0)}${words[1].charAt(0)}`.toUpperCase();
      }
      return name.substring(0, 2).toUpperCase();
    }
  }

  getClientName(clientId: string): string {
    const client = this.clients.find(c => c.id === clientId);
    return client ? this.getClientDisplayName(client) : 'Client inconnu';
  }

  getClientProjets(clientId: string): ProjetClient[] {
    return [];//this.clientService.getProjetsByClientId(clientId);
  }

  getClientInteractions(clientId: string): InteractionClient[] {
    return []// this.clientService.getInteractionsByClientId(clientId);
  }

  setActiveTab(tab: 'general' | 'projets' | 'factures'): void {
    this.activeTab = tab;
  }

  showAddProjetForm(): void {
    // À implémenter - formulaire d'ajout de projet
    console.log('Ajouter un projet');
  }

  showAddFactureForm(): void {
    this.isEditModeFacture = false;
    this.selectedFacture = null;
    this.factureFormData = {
      numero: this.generateFactureNumber(),
      montant: 0,
      dateEmission: this.formatDateForInput(new Date()),
      dateEcheance: this.formatDateForInput(this.getDefaultEcheance()),
      statut: StatutFacture.BROUILLON,
      projetId: '',
      description: ''
    };
    this.showFactureForm = true;
  }

  editProjet(projet: ProjetClient): void {
    // À implémenter - modification de projet
    console.log('Modifier projet:', projet);
  }

  deleteProjet(projetId: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce projet ?')) {
      this.clientFormData.projets = this.clientFormData.projets.filter((p: any) => p.id !== projetId);
    }
  }

  editFacture(facture: FactureClient): void {
    this.isEditModeFacture = true;
    this.selectedFacture = facture;
    this.factureFormData = {
      numero: facture.numero,
      montant: facture.montant,
      dateEmission: this.formatDateForInput(facture.dateEmission),
      dateEcheance: this.formatDateForInput(facture.dateEcheance),
      statut: facture.statut,
      projetId: facture.projetId || '',
      description: ''
    };
    this.showFactureForm = true;
  }

  saveFacture(): void {
    const factureData: Omit<FactureClient, 'id'> = {
      clientId: this.selectedClient?.id || '',
      numero: this.factureFormData.numero,
      montant: this.factureFormData.montant,
      dateEmission: new Date(this.factureFormData.dateEmission),
      dateEcheance: new Date(this.factureFormData.dateEcheance),
      statut: this.factureFormData.statut,
      projetId: this.factureFormData.projetId || undefined
    };

    if (this.isEditModeFacture && this.selectedFacture) {
      // Mettre à jour la facture existante dans la liste
      if (!this.clientFormData.factures) {
        this.clientFormData.factures = [];
      }
      const index = this.clientFormData.factures.findIndex((f: any) => f.id === this.selectedFacture!.id);
      if (index !== -1) {
        this.clientFormData.factures[index] = { ...this.selectedFacture, ...factureData };
      }
    } else {
      // Ajouter nouvelle facture
      const newFacture = {
        id: Date.now().toString(),
        ...factureData
      };

      if (!this.clientFormData.factures) {
        this.clientFormData.factures = [];
      }
      this.clientFormData.factures.push(newFacture);
    }

    this.closeFactureForm();
  }

  closeFactureForm(): void {
    this.showFactureForm = false;
    this.isEditModeFacture = false;
    this.selectedFacture = null;
    this.factureFormData = {};
  }

  deleteFacture(factureId: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette facture ?')) {
      if (this.clientFormData.factures) {
        this.clientFormData.factures = this.clientFormData.factures.filter((f: any) => f.id !== factureId);
      }
    }
  }

  printFacture(facture: FactureClient): void {
    const printContent = this.generateFacturePrintContent(facture);
    const printWindow = window.open('', '', 'width=800,height=600');

    if (printWindow) {
      printWindow.document.write(`
        <html>
          <head>
            <title>Facture ${facture.numero}</title>
            <style>
              body { font-family: Arial, sans-serif; padding: 20px; }
              .facture-header { text-align: center; margin-bottom: 30px; border-bottom: 2px solid #333; padding-bottom: 20px; }
              .facture-info { display: flex; justify-content: space-between; margin-bottom: 30px; }
              .client-info, .facture-details { flex: 1; }
              .facture-details { text-align: right; }
              .montant-total { font-size: 24px; font-weight: bold; color: #10b981; margin-top: 20px; }
              .footer { margin-top: 50px; text-align: center; font-size: 12px; color: #666; }
            </style>
          </head>
          <body>
            ${printContent}
            <script>window.print(); window.close();</script>
          </body>
        </html>
      `);
      printWindow.document.close();
    }
  }

  private generateFacturePrintContent(facture: FactureClient): string {
    const client = this.selectedClient;
    if (!client) return '';

    return `
      <div class="facture-header">
        <h1>FACTURE</h1>
        <h2>${facture.numero}</h2>
      </div>
      
      <div class="facture-info">
        <div class="client-info">
          <h3>Client:</h3>
          <p><strong>${this.getClientDisplayName(client)}</strong></p>
          <p>${client.adresse}</p>
          <p>${client.ville}, ${client.codePostal}</p>
          <p>Tél: ${client.telephone}</p>
          <p>Email: ${client.email}</p>
        </div>
        
        <div class="facture-details">
          <h3>Facture:</h3>
          <p><strong>N°:</strong> ${facture.numero}</p>
          <p><strong>Date d'émission:</strong> ${new Date(facture.dateEmission).toLocaleDateString('fr-FR')}</p>
          <p><strong>Date d'échéance:</strong> ${new Date(facture.dateEcheance).toLocaleDateString('fr-FR')}</p>
          <p><strong>Statut:</strong> ${facture.statut}</p>
        </div>
      </div>
      
      <div class="montant-total">
        <p>Montant Total: ${facture.montant.toLocaleString('fr-FR')} DT</p>
      </div>
      
      <div class="footer">
        <p>SMS2i - Système de Management Intégré</p>
        <p>Facture générée le ${new Date().toLocaleDateString('fr-FR')}</p>
      </div>
    `;
  }

  showAddInteractionForm(): void {
    this.showInteractionForm = true;
    this.interactionFormData = {
      clientId: this.selectedClient?.id || '',
      type: TypeInteraction.EMAIL,
      sujet: '',
      description: '',
      date: this.formatDateForInput(new Date()),
      responsable: '',
      suiviRequis: false
    };
  }

  saveInteraction(): void {
    const interactionData: Omit<InteractionClient, 'id'> = {
      clientId: this.interactionFormData.clientId,
      type: this.interactionFormData.type,
      sujet: this.interactionFormData.sujet,
      description: this.interactionFormData.description,
      date: new Date(this.interactionFormData.date),
      responsable: this.interactionFormData.responsable,
      suiviRequis: this.interactionFormData.suiviRequis
    };

    // this.clientService.addInteraction(interactionData);
    this.closeInteractionForm();
  }

  closeInteractionForm(): void {
    this.showInteractionForm = false;
    this.interactionFormData = {};
  }

  applyProjetFilters(): void {
    let filtered = [...this.projets];

    // Filtre par terme de recherche
    if (this.searchTermProjet) {
      const term = this.searchTermProjet.toLowerCase();
      filtered = filtered.filter(projet =>
        projet.nom.toLowerCase().includes(term) ||
        projet.description.toLowerCase().includes(term)
      );
    }

    // Filtre par client
    if (this.filterClientId) {
      filtered = filtered.filter(projet => projet.clientId === this.filterClientId);
    }

    // Filtre par statut
    if (this.filterStatutProjet) {
      filtered = filtered.filter(projet => projet.statut === this.filterStatutProjet);
    }

    this.filteredProjets = filtered;
  }

  applyFactureFilters(): void {
    let filtered = [...this.factures];

    // Filtre par terme de recherche
    if (this.searchTermFacture) {
      const term = this.searchTermFacture.toLowerCase();
      filtered = filtered.filter(facture =>
        facture.numero.toLowerCase().includes(term)
      );
    }

    // Filtre par client
    if (this.filterClientIdFacture) {
      filtered = filtered.filter(facture => facture.clientId === this.filterClientIdFacture);
    }

    // Filtre par statut
    if (this.filterStatutFacture) {
      filtered = filtered.filter(facture => facture.statut === this.filterStatutFacture);
    }

    // Filtre par montant minimum
    if (this.filterMontantMin !== null) {
      filtered = filtered.filter(facture => facture.montant >= this.filterMontantMin!);
    }

    // Filtre par montant maximum
    if (this.filterMontantMax !== null) {
      filtered = filtered.filter(facture => facture.montant <= this.filterMontantMax!);
    }

    this.filteredFactures = filtered;
  }

  clearProjetFilters(): void {
    this.searchTermProjet = '';
    this.filterClientId = '';
    this.filterStatutProjet = '';
    this.applyProjetFilters();
  }

  clearFactureFilters(): void {
    this.searchTermFacture = '';
    this.filterClientIdFacture = '';
    this.filterStatutFacture = '';
    this.filterMontantMin = null;
    this.filterMontantMax = null;
    this.applyFactureFilters();
  }

  // Méthodes pour la gestion globale des projets
  showAddProjetGlobalForm(): void {
    // À implémenter - formulaire d'ajout de projet global
    console.log('Ajouter un projet global');
  }

  viewProjetDetails(projet: ProjetClient): void {
    // À implémenter - vue détaillée du projet
    console.log('Voir détails du projet:', projet);
  }

  editProjetGlobal(projet: ProjetClient): void {
    // À implémenter - modification de projet global
    console.log('Modifier projet global:', projet);
  }

  deleteProjetGlobal(projetId: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce projet ?')) {
      //  this.clientService.deleteProjet(projetId);
      this.applyProjetFilters();
    }
  }

  // Méthodes pour la gestion globale des factures
  showAddFactureGlobalForm(): void {
    // À implémenter - formulaire d'ajout de facture globale
    console.log('Ajouter une facture globale');
  }

  viewFactureDetails(facture: FactureClient): void {
    // À implémenter - vue détaillée de la facture
    console.log('Voir détails de la facture:', facture);
  }

  editFactureGlobal(facture: FactureClient): void {
    // À implémenter - modification de facture globale
    console.log('Modifier facture globale:', facture);
  }

  deleteFactureGlobal(factureId: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette facture ?')) {
      //this.clientService.deleteFacture(factureId);
      this.applyFactureFilters();
    }
  }

  markFacturePaid(facture: FactureClient): void {
    if (confirm('Marquer cette facture comme payée ?')) {
      //this.clientService.updateFacture(facture.id, { statut: StatutFacture.PAYEE });
      this.applyFactureFilters();
    }
  }

  isFactureOverdue(facture: FactureClient): boolean {
    const today = new Date();
    const echeance = new Date(facture.dateEcheance);
    return echeance < today && facture.statut !== StatutFacture.PAYEE;
  }

  getProjetName(projetId: string | undefined): string {
    if (!projetId) return 'Aucun projet';
    const projet = this.projets.find(p => p.id === projetId);
    return projet ? projet.nom : 'Projet inconnu';
  }
  private generateFactureNumber(): string {
    const year = new Date().getFullYear();
    const allFactures = this.factures;
    const currentYearFactures = allFactures.filter(f =>
      new Date(f.dateEmission).getFullYear() === year
    );
    const nextNumber = currentYearFactures.length + 1;
    return `FACT-${year}-${String(nextNumber).padStart(3, '0')}`;
  }

  private getDefaultEcheance(): Date {
    const date = new Date();
    date.setDate(date.getDate() + 30); // 30 jours par défaut
    return date;
  }

  private formatDateForInput(date: Date): string {
    return new Date(date).toISOString().split('T')[0];
  }

  getFactureStatusClass(statut: StatutFacture): string {
    return statut.toLowerCase().replace(/[^a-z]/g, '-');
  }

  private extractCityFromLocation(location: string): string {
    // Extraction simple de la ville depuis la localisation
    const parts = location.split(',');
    return parts.length > 1 ? parts[parts.length - 1].trim() : 'Tunis';
  }

  getRecentInteractions(): InteractionClient[] {
    return this.interactions
      .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
      .slice(0, 5);
  }

  getInteractionIcon(type: TypeInteraction): string {
    const icons: { [key: string]: string } = {
      'Appel': '📞',
      'Email': '📧',
      'Réunion': '🤝',
      'Visite': '🏢',
      'Support': '🛠️',
      'Autre': '💬'
    };
    return icons[type] || '💬';
  }

  getTypeClass(type: TypeClient): string {
    return type.toLowerCase().replace(/[^a-z]/g, '');
  }



  getStatusClass(statut: StatutClient): string {
    return statut.toLowerCase().replace(/[^a-z]/g, '');
  }

  getProjetStatusClass(statut: StatutProjetClient): string {
    return statut.toLowerCase().replace(/[^a-z]/g, '-');
  }

  getViewTitle(): string {
    switch (this.currentView) {
      case 'projets': return 'Projets Clients';
      case 'factures': return 'Facturation';
      default: return 'Gestion Clients';
    }
  }

  getViewFeatures(): string[] {
    return [];
  }
}