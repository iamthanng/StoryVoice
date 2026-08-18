import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import translationVI from './locales/vi/translation.json';

const resources = {
  vi: {
    translation: translationVI
  }
};

i18n
  .use(initReactI18next)
  .init({
    resources,
    lng: 'vi', // Ngôn ngữ mặc định
    fallbackLng: 'vi', // Fallback language
    interpolation: {
      escapeValue: false // React đã tự động escape chống XSS
    }
  });

export default i18n;
