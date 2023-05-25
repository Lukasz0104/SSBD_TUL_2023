import { Injectable } from '@angular/core';
import config from '../../assets/ebok.json';

@Injectable({
    providedIn: 'root'
})
export class AppConfigService {
    private appConfig!: AppConfig;

    constructor() {
        this.loadAppConfig();
    }

    loadAppConfig() {
        this.appConfig = config as AppConfig;
    }

    get apiUrl() {
        if (!this.appConfig) {
            throw Error('Config file not loaded!');
        }

        return this.appConfig.apiUrl;
    }

    get languages() {
        if (!this.appConfig) {
            throw Error('Config file not loaded!');
        }

        return this.appConfig.languages;
    }

    get recaptchaKey() {
        if (!this.appConfig) {
            throw Error('Config file not loaded!');
        }

        return this.appConfig.recaptchaKey;
    }

    get accessLevels() {
        if (!this.appConfig) {
            throw Error('Config file not loaded!');
        }

        return this.appConfig.accessLevels;
    }
}

export type AppConfig = {
    apiUrl: string;
    languages: string[];
    recaptchaKey: string;
    accessLevels: {
        NONE: string;
        OWNER: string;
        MANAGER: string;
        ADMIN: string;
        ALL: string;
    };
};
