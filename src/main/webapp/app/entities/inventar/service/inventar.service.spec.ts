import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { TagInventar } from 'app/entities/enumerations/tag-inventar.model';
import { IInventar, Inventar } from '../inventar.model';

import { InventarService } from './inventar.service';

describe('Service Tests', () => {
  describe('Inventar Service', () => {
    let service: InventarService;
    let httpMock: HttpTestingController;
    let elemDefault: IInventar;
    let expectedResult: IInventar | IInventar[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(InventarService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        inventarId: 0,
        nume: 'AAAAAAA',
        stoc: 0,
        tag: TagInventar.MEDICAMENT,
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

      it('should create a Inventar', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Inventar()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Inventar', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            inventarId: 1,
            nume: 'BBBBBB',
            stoc: 1,
            tag: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Inventar', () => {
        const patchObject = Object.assign(
          {
            inventarId: 1,
            nume: 'BBBBBB',
          },
          new Inventar()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Inventar', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            inventarId: 1,
            nume: 'BBBBBB',
            stoc: 1,
            tag: 'BBBBBB',
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

      it('should delete a Inventar', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addInventarToCollectionIfMissing', () => {
        it('should add a Inventar to an empty array', () => {
          const inventar: IInventar = { id: 123 };
          expectedResult = service.addInventarToCollectionIfMissing([], inventar);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(inventar);
        });

        it('should not add a Inventar to an array that contains it', () => {
          const inventar: IInventar = { id: 123 };
          const inventarCollection: IInventar[] = [
            {
              ...inventar,
            },
            { id: 456 },
          ];
          expectedResult = service.addInventarToCollectionIfMissing(inventarCollection, inventar);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Inventar to an array that doesn't contain it", () => {
          const inventar: IInventar = { id: 123 };
          const inventarCollection: IInventar[] = [{ id: 456 }];
          expectedResult = service.addInventarToCollectionIfMissing(inventarCollection, inventar);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(inventar);
        });

        it('should add only unique Inventar to an array', () => {
          const inventarArray: IInventar[] = [{ id: 123 }, { id: 456 }, { id: 67607 }];
          const inventarCollection: IInventar[] = [{ id: 123 }];
          expectedResult = service.addInventarToCollectionIfMissing(inventarCollection, ...inventarArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const inventar: IInventar = { id: 123 };
          const inventar2: IInventar = { id: 456 };
          expectedResult = service.addInventarToCollectionIfMissing([], inventar, inventar2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(inventar);
          expect(expectedResult).toContain(inventar2);
        });

        it('should accept null and undefined values', () => {
          const inventar: IInventar = { id: 123 };
          expectedResult = service.addInventarToCollectionIfMissing([], null, inventar, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(inventar);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
