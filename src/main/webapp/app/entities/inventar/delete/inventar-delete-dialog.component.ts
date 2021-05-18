import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IInventar } from '../inventar.model';
import { InventarService } from '../service/inventar.service';

@Component({
  templateUrl: './inventar-delete-dialog.component.html',
})
export class InventarDeleteDialogComponent {
  inventar?: IInventar;

  constructor(protected inventarService: InventarService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.inventarService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
