import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISalon, Salon } from '../salon.model';
import { SalonService } from '../service/salon.service';

@Injectable({ providedIn: 'root' })
export class SalonRoutingResolveService implements Resolve<ISalon> {
  constructor(protected service: SalonService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISalon> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((salon: HttpResponse<Salon>) => {
          if (salon.body) {
            return of(salon.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Salon());
  }
}
