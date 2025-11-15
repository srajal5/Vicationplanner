import React, { useState } from 'react';
import { useUser, useClerk } from '@clerk/clerk-react';
import { User, LogOut, Settings, LogIn } from 'lucide-react';

const ProfileMenu = () => {
  const [isOpen, setIsOpen] = useState(false);
  const { user, isLoaded } = useUser();
  const { signOut, openSignIn } = useClerk();

  const handleLogout = async () => {
    await signOut();
    setIsOpen(false);
  };

  // Show loading state
  if (!isLoaded) {
    return <div className="w-10 h-10 rounded-full bg-gray-300 dark:bg-gray-600 animate-pulse" />;
  }

  // Show sign in button if not authenticated
  if (!user) {
    return (
      <button
        onClick={() => openSignIn()}
        className="flex items-center gap-2 px-4 py-2 rounded-full bg-gradient-to-r from-blue-600 to-purple-600 text-white font-medium hover:shadow-lg hover:shadow-blue-500/30 transition-all duration-300 hover:scale-105"
      >
        <LogIn className="w-4 h-4" />
        <span className="text-sm">Sign In</span>
      </button>
    );
  }

  // Show user profile and dropdown
  const userEmail = user.emailAddresses[0]?.emailAddress || 'User';
  const userName = user.firstName && user.lastName ? `${user.firstName} ${user.lastName}` : user.username || 'User';
  const userImage = user.imageUrl;

  return (
    <div className="relative">
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="flex items-center gap-2 px-3 py-2 rounded-full bg-gradient-to-r from-blue-500/20 to-purple-500/20 dark:from-blue-500/30 dark:to-purple-500/30 border border-blue-300/30 dark:border-blue-400/30 hover:border-blue-400/50 dark:hover:border-blue-400/50 transition-all duration-300"
      >
        {userImage ? (
          <img src={userImage} alt="User avatar" className="w-8 h-8 rounded-full object-cover" />
        ) : (
          <div className="w-8 h-8 rounded-full bg-gradient-to-r from-blue-500 to-purple-500 flex items-center justify-center">
            <User className="w-4 h-4 text-white" />
          </div>
        )}
        <div className="hidden md:block text-left">
          <p className="text-sm font-semibold text-gray-900 dark:text-white">{userName}</p>
          <p className="text-xs text-gray-600 dark:text-gray-400">{userEmail}</p>
        </div>
        <svg
          className={`w-4 h-4 text-gray-600 dark:text-gray-400 transition-transform duration-200 ${isOpen ? 'rotate-180' : ''}`}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 14l-7 7m0 0l-7-7m7 7V3" />
        </svg>
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-56 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-2xl shadow-xl z-50">
          <div className="p-4 border-b border-gray-100 dark:border-gray-700">
            <p className="font-semibold text-gray-900 dark:text-white">{userName}</p>
            <p className="text-sm text-gray-600 dark:text-gray-400">{userEmail}</p>
          </div>

          <button
            onClick={() => {
              window.location.href = user.profileImageUrl ? '/profile' : '#';
              setIsOpen(false);
            }}
            className="w-full flex items-center gap-3 px-4 py-3 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700/50 transition-colors duration-200 text-left"
          >
            <Settings className="w-4 h-4" />
            <span className="text-sm">Profile Settings</span>
          </button>

          <button
            onClick={handleLogout}
            className="w-full flex items-center gap-3 px-4 py-3 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors duration-200 text-left border-t border-gray-100 dark:border-gray-700"
          >
            <LogOut className="w-4 h-4" />
            <span className="text-sm">Sign Out</span>
          </button>
        </div>
      )}
    </div>
  );
};

export default ProfileMenu;
