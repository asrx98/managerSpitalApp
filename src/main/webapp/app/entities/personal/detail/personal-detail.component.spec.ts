import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PersonalDetailComponent } from './personal-detail.component';

describe('Component Tests', () => {
  describe('Personal Management Detail Component', () => {
    let comp: PersonalDetailComponent;
    let fixture: ComponentFixture<PersonalDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [PersonalDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ personal: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(PersonalDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PersonalDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load personal on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.personal).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
