import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SectieComponent } from '../list/sectie.component';
import { SectieDetailComponent } from '../detail/sectie-detail.component';
import { SectieUpdateComponent } from '../update/sectie-update.component';
import { SectieRoutingResolveService } from './sectie-routing-resolve.service';

const sectieRoute: Routes = [
  {
    path: '',
    component: SectieComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SectieDetailComponent,
    resolve: {
      sectie: SectieRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SectieUpdateComponent,
    resolve: {
      sectie: SectieRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SectieUpdateComponent,
    resolve: {
      sectie: SectieRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(sectieRoute)],
  exports: [RouterModule],
})
export class SectieRoutingModule {}
