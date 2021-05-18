import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPersonal, Personal } from '../personal.model';
import { PersonalService } from '../service/personal.service';

@Injectable({ providedIn: 'root' })
export class PersonalRoutingResolveService implements Resolve<IPersonal> {
  constructor(protected service: PersonalService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPersonal> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((personal: HttpResponse<Personal>) => {
          if (personal.body) {
            return of(personal.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Personal());
  }
}
