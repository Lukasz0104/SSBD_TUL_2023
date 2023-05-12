export const environment = {
    production: true,
    apiUrl: 'https://team-5.proj-sum.it.p.lodz.pl/api',
    passwordRegex: new RegExp(
        '^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W)[A-Za-z\\d\\W]{8,}$'
    ),
    languages: ['EN', 'PL']
};
