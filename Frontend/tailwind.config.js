/** @type {import('tailwindcss').Config} */
import forms from '@tailwindcss/forms'
import containerQueries from '@tailwindcss/container-queries'

export default {
    content: [
        "./index.html",
        "./src/**/*.{vue,js,ts,jsx,tsx}",
    ],
    darkMode: "class",
    theme: {
        extend: {
            colors: {
                "primary": "#ffffff",
                "primary-hover": "#f3f4f6",
                "primary-foreground": "#111827",
                "background-light": "#ffffff",
                "background-dark": "#fafafa",
                "surface-light": "#ffffff",
                "surface-muted": "#f9fafb",
                "surface-dark": "#f3f4f6",
                "surface-dark-highlight": "#e5e7eb",
                "text-secondary": "#6b7280",
                "border-dark": "#e5e7eb",
            },
            fontFamily: {
                "display": ['"DM Sans"', '"Be Vietnam Pro"', "sans-serif"],
                "headline": ['"DM Sans"', '"Be Vietnam Pro"', "sans-serif"],
            },
            boxShadow: {
                "card-soft": "0 1px 3px rgba(0, 0, 0, 0.06), 0 8px 24px rgba(0, 0, 0, 0.04)",
                "card-hover": "0 4px 20px rgba(0, 0, 0, 0.08)",
            },
        },
    },
    plugins: [forms, containerQueries],
}
