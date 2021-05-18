import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IInventar } from '../inventar.model';

@Component({
  selector: 'jhi-inventar-detail',
  templateUrl: './inventar-detail.component.html',
})
export class InventarDetailComponent implements OnInit {
  inventar: IInventar | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ inventar }) => {
      this.inventar = inventar;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
