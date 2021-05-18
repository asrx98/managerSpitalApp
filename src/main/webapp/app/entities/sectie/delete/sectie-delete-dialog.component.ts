import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISectie } from '../sectie.model';
import { SectieService } from '../service/sectie.service';

@Component({
  templateUrl: './sectie-delete-dialog.component.html',
})
export class SectieDeleteDialogComponent {
  sectie?: ISectie;

  constructor(protected sectieService: SectieService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sectieService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
