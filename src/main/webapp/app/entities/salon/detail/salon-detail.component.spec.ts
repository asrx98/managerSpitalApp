import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SalonDetailComponent } from './salon-detail.component';

describe('Component Tests', () => {
  describe('Salon Management Detail Component', () => {
    let comp: SalonDetailComponent;
    let fixture: ComponentFixture<SalonDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [SalonDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ salon: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(SalonDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SalonDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load salon on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.salon).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
