import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPacient } from '../pacient.model';
import { PacientService } from '../service/pacient.service';

@Component({
  templateUrl: './pacient-delete-dialog.component.html',
})
export class PacientDeleteDialogComponent {
  pacient?: IPacient;

  constructor(protected pacientService: PacientService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.pacientService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
