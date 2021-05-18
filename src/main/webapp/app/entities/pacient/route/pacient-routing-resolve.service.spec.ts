jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IPacient, Pacient } from '../pacient.model';
import { PacientService } from '../service/pacient.service';

import { PacientRoutingResolveService } from './pacient-routing-resolve.service';

describe('Service Tests', () => {
  describe('Pacient routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: PacientRoutingResolveService;
    let service: PacientService;
    let resultPacient: IPacient | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(PacientRoutingResolveService);
      service = TestBed.inject(PacientService);
      resultPacient = undefined;
    });

    describe('resolve', () => {
      it('should return IPacient returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultPacient = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultPacient).toEqual({ id: 123 });
      });

      it('should return new IPacient if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultPacient = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultPacient).toEqual(new Pacient());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultPacient = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultPacient).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
