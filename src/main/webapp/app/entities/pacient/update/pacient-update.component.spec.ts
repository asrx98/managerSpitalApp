jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PacientService } from '../service/pacient.service';
import { IPacient, Pacient } from '../pacient.model';
import { IPersonal } from 'app/entities/personal/personal.model';
import { PersonalService } from 'app/entities/personal/service/personal.service';
import { ISectie } from 'app/entities/sectie/sectie.model';
import { SectieService } from 'app/entities/sectie/service/sectie.service';
import { ISalon } from 'app/entities/salon/salon.model';
import { SalonService } from 'app/entities/salon/service/salon.service';

import { PacientUpdateComponent } from './pacient-update.component';

describe('Component Tests', () => {
  describe('Pacient Management Update Component', () => {
    let comp: PacientUpdateComponent;
    let fixture: ComponentFixture<PacientUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let pacientService: PacientService;
    let personalService: PersonalService;
    let sectieService: SectieService;
    let salonService: SalonService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PacientUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PacientUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PacientUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      pacientService = TestBed.inject(PacientService);
      personalService = TestBed.inject(PersonalService);
      sectieService = TestBed.inject(SectieService);
      salonService = TestBed.inject(SalonService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Personal query and add missing value', () => {
        const pacient: IPacient = { id: 456 };
        const personal: IPersonal = { id: 69709 };
        pacient.personal = personal;

        const personalCollection: IPersonal[] = [{ id: 60643 }];
        spyOn(personalService, 'query').and.returnValue(of(new HttpResponse({ body: personalCollection })));
        const additionalPersonals = [personal];
        const expectedCollection: IPersonal[] = [...additionalPersonals, ...personalCollection];
        spyOn(personalService, 'addPersonalToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ pacient });
        comp.ngOnInit();

        expect(personalService.query).toHaveBeenCalled();
        expect(personalService.addPersonalToCollectionIfMissing).toHaveBeenCalledWith(personalCollection, ...additionalPersonals);
        expect(comp.personalsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Sectie query and add missing value', () => {
        const pacient: IPacient = { id: 456 };
        const sectie: ISectie = { id: 12129 };
        pacient.sectie = sectie;

        const sectieCollection: ISectie[] = [{ id: 22 }];
        spyOn(sectieService, 'query').and.returnValue(of(new HttpResponse({ body: sectieCollection })));
        const additionalSecties = [sectie];
        const expectedCollection: ISectie[] = [...additionalSecties, ...sectieCollection];
        spyOn(sectieService, 'addSectieToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ pacient });
        comp.ngOnInit();

        expect(sectieService.query).toHaveBeenCalled();
        expect(sectieService.addSectieToCollectionIfMissing).toHaveBeenCalledWith(sectieCollection, ...additionalSecties);
        expect(comp.sectiesSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Salon query and add missing value', () => {
        const pacient: IPacient = { id: 456 };
        const salon: ISalon = { id: 16902 };
        pacient.salon = salon;

        const salonCollection: ISalon[] = [{ id: 8824 }];
        spyOn(salonService, 'query').and.returnValue(of(new HttpResponse({ body: salonCollection })));
        const additionalSalons = [salon];
        const expectedCollection: ISalon[] = [...additionalSalons, ...salonCollection];
        spyOn(salonService, 'addSalonToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ pacient });
        comp.ngOnInit();

        expect(salonService.query).toHaveBeenCalled();
        expect(salonService.addSalonToCollectionIfMissing).toHaveBeenCalledWith(salonCollection, ...additionalSalons);
        expect(comp.salonsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const pacient: IPacient = { id: 456 };
        const personal: IPersonal = { id: 41042 };
        pacient.personal = personal;
        const sectie: ISectie = { id: 98763 };
        pacient.sectie = sectie;
        const salon: ISalon = { id: 35035 };
        pacient.salon = salon;

        activatedRoute.data = of({ pacient });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(pacient));
        expect(comp.personalsSharedCollection).toContain(personal);
        expect(comp.sectiesSharedCollection).toContain(sectie);
        expect(comp.salonsSharedCollection).toContain(salon);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const pacient = { id: 123 };
        spyOn(pacientService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ pacient });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: pacient }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(pacientService.update).toHaveBeenCalledWith(pacient);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const pacient = new Pacient();
        spyOn(pacientService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ pacient });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: pacient }));
        saveSubject.complete();

        // THEN
        expect(pacientService.create).toHaveBeenCalledWith(pacient);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const pacient = { id: 123 };
        spyOn(pacientService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ pacient });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(pacientService.update).toHaveBeenCalledWith(pacient);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackPersonalById', () => {
        it('Should return tracked Personal primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackPersonalById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackSectieById', () => {
        it('Should return tracked Sectie primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackSectieById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackSalonById', () => {
        it('Should return tracked Salon primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackSalonById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
