import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IInventar, getInventarIdentifier } from '../inventar.model';

export type EntityResponseType = HttpResponse<IInventar>;
export type EntityArrayResponseType = HttpResponse<IInventar[]>;

@Injectable({ providedIn: 'root' })
export class InventarService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/inventars');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/inventars');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(inventar: IInventar): Observable<EntityResponseType> {
    return this.http.post<IInventar>(this.resourceUrl, inventar, { observe: 'response' });
  }

  update(inventar: IInventar): Observable<EntityResponseType> {
    return this.http.put<IInventar>(`${this.resourceUrl}/${getInventarIdentifier(inventar) as number}`, inventar, { observe: 'response' });
  }

  partialUpdate(inventar: IInventar): Observable<EntityResponseType> {
    return this.http.patch<IInventar>(`${this.resourceUrl}/${getInventarIdentifier(inventar) as number}`, inventar, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IInventar>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IInventar[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IInventar[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addInventarToCollectionIfMissing(inventarCollection: IInventar[], ...inventarsToCheck: (IInventar | null | undefined)[]): IInventar[] {
    const inventars: IInventar[] = inventarsToCheck.filter(isPresent);
    if (inventars.length > 0) {
      const inventarCollectionIdentifiers = inventarCollection.map(inventarItem => getInventarIdentifier(inventarItem)!);
      const inventarsToAdd = inventars.filter(inventarItem => {
        const inventarIdentifier = getInventarIdentifier(inventarItem);
        if (inventarIdentifier == null || inventarCollectionIdentifiers.includes(inventarIdentifier)) {
          return false;
        }
        inventarCollectionIdentifiers.push(inventarIdentifier);
        return true;
      });
      return [...inventarsToAdd, ...inventarCollection];
    }
    return inventarCollection;
  }
}
