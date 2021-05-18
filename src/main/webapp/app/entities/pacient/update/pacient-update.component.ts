import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPacient, Pacient } from '../pacient.model';
import { PacientService } from '../service/pacient.service';
import { IPersonal } from 'app/entities/personal/personal.model';
import { PersonalService } from 'app/entities/personal/service/personal.service';
import { ISectie } from 'app/entities/sectie/sectie.model';
import { SectieService } from 'app/entities/sectie/service/sectie.service';
import { ISalon } from 'app/entities/salon/salon.model';
import { SalonService } from 'app/entities/salon/service/salon.service';

@Component({
  selector: 'jhi-pacient-update',
  templateUrl: './pacient-update.component.html',
})
export class PacientUpdateComponent implements OnInit {
  isSaving = false;

  personalsSharedCollection: IPersonal[] = [];
  sectiesSharedCollection: ISectie[] = [];
  salonsSharedCollection: ISalon[] = [];

  editForm = this.fb.group({
    id: [],
    pacientId: [null, [Validators.required]],
    nume: [null, [Validators.required]],
    prenume: [null, [Validators.required]],
    sectieId: [null, [Validators.required]],
    salonId: [null, [Validators.required]],
    personal: [],
    sectie: [],
    salon: [],
  });

  constructor(
    protected pacientService: PacientService,
    protected personalService: PersonalService,
    protected sectieService: SectieService,
    protected salonService: SalonService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pacient }) => {
      this.updateForm(pacient);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const pacient = this.createFromForm();
    if (pacient.id !== undefined) {
      this.subscribeToSaveResponse(this.pacientService.update(pacient));
    } else {
      this.subscribeToSaveResponse(this.pacientService.create(pacient));
    }
  }

  trackPersonalById(index: number, item: IPersonal): number {
    return item.id!;
  }

  trackSectieById(index: number, item: ISectie): number {
    return item.id!;
  }

  trackSalonById(index: number, item: ISalon): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPacient>>): void {
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

  protected updateForm(pacient: IPacient): void {
    this.editForm.patchValue({
      id: pacient.id,
      pacientId: pacient.pacientId,
      nume: pacient.nume,
      prenume: pacient.prenume,
      sectieId: pacient.sectieId,
      salonId: pacient.salonId,
      personal: pacient.personal,
      sectie: pacient.sectie,
      salon: pacient.salon,
    });

    this.personalsSharedCollection = this.personalService.addPersonalToCollectionIfMissing(
      this.personalsSharedCollection,
      pacient.personal
    );
    this.sectiesSharedCollection = this.sectieService.addSectieToCollectionIfMissing(this.sectiesSharedCollection, pacient.sectie);
    this.salonsSharedCollection = this.salonService.addSalonToCollectionIfMissing(this.salonsSharedCollection, pacient.salon);
  }

  protected loadRelationshipsOptions(): void {
    this.personalService
      .query()
      .pipe(map((res: HttpResponse<IPersonal[]>) => res.body ?? []))
      .pipe(
        map((personals: IPersonal[]) =>
          this.personalService.addPersonalToCollectionIfMissing(personals, this.editForm.get('personal')!.value)
        )
      )
      .subscribe((personals: IPersonal[]) => (this.personalsSharedCollection = personals));

    this.sectieService
      .query()
      .pipe(map((res: HttpResponse<ISectie[]>) => res.body ?? []))
      .pipe(map((secties: ISectie[]) => this.sectieService.addSectieToCollectionIfMissing(secties, this.editForm.get('sectie')!.value)))
      .subscribe((secties: ISectie[]) => (this.sectiesSharedCollection = secties));

    this.salonService
      .query()
      .pipe(map((res: HttpResponse<ISalon[]>) => res.body ?? []))
      .pipe(map((salons: ISalon[]) => this.salonService.addSalonToCollectionIfMissing(salons, this.editForm.get('salon')!.value)))
      .subscribe((salons: ISalon[]) => (this.salonsSharedCollection = salons));
  }

  protected createFromForm(): IPacient {
    return {
      ...new Pacient(),
      id: this.editForm.get(['id'])!.value,
      pacientId: this.editForm.get(['pacientId'])!.value,
      nume: this.editForm.get(['nume'])!.value,
      prenume: this.editForm.get(['prenume'])!.value,
      sectieId: this.editForm.get(['sectieId'])!.value,
      salonId: this.editForm.get(['salonId'])!.value,
      personal: this.editForm.get(['personal'])!.value,
      sectie: this.editForm.get(['sectie'])!.value,
      salon: this.editForm.get(['salon'])!.value,
    };
  }
}
