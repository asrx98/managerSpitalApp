import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ISectie, getSectieIdentifier } from '../sectie.model';

export type EntityResponseType = HttpResponse<ISectie>;
export type EntityArrayResponseType = HttpResponse<ISectie[]>;

@Injectable({ providedIn: 'root' })
export class SectieService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/secties');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/secties');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(sectie: ISectie): Observable<EntityResponseType> {
    return this.http.post<ISectie>(this.resourceUrl, sectie, { observe: 'response' });
  }

  update(sectie: ISectie): Observable<EntityResponseType> {
    return this.http.put<ISectie>(`${this.resourceUrl}/${getSectieIdentifier(sectie) as number}`, sectie, { observe: 'response' });
  }

  partialUpdate(sectie: ISectie): Observable<EntityResponseType> {
    return this.http.patch<ISectie>(`${this.resourceUrl}/${getSectieIdentifier(sectie) as number}`, sectie, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISectie>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISectie[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISectie[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addSectieToCollectionIfMissing(sectieCollection: ISectie[], ...sectiesToCheck: (ISectie | null | undefined)[]): ISectie[] {
    const secties: ISectie[] = sectiesToCheck.filter(isPresent);
    if (secties.length > 0) {
      const sectieCollectionIdentifiers = sectieCollection.map(sectieItem => getSectieIdentifier(sectieItem)!);
      const sectiesToAdd = secties.filter(sectieItem => {
        const sectieIdentifier = getSectieIdentifier(sectieItem);
        if (sectieIdentifier == null || sectieCollectionIdentifiers.includes(sectieIdentifier)) {
          return false;
        }
        sectieCollectionIdentifiers.push(sectieIdentifier);
        return true;
      });
      return [...sectiesToAdd, ...sectieCollection];
    }
    return sectieCollection;
  }
}
