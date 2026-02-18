import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LivrablePopup } from './livrable-popup';

describe('LivrablePopup', () => {
  let component: LivrablePopup;
  let fixture: ComponentFixture<LivrablePopup>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LivrablePopup]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LivrablePopup);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
