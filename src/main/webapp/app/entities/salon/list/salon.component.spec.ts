jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SalonService } from '../service/salon.service';

import { SalonComponent } from './salon.component';

describe('Component Tests', () => {
  describe('Salon Management Component', () => {
    let comp: SalonComponent;
    let fixture: ComponentFixture<SalonComponent>;
    let service: SalonService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SalonComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(SalonComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SalonComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(SalonService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.salons?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
