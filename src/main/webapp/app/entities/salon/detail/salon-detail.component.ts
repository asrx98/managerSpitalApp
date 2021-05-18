import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISalon } from '../salon.model';

@Component({
  selector: 'jhi-salon-detail',
  templateUrl: './salon-detail.component.html',
})
export class SalonDetailComponent implements OnInit {
  salon: ISalon | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ salon }) => {
      this.salon = salon;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
