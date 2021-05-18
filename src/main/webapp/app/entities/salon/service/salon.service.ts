import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ISalon, getSalonIdentifier } from '../salon.model';

export type EntityResponseType = HttpResponse<ISalon>;
export type EntityArrayResponseType = HttpResponse<ISalon[]>;

@Injectable({ providedIn: 'root' })
export class SalonService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/salons');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/salons');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(salon: ISalon): Observable<EntityResponseType> {
    return this.http.post<ISalon>(this.resourceUrl, salon, { observe: 'response' });
  }

  update(salon: ISalon): Observable<EntityResponseType> {
    return this.http.put<ISalon>(`${this.resourceUrl}/${getSalonIdentifier(salon) as number}`, salon, { observe: 'response' });
  }

  partialUpdate(salon: ISalon): Observable<EntityResponseType> {
    return this.http.patch<ISalon>(`${this.resourceUrl}/${getSalonIdentifier(salon) as number}`, salon, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISalon>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISalon[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISalon[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addSalonToCollectionIfMissing(salonCollection: ISalon[], ...salonsToCheck: (ISalon | null | undefined)[]): ISalon[] {
    const salons: ISalon[] = salonsToCheck.filter(isPresent);
    if (salons.length > 0) {
      const salonCollectionIdentifiers = salonCollection.map(salonItem => getSalonIdentifier(salonItem)!);
      const salonsToAdd = salons.filter(salonItem => {
        const salonIdentifier = getSalonIdentifier(salonItem);
        if (salonIdentifier == null || salonCollectionIdentifiers.includes(salonIdentifier)) {
          return false;
        }
        salonCollectionIdentifiers.push(salonIdentifier);
        return true;
      });
      return [...salonsToAdd, ...salonCollection];
    }
    return salonCollection;
  }
}
