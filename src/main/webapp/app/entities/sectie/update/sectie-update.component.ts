import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISectie, Sectie } from '../sectie.model';
import { SectieService } from '../service/sectie.service';

@Component({
  selector: 'jhi-sectie-update',
  templateUrl: './sectie-update.component.html',
})
export class SectieUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    sectieId: [null, [Validators.required]],
    nume: [null, [Validators.required]],
    sefId: [null, [Validators.required]],
    tag: [null, [Validators.required]],
    nrPaturi: [null, [Validators.required]],
  });

  constructor(protected sectieService: SectieService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sectie }) => {
      this.updateForm(sectie);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sectie = this.createFromForm();
    if (sectie.id !== undefined) {
      this.subscribeToSaveResponse(this.sectieService.update(sectie));
    } else {
      this.subscribeToSaveResponse(this.sectieService.create(sectie));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISectie>>): void {
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

  protected updateForm(sectie: ISectie): void {
    this.editForm.patchValue({
      id: sectie.id,
      sectieId: sectie.sectieId,
      nume: sectie.nume,
      sefId: sectie.sefId,
      tag: sectie.tag,
      nrPaturi: sectie.nrPaturi,
    });
  }

  protected createFromForm(): ISectie {
    return {
      ...new Sectie(),
      id: this.editForm.get(['id'])!.value,
      sectieId: this.editForm.get(['sectieId'])!.value,
      nume: this.editForm.get(['nume'])!.value,
      sefId: this.editForm.get(['sefId'])!.value,
      tag: this.editForm.get(['tag'])!.value,
      nrPaturi: this.editForm.get(['nrPaturi'])!.value,
    };
  }
}
