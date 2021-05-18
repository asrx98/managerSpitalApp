import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPacient } from '../pacient.model';

@Component({
  selector: 'jhi-pacient-detail',
  templateUrl: './pacient-detail.component.html',
})
export class PacientDetailComponent implements OnInit {
  pacient: IPacient | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pacient }) => {
      this.pacient = pacient;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
