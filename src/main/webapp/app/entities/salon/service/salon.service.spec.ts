import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISalon, Salon } from '../salon.model';

import { SalonService } from './salon.service';

describe('Service Tests', () => {
  describe('Salon Service', () => {
    let service: SalonService;
    let httpMock: HttpTestingController;
    let elemDefault: ISalon;
    let expectedResult: ISalon | ISalon[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(SalonService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        salonId: 0,
        sectieId: 0,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Salon', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Salon()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Salon', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            salonId: 1,
            sectieId: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Salon', () => {
        const patchObject = Object.assign(
          {
            salonId: 1,
          },
          new Salon()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Salon', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            salonId: 1,
            sectieId: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Salon', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addSalonToCollectionIfMissing', () => {
        it('should add a Salon to an empty array', () => {
          const salon: ISalon = { id: 123 };
          expectedResult = service.addSalonToCollectionIfMissing([], salon);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(salon);
        });

        it('should not add a Salon to an array that contains it', () => {
          const salon: ISalon = { id: 123 };
          const salonCollection: ISalon[] = [
            {
              ...salon,
            },
            { id: 456 },
          ];
          expectedResult = service.addSalonToCollectionIfMissing(salonCollection, salon);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Salon to an array that doesn't contain it", () => {
          const salon: ISalon = { id: 123 };
          const salonCollection: ISalon[] = [{ id: 456 }];
          expectedResult = service.addSalonToCollectionIfMissing(salonCollection, salon);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(salon);
        });

        it('should add only unique Salon to an array', () => {
          const salonArray: ISalon[] = [{ id: 123 }, { id: 456 }, { id: 27322 }];
          const salonCollection: ISalon[] = [{ id: 123 }];
          expectedResult = service.addSalonToCollectionIfMissing(salonCollection, ...salonArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const salon: ISalon = { id: 123 };
          const salon2: ISalon = { id: 456 };
          expectedResult = service.addSalonToCollectionIfMissing([], salon, salon2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(salon);
          expect(expectedResult).toContain(salon2);
        });

        it('should accept null and undefined values', () => {
          const salon: ISalon = { id: 123 };
          expectedResult = service.addSalonToCollectionIfMissing([], null, salon, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(salon);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
