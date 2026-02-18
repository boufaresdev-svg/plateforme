import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommentairePopup } from './commentaire-popup';

describe('CommentairePopup', () => {
  let component: CommentairePopup;
  let fixture: ComponentFixture<CommentairePopup>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommentairePopup]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CommentairePopup);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
