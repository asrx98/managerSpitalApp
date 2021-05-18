import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPacient, getPacientIdentifier } from '../pacient.model';

export type EntityResponseType = HttpResponse<IPacient>;
export type EntityArrayResponseType = HttpResponse<IPacient[]>;

@Injectable({ providedIn: 'root' })
export class PacientService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/pacients');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/pacients');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(pacient: IPacient): Observable<EntityResponseType> {
    return this.http.post<IPacient>(this.resourceUrl, pacient, { observe: 'response' });
  }

  update(pacient: IPacient): Observable<EntityResponseType> {
    return this.http.put<IPacient>(`${this.resourceUrl}/${getPacientIdentifier(pacient) as number}`, pacient, { observe: 'response' });
  }

  partialUpdate(pacient: IPacient): Observable<EntityResponseType> {
    return this.http.patch<IPacient>(`${this.resourceUrl}/${getPacientIdentifier(pacient) as number}`, pacient, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPacient>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPacient[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPacient[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addPacientToCollectionIfMissing(pacientCollection: IPacient[], ...pacientsToCheck: (IPacient | null | undefined)[]): IPacient[] {
    const pacients: IPacient[] = pacientsToCheck.filter(isPresent);
    if (pacients.length > 0) {
      const pacientCollectionIdentifiers = pacientCollection.map(pacientItem => getPacientIdentifier(pacientItem)!);
      const pacientsToAdd = pacients.filter(pacientItem => {
        const pacientIdentifier = getPacientIdentifier(pacientItem);
        if (pacientIdentifier == null || pacientCollectionIdentifiers.includes(pacientIdentifier)) {
          return false;
        }
        pacientCollectionIdentifiers.push(pacientIdentifier);
        return true;
      });
      return [...pacientsToAdd, ...pacientCollection];
    }
    return pacientCollection;
  }
}
