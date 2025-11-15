import React from 'react';


const images = [
  {
    id: 'india',
    country: 'India',
    landmark: 'Taj Mahal, Agra',
    alt: 'Taj Mahal at sunrise, India',
    src: 'https://images.pexels.com/photos/1603650/pexels-photo-1603650.jpeg'
    
  },
  {
    id: 'usa',
    country: 'USA',
    landmark: 'Statue of Liberty, New York',
    alt: 'Statue of Liberty, New York, USA',
    src: 'https://images.pexels.com/photos/887849/pexels-photo-887849.jpeg'
  },
  {
    id: 'malaysia',
    country: 'Malaysia',
    landmark: 'Petronas Twin Towers, Kuala Lumpur',
    alt: 'Petronas Twin Towers, Kuala Lumpur, Malaysia',
    src: 'https://images.pexels.com/photos/3607991/pexels-photo-3607991.jpeg'
  },
  {
    id: 'dubai',
    country: 'Dubai',
    landmark: 'Burj Khalifa, Dubai',
    alt: 'Burj Khalifa skyline, Dubai',
    src: 'https://images.pexels.com/photos/5087050/pexels-photo-5087050.jpeg'
  }
];

const HeroImages = ({ onSelectDestination }) => {
  const handleCardClick = (landmark) => {
    if (onSelectDestination) {
      onSelectDestination(landmark);
    }
  };

  return (
    <section className="w-full max-w-5xl mx-auto mb-8">
      <div className="mb-3">
        <p className="text-sm text-gray-600 dark:text-gray-300 font-medium">Click any destination to get started</p>
      </div>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {images.map((img) => (
          <button
            key={img.id}
            onClick={() => handleCardClick(img.landmark)}
            className="relative overflow-hidden rounded-2xl shadow-lg bg-gray-100 dark:bg-gray-800 transform transition-all duration-400 hover:scale-105 hover:-translate-y-1 z-0 cursor-pointer focus:outline-none focus:ring-2 focus:ring-blue-400 focus:ring-offset-2 dark:focus:ring-offset-gray-900"
            style={{ minHeight: 140 }}
            type="button"
            aria-label={`Select ${img.landmark} as destination`}
          >
            <img
              src={img.src}
              alt={img.alt}
              className="w-full h-full object-cover block pop-up-image pointer-events-none"
              style={{ display: 'block' }}
            />

            <div className="absolute inset-0 bg-black/0 hover:bg-black/20 transition-colors duration-300 pointer-events-none" />

            <div className="absolute left-3 bottom-3 px-3 py-2 rounded-xl bg-white/85 dark:bg-black/60 text-xs font-bold text-gray-900 dark:text-gray-100 backdrop-blur-sm pointer-events-none">
              <div className="text-sm">{img.country}</div>
              <div className="text-xs text-gray-700 dark:text-gray-300 mt-0.5">{img.landmark.split(', ')[0]}</div>
            </div>
          </button>
        ))}
      </div>
    </section>
  );
};

export default HeroImages;
