import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'inventar',
        data: { pageTitle: 'managerSpitalApp.inventar.home.title' },
        loadChildren: () => import('./inventar/inventar.module').then(m => m.InventarModule),
      },
      {
        path: 'pacient',
        data: { pageTitle: 'managerSpitalApp.pacient.home.title' },
        loadChildren: () => import('./pacient/pacient.module').then(m => m.PacientModule),
      },
      {
        path: 'personal',
        data: { pageTitle: 'managerSpitalApp.personal.home.title' },
        loadChildren: () => import('./personal/personal.module').then(m => m.PersonalModule),
      },
      {
        path: 'sectie',
        data: { pageTitle: 'managerSpitalApp.sectie.home.title' },
        loadChildren: () => import('./sectie/sectie.module').then(m => m.SectieModule),
      },
      {
        path: 'salon',
        data: { pageTitle: 'managerSpitalApp.salon.home.title' },
        loadChildren: () => import('./salon/salon.module').then(m => m.SalonModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
