import { create } from "zustand";

type Theme = "light" | "dark";

interface ThemeState {
  theme: Theme;
  toggleTheme: () => void;
  setTheme: (theme: Theme) => void;
}

const getInitialTheme = (): Theme => {
  const saved = localStorage.getItem("theme-preference") as Theme | null;
  if (saved === "light" || saved === "dark") return saved;
  return window.matchMedia("(prefers-color-scheme: dark)").matches
    ? "dark"
    : "light";
};

export const useThemeStore = create<ThemeState>((set) => ({
  theme: getInitialTheme(),
  toggleTheme: () =>
    set((state) => {
      const nextTheme = state.theme === "light" ? "dark" : "light";
      localStorage.setItem("theme-preference", nextTheme);
      document.documentElement.classList.toggle("dark", nextTheme === "dark");
      return { theme: nextTheme };
    }),
  setTheme: (theme) => {
    localStorage.setItem("theme-preference", theme);
    document.documentElement.classList.toggle("dark", theme === "dark");
    set({ theme });
  },
}));
