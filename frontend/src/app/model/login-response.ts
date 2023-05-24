export interface LoginResponse {
    jwt: string;
    refreshToken: string;
    language: string;
    lightThemePreferred: boolean;
}
