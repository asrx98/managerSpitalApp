import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPersonal, getPersonalIdentifier } from '../personal.model';

export type EntityResponseType = HttpResponse<IPersonal>;
export type EntityArrayResponseType = HttpResponse<IPersonal[]>;

@Injectable({ providedIn: 'root' })
export class PersonalService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/personals');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/personals');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(personal: IPersonal): Observable<EntityResponseType> {
    return this.http.post<IPersonal>(this.resourceUrl, personal, { observe: 'response' });
  }

  update(personal: IPersonal): Observable<EntityResponseType> {
    return this.http.put<IPersonal>(`${this.resourceUrl}/${getPersonalIdentifier(personal) as number}`, personal, { observe: 'response' });
  }

  partialUpdate(personal: IPersonal): Observable<EntityResponseType> {
    return this.http.patch<IPersonal>(`${this.resourceUrl}/${getPersonalIdentifier(personal) as number}`, personal, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPersonal>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPersonal[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPersonal[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addPersonalToCollectionIfMissing(personalCollection: IPersonal[], ...personalsToCheck: (IPersonal | null | undefined)[]): IPersonal[] {
    const personals: IPersonal[] = personalsToCheck.filter(isPresent);
    if (personals.length > 0) {
      const personalCollectionIdentifiers = personalCollection.map(personalItem => getPersonalIdentifier(personalItem)!);
      const personalsToAdd = personals.filter(personalItem => {
        const personalIdentifier = getPersonalIdentifier(personalItem);
        if (personalIdentifier == null || personalCollectionIdentifiers.includes(personalIdentifier)) {
          return false;
        }
        personalCollectionIdentifiers.push(personalIdentifier);
        return true;
      });
      return [...personalsToAdd, ...personalCollection];
    }
    return personalCollection;
  }
}
