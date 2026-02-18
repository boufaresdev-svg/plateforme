import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

import { Formateur } from '../../../models/formation/Formateur.model';
import { FormateurService } from '../../../services/formation_managment/Formateur.service';

@Component({
  selector: 'app-formateurs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './formateurs.component.html',
  styleUrls: ['./formateurs.component.css']
})
export class FormateursComponent implements OnInit {
  formateurs: Formateur[] = [];
  selectedFormateur: Formateur | null = null;
  isEditMode = false;
  isLoading = false;
  searchTerm = '';

  formateurFormData: any = {
    nom: '',
    prenom: '',
    specialite: '',
    contact: '',
    experience: ''
  };

  showModal = false;
  modalType: 'formateur' | null = null;
  modalTitle = '';

  constructor(private formateurService: FormateurService) {}

  ngOnInit() {
    this.loadFormateurs();
  }

  loadFormateurs() {
    this.isLoading = true;
    this.formateurService.getAllFormateurs().subscribe({
      next: (data: any) => {
        this.formateurs = data;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des formateurs:', error);
        Swal.fire('Erreur', 'Erreur lors du chargement des formateurs', 'error');
        this.isLoading = false;
      }
    });
  }

  filteredFormateurs(): Formateur[] {
    if (!this.searchTerm) return this.formateurs;
    return this.formateurs.filter((f) => {
      const searchLower = this.searchTerm.toLowerCase();
      return (
        f.nom?.toLowerCase().includes(searchLower) ||
        f.prenom?.toLowerCase().includes(searchLower) ||
        f.specialite?.toLowerCase().includes(searchLower) ||
        f.contact?.toLowerCase().includes(searchLower)
      );
    });
  }

  openModal(type: string) {
    this.modalType = type as any;
    this.showModal = true;
    this.modalTitle = this.isEditMode ? 'Modifier un formateur' : 'Nouveau formateur';
  }

  closeModal() {
    this.showModal = false;
    this.modalType = null;
    this.resetForm();
  }

  resetForm() {
    this.formateurFormData = {
      nom: '',
      prenom: '',
      specialite: '',
      contact: '',
      experience: ''
    };
    this.isEditMode = false;
    this.selectedFormateur = null;
  }

  editFormateur(formateur: Formateur) {
    this.formateurFormData = { ...formateur };
    this.selectedFormateur = formateur;
    this.isEditMode = true;
    this.openModal('formateur');
  }

  saveFormateur() {
    if (!this.formateurFormData.nom || !this.formateurFormData.prenom) {
      Swal.fire('Erreur', 'Le nom et prénom sont requis', 'error');
      return;
    }

    if (this.isEditMode && this.selectedFormateur?.idFormateur) {
      this.formateurService.updateFormateur(this.selectedFormateur.idFormateur, this.formateurFormData).subscribe({
        next: (data: any) => {
          Swal.fire('Succès', 'Formateur mis à jour avec succès', 'success');
          this.closeModal();
          this.loadFormateurs();
        },
        error: (error: any) => {
          console.error('Erreur:', error);
          Swal.fire('Erreur', 'Erreur lors de la mise à jour', 'error');
        }
      });
    } else {
      this.formateurService.addFormateur(this.formateurFormData).subscribe({
        next: (data: any) => {
          Swal.fire('Succès', 'Formateur créé avec succès', 'success');
          this.closeModal();
          this.loadFormateurs();
        },
        error: (error: any) => {
          console.error('Erreur:', error);
          Swal.fire('Erreur', 'Erreur lors de la création', 'error');
        }
      });
    }
  }

  deleteFormateur(id: number) {
    Swal.fire({
      title: 'Confirmer la suppression',
      text: 'Êtes-vous sûr de vouloir supprimer ce formateur?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Oui, supprimer',
      cancelButtonText: 'Annuler'
    }).then((result: any) => {
      if (result.isConfirmed) {
        this.formateurService.deleteFormateur(id).subscribe({
          next: (data: any) => {
            Swal.fire('Supprimé', 'Formateur supprimé avec succès', 'success');
            this.loadFormateurs();
          },
          error: (error: any) => {
            console.error('Erreur:', error);
            Swal.fire('Erreur', 'Erreur lors de la suppression', 'error');
          }
        });
      }
    });
  }
}
