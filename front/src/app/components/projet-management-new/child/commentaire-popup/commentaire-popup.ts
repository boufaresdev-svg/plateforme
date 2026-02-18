import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TypeCommentaire } from '../../../../models/projet/enum.model';

@Component({
  selector: 'app-commentaire-popup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './commentaire-popup.html',
  styleUrls: ['./commentaire-popup.css']
})
export class CommentairePopup {
  @Input() show: boolean = false;
  @Input() tacheId!: string;

  @Output() close = new EventEmitter<void>();
  @Output() commentaireData = new EventEmitter<any>();

  commentaireForm!: FormGroup;
  submitted: boolean = false;
  typesCommentaires = Object.values(TypeCommentaire);
  constructor(private fb: FormBuilder) {
    this.initForm();
  }

  initForm() {
    this.commentaireForm = this.fb.group({
      tacheId: ['', Validators.required],
      auteur: ['', Validators.required],
      type: ['', Validators.required],
      contenu: ['', Validators.required]
    });
  }

  get fc() { return this.commentaireForm.controls; }

  showForm() {
    this.submitted = false;
    this.commentaireForm.reset({ tacheId: this.tacheId });
  }

  save() {
    this.submitted = true;
    if (this.commentaireForm.invalid) return;

    this.commentaireData.emit(this.commentaireForm.value);
    this.closePopup();
  }

  closePopup() {
    this.submitted = false;
    this.commentaireForm.reset();
    this.close.emit();
  }
}
