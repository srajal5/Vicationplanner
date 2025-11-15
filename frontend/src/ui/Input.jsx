import React from 'react';

const Input = ({ label, value, onChange, placeholder = ' ', type = 'text', className = '' }) => {
  return (
    <div className="relative w-full">
      <input
        className={`input-modern ${className}`}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        type={type}
      />
      {label && <label className="input-label">{label}</label>}
    </div>
  );
};

export default Input;
