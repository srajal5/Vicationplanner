/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/**/*.{js,jsx,ts,tsx,html}',
  ],
  theme: {
    extend: {},
  },
  plugins: [
    // lightweight animations plugin already in package.json
    require('tailwindcss-animate'),
  ],
};
