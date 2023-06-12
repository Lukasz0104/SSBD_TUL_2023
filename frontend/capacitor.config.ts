import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'pl.lodz.p.it.ssbd2023.ssbd05',
  appName: 'frontend',
  webDir: 'dist/frontend',
  server: {
    androidScheme: 'https'
  }
};

export default config;
