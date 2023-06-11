import { Building } from './building';

export interface Place {
    id: number;
    version: number;
    placeNumber: number;
    residentsNumber: number;
    squareFootage: number;
    building: Building;
    active: boolean;
}

export interface PlaceEdit {
    id: number;
    version: number;
    placeNumber: number;
    residentsNumber: number;
    squareFootage: number;
    active: boolean;
}
