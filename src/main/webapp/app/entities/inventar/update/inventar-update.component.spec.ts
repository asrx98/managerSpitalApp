jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { InventarService } from '../service/inventar.service';
import { IInventar, Inventar } from '../inventar.model';

import { InventarUpdateComponent } from './inventar-update.component';

describe('Component Tests', () => {
  describe('Inventar Management Update Component', () => {
    let comp: InventarUpdateComponent;
    let fixture: ComponentFixture<InventarUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let inventarService: InventarService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [InventarUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(InventarUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(InventarUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      inventarService = TestBed.inject(InventarService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const inventar: IInventar = { id: 456 };

        activatedRoute.data = of({ inventar });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(inventar));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const inventar = { id: 123 };
        spyOn(inventarService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ inventar });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: inventar }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(inventarService.update).toHaveBeenCalledWith(inventar);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const inventar = new Inventar();
        spyOn(inventarService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ inventar });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: inventar }));
        saveSubject.complete();

        // THEN
        expect(inventarService.create).toHaveBeenCalledWith(inventar);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const inventar = { id: 123 };
        spyOn(inventarService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ inventar });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(inventarService.update).toHaveBeenCalledWith(inventar);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
