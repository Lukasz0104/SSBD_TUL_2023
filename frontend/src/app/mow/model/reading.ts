import { ControlledEntity } from '../../shared/model/controlled-entity';

export interface Reading extends ControlledEntity {
    id: number;
    value: number;
    date: Date;
    reliable: boolean;
}
