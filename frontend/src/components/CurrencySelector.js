import React, { useState } from 'react';
import { DollarSign, IndianRupee, PoundSterling, EuroIcon, Banknote, WalletCards } from 'lucide-react';

// Currency list with icons and codes
const currencies = [
  { code: 'USD', name: 'US Dollar', icon: DollarSign, symbol: '$' },
  { code: 'INR', name: 'Indian Rupee', icon: IndianRupee, symbol: '₹' },
  { code: 'GBP', name: 'British Pound', icon: PoundSterling, symbol: '£' },
  { code: 'EUR', name: 'Euro', icon: EuroIcon, symbol: '€' },
  { code: 'JPY', name: 'Japanese Yen', icon: Banknote, symbol: '¥' },
  { code: 'AED', name: 'UAE Dirham', icon: WalletCards, symbol: 'د.إ' },
  { code: 'MYR', name: 'Malaysian Ringgit', icon: DollarSign, symbol: 'RM' },
  { code: 'SGD', name: 'Singapore Dollar', icon: DollarSign, symbol: 'S$' },
];

const CurrencySelector = ({ value, onChange }) => {
  const [isOpen, setIsOpen] = useState(false);
  
  const selectedCurrency = currencies.find(c => c.code === value) || currencies[0];
  const SelectedIcon = selectedCurrency.icon;

  return (
    <div className="relative">
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        className="w-full flex items-center gap-2 px-4 py-3 rounded-2xl border-2 border-gray-200/60 bg-white/50 dark:bg-gray-800/50 dark:border-gray-600/60 hover:border-gray-300 dark:hover:border-gray-500 hover:bg-white/70 dark:hover:bg-gray-800/70 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-blue-400 focus:border-blue-400 text-left"
      >
        <SelectedIcon className="w-5 h-5 text-blue-600 dark:text-blue-400 flex-shrink-0" />
        <div className="flex-grow text-sm">
          <div className="font-semibold text-gray-900 dark:text-gray-100">{selectedCurrency.code}</div>
          <div className="text-xs text-gray-600 dark:text-gray-400">{selectedCurrency.name}</div>
        </div>
        <svg
          className={`w-5 h-5 text-gray-600 dark:text-gray-400 transition-transform duration-200 ${isOpen ? 'rotate-180' : ''}`}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 14l-7 7m0 0l-7-7m7 7V3" />
        </svg>
      </button>

      {isOpen && (
        <div className="absolute top-full left-0 right-0 mt-2 bg-white dark:bg-gray-800 border-2 border-gray-200 dark:border-gray-700 rounded-2xl shadow-lg z-10 overflow-hidden">
          <div className="max-h-80 overflow-y-auto">
            {currencies.map((currency) => {
              const CurrencyIcon = currency.icon;
              const isSelected = value === currency.code;
              return (
                <button
                  key={currency.code}
                  type="button"
                  onClick={() => {
                    onChange({ target: { value: currency.code } });
                    setIsOpen(false);
                  }}
                  className={`w-full flex items-center gap-3 px-4 py-3 text-left transition-colors duration-200 border-b border-gray-100 dark:border-gray-700 last:border-b-0 ${
                    isSelected
                      ? 'bg-blue-50 dark:bg-blue-900/30 text-blue-900 dark:text-blue-100'
                      : 'hover:bg-gray-50 dark:hover:bg-gray-700/50 text-gray-900 dark:text-gray-100'
                  }`}
                >
                  <CurrencyIcon className={`w-5 h-5 flex-shrink-0 ${isSelected ? 'text-blue-600 dark:text-blue-400' : 'text-gray-400 dark:text-gray-500'}`} />
                  <div className="flex-grow">
                    <div className="font-semibold text-sm">{currency.code}</div>
                    <div className="text-xs text-gray-600 dark:text-gray-400">{currency.name}</div>
                  </div>
                  <span className="text-sm font-medium text-gray-500 dark:text-gray-400">{currency.symbol}</span>
                  {isSelected && (
                    <svg className="w-5 h-5 text-blue-600 dark:text-blue-400" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                    </svg>
                  )}
                </button>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};

export default CurrencySelector;
