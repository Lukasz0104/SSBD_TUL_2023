import config from '../../assets/ebok.json';

export const AccessLevels = config.accessLevels;

export type AccessType = string;

export interface ChangeAccessLevelDto {
    accessType: AccessType;
}
