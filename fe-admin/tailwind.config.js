/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./components/**/*.{js,vue,ts}",
    "./layouts/**/*.vue",
    "./pages/**/*.vue",
    "./plugins/**/*.{js,ts}",
    "./app.vue",
    "./error.vue",
  ],
  theme: {
    extend: {
      colors: {
        primary: "var(--color-primary)",
        "primary-hover": "var(--color-primary-hover)",
        "primary-active": "var(--color-primary-active)",
        secondary: "var(--color-secondary)",
        "text-primary": "var(--color-text-primary)",
        error: "var(--color-error)",
        info: "var(--color-info)",
        success: "var(--color-success)",
        warning: "var(--color-warning)",
      },
      borderRadius: {
        DEFAULT: "var(--border-radius)",
      },
    },
  },
  plugins: [],
};
