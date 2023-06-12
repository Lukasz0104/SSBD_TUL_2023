import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Meter } from '../../model/meter';
import { BehaviorSubject, Observable } from 'rxjs';
import { ReadingPage } from '../../model/reading-page';
import { AuthService } from '../../../shared/services/auth.service';
import { MeterService } from '../../services/meter.service';
import { DatePipe } from '@angular/common';
import { Reading } from '../../model/reading';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AddReadingComponent } from '../add-reading/add-reading.component';

@Component({
    selector: 'app-meter',
    templateUrl: './meter.component.html'
})
export class MeterComponent implements OnInit {
    @Input() public meter: BehaviorSubject<Meter> | undefined;
    readings$: Observable<ReadingPage> | undefined;
    page = 1;
    pageSize = 5;

    @Output() readingAdded = new EventEmitter();

    constructor(
        private meterService: MeterService,
        protected authService: AuthService,
        private datePipe: DatePipe,
        private modalService: NgbModal
    ) {}

    ngOnInit() {
        this.getReadings();
    }

    getReadings() {
        if (this.meter) {
            if (this.authService.isOwner()) {
                this.readings$ = this.meterService.getReadingsAsOwner(
                    this.meter.getValue().id,
                    this.page - 1,
                    this.pageSize
                );
            } else {
                this.readings$ = this.meterService.getReadingsAsManager(
                    this.meter.getValue().id,
                    this.page - 1,
                    this.pageSize
                );
            }
        }
    }

    getCreated(reading: Reading) {
        if (reading.createdTime == null || reading.createdBy == null) {
            return '-';
        }
        return `${this.datePipe.transform(
            reading.createdTime,
            'dd/MM/yy HH:mm:ss'
        )}, ${reading.createdBy}`;
    }

    getUpdated(reading: Reading) {
        if (reading.updatedTime == null || reading.updatedBy == null) {
            return '-';
        }
        return `${this.datePipe.transform(
            reading.updatedTime.toLocaleString(),
            'dd/MM/yy HH:mm:ss'
        )}, ${reading.updatedBy}`;
    }

    isNextReadingBeforeNow() {
        if (this.meter) {
            return (
                new Date(this.meter.getValue().dateOfNextReading).getTime() -
                    new Date().getTime() <=
                0
            );
        }
        return false;
    }

    showAddReadingModal() {
        const ref = this.modalService.open(AddReadingComponent, {
            centered: true,
            scrollable: true
        });
        ref.componentInstance.meterId = this.meter?.getValue().id;
        ref.componentInstance.latestReading = this.meter;
        ref.result
            .then(() => {
                this.readingAdded.emit();
                this.getReadings();
            })
            .catch(() => {
                this.readingAdded.emit();
                this.getReadings();
            });
    }
}
