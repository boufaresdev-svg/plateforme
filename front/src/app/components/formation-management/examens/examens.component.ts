import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

import { Examen } from '../../../models/formation/Examen.model';
import { Certificat } from '../../../models/formation/Certificat.model';
import { PlanFormation } from '../../../models/formation/PlanFormation.model';
import { Apprenant } from '../../../models/formation/Apprenant.model';

import { ExamenService } from '../../../services/formation_managment/Examen.service';
import { CertificatService } from '../../../services/formation_managment/Certificat.service';
import { PlanFormationService } from '../../../services/formation_managment/PlanFormation.service';
import { ApprenantService } from '../../../services/formation_managment/Apprenants.service';

@Component({
  selector: 'app-examens',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './examens.component.html',
  styleUrls: ['./examens.component.css']
})
export class ExamensComponent implements OnInit {
  examens: Examen[] = [];
  certificats: Certificat[] = [];
  plansFormation: PlanFormation[] = [];
  apprenants: Apprenant[] = [];

  selectedExamen: Examen | null = null;
  selectedCertificat: Certificat | null = null;
  isEditMode = false;
  isLoading = false;
  searchTerm = '';
  currentTab: 'examens' | 'certificats' = 'examens';

  certificatsByExamen: { [idExamen: number]: Certificat[] } = {};

  // Form Data
  examenFormData = {
    type: '',
    date: this.toInputDate(new Date()),
    description: '',
    score: undefined as number | undefined,
    idPlanFormation: undefined as number | undefined,
    idApprenant: undefined as number | undefined
  };

  certificatFormData = {
    titre: '',
    description: '',
    niveau: '',
    idExamen: undefined as number | undefined,
    idApprenant: undefined as number | undefined
  };

  // Modal control
  showModal = false;
  modalType: 'examen' | 'certificat' | null = null;
  modalTitle = '';

  constructor(
    private examenService: ExamenService,
    private certificatService: CertificatService,
    private planFormationService: PlanFormationService,
    private apprenantService: ApprenantService
  ) {}

  ngOnInit() {
    this.loadAll();
  }

  loadAll() {
    this.isLoading = true;
    this.loadExamens();
    this.loadCertificats();
    this.loadPlansFormation();
    this.loadApprenants();
    setTimeout(() => {
      this.isLoading = false;
    }, 1000);
  }

  loadExamens() {
    this.examenService.getAllExamens().subscribe({
      next: (data: any) => {
        this.examens = data;
      },
      error: (error: any) => {
        console.error('Erreur:', error);
        Swal.fire('Erreur', 'Erreur lors du chargement des examens', 'error');
      }
    });
  }

  loadCertificats() {
    this.certificatService.getAllCertificats().subscribe({
      next: (data: any) => {
        this.certificats = data;
        data.forEach((cert: any) => {
          if (cert.examen?.idExamen) {
            if (!this.certificatsByExamen[cert.examen.idExamen]) {
              this.certificatsByExamen[cert.examen.idExamen] = [];
            }
            this.certificatsByExamen[cert.examen.idExamen].push(cert);
          }
        });
      },
      error: (error: any) => {
        console.error('Erreur:', error);
      }
    });
  }

  loadPlansFormation() {
    this.planFormationService.getAllPlans().subscribe({
      next: (data: any) => {
        this.plansFormation = data;
      },
      error: (error: any) => {
        console.error('Erreur:', error);
      }
    });
  }

  loadApprenants() {
    this.apprenantService.getAllApprenants().subscribe({
      next: (data: any) => {
        this.apprenants = data;
      },
      error: (error: any) => {
        console.error('Erreur:', error);
      }
    });
  }

  toInputDate(date: Date): string {
    const d = new Date(date);
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${d.getFullYear()}-${month}-${day}`;
  }

  filteredItems(): any[] {
    const searchLower = this.searchTerm.toLowerCase();
    if (this.currentTab === 'examens') {
      return this.examens.filter(
        (e) =>
          e.type?.toLowerCase().includes(searchLower) ||
          e.description?.toLowerCase().includes(searchLower)
      );
    } else {
      return this.certificats.filter(
        (c) =>
          c.titre?.toLowerCase().includes(searchLower) ||
          c.niveau?.toLowerCase().includes(searchLower)
      );
    }
  }

  openModal(type: string) {
    this.modalType = type as any;
    this.showModal = true;
    this.updateModalTitle();
  }

  updateModalTitle() {
    const action = this.isEditMode ? 'Modifier' : 'Nouveau';
    const names = {
      examen: 'examen',
      certificat: 'certificat'
    };
    this.modalTitle = `${action} ${names[this.modalType!] || ''}`;
  }

  closeModal() {
    this.showModal = false;
    this.modalType = null;
    this.resetForms();
  }

  resetForms() {
    this.examenFormData = {
      type: '',
      date: this.toInputDate(new Date()),
      description: '',
      score: undefined,
      idPlanFormation: undefined,
      idApprenant: undefined
    };
    this.certificatFormData = {
      titre: '',
      description: '',
      niveau: '',
      idExamen: undefined,
      idApprenant: undefined
    };
    this.isEditMode = false;
    this.selectedExamen = null;
    this.selectedCertificat = null;
  }

  // =============== EXAMENS ===============
  editExamen(examen: Examen) {
    this.examenFormData = {
      type: examen.type || '',
      date: examen.date ? this.toInputDate(new Date(examen.date)) : this.toInputDate(new Date()),
      description: examen.description || '',
      score: examen.score,
      idPlanFormation: examen.planFormation?.idPlanFormation,
      idApprenant: examen.apprenant?.idApprenant
    };
    this.selectedExamen = examen;
    this.isEditMode = true;
    this.openModal('examen');
  }

  saveExamen() {
    if (!this.examenFormData.type) {
      Swal.fire('Erreur', 'Le type est requis', 'error');
      return;
    }

    if (this.isEditMode && this.selectedExamen?.idExamen) {
      const payload = { ...this.examenFormData, idExamen: this.selectedExamen.idExamen };
      this.examenService.updateExamen(payload).subscribe({
        next: (data: any) => {
          Swal.fire('Succès', 'Examen mis à jour', 'success');
          this.closeModal();
          this.loadAll();
        },
        error: (error: any) => {
          console.error('Erreur:', error);
          Swal.fire('Erreur', 'Erreur lors de la mise à jour', 'error');
        }
      });
    } else {
      this.examenService.createExamen(this.examenFormData).subscribe({
        next: (data: any) => {
          Swal.fire('Succès', 'Examen créé', 'success');
          this.closeModal();
          this.loadAll();
        },
        error: (error: any) => {
          console.error('Erreur:', error);
          Swal.fire('Erreur', 'Erreur lors de la création', 'error');
        }
      });
    }
  }

  deleteExamen(id: number) {
    Swal.fire({
      title: 'Confirmer la suppression',
      text: 'Êtes-vous sûr?',
      icon: 'warning',
      showCancelButton: true
    }).then((result: any) => {
      if (result.isConfirmed) {
        this.examenService.deleteExamen(id).subscribe({
          next: (data: any) => {
            Swal.fire('Supprimé', 'Examen supprimé', 'success');
            this.loadAll();
          },
          error: (error: any) => {
            console.error('Erreur:', error);
            Swal.fire('Erreur', 'Erreur lors de la suppression', 'error');
          }
        });
      }
    });
  }

  // =============== CERTIFICATS ===============
  editCertificat(certificat: Certificat) {
    this.certificatFormData = {
      titre: certificat.titre || '',
      description: certificat.description || '',
      niveau: certificat.niveau || '',
      idExamen: certificat.examen?.idExamen,
      idApprenant: certificat.apprenant?.idApprenant
    };
    this.selectedCertificat = certificat;
    this.isEditMode = true;
    this.openModal('certificat');
  }

  saveCertificat() {
    if (!this.certificatFormData.titre) {
      Swal.fire('Erreur', 'Le titre est requis', 'error');
      return;
    }

    if (this.isEditMode && this.selectedCertificat?.idCertificat) {
      this.certificatService.updateCertificat(this.selectedCertificat.idCertificat, this.certificatFormData).subscribe({
        next: (data: any) => {
          Swal.fire('Succès', 'Certificat mis à jour', 'success');
          this.closeModal();
          this.loadAll();
        },
        error: (error: any) => {
          console.error('Erreur:', error);
          Swal.fire('Erreur', 'Erreur lors de la mise à jour', 'error');
        }
      });
    } else {
      this.certificatService.createCertificat(this.certificatFormData).subscribe({
        next: (data: any) => {
          Swal.fire('Succès', 'Certificat créé', 'success');
          this.closeModal();
          this.loadAll();
        },
        error: (error: any) => {
          console.error('Erreur:', error);
          Swal.fire('Erreur', 'Erreur lors de la création', 'error');
        }
      });
    }
  }

  deleteCertificat(id: number) {
    Swal.fire({
      title: 'Confirmer la suppression',
      text: 'Êtes-vous sûr?',
      icon: 'warning',
      showCancelButton: true
    }).then((result: any) => {
      if (result.isConfirmed) {
        this.certificatService.deleteCertificat(id).subscribe({
          next: (data: any) => {
            Swal.fire('Supprimé', 'Certificat supprimé', 'success');
            this.loadAll();
          },
          error: (error: any) => {
            console.error('Erreur:', error);
            Swal.fire('Erreur', 'Erreur lors de la suppression', 'error');
          }
        });
      }
    });
  }

  viewCertificatsForExamen(examen: Examen) {
    if (examen.idExamen) {
      // Could expand to show certificats for this examen
    }
  }
}
