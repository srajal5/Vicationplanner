import React from 'react';

const Card = ({ className = '', children, title }) => {
  return (
    <div className={`card-modern p-6 ${className}`}>
      {title && <h3 className="text-xl font-semibold mb-3">{title}</h3>}
      <div>{children}</div>
    </div>
  );
};

export default Card;
