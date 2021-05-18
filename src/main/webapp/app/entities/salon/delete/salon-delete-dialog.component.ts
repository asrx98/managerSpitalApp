import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISalon } from '../salon.model';
import { SalonService } from '../service/salon.service';

@Component({
  templateUrl: './salon-delete-dialog.component.html',
})
export class SalonDeleteDialogComponent {
  salon?: ISalon;

  constructor(protected salonService: SalonService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.salonService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
