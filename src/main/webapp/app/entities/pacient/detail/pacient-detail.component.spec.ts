import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PacientDetailComponent } from './pacient-detail.component';

describe('Component Tests', () => {
  describe('Pacient Management Detail Component', () => {
    let comp: PacientDetailComponent;
    let fixture: ComponentFixture<PacientDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [PacientDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ pacient: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(PacientDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PacientDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load pacient on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.pacient).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
