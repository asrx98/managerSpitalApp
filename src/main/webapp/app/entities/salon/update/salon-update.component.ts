import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISalon, Salon } from '../salon.model';
import { SalonService } from '../service/salon.service';

@Component({
  selector: 'jhi-salon-update',
  templateUrl: './salon-update.component.html',
})
export class SalonUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    salonId: [],
    sectieId: [],
  });

  constructor(protected salonService: SalonService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ salon }) => {
      this.updateForm(salon);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const salon = this.createFromForm();
    if (salon.id !== undefined) {
      this.subscribeToSaveResponse(this.salonService.update(salon));
    } else {
      this.subscribeToSaveResponse(this.salonService.create(salon));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISalon>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(salon: ISalon): void {
    this.editForm.patchValue({
      id: salon.id,
      salonId: salon.salonId,
      sectieId: salon.sectieId,
    });
  }

  protected createFromForm(): ISalon {
    return {
      ...new Salon(),
      id: this.editForm.get(['id'])!.value,
      salonId: this.editForm.get(['salonId'])!.value,
      sectieId: this.editForm.get(['sectieId'])!.value,
    };
  }
}
