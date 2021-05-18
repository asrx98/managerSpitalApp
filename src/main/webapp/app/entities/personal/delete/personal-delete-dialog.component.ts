import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPersonal } from '../personal.model';
import { PersonalService } from '../service/personal.service';

@Component({
  templateUrl: './personal-delete-dialog.component.html',
})
export class PersonalDeleteDialogComponent {
  personal?: IPersonal;

  constructor(protected personalService: PersonalService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.personalService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
