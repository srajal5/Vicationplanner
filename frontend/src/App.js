import React from 'react';
import Navbar from './components/Navbar';
import { ThemeProvider } from './contexts/ThemeContext';
import { Routes, Route } from 'react-router-dom';
import TripPlanner from './pages/TripPlanner';
import TripResults from './pages/TripResults';
import Booking from './pages/Booking';
import SavedTrips from './pages/SavedTrips';

function App() {
  return (
    <ThemeProvider>
      <div className="flex flex-col min-h-screen">
        <Navbar />
        <main className="flex-grow container mx-auto p-4">
          <Routes>
            <Route path="/" element={<TripPlanner />} />
            <Route path="/results/:tripId" element={<TripResults />} />
            <Route path="/booking/:tripId" element={<Booking />} />
            <Route path="/saved" element={<SavedTrips />} />
            {/* fallback to planner */}
            <Route path="*" element={<TripPlanner />} />
          </Routes>
        </main>
        <footer className="bg-gray-800 text-white p-4 text-center">
          <p>&copy; 2024 Vicationplanner. All rights reserved.</p>
        </footer>
      </div>
    </ThemeProvider>
  );
}

export default App;

