import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SalonComponent } from '../list/salon.component';
import { SalonDetailComponent } from '../detail/salon-detail.component';
import { SalonUpdateComponent } from '../update/salon-update.component';
import { SalonRoutingResolveService } from './salon-routing-resolve.service';

const salonRoute: Routes = [
  {
    path: '',
    component: SalonComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SalonDetailComponent,
    resolve: {
      salon: SalonRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SalonUpdateComponent,
    resolve: {
      salon: SalonRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SalonUpdateComponent,
    resolve: {
      salon: SalonRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(salonRoute)],
  exports: [RouterModule],
})
export class SalonRoutingModule {}
