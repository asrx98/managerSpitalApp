import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IInventar, Inventar } from '../inventar.model';
import { InventarService } from '../service/inventar.service';

@Component({
  selector: 'jhi-inventar-update',
  templateUrl: './inventar-update.component.html',
})
export class InventarUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    inventarId: [null, [Validators.required]],
    nume: [null, [Validators.required]],
    stoc: [null, [Validators.required]],
    tag: [null, [Validators.required]],
  });

  constructor(protected inventarService: InventarService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ inventar }) => {
      this.updateForm(inventar);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const inventar = this.createFromForm();
    if (inventar.id !== undefined) {
      this.subscribeToSaveResponse(this.inventarService.update(inventar));
    } else {
      this.subscribeToSaveResponse(this.inventarService.create(inventar));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInventar>>): void {
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

  protected updateForm(inventar: IInventar): void {
    this.editForm.patchValue({
      id: inventar.id,
      inventarId: inventar.inventarId,
      nume: inventar.nume,
      stoc: inventar.stoc,
      tag: inventar.tag,
    });
  }

  protected createFromForm(): IInventar {
    return {
      ...new Inventar(),
      id: this.editForm.get(['id'])!.value,
      inventarId: this.editForm.get(['inventarId'])!.value,
      nume: this.editForm.get(['nume'])!.value,
      stoc: this.editForm.get(['stoc'])!.value,
      tag: this.editForm.get(['tag'])!.value,
    };
  }
}
