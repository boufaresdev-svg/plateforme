import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CarteGazoilService } from '../../services/vehicule-managment/carte-gazoil.service';
import Swal from 'sweetalert2';
import { VehicleService } from '../../services/vehicule-managment/vehicle.service';
import { Vehicle } from '../../models/vehicule-mangment/vehicle.model';
import { CarteGazoil } from '../../models/vehicule-mangment/carte-gazoil.model';
import { TransactionCarburant } from '../../models/vehicule-mangment/transaction.model';
import { CarteGazoilStats, FournisseurCarburant, FournisseurTelepeage, StatutCarte, TypeCarburant, TypeReparation, VehicleStats } from '../../models/vehicule-mangment/enum.model';
import { Reparation } from '../../models/vehicule-mangment/reparation.model';
import { TransactionService } from '../../services/vehicule-managment/transaction.service';
import { ReparationService } from '../../services/vehicule-managment/reparation.service';
import { CarteTelepeage } from '../../models/vehicule-mangment/CarteTelepeage.model';
import { CarteTelepeageService } from '../../services/vehicule-managment/carte-telepeage';
import { PrixCarburantService } from '../../services/vehicule-managment/prix-carburant';
import { PrixCarburant } from '../../models/vehicule-mangment/PrixCarburant.model';
import { Router } from '@angular/router';


@Component({
  selector: 'app-vehicle-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './vehicle-management.component.html',
  styleUrls: ['./vehicle-management.component.css']
})
export class VehicleManagementComponent implements OnInit {
  vehicles: Vehicle[] = [];
  filteredVehicles: Vehicle[] = [];
  cartes: CarteGazoil[] = [];
  filteredCartes: CarteGazoil[] = [];
  transactions: TransactionCarburant[] = [];
  reparations: Reparation[] = [];

  vehicleStats: VehicleStats = {
    totalVehicles: 0,
    vehiculesEnPanne: 0,
    prochainVidangeKm: 0,
    visiteTechniqueExpire: 0,
    assuranceExpire: 0,
    taxeExpire: 0,
    totalReparations: 0,
    coutTotalReparations: 0
  };

  currentView: | 'dashboard' | 'vehicles' | 'cartes' | 'reparations' | 'transactions' | 'details' | 'carte-details' | 'telepeage' | 'telepeage-details' = 'dashboard';
  selectedVehicle: Vehicle | null = null;
  selectedCarte: CarteGazoil | null = null;
  searchTerm = '';
  searchTermCarte = '';

  // Filtres pour réparations
  filteredReparations: Reparation[] = [];
  filterVehicleId = '';
  filterTypeReparation = '';
  filterPrixMin: number | null = null;
  filterPrixMax: number | null = null;

  // Filtres pour transactions
  filteredTransactions: TransactionCarburant[] = [];
  filterVehicleIdTransaction = '';
  filterConducteur = '';
  filterMontantMin: number | null = null;
  filterMontantMax: number | null = null;

  // Formulaires et popups
  showVehicleForm = false;
  showCarteForm = false;
  showTransactionForm = false;
  showReparationForm = false;
  isEditMode = false;
  isEditModeCard = false;
  showPrixForm = false;


  vehicleFormData: any = {};
  carteFormData: any = {};
  transactionFormData: any = {};
  reparationFormData: any = {};
  vehicleForm!: FormGroup;
  submitted = false;
  submittedTransaction = false;
  submittedReparation = false;
  carteForm!: FormGroup;
  prixForm!: FormGroup;
  reparationForm!: FormGroup;
  transactionForm!: FormGroup;
  isEditModeReparation = false;
  selectedReparation: Reparation | null = null;
  isEditModeTransaction: boolean = false;
  selectedTransaction: TransactionCarburant | null = null;
  // Énumérations
  typesReparation = Object.values(TypeReparation);
  fournisseursCarburant = Object.values(FournisseurCarburant);
  typesCarburant = Object.values(TypeCarburant);
  fournisseursTelepeage = Object.values(FournisseurTelepeage);

  nb_visite_expirees: number = 0;
  nb_assurance_expirees: number = 0;
  nb_taxe_expirees: number = 0;

  telepeageForm!: FormGroup;
  submittedTelepeage = false;
  isEditModeTelepeage = false;
  selectedTelepeage: CarteTelepeage | null = null;
  showTelepeageForm = false;
  telepeages: CarteTelepeage[] = [];
  PrixCarburant !: PrixCarburant
  filteredTelepeages: CarteTelepeage[] = [];

  submittedCard = false;
  searchTermTelepeage: string = '';

  constructor(
    private fb: FormBuilder,
    private vehicleService: VehicleService,
    private carteGazoilService: CarteGazoilService,
    private transactionService: TransactionService,
    private reparationService: ReparationService,
    private carteTelepeageService: CarteTelepeageService,
    private prixCarbuarntService: PrixCarburantService,
    private router: Router
  ) { }

  goBackToMenu(): void {
    this.router.navigate(['/menu']);
  }

  ngOnInit(): void {
    this.initFormVéhicule();
    this.initFormCarte();
    this.initFormReparation();
    this.initFormTransaction();
    this.initFormPrixCarburant();
    this.loadVehicles();
    this.LoadCartes();
    this.loadReparations();
    this.loadTransactions();
    this.loadTelepeages();
    this.loadPrix_carburant()
    this.applyTransactionFilters();

  }
  /*******************************************************          //   Get Data  //                      ************************************************/

  loadVehicles() {
    this.vehicleService.getVehicles().subscribe(vehicles => {
      this.vehicles = vehicles;
      this.filteredVehicles = vehicles;
      this.calcule_papier_experie()
    });
  }

  loadReparations() {
    this.reparationService.getReparations().subscribe(reparations => {
      this.reparations = reparations;
      this.applyReparationFilters();
    });
  }

  loadTransactions() {
    this.transactionService.getTransactions().subscribe(transactions => {
      this.transactions = transactions;
      this.filteredTransactions = transactions
    });
  }

  LoadCartes() {
    this.carteGazoilService.getCartes().subscribe(cartes => {
      this.cartes = cartes;
      this.filteredCartes = cartes;
    });
  }

  loadTelepeages(): void {
    this.carteTelepeageService.getCartes().subscribe(telepeages => {
      this.telepeages = telepeages;
      this.filteredTelepeages = telepeages;
    });
  }

  loadPrix_carburant(): void {
    this.prixCarbuarntService.get().subscribe((p: any) => {
      if (p) {
        this.PrixCarburant = {
          id: p.id ?? '',
          essence: p.essence ?? 0,
          gasoil: p.gasoil ?? 0,
          gasoil_50: p.gasoil_50 ?? 0,
          gpl: p.gpl ?? 0
        };
        this.prixForm.patchValue({
          essence: this.PrixCarburant.essence,
          gasoil: this.PrixCarburant.gasoil,
          gasoil50: this.PrixCarburant.gasoil_50,
          gpl: this.PrixCarburant.gpl
        });
      } else {

        this.PrixCarburant = { id: '', essence: 0, gasoil: 0, gasoil_50: 0, gpl: 0 };
      }
    });
  }

  calcule_papier_experie() {

    const today = new Date();
    this.vehicles.forEach(vehicle => {
      const joursVisite = Math.ceil((new Date(vehicle.dateVisiteTechnique).getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
      if (joursVisite > 0 && joursVisite <= 30) {
        this.nb_visite_expirees++;
      }

      const joursAssurance = Math.ceil((new Date(vehicle.dateAssurance).getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
      if (joursAssurance > 0 && joursAssurance <= 30) {
        this.nb_assurance_expirees++;
      }

      const joursTaxe = Math.ceil((new Date(vehicle.dateTaxe).getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
      if (joursTaxe > 0 && joursTaxe <= 30) {
        this.nb_taxe_expirees++;
      }
    });

  }

  setView(
    view: | 'dashboard' | 'vehicles' | 'cartes' | 'reparations' | 'transactions' | 'details' | 'carte-details' | 'telepeage' | 'telepeage-details'): void {
    this.currentView = view;
    this.selectedVehicle = null;
    this.selectedCarte = null;
  }

  goBack(): void {
    this.currentView = 'vehicles';
    this.selectedVehicle = null;
  }

  goBackToCartes(): void {
    this.currentView = 'cartes';
    this.selectedCarte = null;
  }

  goBackToTelepeages(): void {
    this.currentView = 'telepeage';
    this.selectedTelepeage = null;
  }

  get searchTerm_() {
    return this.searchTerm;
  }

  set searchTerm_(value: string) {
    this.searchTerm = value;
    this.filterVehicles();
  }

  get searchTermCarte_() {
    return this.searchTermCarte;
  }

  set searchTermCarte_(value: string) {
    this.searchTermCarte = value;
    this.filterCartes();
  }

  /*******************************************************          //   Gestion des véhicules  //                      ************************************************/

  private initFormVéhicule(): void {
    this.vehicleForm = this.fb.group({
      serie: ['', [Validators.pattern(/.../)]],
      marque: ['', Validators.required],
      kmActuel: [0, Validators.min(0)],
      prochainVidangeKm: [0, Validators.min(0)],
      prochaineChaineKm: [0, Validators.min(0)],
      dateVisiteTechnique: ['', Validators.required],
      dateAssurance: ['', Validators.required],
      dateTaxe: ['', Validators.required],
      dateChangementBatterie: [''],
      consommation100km: [0, Validators.min(4)]
    });
  }

  viewVehicleDetails(vehicle: Vehicle): void {
    this.selectedVehicle = vehicle;
    this.currentView = 'details';
  }

  showAddVehicleForm(): void {
    this.isEditMode = false;
    this.selectedVehicle = null;
    this.vehicleForm.reset({
      serie: '',
      marque: '',
      kmActuel: 0,
      prochainVidangeKm: 0,
      dateVisiteTechnique: '',
      dateAssurance: '',
      dateTaxe: ''
    });
    this.showVehicleForm = true;
  }

  editVehicle(vehicle: Vehicle): void {
    this.isEditMode = true;
    this.selectedVehicle = vehicle;
    this.vehicleForm.setValue({
      serie: vehicle.serie,
      marque: vehicle.marque,
      kmActuel: vehicle.kmActuel,
      prochainVidangeKm: vehicle.prochainVidangeKm ?? 0,
      prochaineChaineKm: vehicle.prochaineChaineKm ?? 0,
      dateVisiteTechnique: this.formatDate(vehicle.dateVisiteTechnique),
      dateAssurance: this.formatDate(vehicle.dateAssurance),
      dateTaxe: this.formatDate(vehicle.dateTaxe),
      dateChangementBatterie: this.formatDate(vehicle.dateChangementBatterie),
      consommation100km: vehicle.consommation100km

    });
    this.showVehicleForm = true;
  }

  saveVehicle(): void {
    this.submitted = true;

    if (this.vehicleForm.invalid) {
      return; // ne pas soumettre si formulaire invalide
    }
    const formValue = this.vehicleForm.value;
    const vehicleData: Partial<Vehicle> = {
      ...formValue,
      dateVisiteTechnique: new Date(formValue.dateVisiteTechnique),
      dateAssurance: new Date(formValue.dateAssurance),
      dateTaxe: new Date(formValue.dateTaxe)
    };

    if (this.isEditMode && this.selectedVehicle) {
      this.vehicleService.updateVehicle(this.selectedVehicle.id, vehicleData).subscribe(() => {
        this.showToast('Véhicule modifié avec succès', 'success');
        this.closeVehicleForm();
        this.loadVehicles();
        this.initFormVéhicule();
        this.submitted = false
      });
    } else {
      this.vehicleService.addVehicle(vehicleData).subscribe(() => {
        this.showToast('Véhicule ajouté avec succès', 'success');
        this.closeVehicleForm();
        this.loadVehicles();
        this.initFormVéhicule();
        this.submitted = false
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

  closeVehicleForm(): void {
    this.showVehicleForm = false;
    this.selectedVehicle = null;
    this.isEditMode = false;
  }

  private formatDate(date: Date): string {
    if (!date) return ''; // si date est null ou undefined, retourne une chaîne vide
    return new Date(date).toISOString().split('T')[0]; // format yyyy-mm-dd pour input type=date
  }

  deleteVehicle(id: string): void {
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
        this.vehicleService.deleteVehicle(id).subscribe({
          next: () => {
            Swal.fire(
              'Supprimé !',
              'Le véhicule a été supprimé.',
              'success'
            );
            // 🔹 Ici, tu peux rafraîchir la liste si nécessaire
            this.loadVehicles();
          },
          error: (err) => {
            Swal.fire(
              'Erreur !',
              'Impossible de supprimer le véhicule.',
              'error'
            );
            console.error(err);
          }
        });
      }
    });
  }

  filterVehicles(): void {

    if (!this.searchTerm) {
      this.filteredVehicles = this.vehicles;
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredVehicles = this.vehicles.filter(vehicle =>
        vehicle.marque.toLowerCase().includes(term) ||
        vehicle.serie.toLowerCase().includes(term)
      );
    }
  }

  printVehicle() {
    const content = document.getElementById('print-vehicule')?.innerHTML;
    if (!content) return;

    const printWindow = window.open('', '', 'width=1000,height=800');
    if (printWindow) {
      printWindow.document.write(`
      <html>
        <head>
          <title>Impression Détails Véhicule</title>
          <style>
            body { font-family: Arial, sans-serif; padding: 20px; background: #fff; }
            h1, h2, h3 { margin: 10px 0; }
            .details-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
            .vehicle-title h1 { margin: 0; font-size: 24px; }
            .vehicle-serie-large { font-size: 18px; color: #666; }
            .details-grid { display: flex; gap: 20px; margin-bottom: 20px; }
            .info-card, .echeances-card {
              flex: 1; padding: 15px; border: 1px solid #ccc; border-radius: 8px;
            }
            .info-list, .echeances-list { margin-top: 10px; }
            .info-item, .echeance-item { display: flex; justify-content: space-between; margin: 6px 0; }
            .info-label { font-weight: bold; color: #333; }
            .info-value { color: #555; }
            .highlight { color: #007BFF; font-weight: bold; }
            .warning { color: red; font-weight: bold; }
            .echeance-icon { font-size: 18px; margin-right: 8px; }
            .echeance-content { flex: 1; }
            .recent-section { margin-top: 30px; }
            .section-header h2 { margin-bottom: 10px; }
            .modern-table { width: 100%; border: 1px solid #ddd; border-collapse: collapse; }
            .table-row { display: flex; border-bottom: 1px solid #ddd; }
            .table-row.header { background: #f5f5f5; font-weight: bold; }
            .table-cell { flex: 1; padding: 8px; text-align: left; }
            .empty-state-table { text-align: center; color: gray; padding: 20px; }
          </style>
        </head>
        <body>
          ${content}
          <script>
            window.print();
          </script>
        </body>
      </html>
    `);
      printWindow.document.close();
    }
  }

  exportSelectedVehicle(vehicle: Vehicle) {
    this.vehicleService.exportSelectedVehicle(vehicle);
  }


  /*******************************************************          //   Gestion des cartes gazoil  //                      ************************************************/

  private initFormCarte(): void {
    this.carteForm = this.fb.group({
      numero: [{ value: '', disabled: this.isEditModeCard }, Validators.required],
      fournisseur: ['', Validators.required],
      solde: [0, [Validators.required, Validators.min(0)]],
      consomation: [0],
      dateEmission: ['', Validators.required],
    });
  }

  viewCarteDetails(carte: CarteGazoil): void {
    this.selectedCarte = carte;
    this.currentView = 'carte-details';
  }

  showAddCarteForm(): void {
    this.isEditModeCard = false;
    this.carteFormData = {
      numero: '',
      fournisseur: FournisseurCarburant.AGIL,
      solde: 0,
      consomation: 0,
      dateEmission: this.formatDateForInput(new Date()),
    };
    this.showCarteForm = true;
  }

  editCarte(carte: CarteGazoil): void {
    this.isEditModeCard = true;
    this.selectedCarte = carte;
    this.carteForm.setValue({
      numero: carte.numero,
      fournisseur: carte.fournisseur,
      solde: carte.solde ?? 0,
      dateEmission: this.formatDate(carte.dateEmission),
      consomation: carte.consomation ?? 0
    });
    this.showCarteForm = true;
  }

  saveCarte(): void {
    this.submittedCard = true;

    if (this.carteForm.invalid) {
      return;
    }
    const formValue = this.carteForm.value;
    const carteData: Partial<CarteGazoil> = {
      ...formValue,
      dateEmission: formValue.dateEmission ? new Date(formValue.dateEmission) : null,
    };

    if (this.isEditModeCard && this.selectedCarte?.id) {
      this.carteGazoilService.updateCarte(this.selectedCarte.id, carteData).subscribe({
        next: () => {
          this.showToast('Carte modifiée avec succès', 'success');
          this.closeCarteForm();
          this.LoadCartes();
          this.initFormCarte()
          this.submittedCard = false
        },
        error: (err) => this.showToast('Erreur lors de la modification', 'error')
      });
    } else {
      this.carteGazoilService.addCarte(carteData).subscribe({
        next: () => {
          this.showToast('Carte ajoutée avec succès', 'success');
          this.closeCarteForm();
          this.LoadCartes();
          this.initFormCarte()
          this.submittedCard = false

        },
        error: (err) => this.showToast('Erreur lors de l\'ajout', 'error')
      });
    }
  }

  closeCarteForm(): void {
    this.showCarteForm = false;
    this.selectedCarte = null;
    this.isEditModeCard = false;
  }

  deleteCarte(id: string): void {
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
        // Appel du service pour supprimer la carte
        this.carteGazoilService.deleteCarte(id).subscribe({
          next: () => {
            Swal.fire(
              'Supprimée !',
              'La carte carburant a été supprimée.',
              'success'
            );
            // Rafraîchir la liste après suppression
            this.LoadCartes();
          },
          error: (err) => {
            Swal.fire(
              'Erreur !',
              'Impossible de supprimer la carte.',
              'error'
            );
            console.error(err);
          }
        });
      }
    });
  }

  chargerCarte(carte: any) {
    Swal.fire({
      title: `Charger la carte ${carte.numero}`,
      input: 'number',
      inputLabel: 'Montant à ajouter (DT)',
      inputAttributes: {
        min: '1',
        step: '1'
      },
      showCancelButton: true,
      confirmButtonText: 'Charger',
      cancelButtonText: 'Annuler',
      inputValidator: (value) => {
        const montant = Number(value);
        if (!value || isNaN(montant) || montant <= 0) {
          return 'Veuillez saisir un montant valide';
        }
        return null;
      }
    }).then((result) => {
      if (result.isConfirmed) {
        const montant = Number(result.value);
        const carteData = {
          solde: carte.solde + montant
        };
        this.carteGazoilService.updateCarte(carte.id, carteData).subscribe({
          next: () => {
            this.showToast('Carte Charger avec succès', 'success');
            this.LoadCartes();
            this.initFormCarte();
          },
          error: (err) => this.showToast('Erreur lors de la chargement', 'error')
        });
      }
    });
  }

  filterCartes(): void {
    if (!this.searchTermCarte) {
      this.filteredCartes = this.cartes;
    } else {
      const term = this.searchTermCarte.toLowerCase();
      this.filteredCartes = this.cartes.filter(carte =>
        carte.numero.toLowerCase().includes(term) ||
        carte.fournisseur.toLowerCase().includes(term)
      );
    }
  }

  printCarte(): void {
    if (!this.selectedCarte) return;

    const printContents = document.getElementById('print-carte')?.innerHTML;
    if (!printContents) return;

    const popupWin = window.open('', '_blank', 'width=900,height=700');
    if (!popupWin) return;

    // Récupère le CSS de ton projet (optionnel si tu veux exactement le même style)
    const styles = Array.from(document.querySelectorAll('style, link[rel="stylesheet"]'))
      .map(node => node.outerHTML)
      .join('\n');

    popupWin.document.open();
    popupWin.document.write(`
    <html>
      <head>
        <title>Impression Carte ${this.selectedCarte.numero}</title>
        ${styles} <!-- Injection du style existant -->
        <style>
          body { margin: 20px; }
          .highlight { font-weight: bold; color: green; }
          /* Optionnel : ajustements spécifiques pour impression */
          button, .action-buttons { display: none; }
        </style>
      </head>
      <body>
        ${printContents}
        <script>
          window.onload = function() {
            window.print();
            window.onafterprint = function(){ window.close(); }
          };
        </script>
      </body>
    </html>
  `);
    popupWin.document.close();
  }

  exportSelectedCarte(carte: CarteGazoil) {
    this.carteGazoilService.exporteCarte(carte);
  }

  /******************************************************* //   Gestion des cartes télépéage //  *******************************************************/

  filterTelepeages(): void {
    if (!this.searchTermTelepeage) {
      this.filteredTelepeages = [...this.telepeages]; // aucune recherche -> tout afficher
      return;
    }

    const term = this.searchTermTelepeage.toLowerCase();
    this.filteredTelepeages = this.telepeages.filter(t =>
      t.numero.toLowerCase().includes(term) ||
      t.fournisseur.toLowerCase().includes(term)
    );
  }

  private initFormTelepeage(): void {
    this.telepeageForm = this.fb.group({
      numero: [{ value: '', disabled: this.isEditModeTelepeage }, Validators.required],
      fournisseur: ['', Validators.required],
      solde: [0, [Validators.required, Validators.min(0)]],
      consomation: [0],
      dateEmission: [new Date(), Validators.required],
    });
  }

  viewTelepeageDetails(carte: CarteTelepeage): void {
    this.selectedTelepeage = carte;
    this.currentView = 'telepeage-details';
  }

  showAddTelepeageForm(): void {
    this.isEditModeTelepeage = false;
    this.selectedTelepeage = null;
    this.showTelepeageForm = true;
    this.initFormTelepeage();
  }

  editTelepeage(carte: CarteTelepeage): void {
    if (!this.telepeageForm) {
      this.initFormTelepeage(); // ⚡ garantit que le form existe
    }
    this.isEditModeTelepeage = true;
    this.selectedTelepeage = carte;
    this.showTelepeageForm = true;

    this.telepeageForm.patchValue({
      numero: carte.numero,
      fournisseur: carte.fournisseur,
      solde: carte.solde,
      consomation: carte.consomation ?? 0,
      dateEmission: this.formatDate(carte.dateEmission),
    });

    // Désactiver le champ numéro seulement en mode édition
    this.telepeageForm.get('numero')?.disable();
  }

  saveTelepeage(): void {
    this.submittedTelepeage = true;
    if (this.telepeageForm.invalid) return;

    const formValue = this.telepeageForm.value;
    const telepeageData: Partial<CarteTelepeage> = {
      ...formValue,
      dateEmission: new Date(formValue.dateEmission)
    };

    if (this.isEditModeTelepeage && this.selectedTelepeage?.id) {
      this.carteTelepeageService.updateCarte(this.selectedTelepeage.id, telepeageData).subscribe({
        next: () => {
          this.showToast('Carte modifiée avec succès', 'success');
          this.closeTelepeageForm();
          this.loadTelepeages();
        },
        error: () => this.showToast('Erreur lors de la modification', 'error')
      });
    } else {
      this.carteTelepeageService.addCarte(telepeageData).subscribe({
        next: () => {
          this.showToast('Carte ajoutée avec succès', 'success');
          this.closeTelepeageForm();
          this.loadTelepeages();
        },
        error: () => this.showToast('Erreur lors de l\'ajout', 'error')
      });
    }
  }

  closeTelepeageForm(): void {
    this.showTelepeageForm = false;
    this.selectedTelepeage = null;
    this.isEditModeTelepeage = false;
  }

  deleteTelepeage(id: string): void {
    if (!id) return;

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
        this.carteTelepeageService.deleteCarte(id).subscribe({
          next: () => {
            Swal.fire(
              'Supprimée !',
              'La carte télépéage a été supprimée.',
              'success'
            );
            this.loadTelepeages();
          },
          error: () => {
            Swal.fire(
              'Erreur !',
              'Impossible de supprimer la carte télépéage.',
              'error'
            );
          }
        });
      }
    });
  }

  printTelepeage(): void {
    if (!this.selectedTelepeage) return;

    const printContents = document.getElementById('print-telepeage')?.innerHTML;
    if (!printContents) return;

    const popupWin = window.open('', '_blank', 'width=900,height=700');
    if (!popupWin) return;

    // Récupérer le CSS existant
    const styles = Array.from(document.querySelectorAll('style, link[rel="stylesheet"]'))
      .map(node => node.outerHTML)
      .join('\n');

    popupWin.document.open();
    popupWin.document.write(`
    <html>
      <head>
        <title>Impression Carte Télépéage ${this.selectedTelepeage.numero}</title>
        ${styles}
        <style>
          body { margin: 20px; }
          .highlight { font-weight: bold; color: green; }
          button, .action-buttons { display: none; }
        </style>
      </head>
      <body>
        ${printContents}
        <script>
          window.onload = function() {
            window.print();
            window.onafterprint = function() { window.close(); }
          };
        </script>
      </body>
    </html>
  `);
    popupWin.document.close();
  }

  exportSelectedCarteTelepeage(carte: CarteTelepeage) {
    this.carteTelepeageService.exporteCarte(carte);
  }

  chargerCarteTelepeage(carte: any) {
    Swal.fire({
      title: `Charger la carte ${carte.numero}`,
      input: 'number',
      inputLabel: 'Montant à ajouter (DT)',
      inputAttributes: {
        min: '1',
        step: '1'
      },
      showCancelButton: true,
      confirmButtonText: 'Charger',
      cancelButtonText: 'Annuler',
      inputValidator: (value) => {
        const montant = Number(value);
        if (!value || isNaN(montant) || montant <= 0) {
          return 'Veuillez saisir un montant valide';
        }
        return null;
      }
    }).then((result) => {
      if (result.isConfirmed) {
        const montant = Number(result.value);
        const carteData = {
          solde: carte.solde + montant
        };
        this.carteTelepeageService.updateCarte(carte.id, carteData).subscribe({
          next: () => {
            this.showToast('Carte Charger avec succès', 'success');
            this.loadTelepeages();
          },
          error: (err) => this.showToast('Erreur lors de la chargement', 'error')
        });
      }
    });
  }

  /*******************************************************          //   Gestion des transactions  //                      ************************************************/

  addTransaction(carte: CarteGazoil): void {
    this.isEditModeTransaction = false;
    this.selectedTransaction = null;
    this.selectedCarte = carte;

    this.transactionForm.setValue({
      carteId: carte.id,
      vehiculeId: '',
      station: '',
      adresseStation: '',
      quantite: 0,
      prixLitre: 1.85,
      montantTotal: 0,
      kilometrage: 0,
      typeCarburant: TypeCarburant.GASOIL,
      conducteur: '',
      date: this.formatDateForInput(new Date())
    });

    this.showTransactionForm = true;
  }

  private initFormTransaction(): void {
    this.transactionForm = this.fb.group({
      vehiculeId: ['', Validators.required],
      carteId: ['', Validators.required],
      station: ['', Validators.required],
      adresseStation: [''],
      quantite: [{ value: 0, disabled: true }, [Validators.required, Validators.min(0.1)]],
      prixLitre: [{ value: 0, disabled: true }, [Validators.required, Validators.min(0.01)]],
      montantTotal: [0, [Validators.required, Validators.min(0.01)]],
      kilometrage: [0, [Validators.required, Validators.min(0)]],
      typeCarburant: ['', Validators.required],
      conducteur: ['', Validators.required],
      date: ['', Validators.required],
      consommation: [0, Validators.required],
      carteTelepeageId: [''],
      montantTelepeage: [0]
    });

    this.transactionForm.get('typeCarburant')?.valueChanges.subscribe(type => {

      let prix = 0;
      switch (type) {
        case TypeCarburant.ESSENCE: prix = this.PrixCarburant.essence; break;
        case TypeCarburant.GASOIL: prix = this.PrixCarburant.gasoil; break;
        case TypeCarburant.GASOIL_50: prix = this.PrixCarburant.gasoil_50; break;
        case TypeCarburant.GPL: prix = this.PrixCarburant.gpl; break;
      }
      this.transactionForm.patchValue({ prixLitre: prix }, { emitEvent: false });
      this.updateQuantite(); // recalcul quantité si montantTotal déjà saisi
    });

    // Calcul automatique de la quantité à partir du montant total
    this.transactionForm.get('montantTotal')?.valueChanges.subscribe(() => {
      this.updateQuantite();
      this.updateConsommation();
    });

    // Calcul consommation si kilometrage ou quantite change
    this.transactionForm.get('kilometrage')?.valueChanges.subscribe(() => this.updateConsommation());
    this.transactionForm.get('quantite')?.valueChanges.subscribe(() => this.updateConsommation());
  }

  // Calcule la quantité à partir du montant total et du prix par litre
  updateQuantite() {
    const montantTotal = this.transactionForm.get('montantTotal')?.value || 0;
    const prixLitre = this.transactionForm.get('prixLitre')?.value || 1; // éviter division par 0
    const quantite = montantTotal / prixLitre;
    this.transactionForm.patchValue({ quantite: quantite }, { emitEvent: false });
  }

  // Calcule la consommation
  updateConsommation() {
    const vehiculeId = this.transactionForm.get('vehiculeId')?.value;
    if (!vehiculeId) return;

    const kmSaisi = this.transactionForm.get('kilometrage')?.value || 0;
    const qte = this.transactionForm.get('quantite')?.value || 0;

    // Kilométrage actuel du véhicule
    const kmActuel = this.getVehiculeKmActuel(vehiculeId);

    // Distance réellement parcourue
    const distance = kmSaisi - kmActuel;
    if (distance <= 0) {
      this.transactionForm.patchValue({ consommation: 0 }, { emitEvent: false });
      return;
    }

    // Calcul consommation L/100 km
    const consommation = (qte / distance) * 100;

    this.transactionForm.patchValue({ consommation: parseFloat(consommation.toFixed(3)) }, { emitEvent: false });
  }

  saveTransaction(): void {
    if (this.transactionForm.invalid) {
      this.transactionForm.markAllAsTouched();
      return;
    }
    const vehiculeId = this.transactionForm.get('vehiculeId')?.value;
    const kilometrage = this.transactionForm.get('kilometrage')?.value;
    if (vehiculeId && kilometrage < this.getVehiculeKmActuel(vehiculeId)) return;

    // Montant > solde
    const carteId = this.transactionForm.get('carteId')?.value;
    const montantTotal = this.transactionForm.get('montantTotal')?.value;

    if (carteId && montantTotal > this.getCarteSolde(carteId)) return;
    const transactionData = {
      ...this.transactionForm.getRawValue(),
      date: new Date(this.transactionForm.value.date),
      ancienkilometrage: this.getVehiculeKmActuel(vehiculeId)
    };

    if (this.isEditModeTransaction && this.selectedTransaction?.id) {
      this.transactionService.updateTransaction(this.selectedTransaction.id, transactionData).subscribe({
        next: () => {
          this.showToast('Transaction mise à jour avec succès ✅', 'success');
          this.closeTransactionForm();
          this.loadTransactions();
          this.loadVehicles();
          this.LoadCartes();
          this.loadTelepeages();
        },
        error: (err) => {
          console.error(err);
          this.showToast('❌ Erreur lors de la mise à jour', 'error');
        }
      });
    } else {
      this.transactionService.addTransaction(transactionData).subscribe({
        next: () => {
          this.showToast('Transaction ajoutée avec succès ✅', 'success');
          this.closeTransactionForm();
          this.loadTransactions();
          this.loadVehicles();
          this.LoadCartes();
          this.loadTelepeages();
        },
        error: (err) => {
          console.error(err);
          this.showToast('❌ Erreur lors de l\'ajout', 'error');
        }
      });
    }
  }

  getCarteTelepeageSolde(carteId: string): number {
    const carte = this.telepeages.find(c => c.id === carteId);
    return carte ? carte.solde : 0;
  }


  getVehiculeKmActuel(vehiculeId: string): number {
    const vehicule = this.vehicles.find(v => v.id === vehiculeId);
    return vehicule ? vehicule.kmActuel : 0;
  }

  getCarteSolde(carteId: string): number {
    const carte = this.cartes.find(c => c.id === carteId);
    return carte ? carte.solde : 0;
  }

  editTransaction(transaction: TransactionCarburant): void {
    this.isEditModeTransaction = true;
    this.selectedTransaction = transaction;

    this.transactionForm.setValue({
      station: transaction.station ?? '',
      adresseStation: transaction.adresseStation ?? '',
      quantite: transaction.quantite ?? 0,
      prixLitre: transaction.prixLitre ?? 0,
      montantTotal: transaction.montantTotal ?? 0,
      kilometrage: transaction.kilometrage ?? 0,
      typeCarburant: transaction.typeCarburant ?? '',
      conducteur: transaction.conducteur ?? '',
      date: this.formatDateForInput(transaction.date),
      vehiculeId: transaction.vehicule?.id,
      carteId: transaction.carte?.id,
      consommation: transaction.consommation,
      carteTelepeageId: transaction.carteTelepeageId ?? '',
      montantTelepeage: transaction.montantTelepeage ?? 0

    });

    this.showTransactionForm = true;
  }

  deleteTransaction(id: string): void {
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
        this.transactionService.deleteTransaction(id).subscribe({
          next: () => {
            Swal.fire(
              'Supprimée !',
              'La transaction a été supprimée.',
              'success'
            );
            this.loadTransactions();
            this.LoadCartes();
            this.loadTelepeages();
            this.loadVehicles();
          },
          error: (err) => {
            Swal.fire(
              'Erreur !',
              'Impossible de supprimer la transaction.',
              'error'
            );
            console.error(err);
          }
        });
      }
    });
  }

  closeTransactionForm(): void {
    this.showTransactionForm = false;
    this.transactionForm.reset();
    this.selectedCarte = null;
    this.selectedTransaction = null;
    this.isEditModeTransaction = false;
  }

  showAddTransactionForm(): void {
    this.transactionFormData = {
      carteId: '',
      station: '',
      adresseStation: '',
      quantite: 0,
      prixLitre: 1.85,
      montantTotal: 0,
      kilometrage: 0,
      typeCarburant: TypeCarburant.GASOIL,
      conducteur: '',
      date: this.formatDateForInput(new Date())
    };
    this.showTransactionForm = true;
  }

  applyTransactionFilters(): void {
    let filtered = [...this.transactions];

    // Filtre par véhicule
    if (this.filterVehicleIdTransaction) {
      filtered = filtered.filter(
        t => t.vehicule?.id === this.filterVehicleIdTransaction
      );
    }

    // Filtre par conducteur
    if (this.filterConducteur) {
      const term = this.filterConducteur.toLowerCase();
      filtered = filtered.filter(t =>
        t.conducteur && t.conducteur.toLowerCase().includes(term)
      );
    }

    // Filtre par montant min
    if (this.filterMontantMin != null) {
      filtered = filtered.filter(t => t.montantTotal >= this.filterMontantMin!);
    }

    // Filtre par montant max
    if (this.filterMontantMax != null) {
      filtered = filtered.filter(t => t.montantTotal <= this.filterMontantMax!);
    }

    // Tri par date descendante
    this.filteredTransactions = filtered.sort(
      (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
    );
  }

  clearTransactionFilters(): void {
    this.filterVehicleIdTransaction = '';
    this.filterConducteur = '';
    this.filterMontantMin = null;
    this.filterMontantMax = null;
    this.applyTransactionFilters();
  }

  /*******************************************************          // Gestion des réparations  //                      ************************************************/

  private initFormReparation(): void {
    this.reparationForm = this.fb.group({
      type: ['', Validators.required],
      prix: [0, [Validators.required, Validators.min(0)]],
      date: ['', Validators.required],
      description: [''],
      url: [''],
      vehiculeId: ['', Validators.required]
    });
  }

  showAddReparationForm(): void {
    this.reparationFormData = {
      vehicleId: '',
      type: TypeReparation.VIDANGE,
      prix: 0,
      date: this.formatDateForInput(new Date()),
      description: ''
    };
    this.showReparationForm = true;
  }

  addReparation(vehicle: Vehicle): void {
    this.selectedVehicle = vehicle;
    this.reparationFormData = {
      vehicleId: vehicle.id,
      type: TypeReparation.VIDANGE,
      prix: 0,
      date: this.formatDateForInput(new Date()),
      description: ''
    };
    this.showReparationForm = true;
  }

  saveReparation(): void {
    this.submittedReparation = true;

    if (this.reparationForm.invalid) {
      this.reparationForm.markAllAsTouched();
      return;
    }

    const formValue = this.reparationForm.getRawValue();
    const reparationData: Partial<Reparation> = {
      ...formValue,
      date: formValue.date ? new Date(formValue.date) : null,
    };

    if (this.isEditModeReparation && this.selectedReparation?.id) {
      // --- UPDATE ---
      this.reparationService.updateReparation(this.selectedReparation.id, reparationData).subscribe({
        next: () => {
          this.showToast('Réparation modifiée avec succès', 'success');
          this.closeReparationForm();
          this.loadReparations();
          this.initFormReparation();
          this.loadVehicles();

          this.submittedReparation = false;
        },
        error: () => this.showToast('Erreur lors de la modification', 'error')
      });
    } else {
      // --- ADD ---
      this.reparationService.addReparation(reparationData).subscribe({
        next: () => {
          this.showToast('Réparation ajoutée avec succès', 'success');
          this.closeReparationForm();
          this.loadReparations();
          this.initFormReparation();
          this.loadVehicles();
          this.submittedReparation = false;
        },
        error: () => this.showToast('Erreur lors de l\'ajout', 'error')
      });
    }
  }

  closeReparationForm(): void {
    this.showReparationForm = false;
    this.selectedVehicle = null;
  }

  editReparation(reparation: Reparation): void {
    this.isEditModeReparation = true;
    this.selectedReparation = reparation;
    this.reparationForm.setValue({
      type: reparation.type,
      prix: reparation.prix ?? 0,
      date: this.formatDateForInput(reparation.date),
      description: reparation.description ?? '',
      vehiculeId: reparation.vehicule?.id,
      url: reparation.url
    });
    this.showReparationForm = true;
  }

  deleteReparation(id: string): void {
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
        this.reparationService.deleteReparation(id).subscribe({
          next: () => {
            Swal.fire('Supprimée !', 'La réparation a été supprimée.', 'success');
            this.loadReparations();
          },
          error: () => {
            Swal.fire('Erreur !', 'Impossible de supprimer la réparation.', 'error');
          }
        });
      }
    });
  }

  downloadFile(url: string) {
    if (!url) return;

    // Extraire l'ID du fichier à partir de l'URL
    const fileId = url.match(/\/d\/([a-zA-Z0-9_-]+)/)?.[1];
    if (!fileId) {
      console.error("ID de fichier non trouvé dans l'URL");
      return;
    }

    // Créer le lien de téléchargement direct
    const downloadUrl = `https://drive.google.com/uc?export=download&id=${fileId}`;

    // Créer un élément <a> pour initier le téléchargement
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = "piece_jointe.pdf"; // Nom du fichier à télécharger
    link.target = '_blank'; // Ouvrir dans un nouvel onglet
    link.click();
  }

  applyReparationFilters(): void {
    let filtered = [...this.reparations];

    // Filtre par véhicule
    if (this.filterVehicleId) {
      filtered = filtered.filter(r => r.vehicule?.id === this.filterVehicleId);
    }

    // Filtre par type
    if (this.filterTypeReparation) {
      filtered = filtered.filter(r => r.type === this.filterTypeReparation);
    }

    // Filtre par prix min
    if (this.filterPrixMin != null) {
      filtered = filtered.filter(r => r.prix >= this.filterPrixMin!);
    }

    // Filtre par prix max
    if (this.filterPrixMax != null) {
      filtered = filtered.filter(r => r.prix <= this.filterPrixMax!);
    }

    // Tri par date descendante
    this.filteredReparations = filtered.sort((a, b) =>
      new Date(b.date).getTime() - new Date(a.date).getTime()
    );
  }

  clearReparationFilters(): void {
    this.filterVehicleId = '';
    this.filterTypeReparation = '';
    this.filterPrixMin = null;
    this.filterPrixMax = null;
    this.applyReparationFilters();
  }

  /*******************************************************          // dashboard  //                      ************************************************/

  getVehicleStatusClass(vehicle: Vehicle): string {
    const today = new Date();

    if (this.isDateExpired(vehicle.dateVisiteTechnique) ||
      this.isDateExpired(vehicle.dateAssurance) ||
      this.isDateExpired(vehicle.dateTaxe)) {
      return 'danger';
    }
    if (vehicle.kmActuel >= vehicle.prochainVidangeKm) {
      return 'warning';
    }

    return 'success';
  }

  getCarteStatusClass(statut: StatutCarte): string {
    // return statut.toLowerCase().replace(/[^a-z]/g, '');
    return "null";
  }

  isDateExpired(date: Date): boolean {
    return new Date(date) < new Date();
  }

  private formatDateForInput(date: Date): string {
    return new Date(date).toISOString().split('T')[0];
  }

  getPapiersAExpirer(): any[] {
    const papiers: any[] = [];
    const today = new Date();

    this.vehicles.forEach(vehicle => {
      const checkPapier = (type: string, date: string | Date) => {
        if (!date) return;
        const dateValue = new Date(date);
        const joursRestants = Math.ceil((dateValue.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));

        // Inclut seulement si joursRestants > 0 et <= 30
        if (joursRestants > 0 && joursRestants <= 30) {
          papiers.push({
            vehicle,
            type,
            dateExpiration: dateValue,
            joursRestants
          });
        }
      };

      checkPapier('Visite Technique', vehicle.dateVisiteTechnique);
      checkPapier('Assurance', vehicle.dateAssurance);
      checkPapier('Taxe', vehicle.dateTaxe);
    });

    return papiers.sort((a, b) => a.joursRestants - b.joursRestants);

  }

  getStatusMessage(joursRestants: number): string {
    if (joursRestants < 0) {
      return 'Expiré';
    } else if (joursRestants < 7) {
      return 'Critique';
    } else if (joursRestants <= 30) {
      return 'Expire bientôt';
    } else {
      return 'À jour';
    }
  }

  getEntretiensAExpirer(): any[] {
    const entretiens: any[] = [];

    this.vehicles.forEach(vehicle => {
      const checkKm = (type: string, prochainKm: number, kmActuel: number) => {
        if (!prochainKm || !kmActuel) return;
        const kmRestants = prochainKm - kmActuel;

        // Alerte seulement si <= 1000 km avant échéance
        if (kmRestants <= 1000 && kmRestants >= 0) {
          entretiens.push({
            vehicle,
            type,
            kmEcheance: prochainKm,
            kmActuel,
            kmRestants
          });
        }
      };

      checkKm('Vidange', vehicle.prochainVidangeKm, vehicle.kmActuel);
      checkKm('Chaîne de distribution', vehicle.prochaineChaineKm, vehicle.kmActuel);
    });

    return entretiens.sort((a, b) => a.kmRestants - b.kmRestants);
  }

  getKmStatusMessage(kmRestants: number): string {
    if (kmRestants < 0) {
      return 'Entretien dépassé';
    } else if (kmRestants < 200) {
      return 'Critique';
    } else if (kmRestants <= 1000) {
      return 'À prévoir bientôt';
    } else {
      return 'À jour';
    }
  }

  // Méthodes pour les tableaux du dashboard
  getRecentReparations(): Reparation[] {
    return this.reparations
      .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
      .slice(0, 10);
  }

  getRecentTransactions(): TransactionCarburant[] {
    return this.transactions
      .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
      .slice(0, 10);
  }

  getVehicleByReparation(vehicleId: string): Vehicle | undefined {
    return this.vehicles.find(v => v.id === vehicleId);
  }

  getCarteByTransaction(carteId: string): CarteGazoil | undefined {
    return this.cartes.find(c => c.id === carteId);
  }

  viewVehicleByReparation(reparation: Reparation): void {
    const vehicle = this.getVehicleByReparation(reparation.vehicleId);
    if (vehicle) {
      this.viewVehicleDetails(vehicle);
    }
  }

  viewCarteByTransaction(transaction: TransactionCarburant): void {
    const carte = this.getCarteByTransaction(transaction.carteId);
    if (carte) {
      this.viewCarteDetails(carte);
    }
  }




  //------------------------------------------ prix carburant ------------------------------------

  initFormPrixCarburant() {
    this.prixForm = this.fb.group({
      essence: [0, [Validators.required, Validators.min(0.1)]],
      gasoil: [0, [Validators.required, Validators.min(0.1)]],
      gasoil50: [0, [Validators.required, Validators.min(0.1)]],
      gpl: [0, [Validators.required, Validators.min(0.1)]],
    });
  }

  onSave(): void {
    if (this.prixForm.valid && this.PrixCarburant) {
      const formValues = this.prixForm.value;

      this.PrixCarburant.essence = formValues.essence;
      this.PrixCarburant.gasoil = formValues.gasoil;
      this.PrixCarburant.gasoil_50 = formValues.gasoil50;
      this.PrixCarburant.gpl = formValues.gpl;

      this.prixCarbuarntService.update(this.PrixCarburant.id, this.PrixCarburant).subscribe(() => {
        Swal.fire({
          icon: 'success',
          title: 'Succès',
          text: 'Prix du carburant mis à jour ✅',
          timer: 2000,
          showConfirmButton: false
        });
        this.closePrixForm();
      });
    }
  }

  closePrixForm(): void {
    this.showPrixForm = false;
    this.isEditMode = true;
  }

  showAddPrixForm(): void {
    this.showPrixForm = true;
  }

}
