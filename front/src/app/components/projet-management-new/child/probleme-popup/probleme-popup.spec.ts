import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProblemePopup } from './probleme-popup';

describe('ProblemePopup', () => {
  let component: ProblemePopup;
  let fixture: ComponentFixture<ProblemePopup>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProblemePopup]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProblemePopup);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
