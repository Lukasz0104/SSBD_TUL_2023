import { DOCUMENT } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';

@Component({
    selector: 'app-theme-switch',
    templateUrl: './theme-switch.component.html'
})
export class ThemeSwitchComponent implements OnInit {
    private readonly ATTR_NAME = 'data-bs-theme';
    private readonly LOCAL_STORAGE_KEY = 'light_theme';
    protected lightTheme = true;

    constructor(@Inject(DOCUMENT) private document: Document) {}

    ngOnInit(): void {
        this.readLocalStorage();
        window.addEventListener('storage', () => this.readLocalStorage());
    }

    themeChanged() {
        this.lightTheme = !this.lightTheme;

        this.document.documentElement.setAttribute(
            this.ATTR_NAME,
            this.lightTheme ? 'light' : 'dark'
        );

        localStorage.setItem(
            this.LOCAL_STORAGE_KEY,
            JSON.stringify(this.lightTheme)
        );
    }

    private readLocalStorage() {
        const saved = localStorage.getItem(this.LOCAL_STORAGE_KEY);

        if (saved) {
            this.lightTheme = saved !== 'false';
        }

        this.document.documentElement.setAttribute(
            this.ATTR_NAME,
            this.lightTheme ? 'light' : 'dark'
        );
    }
}
