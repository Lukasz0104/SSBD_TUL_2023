import { Component } from '@angular/core';
import { Building } from '../../model/building';
import { Observable } from 'rxjs';
import { BuildingService } from '../../services/building.service';
import { AuthService } from '../../../shared/services/auth.service';

@Component({
    selector: 'app-buildings',
    templateUrl: './buildings.component.html'
})
export class BuildingsComponent {
    protected buildings$: Observable<Building[]>;

    constructor(
        private buildingService: BuildingService,
        protected auth: AuthService
    ) {
        this.buildings$ = this.buildingService.findAllBuildings();
    }
}
