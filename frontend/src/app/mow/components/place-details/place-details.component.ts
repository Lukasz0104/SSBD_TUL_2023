import { Component } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { PlaceCategoriesComponent } from '../place-categories/place-categories.component';

@Component({
    selector: 'app-place-details',
    templateUrl: './place-details.component.html',
    styleUrls: ['./place-details.component.css']
})
export class PlaceDetailsComponent {
    constructor(private modalService: NgbModal) {}

    placeCategories(id: number) {
        const modalRef: NgbModalRef = this.modalService.open(
            PlaceCategoriesComponent,
            {
                centered: true,
                size: 'xl'
            }
        );
        modalRef.componentInstance.id = id;
    }
}
