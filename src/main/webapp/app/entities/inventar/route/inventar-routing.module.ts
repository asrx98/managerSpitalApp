import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { InventarComponent } from '../list/inventar.component';
import { InventarDetailComponent } from '../detail/inventar-detail.component';
import { InventarUpdateComponent } from '../update/inventar-update.component';
import { InventarRoutingResolveService } from './inventar-routing-resolve.service';

const inventarRoute: Routes = [
  {
    path: '',
    component: InventarComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: InventarDetailComponent,
    resolve: {
      inventar: InventarRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: InventarUpdateComponent,
    resolve: {
      inventar: InventarRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: InventarUpdateComponent,
    resolve: {
      inventar: InventarRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(inventarRoute)],
  exports: [RouterModule],
})
export class InventarRoutingModule {}
