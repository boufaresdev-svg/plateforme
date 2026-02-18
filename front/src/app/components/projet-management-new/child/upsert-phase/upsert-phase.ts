import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-upsert-phase',
  standalone: true,
  templateUrl: './upsert-phase.html',
  styleUrls: ['./upsert-phase.css']
})
export class UpsertPhaseComponent {

  @Input() show: boolean = false;
  @Input() projets: any[] = [];
  @Input() selectedPhase: any = null;

  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<any>();

  phaseForm: FormGroup;
  submittedPhase = false;

  constructor(private fb: FormBuilder) {
    this.phaseForm = this.fb.group({
      id: [''],
      projetId: ['', Validators.required],
      nom: ['', [Validators.required, Validators.minLength(3)]],
      ordre: [1, [Validators.required, Validators.min(1), Validators.max(5)]],
      statut: ['', Validators.required],
      dateDebut: ['', Validators.required],
      dateFin: ['', Validators.required],
      budget: [0, Validators.min(0)],
      progression: [0, [Validators.min(0), Validators.max(100)]],
      description: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  ngOnChanges() {
    if (this.selectedPhase) {
      this.phaseForm.patchValue(this.selectedPhase);
    } else {
      this.phaseForm.reset({
        projetId: '',
        ordre: 1,
        statut: 'PLANIFIEE',
        budget: 0,
        progression: 0
      });
    }
  }

  get fph() {
    return this.phaseForm.controls;
  }

  closePhaseForm() {
    this.show = false;
    this.close.emit();
  }

  savePhase() {
    this.submittedPhase = true;
    if (this.phaseForm.invalid) return;

    this.save.emit(this.phaseForm.value);
    this.closePhaseForm();
  }
}
