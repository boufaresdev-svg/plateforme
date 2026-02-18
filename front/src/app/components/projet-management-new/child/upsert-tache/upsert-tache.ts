import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-upsert-tache',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './upsert-tache.html',
  styleUrl: './upsert-tache.css'
})
export class UpsertTache {

  @Input() showTacheForm = false;
  @Input() selectedTache: any = null;
  @Input() missions: any[] = [];
  @Input() prioritesTaches: string[] = [];
  @Input() statutsTaches: string[] = [];

  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<any>();

  submittedTache = false;
  isEditModeTache = false;

  get isEdit(): boolean {
    return !!this.selectedTache;  // ✅ Automatique
  }

  tacheForm = this.fb.group({
    id: [''],
    missionId: ['', Validators.required],
    nom: ['', [Validators.required, Validators.minLength(3)]],
    priorite: ['', Validators.required],
    statut: ['', Validators.required],
    dureeEstimee: [0, [Validators.required, Validators.min(0)]],
    progression: [0, [Validators.min(0), Validators.max(100)]],
    dateDebut: ['', Validators.required],
    dateFin: ['', Validators.required],
    description: ['', [Validators.required, Validators.minLength(10)]]
  });

  constructor(private fb: FormBuilder) {}

  ngOnChanges() {
    if (this.selectedTache) {
      this.isEditModeTache = true;
      this.tacheForm.patchValue(this.selectedTache);
    } else {
      this.isEditModeTache = false;
      this.tacheForm.reset();
      this.tacheForm.patchValue({ progression: 0, dureeEstimee: 0 });
    }
  }

  get ft() { return this.tacheForm.controls; }

  saveTache() {
    this.submittedTache = true;
    if (this.tacheForm.invalid) return;

    this.save.emit(this.tacheForm.value);
  }

  closeTacheForm() {
    this.close.emit();
  }
}
