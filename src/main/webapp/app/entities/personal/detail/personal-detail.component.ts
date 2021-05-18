import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPersonal } from '../personal.model';

@Component({
  selector: 'jhi-personal-detail',
  templateUrl: './personal-detail.component.html',
})
export class PersonalDetailComponent implements OnInit {
  personal: IPersonal | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ personal }) => {
      this.personal = personal;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
