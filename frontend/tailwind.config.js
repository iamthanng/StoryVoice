/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        background: '#121212',
        surface: '#1E1E1E',
        primary: '#FF6B00',
        textPrimary: '#E0E0E0',
        textSecondary: '#A0A0A0'
      }
    },
  },
  plugins: [],
}
