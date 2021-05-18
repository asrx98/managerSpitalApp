import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SectieDetailComponent } from './sectie-detail.component';

describe('Component Tests', () => {
  describe('Sectie Management Detail Component', () => {
    let comp: SectieDetailComponent;
    let fixture: ComponentFixture<SectieDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [SectieDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ sectie: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(SectieDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SectieDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load sectie on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.sectie).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
