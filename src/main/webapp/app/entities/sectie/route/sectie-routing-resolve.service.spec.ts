jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ISectie, Sectie } from '../sectie.model';
import { SectieService } from '../service/sectie.service';

import { SectieRoutingResolveService } from './sectie-routing-resolve.service';

describe('Service Tests', () => {
  describe('Sectie routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: SectieRoutingResolveService;
    let service: SectieService;
    let resultSectie: ISectie | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(SectieRoutingResolveService);
      service = TestBed.inject(SectieService);
      resultSectie = undefined;
    });

    describe('resolve', () => {
      it('should return ISectie returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSectie = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSectie).toEqual({ id: 123 });
      });

      it('should return new ISectie if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSectie = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultSectie).toEqual(new Sectie());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSectie = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSectie).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
