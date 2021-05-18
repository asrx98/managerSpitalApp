import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { InventarComponent } from './list/inventar.component';
import { InventarDetailComponent } from './detail/inventar-detail.component';
import { InventarUpdateComponent } from './update/inventar-update.component';
import { InventarDeleteDialogComponent } from './delete/inventar-delete-dialog.component';
import { InventarRoutingModule } from './route/inventar-routing.module';

@NgModule({
  imports: [SharedModule, InventarRoutingModule],
  declarations: [InventarComponent, InventarDetailComponent, InventarUpdateComponent, InventarDeleteDialogComponent],
  entryComponents: [InventarDeleteDialogComponent],
})
export class InventarModule {}
