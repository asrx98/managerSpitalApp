import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IInventar, Inventar } from '../inventar.model';
import { InventarService } from '../service/inventar.service';

@Injectable({ providedIn: 'root' })
export class InventarRoutingResolveService implements Resolve<IInventar> {
  constructor(protected service: InventarService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IInventar> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((inventar: HttpResponse<Inventar>) => {
          if (inventar.body) {
            return of(inventar.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Inventar());
  }
}
