jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IInventar, Inventar } from '../inventar.model';
import { InventarService } from '../service/inventar.service';

import { InventarRoutingResolveService } from './inventar-routing-resolve.service';

describe('Service Tests', () => {
  describe('Inventar routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: InventarRoutingResolveService;
    let service: InventarService;
    let resultInventar: IInventar | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(InventarRoutingResolveService);
      service = TestBed.inject(InventarService);
      resultInventar = undefined;
    });

    describe('resolve', () => {
      it('should return IInventar returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultInventar = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultInventar).toEqual({ id: 123 });
      });

      it('should return new IInventar if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultInventar = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultInventar).toEqual(new Inventar());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultInventar = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultInventar).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
