import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISalon } from '../salon.model';
import { SalonService } from '../service/salon.service';
import { SalonDeleteDialogComponent } from '../delete/salon-delete-dialog.component';

@Component({
  selector: 'jhi-salon',
  templateUrl: './salon.component.html',
})
export class SalonComponent implements OnInit {
  salons?: ISalon[];
  isLoading = false;
  currentSearch: string;

  constructor(protected salonService: SalonService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.salonService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<ISalon[]>) => {
            this.isLoading = false;
            this.salons = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.salonService.query().subscribe(
      (res: HttpResponse<ISalon[]>) => {
        this.isLoading = false;
        this.salons = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ISalon): number {
    return item.id!;
  }

  delete(salon: ISalon): void {
    const modalRef = this.modalService.open(SalonDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.salon = salon;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
