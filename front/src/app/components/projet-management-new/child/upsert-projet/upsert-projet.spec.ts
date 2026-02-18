import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpsertProjet } from './upsert-projet';

describe('UpsertProjet', () => {
  let component: UpsertProjet;
  let fixture: ComponentFixture<UpsertProjet>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpsertProjet]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpsertProjet);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
