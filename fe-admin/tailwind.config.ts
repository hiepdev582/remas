import type { Config } from "tailwindcss";

export default <Partial<Config>>{
  content: [
    "./app/**/*.{vue,js,ts,jsx,tsx}",
    "./components/**/*.{vue,js,ts,jsx,tsx}",
    "./layouts/**/*.{vue,js,ts,jsx,tsx}",
    "./pages/**/*.{vue,js,ts,jsx,tsx}",
    "./plugins/**/*.{js,ts}",
    "./nuxt.config.{js,ts}",
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
        custom: "var(--border-radius)",
      },
    },
  },
};
