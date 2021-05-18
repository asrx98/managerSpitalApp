import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PersonalComponent } from '../list/personal.component';
import { PersonalDetailComponent } from '../detail/personal-detail.component';
import { PersonalUpdateComponent } from '../update/personal-update.component';
import { PersonalRoutingResolveService } from './personal-routing-resolve.service';

const personalRoute: Routes = [
  {
    path: '',
    component: PersonalComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PersonalDetailComponent,
    resolve: {
      personal: PersonalRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PersonalUpdateComponent,
    resolve: {
      personal: PersonalRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PersonalUpdateComponent,
    resolve: {
      personal: PersonalRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(personalRoute)],
  exports: [RouterModule],
})
export class PersonalRoutingModule {}
