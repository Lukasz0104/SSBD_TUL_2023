import { Building } from './building';

export interface Place {
    id: number;
    placeNumber: number;
    residentsNumber: number;
    squareFootage: number;
    building: Building;
    active: boolean;
}

export interface CreatePlaceDto {
    buildingId: number;
    placeNumber: number;
    squareFootage: number;
    residentsNumber: number;
}
