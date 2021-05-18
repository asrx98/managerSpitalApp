import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISectie } from '../sectie.model';

@Component({
  selector: 'jhi-sectie-detail',
  templateUrl: './sectie-detail.component.html',
})
export class SectieDetailComponent implements OnInit {
  sectie: ISectie | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sectie }) => {
      this.sectie = sectie;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
