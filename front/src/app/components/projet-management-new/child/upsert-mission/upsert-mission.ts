import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-upsert-mission',
  templateUrl: './upsert-mission.html',
  styleUrls: ['./upsert-mission.css'],
  standalone: true,
  imports: [CommonModule]
})
export class UpsertMission {

  @Input() show = false;
  @Input() isEditMode = false;
  @Input() projets: any[] = [];
  @Input() phases: any[] = [];
  @Input() missionData: any = null;

  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<any>();

  submitted = false;
  missionForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.missionForm = this.fb.group({
      id: [''],
      projetId: [null, Validators.required],
      phaseId: [null, Validators.required],
      nom: ['', [Validators.required, Validators.minLength(3)]],
      priorite: ['', Validators.required],
      statut: ['', Validators.required],
      budget: [0, Validators.min(0)],
      dateDebut: ['', Validators.required],
      dateFin: ['', Validators.required],
      progression: [0, [Validators.min(0), Validators.max(100)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      objectif: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  ngOnChanges() {
    if (this.missionData) {
      this.missionForm.patchValue(this.missionData);
    }
  }

  get f() {
    return this.missionForm.controls;
  }

  onSubmit() {
    this.submitted = true;
    if (this.missionForm.invalid) return;
    this.save.emit(this.missionForm.value);
  }

  onClose() {
    this.close.emit();
  }
}
