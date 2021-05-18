import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPacient, Pacient } from '../pacient.model';
import { PacientService } from '../service/pacient.service';

@Injectable({ providedIn: 'root' })
export class PacientRoutingResolveService implements Resolve<IPacient> {
  constructor(protected service: PacientService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPacient> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((pacient: HttpResponse<Pacient>) => {
          if (pacient.body) {
            return of(pacient.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Pacient());
  }
}
