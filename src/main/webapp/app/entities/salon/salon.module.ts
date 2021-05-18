import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { SalonComponent } from './list/salon.component';
import { SalonDetailComponent } from './detail/salon-detail.component';
import { SalonUpdateComponent } from './update/salon-update.component';
import { SalonDeleteDialogComponent } from './delete/salon-delete-dialog.component';
import { SalonRoutingModule } from './route/salon-routing.module';

@NgModule({
  imports: [SharedModule, SalonRoutingModule],
  declarations: [SalonComponent, SalonDetailComponent, SalonUpdateComponent, SalonDeleteDialogComponent],
  entryComponents: [SalonDeleteDialogComponent],
})
export class SalonModule {}
