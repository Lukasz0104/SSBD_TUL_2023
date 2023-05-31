import { Building } from './building';

export interface Place {
    id: number;
    placeNumber: number;
    residentsNumber: number;
    squareFootage: number;
    building: Building;
    active: boolean;
}
