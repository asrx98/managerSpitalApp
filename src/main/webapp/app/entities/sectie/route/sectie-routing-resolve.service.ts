import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISectie, Sectie } from '../sectie.model';
import { SectieService } from '../service/sectie.service';

@Injectable({ providedIn: 'root' })
export class SectieRoutingResolveService implements Resolve<ISectie> {
  constructor(protected service: SectieService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISectie> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((sectie: HttpResponse<Sectie>) => {
          if (sectie.body) {
            return of(sectie.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Sectie());
  }
}
