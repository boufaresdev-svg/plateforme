import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-upsert-affectation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './upsert-affectation.html',
  styleUrls: ['./upsert-affectation.css']
})
export class UpsertAffectation implements OnInit {

  @Input() employees: any[] = [];
  @Input() rolesProjets: string[] = [];
  @Input() showAffectationForm: boolean = false;

  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<any>();

  affectationForm!: FormGroup;
  submittedAffectation = false;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.affectationForm = this.fb.group({
      employeId: [null, Validators.required],
      role: [null, Validators.required],
      tauxHoraire: [0, [Validators.required, Validators.min(0)]],
      heuresAllouees: [1, [Validators.required, Validators.min(1)]]
    });
  }

  get fa() {
    return this.affectationForm.controls;
  }

  saveAffectation() {
    this.submittedAffectation = true;
    if (this.affectationForm.invalid) return;
    this.save.emit(this.affectationForm.value);
  }

  closePopup() {
    this.close.emit();
  }
}
