import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { SectieComponent } from './list/sectie.component';
import { SectieDetailComponent } from './detail/sectie-detail.component';
import { SectieUpdateComponent } from './update/sectie-update.component';
import { SectieDeleteDialogComponent } from './delete/sectie-delete-dialog.component';
import { SectieRoutingModule } from './route/sectie-routing.module';

@NgModule({
  imports: [SharedModule, SectieRoutingModule],
  declarations: [SectieComponent, SectieDetailComponent, SectieUpdateComponent, SectieDeleteDialogComponent],
  entryComponents: [SectieDeleteDialogComponent],
})
export class SectieModule {}
