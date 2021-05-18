import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPersonal, Personal } from '../personal.model';
import { PersonalService } from '../service/personal.service';
import { ISectie } from 'app/entities/sectie/sectie.model';
import { SectieService } from 'app/entities/sectie/service/sectie.service';

@Component({
  selector: 'jhi-personal-update',
  templateUrl: './personal-update.component.html',
})
export class PersonalUpdateComponent implements OnInit {
  isSaving = false;

  sectiesSharedCollection: ISectie[] = [];
  personalIdsCollection: ISectie[] = [];

  editForm = this.fb.group({
    id: [],
    pacientId: [null, [Validators.required]],
    nume: [null, [Validators.required]],
    prenume: [null, [Validators.required]],
    sectieId: [null, [Validators.required]],
    tag: [null, [Validators.required]],
    personalId: [],
    sectie: [],
  });

  constructor(
    protected personalService: PersonalService,
    protected sectieService: SectieService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ personal }) => {
      this.updateForm(personal);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const personal = this.createFromForm();
    if (personal.id !== undefined) {
      this.subscribeToSaveResponse(this.personalService.update(personal));
    } else {
      this.subscribeToSaveResponse(this.personalService.create(personal));
    }
  }

  trackSectieById(index: number, item: ISectie): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPersonal>>): void {
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

  protected updateForm(personal: IPersonal): void {
    this.editForm.patchValue({
      id: personal.id,
      pacientId: personal.pacientId,
      nume: personal.nume,
      prenume: personal.prenume,
      sectieId: personal.sectieId,
      tag: personal.tag,
      personalId: personal.personalId,
      sectie: personal.sectie,
    });

    this.sectiesSharedCollection = this.sectieService.addSectieToCollectionIfMissing(this.sectiesSharedCollection, personal.sectie);
    this.personalIdsCollection = this.sectieService.addSectieToCollectionIfMissing(this.personalIdsCollection, personal.personalId);
  }

  protected loadRelationshipsOptions(): void {
    this.sectieService
      .query()
      .pipe(map((res: HttpResponse<ISectie[]>) => res.body ?? []))
      .pipe(map((secties: ISectie[]) => this.sectieService.addSectieToCollectionIfMissing(secties, this.editForm.get('sectie')!.value)))
      .subscribe((secties: ISectie[]) => (this.sectiesSharedCollection = secties));

    this.sectieService
      .query({ filter: 'sefid-is-null' })
      .pipe(map((res: HttpResponse<ISectie[]>) => res.body ?? []))
      .pipe(map((secties: ISectie[]) => this.sectieService.addSectieToCollectionIfMissing(secties, this.editForm.get('personalId')!.value)))
      .subscribe((secties: ISectie[]) => (this.personalIdsCollection = secties));
  }

  protected createFromForm(): IPersonal {
    return {
      ...new Personal(),
      id: this.editForm.get(['id'])!.value,
      pacientId: this.editForm.get(['pacientId'])!.value,
      nume: this.editForm.get(['nume'])!.value,
      prenume: this.editForm.get(['prenume'])!.value,
      sectieId: this.editForm.get(['sectieId'])!.value,
      tag: this.editForm.get(['tag'])!.value,
      personalId: this.editForm.get(['personalId'])!.value,
      sectie: this.editForm.get(['sectie'])!.value,
    };
  }
}
