import { Component } from '@angular/core';
import { ToastService } from '../../services/toast.service';

@Component({
    selector: 'app-toast-section',
    templateUrl: './toast-section.component.html',
    styleUrls: ['./toast-section.component.css'],
})
export class ToastSectionComponent {
    constructor(public toastService: ToastService) {}
}
