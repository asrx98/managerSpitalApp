import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PacientComponent } from '../list/pacient.component';
import { PacientDetailComponent } from '../detail/pacient-detail.component';
import { PacientUpdateComponent } from '../update/pacient-update.component';
import { PacientRoutingResolveService } from './pacient-routing-resolve.service';

const pacientRoute: Routes = [
  {
    path: '',
    component: PacientComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PacientDetailComponent,
    resolve: {
      pacient: PacientRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PacientUpdateComponent,
    resolve: {
      pacient: PacientRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PacientUpdateComponent,
    resolve: {
      pacient: PacientRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(pacientRoute)],
  exports: [RouterModule],
})
export class PacientRoutingModule {}
