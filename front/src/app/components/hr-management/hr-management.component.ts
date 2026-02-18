import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HRService } from '../../services/hr.service';
import { HRStats } from '../../models/rh/HRStats.model';
import { Employee } from '../../models/rh/Employee.model';
import { FichePaie } from '../../models/rh/FichePaie.model';
import { Prime } from '../../models/rh/Prime.model';
import { Retenue } from '../../models/rh/Retenue.model';
import { SituationFamiliale, StatutConge, StatutEmployee, StatutFichePaie, TypeConge, TypePieceIdentite } from '../../models/rh/enum.model';
import { Congee } from '../../models/rh/Congee.model';
import { CongeeService } from '../../services/rh_managment/congee-service';
import { EmployeeService } from '../../services/rh_managment/employee-service';
import { Regle, TypeRegle, StatutRegle, RegleStats } from '../../models/rh/Regle.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-hr-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './hr-management.component.html',
  styleUrls: ['./hr-management.component.css']
})
export class HRManagementComponent implements OnInit {
  employees: Employee[] = [];
  filteredEmployees: Employee[] = [];
  conges: Congee[] = [];
  filteredConges: Congee[] = [];
  fiches: FichePaie[] = [];
  filteredFiches: FichePaie[] = [];
  primes: Prime[] = [];
  retenues: Retenue[] = [];
  regles: Regle[] = [];
  filteredRegles: Regle[] = [];
  regleStats: RegleStats = {
    totalRegles: 0,
    reglesActives: 0,
    reglesInactives: 0,
    primes: 0,
    retenues: 0,
    reglesAutomatiques: 0,
    employesConcernes: 0,
    montantTotalPrimes: 0,
    montantTotalRetenues: 0
  };

  stats: HRStats = {
    totalEmployees: 0,
    employeesActifs: 0,
    employeesEnConge: 0,
    congesEnAttente: 0,
    masseSalariale: 0,
    fichesNonPayees: 0,
    moyenneCongesUtilises: 0,
    turnoverRate: 0
  };

  currentView: 'dashboard' | 'employees' | 'conges' | 'fiches' | 'primes' | 'retenues' | 'regles' | 'details' = 'dashboard';
  selectedEmployee: Employee | null = null;
  selectedConge: Congee | null = null;
  selectedFiche: FichePaie | null = null;
  selectedPrime: Prime | null = null;
  selectedRetenue: Retenue | null = null;
  selectedRegle: Regle | null = null;

  searchTerm = '';
  searchTermConge = '';
  searchTermFiche = '';
  searchTermPrime = '';
  searchTermRetenue = '';
  filterEmployeeId = '';
  filterEmployeeIdPrime = '';
  filterEmployeeIdRetenue = '';
  filterTypeConge = '';
  filterStatutConge = '';
  filterStatutFiche = '';
  filterMontantMinPrime: number | null = null;
  filterMontantMaxPrime: number | null = null;
  filterMontantMinRetenue: number | null = null;
  filterMontantMaxRetenue: number | null = null;
  filterEmployeeIdFiche = '';
  searchTermRegle = '';
  filterTypeRegle = '';
  filterStatutRegle = '';
  filterAncienneteMin: number | null = null;
  filterAncienneteMax: number | null = null;
  filteredPrimes: Prime[] = [];
  filteredRetenues: Retenue[] = [];

  // Formulaires et popups
  showEmployeeForm = false;
  showCongeForm = false;
  showFicheForm = false;
  showPrimeForm = false;
  showRetenueForm = false;
  showRegleForm = false;
  showEmployeeDetails = false;
  showCongeDetails = false;
  showFicheDetails = false;
  showPrimeDetails = false;
  showRetenueDetails = false;
  showRegleDetails = false;

  isEditMode = false;
  isEditModeConge = false;
  isEditModeFiche = false;
  isEditModePrime = false;
  isEditModeRetenue = false;
  isEditModeRegle = false;
  submitted = false;
  submittedConge = false;
  submittedFiche = false;
  submittedPrime = false;
  submittedRetenue = false;
  submittedRegle = false;

  // FormGroups
  employeeForm!: FormGroup;
  congeForm!: FormGroup;
  ficheForm!: FormGroup;
  primeForm!: FormGroup;
  retenueForm!: FormGroup;
  regleForm!: FormGroup;

  // Énumérations
  statutsEmployee = Object.values(StatutEmployee);
  situationsFamiliales = Object.values(SituationFamiliale);
  typesConge = Object.values(TypeConge);
  statutsConge = Object.values(StatutConge);
  statutsFiche = Object.values(StatutFichePaie);
  typesPieceIdentite = Object.values(TypePieceIdentite);
  typesRegle = Object.values(TypeRegle);
  statutsRegle = Object.values(StatutRegle);

  constructor(
    private fb: FormBuilder,
    private hrService: HRService,
    private router: Router,
    private congeeService: CongeeService,
    private employeeService: EmployeeService,
   
  ) { }

  goBackToMenu(): void {
    this.router.navigate(['/menu']);
  }

  ngOnInit(): void {
    this.initForms();
    this.loadEmployees();
    this.loadConges();
    this.loadFiches();
    this.loadPrimes();
    this.loadRetenues();
    this.loadRegles();
    this.loadRegles();
    this.applyFicheFilters();
    this.applyPrimeFilters();
    this.applyRetenueFilters();
    this.applyRegleFilters();
  }

  private initForms(): void {
    this.initEmployeeForm();
    this.initCongeForm();
    this.initFicheForm();
    this.initPrimeForm();
    this.initRetenueForm();
    this.initRegleForm();

    this.employeeForm.get('salaire')?.valueChanges.subscribe(salaire => {
    if (salaire != null && !isNaN(salaire)) { 
          this.employeeForm.get('pointsDemandesParAn')?.setValue(salaire* 12 );
    } else {
      this.employeeForm.get('pointsDemandesParAn')?.setValue(0);
    }
  });
  }

  private initEmployeeForm(): void {
    this.employeeForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(2)]],
      prenom: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      telephone: ['', [Validators.required,  Validators.pattern(/^(?:\+|00)?\d{11}$|^\d{8}$/)]],
      poste: ['', Validators.required],
      departement: ['', Validators.required],
      dateEmbauche: ['', Validators.required],
      salaire: [0, [Validators.required, Validators.min(0)]],
      soldeConges: [25, [Validators.required, Validators.min(0)]],
      congesUtilises: [0, [Validators.required, Validators.min(0)]],
      soldePoints: [0, [Validators.required, Validators.min(0)]],
      pointsDemandesParAn: [0, [Validators.required, Validators.min(0)]],
      adresse: ['', Validators.required],
      typePieceIdentite: [TypePieceIdentite.CIN, Validators.required],
      numeroPieceIdentite: ['', [Validators.required, this.numeroPieceValidator.bind(this)]],
    });
  }

  // Validator personnalisé
  numeroPieceValidator(control: AbstractControl) {
    const type = this.employeeForm?.get('typePieceIdentite')?.value;
    const value = control.value;

    if (!value) return null;

    if (type === TypePieceIdentite.CIN) {
      return /^\d{8}$/.test(value) ? null : { invalidCin: true };
    } else if (type === TypePieceIdentite.PASSEPORT) {
      return /^[A-Z0-9]{6,9}$/i.test(value) ? null : { invalidPassport: true };
    }

    return null;
  }

  // Pour le message d'erreur
  getNumeroPieceErrorMessage() {
    const errors = this.f['numeroPieceIdentite'].errors;
    if (!errors) return '';
    if (errors['required']) return 'Le numéro de pièce est requis';
    if (errors['invalidCin']) return 'Le CIN doit comporter exactement 8 chiffres';
    if (errors['invalidPassport']) return 'Le passport doit comporter 6 à 9 caractères alphanumériques';
    return '';
  }

  private initCongeForm(): void {
    this.congeForm = this.fb.group({
      employeeId: ['', Validators.required],
      type: [TypeConge.ANNUEL, Validators.required],
      dateDebut: ['', Validators.required],
      dateFin: ['', Validators.required],
      nombreJours: [{ value: 0, disabled: true }],
      motif: ['', Validators.required],
      statut: [StatutConge.EN_ATTENTE, Validators.required]
    });

    // Calcul automatique du nombre de jours
    this.congeForm.get('dateDebut')?.valueChanges.subscribe(() => this.calculateNombreJours());
    this.congeForm.get('dateFin')?.valueChanges.subscribe(() => this.calculateNombreJours());
  }

  private initFicheForm(): void {
    this.ficheForm = this.fb.group({
      employeeId: ['', Validators.required],
      dateEmission: ['', Validators.required],
      salaireDeBase: [0, [Validators.required, Validators.min(0)]],
      netAPayer: [{ value: 0, disabled: true }],
      statut: [StatutFichePaie.BROUILLON, Validators.required]
    });

    // Calcul automatique du net à payer
    this.ficheForm.get('salaireDeBase')?.valueChanges.subscribe(() => this.calculateNetAPayer());
  }

  private initPrimeForm(): void {
    this.primeForm = this.fb.group({
      employeeId: ['', Validators.required],
      libelle: ['', Validators.required],
      montant: [0, [Validators.required, Validators.min(0)]],
      nombrePoints: [0, [Validators.required, Validators.min(0)]],
      description: ['']
    });
  }

  private initRetenueForm(): void {
    this.retenueForm = this.fb.group({
      employeeId: ['', Validators.required],
      libelle: ['', Validators.required],
      montant: [0, [Validators.required, Validators.min(0)]],
      nombrePoints: [0, [Validators.required, Validators.min(0)]],
      description: ['']
    });
  }

  private initRegleForm(): void {
    this.regleForm = this.fb.group({
      libelle: ['', Validators.required],
      description: [''],
      typeRegle: [TypeRegle.PRIME, Validators.required],
      statut: [StatutRegle.ACTIVE, Validators.required],
      minAnciennete: [0, [Validators.required, Validators.min(0)]],
      maxAnciennete: [null],
      montantFixe: [null, Validators.min(0)],
      pourcentageSalaire: [null, [Validators.min(0), Validators.max(1)]],
      nombreJours: [0, Validators.min(0)],
      nombrePoints: [0, Validators.min(0)],
      conditions: [''],
      automatique: [true]
    });
  }

  private loadEmployees(): void {
    this.employeeService.getEmployees().subscribe(employees => {
      this.employees = employees;
      this.filteredEmployees = employees;
      this.stats = this.hrService.getHRStats();
    });
  }

  private loadConges(): void {
    this.congeeService.getCongees().subscribe(conges => {
      this.conges = conges;
      this.filteredConges = conges;
    });
  }

  private loadFiches(): void {
    this.hrService.getFiches().subscribe(fiches => {
      this.fiches = fiches;
      this.filteredFiches = fiches;
    });
  }

  private loadPrimes(): void {
    this.hrService.getPrimes().subscribe(primes => {
      this.primes = primes;
      this.filteredPrimes = primes;
    });
  }

  private loadRetenues(): void {
    this.hrService.getRetenues().subscribe(retenues => {
      this.retenues = retenues;
      this.filteredRetenues = retenues;
    });
  }

  private loadRegles(): void {
    this.hrService.getRegles().subscribe(regles => {
      this.regles = regles;
      this.filteredRegles = regles;
      this.regleStats = this.hrService.getRegleStats();
    });
  }

  private showToast(message: string, icon: 'success' | 'error' | 'info' = 'info'): void {
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

  setView(view: 'dashboard' | 'employees' | 'conges' | 'fiches' | 'primes' | 'retenues' | 'regles' | 'details'): void {
    this.currentView = view;
  }

  //------------------------------------------------   Gestion des employés -------------------------------------------------------------//

  viewEmployeeDetails(employee: Employee): void {
    this.selectedEmployee = employee;
    this.showEmployeeDetails = true;
  }

  closeEmployeeDetails(): void {
    this.showEmployeeDetails = false;
    this.selectedEmployee = null;
  }

  showAddEmployeeForm(): void {
    this.isEditMode = false;
    this.selectedEmployee = null;
    this.employeeForm.reset({
      matricule: '',
      nom: '',
      prenom: '',
      email: '',
      telephone: '',
      poste: '',
      departement: '',
      dateEmbauche: this.formatDateForInput(new Date()),
      salaire: 0,
      soldeConges: 25,
      congesUtilises: 0,
      soldePoints: 0,
      pointsDemandesParAn: 0,
      adresse: '',
      typePieceIdentite: TypePieceIdentite.CIN,
      numeroPieceIdentite: '',
      situationFamiliale: SituationFamiliale.CELIBATAIRE,
      nombreEnfants: 0,
      statut: StatutEmployee.ACTIF
    });
    this.showEmployeeForm = true;
  }

  editEmployee(employee: Employee): void {
    this.isEditMode = true;
    this.selectedEmployee = employee;
    this.employeeForm.patchValue({
      nom: employee.nom,
      prenom: employee.prenom,
      email: employee.email,
      telephone: employee.telephone,
      poste: employee.poste,
      departement: employee.departement,
      dateEmbauche: this.formatDateForInput(employee.dateEmbauche),
      salaire: employee.salaire,
      soldeConges: employee.soldeConges,
      congesUtilises: employee.congesUtilises,
      soldePoints: employee.soldePoints,
      pointsDemandesParAn: employee.pointsDemandesParAn,
      adresse: employee.adresse,
      typePieceIdentite: employee.typePieceIdentite,
      numeroPieceIdentite: employee.numeroPieceIdentite,
      nombreEnfants: employee.nombreEnfants,

    });
    this.showEmployeeForm = true;
  }

  saveEmployee(): void {
    this.submitted = true;
    if (this.employeeForm.invalid) {
      this.employeeForm.markAllAsTouched();
      return;
    }

    const formValue = this.employeeForm.getRawValue();
    const employeeData: Partial<Employee> = {
      ...formValue,
      dateEmbauche: new Date(formValue.dateEmbauche)
    };

    if (this.isEditMode && this.selectedEmployee) {
      debugger
      this.employeeService.updateEmployee(this.selectedEmployee.id, employeeData).subscribe({

        next: () => {
          this.showToast('Employé modifié avec succès', 'success');
          this.closeEmployeeForm();
          this.loadEmployees();
        },
        error: () => this.showToast('Erreur lors de la modification', 'error')
      });
    } else {
      this.employeeService.addEmployee(employeeData).subscribe({
        next: () => {
          this.showToast('Employé ajouté avec succès', 'success');
          this.closeEmployeeForm();
          this.loadEmployees();
        },
        error: () => this.showToast('Erreur lors de l\'ajout', 'error')
      });
    }
  }

  closeEmployeeForm(): void {
    this.showEmployeeForm = false;
    this.isEditMode = false;
    this.selectedEmployee = null;
    this.submitted = false;
    this.employeeForm.reset();
  }

  deleteEmployee(id: string): void {
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
        this.employeeService.deleteEmployee(id).subscribe({
          next: () => {
            Swal.fire('Supprimé !', 'L\'employé a été supprimé.', 'success');
            this.loadEmployees();
          },
          error: () => {
            Swal.fire('Erreur !', 'Impossible de supprimer l\'employé.', 'error');
          }
        });
      }
    });
  }

  //--------------------------------------------------------------------------  Gestion des congés   ---------------------------------------------------- //
  viewCongeDetails(conge: Congee): void {
    this.selectedConge = conge;
    this.showCongeDetails = true;
  }

  closeCongeDetails(): void {
    this.showCongeDetails = false;
    this.selectedConge = null;
  }

  showAddCongeForm(): void {
    this.isEditModeConge = false;
    this.selectedConge = null;
    this.congeForm.reset({
      employeeId: '',
      type: TypeConge.ANNUEL,
      dateDebut: this.formatDateForInput(new Date()),
      dateFin: this.formatDateForInput(this.getDefaultDateFin()),
      nombreJours: 0,
      motif: '',
      statut: StatutConge.EN_ATTENTE
    });
    this.showCongeForm = true;
  }

  editConge(conge: Congee): void {
    this.isEditModeConge = true;
    this.selectedConge = conge;
    this.congeForm.setValue({
      employeeId: conge.employee.id,
      type: conge.type,
      dateDebut: this.formatDateForInput(conge.dateDebut),
      dateFin: this.formatDateForInput(conge.dateFin),
      nombreJours: conge.nombreJours,
      motif: conge.motif,
      statut: conge.statut
    });
    this.showCongeForm = true;
  }

  saveConge(): void {
    this.submittedConge = true;

    if (this.congeForm.invalid) {
      this.congeForm.markAllAsTouched();
      return;
    }

    const formValue = this.congeForm.getRawValue();
    const congeData: Partial<Congee> = {
      ...formValue,
      dateDebut: new Date(formValue.dateDebut),
      dateFin: new Date(formValue.dateFin)
    };

    if (this.isEditModeConge && this.selectedConge) {
      this.congeeService.updateCongee(this.selectedConge.id, congeData).subscribe({
        next: () => {
          this.showToast('Congé modifié avec succès', 'success');
          this.closeCongeForm();
          this.loadConges();
        },
        error: () => this.showToast('Erreur lors de la modification', 'error')
      });
    } else {
      this.congeeService.addCongee(congeData).subscribe({
        next: () => {
          this.showToast('Congé ajouté avec succès', 'success');
          this.closeCongeForm();
          this.loadConges();
        },
        error: () => this.showToast('Erreur lors de l\'ajout', 'error')
      });
    }
  }

  closeCongeForm(): void {
    this.showCongeForm = false;
    this.isEditModeConge = false;
    this.selectedConge = null;
    this.submittedConge = false;
    this.congeForm.reset();
  }

  approveConge(id: string): void {
    Swal.fire({
      title: 'Approuver ce congé ?',
      text: "Cette action déduira les jours du solde de l'employé",
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#10b981',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Oui, approuver',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        const updateData = {
          statut: StatutConge.APPROUVE,
          dateValidation: new Date(),
          validePar: 'admin'
        };

        this.congeeService.updateCongee(id, updateData).subscribe({
          next: () => {
            this.showToast('Congé approuvé avec succès', 'success');
            this.loadConges();
            this.closeCongeDetails();
          },
          error: () => this.showToast('Erreur lors de l\'approbation', 'error')
        });
      }
    });
  }

  rejectConge(id: string): void {
    Swal.fire({
      title: 'Refuser ce congé ?',
      input: 'textarea',
      inputLabel: 'Motif du refus (optionnel)',
      inputPlaceholder: 'Expliquez pourquoi ce congé est refusé...',
      showCancelButton: true,
      confirmButtonColor: '#ef4444',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Refuser',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        const updateData = {
          statut: StatutConge.REFUSE,
          dateValidation: new Date(),
          validePar: 'admin'
        };

        this.congeeService.updateCongee(id, updateData).subscribe({
          next: () => {
            this.showToast('Congé refusé', 'info');
            this.loadConges();
            this.closeCongeDetails();
          },
          error: () => this.showToast('Erreur lors du refus', 'error')
        });
      }
    });
  }

  deleteConge(id: string): void {
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
        this.congeeService.deleteCongee(id).subscribe({
          next: () => {
            Swal.fire('Supprimé !', 'Le congé a été supprimé.', 'success');
            this.loadConges();
            this.closeCongeDetails();
          },
          error: () => {
            Swal.fire('Erreur !', 'Impossible de supprimer le congé.', 'error');
          }
        });
      }
    });
  }

  // Gestion des fiches de paie
  showAddFicheForm(): void {
    this.isEditModeFiche = false;
    this.selectedFiche = null;
    this.ficheForm.reset({
      employeeId: '',
      dateEmission: this.formatDateForInput(new Date()),
      salaireDeBase: 0,
      netAPayer: 0,
      statut: StatutFichePaie.BROUILLON
    });
    this.showFicheForm = true;
  }

  editFiche(fiche: FichePaie): void {
    this.isEditModeFiche = true;
    this.selectedFiche = fiche;
    this.ficheForm.setValue({
      employeeId: fiche.employee.id,
      dateEmission: this.formatDateForInput(fiche.dateEmission),
      salaireDeBase: fiche.salaireDeBase,
      netAPayer: fiche.netAPayer,
      statut: fiche.statut
    });
    this.showFicheForm = true;
  }

  saveFiche(): void {
    this.submittedFiche = true;

    if (this.ficheForm.invalid) {
      this.ficheForm.markAllAsTouched();
      return;
    }

    const formValue = this.ficheForm.getRawValue();
    const selectedEmployee = this.employees.find(emp => emp.id === formValue.employeeId);

    if (!selectedEmployee) {
      this.showToast('Employé non trouvé', 'error');
      return;
    }

    const ficheData: Omit<FichePaie, 'id'> = {
      ...formValue,
      dateEmission: new Date(formValue.dateEmission),
      employee: selectedEmployee,
      primes: [] as Prime[],
      retenues: [] as Retenue[]
    };

    if (this.isEditModeFiche && this.selectedFiche) {
      this.hrService.updateFiche(this.selectedFiche.id, ficheData);
      this.showToast('Fiche de paie modifiée avec succès', 'success');
    } else {
      this.hrService.addFiche(ficheData);
      this.showToast('Fiche de paie ajoutée avec succès', 'success');
    }

    this.closeFicheForm();
    this.loadFiches();
  }

  viewFicheDetails(fiche: FichePaie): void {
    this.selectedFiche = fiche;
    this.showFicheDetails = true;
  }

  closeFicheDetails(): void {
    this.showFicheDetails = false;
    this.selectedFiche = null;
  }

  printFiche(fiche: FichePaie): void {
    const printContent = this.generateFichePrintContent(fiche);
    const printWindow = window.open('', '', 'width=800,height=600');

    if (printWindow) {
      printWindow.document.write(`
        <html>
          <head>
            <title>Fiche de Paie ${this.getEmployeeName(fiche.employee.id)}</title>
            <style>
              body { font-family: Arial, sans-serif; padding: 20px; }
              .fiche-header { text-align: center; margin-bottom: 30px; border-bottom: 2px solid #333; padding-bottom: 20px; }
              .employee-info { margin-bottom: 30px; }
              .salary-details { display: flex; justify-content: space-between; margin-bottom: 30px; }
              .primes-section, .retenues-section { flex: 1; margin: 0 10px; }
              .total-section { font-size: 24px; font-weight: bold; color: #10b981; margin-top: 20px; text-align: center; }
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

  private generateFichePrintContent(fiche: FichePaie): string {
    const employee = this.employees.find(e => e.id === fiche.employee.id);
    if (!employee) return '';

    return `
      <div class="fiche-header">
        <h1>FICHE DE PAIE</h1>
        <h2>${new Date(fiche.dateEmission).toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' })}</h2>
      </div>
      
      <div class="employee-info">
        <h3>Employé: ${employee.prenom} ${employee.nom}</h3>
        <p>Poste: ${employee.poste}</p>
        <p>Département: ${employee.departement}</p>
      </div>
      
      <div class="salary-details">
        <div class="primes-section">
          <h4>Primes</h4>
          ${fiche.primes.map(p => `<p>${p.libelle}: ${p.montant} DT</p>`).join('')}
          <p><strong>Total: ${this.getTotalPrimesFiche(fiche.id)} DT</strong></p>
        </div>
        
        <div class="retenues-section">
          <h4>Retenues</h4>
          ${fiche.retenues.map(r => `<p>${r.libelle}: ${r.montant} DT</p>`).join('')}
          <p><strong>Total: ${this.getTotalRetenuesFiche(fiche.id)} DT</strong></p>
        </div>
      </div>
      
      <div class="total-section">
        <p>Salaire de base: ${fiche.salaireDeBase} DT</p>
        <p>Net à payer: ${fiche.netAPayer} DT</p>
      </div>
      
      <div class="footer">
        <p>SMS2i - Système de Management Intégré</p>
        <p>Fiche générée le ${new Date().toLocaleDateString('fr-FR')}</p>
      </div>
    `;
  }

  validateFiche(fiche: FichePaie): void {
    if (confirm('Valider cette fiche de paie ?')) {
      this.hrService.updateFiche(fiche.id, { statut: StatutFichePaie.VALIDE });
      this.showToast('Fiche de paie validée', 'success');
      this.loadFiches();
    }
  }

  closeFicheForm(): void {
    this.showFicheForm = false;
    this.isEditModeFiche = false;
    this.selectedFiche = null;
    this.submittedFiche = false;
    this.ficheForm.reset();
  }

  deleteFiche(id: string): void {
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
        this.hrService.deleteFiche(id);
        Swal.fire('Supprimée !', 'La fiche de paie a été supprimée.', 'success');
        this.loadFiches();
      }
    });
  }

  // Gestion des primes
  viewPrimeDetails(prime: Prime): void {
    this.selectedPrime = prime;
    this.showPrimeDetails = true;
  }

  closePrimeDetails(): void {
    this.showPrimeDetails = false;
    this.selectedPrime = null;
  }

  showAddPrimeForm(): void {
    this.isEditModePrime = false;
    this.selectedPrime = null;
    this.primeForm.reset({
      employeeId: '',
      libelle: '',
      montant: 0,
      nombrePoints: 0,
      description: ''
    });
    this.showPrimeForm = true;
  }

  editPrime(prime: Prime): void {
    this.isEditModePrime = true;
    this.selectedPrime = prime;
    this.primeForm.setValue({
      employeeId: prime.employee.id,
      libelle: prime.libelle,
      montant: prime.montant,
      nombrePoints: prime.nombrePoints,
      description: prime.description
    });
    this.showPrimeForm = true;
  }

  savePrime(): void {
    this.submittedPrime = true;

    if (this.primeForm.invalid) {
      this.primeForm.markAllAsTouched();
      return;
    }

    const formValue = this.primeForm.getRawValue();
    const selectedEmployee = this.employees.find(emp => emp.id === formValue.employeeId);

    if (!selectedEmployee) {
      this.showToast('Employé non trouvé', 'error');
      return;
    }

    const primeData: Omit<Prime, 'id' | 'createdAt' | 'updatedAt' | 'fichePaie'> = {
      libelle: formValue.libelle,
      montant: formValue.montant,
      nombrePoints: formValue.nombrePoints,
      description: formValue.description,
      employee: selectedEmployee
    };

    if (this.isEditModePrime && this.selectedPrime) {
      this.hrService.updatePrime(this.selectedPrime.id, primeData);
      this.showToast('Prime modifiée avec succès', 'success');
    } else {
      this.hrService.addPrime(primeData);
      this.showToast('Prime ajoutée avec succès', 'success');
    }

    this.closePrimeForm();
    this.loadPrimes();
  }

  closePrimeForm(): void {
    this.showPrimeForm = false;
    this.isEditModePrime = false;
    this.selectedPrime = null;
    this.submittedPrime = false;
    this.primeForm.reset();
  }

  deletePrime(id: string): void {
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
        this.hrService.deletePrime(id);
        Swal.fire('Supprimée !', 'La prime a été supprimée.', 'success');
        this.loadPrimes();
      }
    });
  }

  addPrimeToFiche(): void {
    // À implémenter - ajouter une prime existante à la fiche
    console.log('Ajouter prime à la fiche');
  }

  removePrimeFromFiche(primeId: string): void {
    if (confirm('Retirer cette prime de la fiche ?')) {
      // À implémenter - retirer la prime de la fiche
      console.log('Retirer prime de la fiche:', primeId);
    }
  }

  // Gestion des retenues
  viewRetenueDetails(retenue: Retenue): void {
    this.selectedRetenue = retenue;
    this.showRetenueDetails = true;
  }

  closeRetenueDetails(): void {
    this.showRetenueDetails = false;
    this.selectedRetenue = null;
  }

  showAddRetenueForm(): void {
    this.isEditModeRetenue = false;
    this.selectedRetenue = null;
    this.retenueForm.reset({
      employeeId: '',
      libelle: '',
      montant: 0,
      nombrePoints: 0,
      description: ''
    });
    this.showRetenueForm = true;
  }

  editRetenue(retenue: Retenue): void {
    this.isEditModeRetenue = true;
    this.selectedRetenue = retenue;
    this.retenueForm.setValue({
      employeeId: retenue.employee.id,
      libelle: retenue.libelle,
      montant: retenue.montant,
      nombrePoints: retenue.nombrePoints,
      description: retenue.description
    });
    this.showRetenueForm = true;
  }

  saveRetenue(): void {
    this.submittedRetenue = true;

    if (this.retenueForm.invalid) {
      this.retenueForm.markAllAsTouched();
      return;
    }

    const formValue = this.retenueForm.getRawValue();
    const selectedEmployee = this.employees.find(emp => emp.id === formValue.employeeId);

    if (!selectedEmployee) {
      this.showToast('Employé non trouvé', 'error');
      return;
    }

    const retenueData: Omit<Retenue, 'id' | 'createdAt' | 'updatedAt' | 'fichePaie'> = {
      libelle: formValue.libelle,
      montant: formValue.montant,
      nombrePoints: formValue.nombrePoints,
      description: formValue.description,
      employee: selectedEmployee
    };

    if (this.isEditModeRetenue && this.selectedRetenue) {
      this.hrService.updateRetenue(this.selectedRetenue.id, retenueData);
      this.showToast('Retenue modifiée avec succès', 'success');
    } else {
      this.hrService.addRetenue(retenueData);
      this.showToast('Retenue ajoutée avec succès', 'success');
    }

    this.closeRetenueForm();
    this.loadRetenues();
  }

  closeRetenueForm(): void {
    this.showRetenueForm = false;
    this.isEditModeRetenue = false;
    this.selectedRetenue = null;
    this.submittedRetenue = false;
    this.retenueForm.reset();
  }

  closeRegleForm(): void {
    this.showRegleForm = false;
    this.isEditModeRegle = false;
    this.selectedRegle = null;
    this.submittedRegle = false;
    this.regleForm.reset();
  }

  showAddRegleForm(): void {
    this.isEditModeRegle = false;
    this.selectedRegle = null;
    this.regleForm.reset({
      libelle: '',
      description: '',
      typeRegle: TypeRegle.PRIME,
      statut: StatutRegle.ACTIVE,
      minAnciennete: 0,
      maxAnciennete: null,
      montantFixe: null,
      pourcentageSalaire: null,
      nombreJours: 0,
      nombrePoints: 0,
      conditionsSpecifiques: '',
      applicationAutomatique: true
    });
    this.showRegleForm = true;
  }

  saveRegle(): void {
    this.submittedRegle = true;

    if (this.regleForm.invalid) {
      this.regleForm.markAllAsTouched();
      return;
    }

    const formValue = this.regleForm.getRawValue();
    const regleData: Omit<Regle, 'id' | 'createdAt' | 'updatedAt'> = {
      libelle: formValue.libelle,
      description: formValue.description,
      typeRegle: formValue.typeRegle,
      statut: formValue.statut,
      minAnciennete: formValue.minAnciennete,
      maxAnciennete: formValue.maxAnciennete,
      montantFixe: formValue.montantFixe,
      pourcentageSalaire: formValue.pourcentageSalaire,
      nombreJours: formValue.nombreJours,
      nombrePoints: formValue.nombrePoints,
      conditions: formValue.conditions,
      automatique: formValue.automatique,
      dateCreation: this.isEditModeRegle && this.selectedRegle ? this.selectedRegle.dateCreation : new Date(),
      dateModification: new Date(),
      prime: undefined,
      retenue: undefined
    };

    if (this.isEditModeRegle && this.selectedRegle) {
      this.hrService.updateRegle(this.selectedRegle.id, regleData);
      this.showToast('Règle modifiée avec succès', 'success');
    } else {
      this.hrService.addRegle(regleData);
      this.showToast('Règle ajoutée avec succès', 'success');
    }

    this.closeRegleForm();
    this.loadRegles();
  }

  closeRegleDetails(): void {
    this.showRegleDetails = false;
    this.selectedRegle = null;
  }

  viewRegleDetails(regle: Regle): void {
    this.selectedRegle = regle;
    this.showRegleDetails = true;
  }

  editRegle(regle: Regle): void {
    this.isEditModeRegle = true;
    this.selectedRegle = regle;
    this.regleForm.patchValue({
      libelle: regle.libelle,
      description: regle.description,
      typeRegle: regle.typeRegle,
      statut: regle.statut,
      minAnciennete: regle.minAnciennete,
      maxAnciennete: regle.maxAnciennete,
      montantFixe: regle.montantFixe,
      pourcentageSalaire: regle.pourcentageSalaire,
      nombreJours: regle.nombreJours,
      nombrePoints: regle.nombrePoints,
      conditions: regle.conditions,
      automatique: regle.automatique
    });
    this.showRegleForm = true;
  }

  deleteRetenue(id: string): void {
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
        this.hrService.deleteRetenue(id);
        Swal.fire('Supprimée !', 'La retenue a été supprimée.', 'success');
        this.loadRetenues();
      }
    });
  }

  addRetenueToFiche(): void {
    // À implémenter - ajouter une retenue existante à la fiche
    console.log('Ajouter retenue à la fiche');
  }

  removeRetenueFromFiche(retenueId: string): void {
    if (confirm('Retirer cette retenue de la fiche ?')) {
      // À implémenter - retirer la retenue de la fiche
      console.log('Retirer retenue de la fiche:', retenueId);
    }
  }

  // Filtres et recherche
  applyCongeFilters(): void {
    let filtered = [...this.conges];

    if (this.searchTermConge) {
      const term = this.searchTermConge.toLowerCase();
      filtered = filtered.filter(conge => {
        const employee = this.getEmployeeName(conge.employee.id);
        return employee.toLowerCase().includes(term) ||
          conge.motif.toLowerCase().includes(term);
      });
    }

    if (this.filterEmployeeId) {
      filtered = filtered.filter(conge => conge.employee.id === this.filterEmployeeId);
    }

    if (this.filterTypeConge) {
      filtered = filtered.filter(conge => conge.type === this.filterTypeConge);
    }

    if (this.filterStatutConge) {
      filtered = filtered.filter(conge => conge.statut === this.filterStatutConge);
    }

    this.filteredConges = filtered;
  }

  applyFicheFilters(): void {
    let filtered = [...this.fiches];

    if (this.searchTermFiche) {
      const term = this.searchTermFiche.toLowerCase();
      filtered = filtered.filter(fiche => {
        const employee = this.getEmployeeName(fiche.employee.id);
        return employee.toLowerCase().includes(term);
      });
    }

    if (this.filterEmployeeIdFiche) {
      filtered = filtered.filter(fiche => fiche.employee.id === this.filterEmployeeIdFiche);
    }

    if (this.filterStatutFiche) {
      filtered = filtered.filter(fiche => fiche.statut === this.filterStatutFiche);
    }

    this.filteredFiches = filtered;
  }

  applyPrimeFilters(): void {
    let filtered = [...this.primes];

    if (this.searchTermPrime) {
      const term = this.searchTermPrime.toLowerCase();
      filtered = filtered.filter(prime =>
        prime.libelle.toLowerCase().includes(term) ||
        prime.description.toLowerCase().includes(term) ||
        this.getEmployeeName(prime.employee.id).toLowerCase().includes(term)
      );
    }

    if (this.filterEmployeeIdPrime) {
      filtered = filtered.filter(prime => prime.employee.id === this.filterEmployeeIdPrime);
    }

    if (this.filterMontantMinPrime !== null) {
      filtered = filtered.filter(prime => prime.montant >= this.filterMontantMinPrime!);
    }

    if (this.filterMontantMaxPrime !== null) {
      filtered = filtered.filter(prime => prime.montant <= this.filterMontantMaxPrime!);
    }

    this.filteredPrimes = filtered;
  }

  applyRetenueFilters(): void {
    let filtered = [...this.retenues];

    if (this.searchTermRetenue) {
      const term = this.searchTermRetenue.toLowerCase();
      filtered = filtered.filter(retenue =>
        retenue.libelle.toLowerCase().includes(term) ||
        retenue.description.toLowerCase().includes(term) ||
        this.getEmployeeName(retenue.employee.id).toLowerCase().includes(term)
      );
    }

    if (this.filterEmployeeIdRetenue) {
      filtered = filtered.filter(retenue => retenue.employee.id === this.filterEmployeeIdRetenue);
    }

    if (this.filterMontantMinRetenue !== null) {
      filtered = filtered.filter(retenue => retenue.montant >= this.filterMontantMinRetenue!);
    }

    if (this.filterMontantMaxRetenue !== null) {
      filtered = filtered.filter(retenue => retenue.montant <= this.filterMontantMaxRetenue!);
    }

    this.filteredRetenues = filtered;
  }

  clearCongeFilters(): void {
    this.searchTermConge = '';
    this.filterEmployeeId = '';
    this.filterTypeConge = '';
    this.filterStatutConge = '';
    this.applyCongeFilters();
  }

  clearFicheFilters(): void {
    this.searchTermFiche = '';
    this.filterEmployeeIdFiche = '';
    this.filterStatutFiche = '';
    this.applyFicheFilters();
  }

  clearPrimeFilters(): void {
    this.searchTermPrime = '';
    this.filterEmployeeIdPrime = '';
    this.filterMontantMinPrime = null;
    this.filterMontantMaxPrime = null;
    this.applyPrimeFilters();
  }

  clearRetenueFilters(): void {
    this.searchTermRetenue = '';
    this.filterEmployeeIdRetenue = '';
    this.filterMontantMinRetenue = null;
    this.filterMontantMaxRetenue = null;
    this.applyRetenueFilters();
  }

  applyRegleFilters(): void {
    let filtered = [...this.regles];

    if (this.searchTermRegle) {
      const term = this.searchTermRegle.toLowerCase();
      filtered = filtered.filter(regle =>
        regle.libelle.toLowerCase().includes(term) ||
        regle.description.toLowerCase().includes(term)
      );
    }

    if (this.filterTypeRegle) {
      filtered = filtered.filter(regle => regle.typeRegle === this.filterTypeRegle);
    }

    if (this.filterStatutRegle) {
      filtered = filtered.filter(regle => regle.statut === this.filterStatutRegle);
    }

    if (this.filterAncienneteMin !== null) {
      filtered = filtered.filter(regle => regle.minAnciennete >= this.filterAncienneteMin!);
    }

    if (this.filterAncienneteMax !== null) {
      filtered = filtered.filter(regle =>
        regle.maxAnciennete === null || regle.maxAnciennete <= this.filterAncienneteMax!
      );
    }

    this.filteredRegles = filtered;
  }

  clearRegleFilters(): void {
    this.searchTermRegle = '';
    this.filterTypeRegle = '';
    this.filterStatutRegle = '';
    this.filterAncienneteMin = null;
    this.filterAncienneteMax = null;
    this.applyRegleFilters();
  }

  // Calculs automatiques
  private calculateNombreJours(): void {
    const dateDebut = this.congeForm.get('dateDebut')?.value;
    const dateFin = this.congeForm.get('dateFin')?.value;

    if (dateDebut && dateFin) {
      const debut = new Date(dateDebut);
      const fin = new Date(dateFin);
      const diffTime = Math.abs(fin.getTime() - debut.getTime());
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
      this.congeForm.patchValue({ nombreJours: diffDays }, { emitEvent: false });
    }
  }

  private calculateNetAPayer(): void {
    const salaireDeBase = this.ficheForm.get('salaireDeBase')?.value || 0;
    const employeeId = this.ficheForm.get('employeeId')?.value;

    if (!employeeId) {
      this.ficheForm.patchValue({ netAPayer: salaireDeBase }, { emitEvent: false });
      return;
    }

    // Calcul avec primes et retenues de l'employé
    const totalPrimes = this.getTotalPrimesEmployee(employeeId);
    const totalRetenues = this.getTotalRetenuesEmployee(employeeId);
    const cotisations = salaireDeBase * 0.09; // 9% de cotisations
    const impots = salaireDeBase * 0.05; // 5% d'impôts

    const netAPayer = salaireDeBase + totalPrimes - totalRetenues - cotisations - impots;

    this.ficheForm.patchValue({ netAPayer: Math.round(netAPayer * 100) / 100 }, { emitEvent: false });
  }

  // Méthodes utilitaires
  getInitials(nom: string, prenom: string): string {
    return `${prenom.charAt(0)}${nom.charAt(0)}`.toUpperCase();
  }

  getEmployeeName(employeeId: string): string {
    const employee = this.employees.find(e => e.id === employeeId);
    return employee ? `${employee.prenom} ${employee.nom}` : 'Employé inconnu';
  }

  getEmployeeEmail(employeeId: string): string {
    const employee = this.employees.find(e => e.id === employeeId);
    return employee ? employee.email : '';
  }

  getEmployeeInitials(employeeId: string): string {
    const employee = this.employees.find(e => e.id === employeeId);
    return employee ? this.getInitials(employee.nom, employee.prenom) : 'XX';
  }

  getStatusClass(statut: StatutEmployee | StatutConge | StatutFichePaie | null | undefined): string {
    if (!statut) {
      return 'status-unknown'; // classe par défaut
    }
    return statut.toString().toLowerCase().replace(/[^a-z]/g, 'a');
  }

  getCongeStatusClass(statut: StatutConge): string {
    return statut.toLowerCase().replace(/[^a-z]/g, '-');
  }

  getTypeCongeClass(type: TypeConge): string {
    return type.toLowerCase().replace(/[^a-z]/g, '');
  }

  getFicheStatusClass(statut: StatutFichePaie): string {
    return statut.toLowerCase().replace(/[^a-z]/g, '');
  }

  private formatDateForInput(date: Date): string {
    return new Date(date).toISOString().split('T')[0];
  }

  private getDefaultDateFin(): Date {
    const date = new Date();
    date.setDate(date.getDate() + 7);
    return date;
  }

  // Validation personnalisée
  get f() { return this.employeeForm.controls; }
  get fc() { return this.congeForm.controls; }
  get ff() { return this.ficheForm.controls; }
  get fp() { return this.primeForm.controls; }
  get fr() { return this.retenueForm.controls; }
  get frg() { return this.regleForm.controls; }

  getViewTitle(): string {
    switch (this.currentView) {
      case 'employees': return 'Gestion des Employés';
      case 'conges': return 'Gestion des Congés';
      case 'fiches': return 'Fiches de Paie';
      case 'primes': return 'Gestion des Primes';
      case 'retenues': return 'Gestion des Retenues';
      default: return 'Gestion RH';
    }
  }

  getViewFeatures(): string[] {
    switch (this.currentView) {
      case 'employees':
        return [
          'Gestion complète des employés',
          'Système de points',
          'Pièces d\'identité',
          'Historique des modifications',
          'Export des données'
        ];
      case 'conges':
        return [
          'Demandes de congés',
          'Workflow d\'approbation',
          'Calcul automatique des jours',
          'Déduction du solde',
          'Historique complet'
        ];
      case 'fiches':
        return [
          'Génération automatique',
          'Calcul des primes et retenues',
          'Validation comptable',
          'Export PDF',
          'Archivage sécurisé'
        ];
      case 'primes':
        return [
          'Gestion des primes',
          'Système de points',
          'Attribution par employé',
          'Historique des primes',
          'Calculs automatiques'
        ];
      case 'retenues':
        return [
          'Gestion des retenues',
          'Calculs automatiques',
          'Types de retenues',
          'Historique complet',
          'Validation comptable'
        ];
      default:
        return [];
    }
  }

  // Méthodes pour les nouvelles vues
  getEmployeePrimes(employeeId: string): Prime[] {
    return this.primes.filter(p => p.employee.id === employeeId);
  }

  getEmployeeRetenues(employeeId: string): Retenue[] {
    return this.retenues.filter(r => r.employee.id === employeeId);
  }

  getEmployeeFiches(employeeId: string): FichePaie[] {
    return this.fiches.filter(f => f.employee.id === employeeId);
  }

  getTotalPrimesEmployee(employeeId: string): number {
    return this.getEmployeePrimes(employeeId).reduce((total, prime) => total + prime.montant, 0);
  }

  getTotalRetenuesEmployee(employeeId: string): number {
    return this.getEmployeeRetenues(employeeId).reduce((total, retenue) => total + retenue.montant, 0);
  }

  getTotalPointsEmployee(employeeId: string): number {
    const primes = this.getEmployeePrimes(employeeId).reduce((total, prime) => total + prime.nombrePoints, 0);
    const retenues = this.getEmployeeRetenues(employeeId).reduce((total, retenue) => total + retenue.nombrePoints, 0);
    return primes - retenues;
  }

  getTotalPrimesFiche(ficheId: string): number {
    const fiche = this.fiches.find(f => f.id === ficheId);
    return fiche ? fiche.primes.reduce((total, prime) => total + prime.montant, 0) : 0;
  }

  getTotalRetenuesFiche(ficheId: string): number {
    const fiche = this.fiches.find(f => f.id === ficheId);
    return fiche ? fiche.retenues.reduce((total, retenue) => total + retenue.montant, 0) : 0;
  }

  // Méthodes pour les règles
  getEmployeeReglesApplicables(employeeId: string): Regle[] {
    return this.hrService.getReglesApplicables(employeeId);
  }

  calculateEmployeeAnciennete(dateEmbauche: Date): number {
    const today = new Date();
    const embauche = new Date(dateEmbauche);
    const diffTime = Math.abs(today.getTime() - embauche.getTime());
    const diffYears = Math.floor(diffTime / (1000 * 60 * 60 * 24 * 365.25));
    return diffYears;
  }

  getRegleTypeClass(type: TypeRegle): string {
    return type.toLowerCase();
  }

  getRegleStatutClass(statut: StatutRegle): string {
    return statut.toLowerCase();
  }

  calculateRegleMontant(regle: Regle, salaire: number): number {
    if (regle.montantFixe) {
      return regle.montantFixe;
    } else if (regle.pourcentageSalaire) {
      return salaire * regle.pourcentageSalaire;
    }
    return 0;
  }

  deleteRegle(id: string): void {
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
        this.hrService.deleteRegle(id);
        Swal.fire('Supprimée !', 'La règle a été supprimée.', 'success');
        this.loadRegles();
      }
    });
  }
}