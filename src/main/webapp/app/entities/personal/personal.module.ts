import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { PersonalComponent } from './list/personal.component';
import { PersonalDetailComponent } from './detail/personal-detail.component';
import { PersonalUpdateComponent } from './update/personal-update.component';
import { PersonalDeleteDialogComponent } from './delete/personal-delete-dialog.component';
import { PersonalRoutingModule } from './route/personal-routing.module';

@NgModule({
  imports: [SharedModule, PersonalRoutingModule],
  declarations: [PersonalComponent, PersonalDetailComponent, PersonalUpdateComponent, PersonalDeleteDialogComponent],
  entryComponents: [PersonalDeleteDialogComponent],
})
export class PersonalModule {}
