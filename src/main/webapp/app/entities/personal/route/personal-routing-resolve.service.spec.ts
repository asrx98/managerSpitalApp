jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IPersonal, Personal } from '../personal.model';
import { PersonalService } from '../service/personal.service';

import { PersonalRoutingResolveService } from './personal-routing-resolve.service';

describe('Service Tests', () => {
  describe('Personal routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: PersonalRoutingResolveService;
    let service: PersonalService;
    let resultPersonal: IPersonal | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(PersonalRoutingResolveService);
      service = TestBed.inject(PersonalService);
      resultPersonal = undefined;
    });

    describe('resolve', () => {
      it('should return IPersonal returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultPersonal = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultPersonal).toEqual({ id: 123 });
      });

      it('should return new IPersonal if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultPersonal = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultPersonal).toEqual(new Personal());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultPersonal = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultPersonal).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
