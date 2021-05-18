import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { InventarDetailComponent } from './inventar-detail.component';

describe('Component Tests', () => {
  describe('Inventar Management Detail Component', () => {
    let comp: InventarDetailComponent;
    let fixture: ComponentFixture<InventarDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [InventarDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ inventar: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(InventarDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(InventarDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load inventar on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.inventar).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
