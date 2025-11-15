import React, { useContext, useMemo } from 'react';
import { ThemeContext } from '../contexts/ThemeContext';
import { Sun, Moon } from 'lucide-react';

// A compact accessible toggle switch that flips between light and dark.
// If the app is set to `system`, the visual state follows the OS preference.
const ThemeToggle = () => {
  const { theme, setTheme } = useContext(ThemeContext);

  // determine whether the UI should be shown as "dark" currently
  const isDark = useMemo(() => {
    if (theme === 'dark') return true;
    if (theme === 'light') return false;
    // system
    return typeof window !== 'undefined' && window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
  }, [theme]);

  const toggle = () => {
    // If currently dark (including when system resolves to dark), switch to light.
    // If currently light, switch to dark.
    // When switching we set explicit 'dark' or 'light' so user choice is remembered.
    setTheme(isDark ? 'light' : 'dark');
  };

  return (
    <button
      aria-pressed={isDark}
      type="button"
      onClick={toggle}
      title={isDark ? 'Switch to light mode' : 'Switch to dark mode'}
      className="flex items-center gap-2 p-1 rounded-full focus:outline-none focus:ring-2 focus:ring-blue-400"
    >
      <div
        className={`relative w-12 h-7 transition-colors duration-200 rounded-full ${isDark ? 'bg-slate-700' : 'bg-slate-200'}`}
        aria-hidden
      >
        <div
          className={`absolute top-0.5 left-0.5 w-6 h-6 bg-white rounded-full shadow transform transition-transform duration-200 ${isDark ? 'translate-x-5' : 'translate-x-0'}`}
        />
      </div>

      <span className="sr-only">Toggle theme</span>
      <span className="hidden md:flex items-center text-sm text-gray-600 dark:text-gray-300">
        {isDark ? <Moon className="w-4 h-4" /> : <Sun className="w-4 h-4" />}
      </span>
    </button>
  );
};

export default ThemeToggle;