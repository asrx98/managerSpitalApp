jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { SectieService } from '../service/sectie.service';
import { ISectie, Sectie } from '../sectie.model';

import { SectieUpdateComponent } from './sectie-update.component';

describe('Component Tests', () => {
  describe('Sectie Management Update Component', () => {
    let comp: SectieUpdateComponent;
    let fixture: ComponentFixture<SectieUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let sectieService: SectieService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SectieUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(SectieUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SectieUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      sectieService = TestBed.inject(SectieService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const sectie: ISectie = { id: 456 };

        activatedRoute.data = of({ sectie });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(sectie));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const sectie = { id: 123 };
        spyOn(sectieService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ sectie });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: sectie }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(sectieService.update).toHaveBeenCalledWith(sectie);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const sectie = new Sectie();
        spyOn(sectieService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ sectie });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: sectie }));
        saveSubject.complete();

        // THEN
        expect(sectieService.create).toHaveBeenCalledWith(sectie);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const sectie = { id: 123 };
        spyOn(sectieService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ sectie });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(sectieService.update).toHaveBeenCalledWith(sectie);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
