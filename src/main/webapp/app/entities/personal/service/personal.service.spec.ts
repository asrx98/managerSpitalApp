import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { TagPersonal } from 'app/entities/enumerations/tag-personal.model';
import { IPersonal, Personal } from '../personal.model';

import { PersonalService } from './personal.service';

describe('Service Tests', () => {
  describe('Personal Service', () => {
    let service: PersonalService;
    let httpMock: HttpTestingController;
    let elemDefault: IPersonal;
    let expectedResult: IPersonal | IPersonal[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(PersonalService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        pacientId: 0,
        nume: 'AAAAAAA',
        prenume: 'AAAAAAA',
        sectieId: 'AAAAAAA',
        tag: TagPersonal.TESA,
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

      it('should create a Personal', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Personal()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Personal', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            pacientId: 1,
            nume: 'BBBBBB',
            prenume: 'BBBBBB',
            sectieId: 'BBBBBB',
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

      it('should partial update a Personal', () => {
        const patchObject = Object.assign({}, new Personal());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Personal', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            pacientId: 1,
            nume: 'BBBBBB',
            prenume: 'BBBBBB',
            sectieId: 'BBBBBB',
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

      it('should delete a Personal', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addPersonalToCollectionIfMissing', () => {
        it('should add a Personal to an empty array', () => {
          const personal: IPersonal = { id: 123 };
          expectedResult = service.addPersonalToCollectionIfMissing([], personal);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(personal);
        });

        it('should not add a Personal to an array that contains it', () => {
          const personal: IPersonal = { id: 123 };
          const personalCollection: IPersonal[] = [
            {
              ...personal,
            },
            { id: 456 },
          ];
          expectedResult = service.addPersonalToCollectionIfMissing(personalCollection, personal);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Personal to an array that doesn't contain it", () => {
          const personal: IPersonal = { id: 123 };
          const personalCollection: IPersonal[] = [{ id: 456 }];
          expectedResult = service.addPersonalToCollectionIfMissing(personalCollection, personal);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(personal);
        });

        it('should add only unique Personal to an array', () => {
          const personalArray: IPersonal[] = [{ id: 123 }, { id: 456 }, { id: 38 }];
          const personalCollection: IPersonal[] = [{ id: 123 }];
          expectedResult = service.addPersonalToCollectionIfMissing(personalCollection, ...personalArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const personal: IPersonal = { id: 123 };
          const personal2: IPersonal = { id: 456 };
          expectedResult = service.addPersonalToCollectionIfMissing([], personal, personal2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(personal);
          expect(expectedResult).toContain(personal2);
        });

        it('should accept null and undefined values', () => {
          const personal: IPersonal = { id: 123 };
          expectedResult = service.addPersonalToCollectionIfMissing([], null, personal, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(personal);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
