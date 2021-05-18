import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { TagSectie } from 'app/entities/enumerations/tag-sectie.model';
import { ISectie, Sectie } from '../sectie.model';

import { SectieService } from './sectie.service';

describe('Service Tests', () => {
  describe('Sectie Service', () => {
    let service: SectieService;
    let httpMock: HttpTestingController;
    let elemDefault: ISectie;
    let expectedResult: ISectie | ISectie[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(SectieService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        sectieId: 0,
        nume: 'AAAAAAA',
        sefId: 'AAAAAAA',
        tag: TagSectie.TESA,
        nrPaturi: 0,
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

      it('should create a Sectie', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Sectie()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Sectie', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            sectieId: 1,
            nume: 'BBBBBB',
            sefId: 'BBBBBB',
            tag: 'BBBBBB',
            nrPaturi: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Sectie', () => {
        const patchObject = Object.assign(
          {
            sectieId: 1,
            nume: 'BBBBBB',
            sefId: 'BBBBBB',
          },
          new Sectie()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Sectie', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            sectieId: 1,
            nume: 'BBBBBB',
            sefId: 'BBBBBB',
            tag: 'BBBBBB',
            nrPaturi: 1,
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

      it('should delete a Sectie', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addSectieToCollectionIfMissing', () => {
        it('should add a Sectie to an empty array', () => {
          const sectie: ISectie = { id: 123 };
          expectedResult = service.addSectieToCollectionIfMissing([], sectie);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(sectie);
        });

        it('should not add a Sectie to an array that contains it', () => {
          const sectie: ISectie = { id: 123 };
          const sectieCollection: ISectie[] = [
            {
              ...sectie,
            },
            { id: 456 },
          ];
          expectedResult = service.addSectieToCollectionIfMissing(sectieCollection, sectie);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Sectie to an array that doesn't contain it", () => {
          const sectie: ISectie = { id: 123 };
          const sectieCollection: ISectie[] = [{ id: 456 }];
          expectedResult = service.addSectieToCollectionIfMissing(sectieCollection, sectie);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(sectie);
        });

        it('should add only unique Sectie to an array', () => {
          const sectieArray: ISectie[] = [{ id: 123 }, { id: 456 }, { id: 50045 }];
          const sectieCollection: ISectie[] = [{ id: 123 }];
          expectedResult = service.addSectieToCollectionIfMissing(sectieCollection, ...sectieArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const sectie: ISectie = { id: 123 };
          const sectie2: ISectie = { id: 456 };
          expectedResult = service.addSectieToCollectionIfMissing([], sectie, sectie2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(sectie);
          expect(expectedResult).toContain(sectie2);
        });

        it('should accept null and undefined values', () => {
          const sectie: ISectie = { id: 123 };
          expectedResult = service.addSectieToCollectionIfMissing([], null, sectie, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(sectie);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
