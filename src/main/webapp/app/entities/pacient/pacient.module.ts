import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { PacientComponent } from './list/pacient.component';
import { PacientDetailComponent } from './detail/pacient-detail.component';
import { PacientUpdateComponent } from './update/pacient-update.component';
import { PacientDeleteDialogComponent } from './delete/pacient-delete-dialog.component';
import { PacientRoutingModule } from './route/pacient-routing.module';

@NgModule({
  imports: [SharedModule, PacientRoutingModule],
  declarations: [PacientComponent, PacientDetailComponent, PacientUpdateComponent, PacientDeleteDialogComponent],
  entryComponents: [PacientDeleteDialogComponent],
})
export class PacientModule {}
