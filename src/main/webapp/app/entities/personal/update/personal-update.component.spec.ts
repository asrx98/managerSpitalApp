jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PersonalService } from '../service/personal.service';
import { IPersonal, Personal } from '../personal.model';
import { ISectie } from 'app/entities/sectie/sectie.model';
import { SectieService } from 'app/entities/sectie/service/sectie.service';

import { PersonalUpdateComponent } from './personal-update.component';

describe('Component Tests', () => {
  describe('Personal Management Update Component', () => {
    let comp: PersonalUpdateComponent;
    let fixture: ComponentFixture<PersonalUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let personalService: PersonalService;
    let sectieService: SectieService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PersonalUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PersonalUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PersonalUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      personalService = TestBed.inject(PersonalService);
      sectieService = TestBed.inject(SectieService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Sectie query and add missing value', () => {
        const personal: IPersonal = { id: 456 };
        const sectie: ISectie = { id: 43739 };
        personal.sectie = sectie;

        const sectieCollection: ISectie[] = [{ id: 2778 }];
        spyOn(sectieService, 'query').and.returnValue(of(new HttpResponse({ body: sectieCollection })));
        const additionalSecties = [sectie];
        const expectedCollection: ISectie[] = [...additionalSecties, ...sectieCollection];
        spyOn(sectieService, 'addSectieToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ personal });
        comp.ngOnInit();

        expect(sectieService.query).toHaveBeenCalled();
        expect(sectieService.addSectieToCollectionIfMissing).toHaveBeenCalledWith(sectieCollection, ...additionalSecties);
        expect(comp.sectiesSharedCollection).toEqual(expectedCollection);
      });

      it('Should call personalId query and add missing value', () => {
        const personal: IPersonal = { id: 456 };
        const personalId: ISectie = { id: 77696 };
        personal.personalId = personalId;

        const personalIdCollection: ISectie[] = [{ id: 98313 }];
        spyOn(sectieService, 'query').and.returnValue(of(new HttpResponse({ body: personalIdCollection })));
        const expectedCollection: ISectie[] = [personalId, ...personalIdCollection];
        spyOn(sectieService, 'addSectieToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ personal });
        comp.ngOnInit();

        expect(sectieService.query).toHaveBeenCalled();
        expect(sectieService.addSectieToCollectionIfMissing).toHaveBeenCalledWith(personalIdCollection, personalId);
        expect(comp.personalIdsCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const personal: IPersonal = { id: 456 };
        const personalId: ISectie = { id: 55799 };
        personal.personalId = personalId;
        const sectie: ISectie = { id: 50699 };
        personal.sectie = sectie;

        activatedRoute.data = of({ personal });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(personal));
        expect(comp.personalIdsCollection).toContain(personalId);
        expect(comp.sectiesSharedCollection).toContain(sectie);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const personal = { id: 123 };
        spyOn(personalService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ personal });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: personal }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(personalService.update).toHaveBeenCalledWith(personal);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const personal = new Personal();
        spyOn(personalService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ personal });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: personal }));
        saveSubject.complete();

        // THEN
        expect(personalService.create).toHaveBeenCalledWith(personal);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const personal = { id: 123 };
        spyOn(personalService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ personal });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(personalService.update).toHaveBeenCalledWith(personal);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackSectieById', () => {
        it('Should return tracked Sectie primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackSectieById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
