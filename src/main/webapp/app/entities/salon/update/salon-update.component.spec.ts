jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { SalonService } from '../service/salon.service';
import { ISalon, Salon } from '../salon.model';

import { SalonUpdateComponent } from './salon-update.component';

describe('Component Tests', () => {
  describe('Salon Management Update Component', () => {
    let comp: SalonUpdateComponent;
    let fixture: ComponentFixture<SalonUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let salonService: SalonService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SalonUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(SalonUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SalonUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      salonService = TestBed.inject(SalonService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const salon: ISalon = { id: 456 };

        activatedRoute.data = of({ salon });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(salon));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const salon = { id: 123 };
        spyOn(salonService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ salon });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: salon }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(salonService.update).toHaveBeenCalledWith(salon);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const salon = new Salon();
        spyOn(salonService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ salon });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: salon }));
        saveSubject.complete();

        // THEN
        expect(salonService.create).toHaveBeenCalledWith(salon);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const salon = { id: 123 };
        spyOn(salonService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ salon });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(salonService.update).toHaveBeenCalledWith(salon);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
