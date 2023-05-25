import { AppConfigService } from '../services/app-config-service';

export const AccessLevels = new AppConfigService().accessLevels;

export type AccessType = string;

export interface ChangeAccessLevelDto {
    accessType: AccessType;
}
