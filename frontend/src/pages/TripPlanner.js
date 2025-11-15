import React, { useState } from 'react';
import axios from 'axios';
import Button from '../ui/Button';
import Card from '../ui/Card';
import Input from '../ui/Input';
import { useNavigate } from 'react-router-dom';
import { useUser, useClerk } from '@clerk/clerk-react';
import HeroImages from '../components/HeroImages';
import CurrencySelector from '../components/CurrencySelector';

const TripPlanner = () => {
  const navigate = useNavigate();
  const { user, isLoaded } = useUser();
  const { openSignIn } = useClerk();
  const [form, setForm] = useState({ destination: '', duration: '', budget: '', travelers: '', theme: '', startDate: '', endDate: '', currency: 'INR', startingPoint: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const handleSelectDestination = (landmark) => {
    setForm({ ...form, destination: landmark });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Check if user is signed in
    if (!user) {
      openSignIn();
      return;
    }

    setError('');
    setLoading(true);

    try {
      const prefs = {
        budget: Number(form.budget) || 0,
          destination: form.destination || '',
        currency: form.currency || 'INR',
        startDate: form.startDate || null,
        endDate: form.endDate || null,
        theme: form.theme || '',
        groupSize: Number(form.travelers) || 1,
        startingPoint: form.startingPoint || '',
      };

      const response = await axios.post('/api/trips/plan', prefs);
      const trip = response.data;
      if (trip && trip.id) {
        navigate(`/results/${trip.id}`);
      } else {
        setError('Failed to receive trip from backend');
      }
    } catch (err) {
      console.error('Error planning trip:', err);
      setError('Server error while planning trip');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-4">
      <HeroImages onSelectDestination={handleSelectDestination} />

      <Card className="max-w-4xl mx-auto">
        <h2 className="text-2xl font-semibold mb-2">Plan Your Trip</h2>
        <p className="text-sm text-gray-600 mb-6">Fill in the details below to get a personalized trip plan.</p>

        <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="space-y-1">
            <label className="text-sm font-medium">Destination (optional)</label>
            <Input value={form.destination} onChange={handleChange('destination')} placeholder="e.g., Paris, France" />
          </div>

          <div className="space-y-1">
            <label className="text-sm font-medium">Start Date</label>
            <Input value={form.startDate} onChange={handleChange('startDate')} type="date" />
          </div>

          <div className="space-y-1">
            <label className="text-sm font-medium">End Date</label>
            <Input value={form.endDate} onChange={handleChange('endDate')} type="date" />
          </div>

          <div className="space-y-1">
            <label className="text-sm font-medium">Budget ($)</label>
            <Input value={form.budget} onChange={handleChange('budget')} type="number" placeholder="e.g., 2000" />
          </div>

          <div className="space-y-1">
            <label className="text-sm font-medium">Number of Travelers</label>
            <Input value={form.travelers} onChange={handleChange('travelers')} type="number" placeholder="e.g., 2" />
          </div>

         
         

          <div className="md:col-span-2 space-y-1">
            <label className="text-sm font-medium">Theme</label>
            <select
              value={form.theme}
              onChange={handleChange('theme')}
              className="w-full rounded-2xl border-2 px-4 py-3 bg-white/50"
            >
              <option value="">Select a theme</option>
              <option value="beach">Beach</option>
              <option value="adventure">Adventure</option>
              <option value="culture">Culture</option>
              <option value="food">Food</option>
            </select>
          </div>

          <div className="md:col-span-2 space-y-1">
            <label className="text-sm font-medium">Currency</label>
            <CurrencySelector value={form.currency} onChange={handleChange('currency')} />
          </div>

          {error && <div className="md:col-span-2 text-red-600">{error}</div>}

          <div className="md:col-span-2 flex justify-end mt-4">
            <Button type="submit" disabled={loading}>{loading ? 'Planning...' : 'Generate Trip Plan'}</Button>
          </div>
        </form>
      </Card>
    </div>
  );
};

export default TripPlanner;

