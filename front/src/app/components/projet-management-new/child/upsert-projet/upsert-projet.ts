import { Component, EventEmitter, Input, OnInit, Output, OnChanges, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

export interface Projet {
  id?: string;
  nom: string;
  type: string;
  priorite: string;
  statut: string;
  chefProjet: string;
  client: string;
  description: string;
  dateDebut: string;
  dateFinPrevue: string;
  budget: number;
}

@Component({
  selector: 'app-upsert-projet',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './upsert-projet.html',
  styleUrls: ['./upsert-projet.css']
})
export class UpsertProjetComponent implements OnInit, OnChanges {

  @Input() show: boolean = false;
  @Input() projetToEdit?: Projet; // si présent, mode édition
  @Input() clients: any[] = [];
  @Input() typesProjet: string[] = [];
  @Input() prioritesProjets: string[] = [];
  @Input() statutsProjets: string[] = [];

  @Output() close = new EventEmitter<void>();
  @Output() saveProjet = new EventEmitter<Projet>(); // émettre le projet ajouté/modifié

  projetForm!: FormGroup;
  submitted: boolean = false;
  isEditMode: boolean = false;

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    this.initProjetForm();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['projetToEdit'] && this.projetToEdit) {
      this.isEditMode = true;
      this.projetForm.patchValue(this.projetToEdit);
    }
  }

  initProjetForm() {
    this.projetForm = this.fb.group({
      id: [''],
      nom: ['', [Validators.required, Validators.minLength(3)]],
      type: ['', Validators.required],
      priorite: ['', Validators.required],
      statut: ['', Validators.required],
      chefProjet: ['', Validators.required],
      client: ['', Validators.required],
      description: ['', [Validators.required, Validators.minLength(10)]],
      dateDebut: ['', Validators.required],
      dateFinPrevue: ['', Validators.required],
      budget: [0, [Validators.required, Validators.min(0)]]
    });
  }

  get f() { return this.projetForm.controls; }

  submitForm() {
    this.submitted = true;
    if (this.projetForm.invalid) return;

    const projet: Projet = this.projetForm.value;
    this.saveProjet.emit(projet); // le parent gère l'API
    this.closeForm();
  }

  closeForm() {
    this.submitted = false;
    this.projetForm.reset();
    this.isEditMode = false;
    this.close.emit();
  }
}
