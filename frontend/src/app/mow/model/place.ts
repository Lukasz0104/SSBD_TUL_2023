import { Building } from '../../shared/model/building';

export interface Place {
    id: number;
    placeNumber: number;
    residentsNumber: number;
    squareFootage: number;
    building: Building;
    active: boolean;
}
