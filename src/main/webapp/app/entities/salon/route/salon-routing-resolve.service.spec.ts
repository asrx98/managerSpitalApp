jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ISalon, Salon } from '../salon.model';
import { SalonService } from '../service/salon.service';

import { SalonRoutingResolveService } from './salon-routing-resolve.service';

describe('Service Tests', () => {
  describe('Salon routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: SalonRoutingResolveService;
    let service: SalonService;
    let resultSalon: ISalon | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(SalonRoutingResolveService);
      service = TestBed.inject(SalonService);
      resultSalon = undefined;
    });

    describe('resolve', () => {
      it('should return ISalon returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSalon = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSalon).toEqual({ id: 123 });
      });

      it('should return new ISalon if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSalon = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultSalon).toEqual(new Salon());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSalon = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSalon).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
