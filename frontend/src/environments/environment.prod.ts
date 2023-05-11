export const environment = {
    production: true,
    apiUrl: 'http://localhost:8080/eBok',
    passwordRegex: new RegExp(
        '^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W)[A-Za-z\\d\\W]{8,}$'
    )
};
