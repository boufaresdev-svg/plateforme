import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CharrgePopup } from './charrge-popup';

describe('CharrgePopup', () => {
  let component: CharrgePopup;
  let fixture: ComponentFixture<CharrgePopup>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CharrgePopup]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CharrgePopup);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
