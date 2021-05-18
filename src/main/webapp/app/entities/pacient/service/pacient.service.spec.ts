import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPacient, Pacient } from '../pacient.model';

import { PacientService } from './pacient.service';

describe('Service Tests', () => {
  describe('Pacient Service', () => {
    let service: PacientService;
    let httpMock: HttpTestingController;
    let elemDefault: IPacient;
    let expectedResult: IPacient | IPacient[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(PacientService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        pacientId: 0,
        nume: 'AAAAAAA',
        prenume: 'AAAAAAA',
        sectieId: 'AAAAAAA',
        salonId: 'AAAAAAA',
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

      it('should create a Pacient', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Pacient()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Pacient', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            pacientId: 1,
            nume: 'BBBBBB',
            prenume: 'BBBBBB',
            sectieId: 'BBBBBB',
            salonId: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Pacient', () => {
        const patchObject = Object.assign(
          {
            nume: 'BBBBBB',
            salonId: 'BBBBBB',
          },
          new Pacient()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Pacient', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            pacientId: 1,
            nume: 'BBBBBB',
            prenume: 'BBBBBB',
            sectieId: 'BBBBBB',
            salonId: 'BBBBBB',
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

      it('should delete a Pacient', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addPacientToCollectionIfMissing', () => {
        it('should add a Pacient to an empty array', () => {
          const pacient: IPacient = { id: 123 };
          expectedResult = service.addPacientToCollectionIfMissing([], pacient);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(pacient);
        });

        it('should not add a Pacient to an array that contains it', () => {
          const pacient: IPacient = { id: 123 };
          const pacientCollection: IPacient[] = [
            {
              ...pacient,
            },
            { id: 456 },
          ];
          expectedResult = service.addPacientToCollectionIfMissing(pacientCollection, pacient);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Pacient to an array that doesn't contain it", () => {
          const pacient: IPacient = { id: 123 };
          const pacientCollection: IPacient[] = [{ id: 456 }];
          expectedResult = service.addPacientToCollectionIfMissing(pacientCollection, pacient);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(pacient);
        });

        it('should add only unique Pacient to an array', () => {
          const pacientArray: IPacient[] = [{ id: 123 }, { id: 456 }, { id: 18047 }];
          const pacientCollection: IPacient[] = [{ id: 123 }];
          expectedResult = service.addPacientToCollectionIfMissing(pacientCollection, ...pacientArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const pacient: IPacient = { id: 123 };
          const pacient2: IPacient = { id: 456 };
          expectedResult = service.addPacientToCollectionIfMissing([], pacient, pacient2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(pacient);
          expect(expectedResult).toContain(pacient2);
        });

        it('should accept null and undefined values', () => {
          const pacient: IPacient = { id: 123 };
          expectedResult = service.addPacientToCollectionIfMissing([], null, pacient, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(pacient);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
