import React from 'react';
import { cva } from 'class-variance-authority';

const button = cva(
  'inline-flex items-center justify-center rounded-md font-medium transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:pointer-events-none',
  {
    variants: {
      intent: {
        primary: 'bg-gradient-to-r from-blue-600 to-indigo-600 text-white shadow-md hover:brightness-105',
        ghost: 'bg-transparent text-gray-700 hover:bg-gray-100',
        subtle: 'bg-white/70 text-gray-900 hover:bg-white/90',
      },
      size: {
        sm: 'px-3 py-1.5 text-sm',
        md: 'px-4 py-2 text-base',
        lg: 'px-6 py-3 text-lg',
      },
    },
    defaultVariants: {
      intent: 'primary',
      size: 'md',
    },
  }
);

const Button = React.forwardRef(({ className, intent, size, children, ...props }, ref) => {
  return (
    <button ref={ref} className={`${button({ intent, size })} ${className || ''}`} {...props}>
      {children}
    </button>
  );
});

Button.displayName = 'Button';

export default Button;
