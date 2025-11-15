import React from 'react';
import { Plane } from 'lucide-react';
import ThemeToggle from './ThemeToggle';
import ProfileMenu from './ProfileMenu';

const Navbar = () => {
  return (
    <nav className="sticky top-0 z-40 backdrop-blur-xl bg-white/10 dark:bg-gray-900/30 border-b border-white/20 dark:border-gray-700/30 shadow-sm">
      <div className="container flex items-center justify-between px-4 py-3 mx-auto">
        {/* Logo and Brand */}
        <div className="flex items-center gap-8">
          <a href="/" className="flex items-center gap-2 group">
            <div className="p-2 rounded-lg bg-gradient-to-br from-blue-600 to-purple-600 group-hover:shadow-lg group-hover:shadow-blue-500/30 transition-all duration-300">
              <Plane className="w-5 h-5 text-white" />
            </div>
            <span className="text-xl font-extrabold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">Vicationplanner</span>
          </a>

          {/* Navigation Links */}
          <div className="items-center hidden gap-6 md:flex">
            <a
              href="/"
              className="text-sm font-medium text-gray-700 dark:text-gray-300 hover:text-blue-600 dark:hover:text-blue-400 transition-colors duration-200 relative group"
            >
              Home
              <span className="absolute bottom-0 left-0 w-0 h-0.5 bg-gradient-to-r from-blue-600 to-purple-600 group-hover:w-full transition-all duration-300" />
            </a>
            <a
              href="/trip-planner"
              className="text-sm font-medium text-gray-700 dark:text-gray-300 hover:text-blue-600 dark:hover:text-blue-400 transition-colors duration-200 relative group"
            >
              Trip Planner
              <span className="absolute bottom-0 left-0 w-0 h-0.5 bg-gradient-to-r from-blue-600 to-purple-600 group-hover:w-full transition-all duration-300" />
            </a>
            <a
              href="/saved"
              className="text-sm font-medium text-gray-700 dark:text-gray-300 hover:text-blue-600 dark:hover:text-blue-400 transition-colors duration-200 relative group"
            >
              Saved Trips
              <span className="absolute bottom-0 left-0 w-0 h-0.5 bg-gradient-to-r from-blue-600 to-purple-600 group-hover:w-full transition-all duration-300" />
            </a>
          </div>
        </div>

        {/* Right Side: Theme Toggle and Profile */}
        <div className="flex items-center gap-4">
          <ThemeToggle />
          <ProfileMenu />
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
