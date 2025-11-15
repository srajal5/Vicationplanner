import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Button from '../ui/Button';
import Card from '../ui/Card';
import Input from '../ui/Input';

const Booking = () => {
  const { tripId } = useParams();
  const navigate = useNavigate();
  const [trip, setTrip] = useState(null);
  const [loading, setLoading] = useState(true);
  const [booking, setBooking] = useState(false);
  const [error, setError] = useState('');
  const [activeStep, setActiveStep] = useState(0);
  const [formData, setFormData] = useState({ name: '', email: '', phone: '', cardNumber: '' });

  const steps = ['Trip Summary', 'Traveler Details', 'Payment', 'Confirmation'];

  useEffect(() => {
    let mounted = true;
    const fetchTrip = async () => {
      try {
        const response = await axios.get(`/api/trips/${tripId}`);
        if (mounted) setTrip(response.data);
      } catch (err) {
        if (mounted) setError('Failed to load trip details');
        console.error('Error fetching trip:', err);
      } finally {
        if (mounted) setLoading(false);
      }
    };

    if (tripId) fetchTrip();
    return () => { mounted = false; };
  }, [tripId]);

  const handleInputChange = (field) => (event) => {
    setFormData((prev) => ({ ...prev, [field]: event.target.value }));
  };

  const handleNext = () => setActiveStep((s) => Math.min(s + 1, steps.length - 1));
  const handleBack = () => setActiveStep((s) => Math.max(s - 1, 0));

  const handleConfirmBooking = async () => {
    setBooking(true);
    setError('');
    try {
      const bookingData = {
        tripId,
        travelerName: formData.name,
        travelerEmail: formData.email,
        travelerPhone: formData.phone,
      };

      const response = await axios.post('/api/booking/book', bookingData);
      if (response?.data?.success) {
        setActiveStep(3);
      } else {
        setError(response?.data?.message || 'Booking failed');
      }
    } catch (err) {
      setError('Booking failed. Please try again.');
      console.error('Error booking trip:', err);
    } finally {
      setBooking(false);
    }
  };

  const renderStepContent = (step) => {
    switch (step) {
      case 0:
        return (
          <div>
            <h3 className="mb-3 text-lg font-semibold">Trip Summary</h3>
            <Card className="p-4">
              <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                <div>
                  <div className="text-xs text-gray-500">Destination</div>
                  <div className="text-lg font-medium">{trip?.destination}</div>
                </div>
                <div>
                  <div className="text-xs text-gray-500">Dates</div>
                  <div className="text-lg font-medium">{trip?.startDate} - {trip?.endDate}</div>
                </div>
                <div>
                  <div className="text-xs text-gray-500">Transportation</div>
                  <div>{trip?.transportation?.type} via {trip?.transportation?.provider}</div>
                </div>
                <div>
                  <div className="text-xs text-gray-500">Accommodation</div>
                  <div>{trip?.accommodation?.name} ({trip?.accommodation?.type})</div>
                </div>
                <div className="flex items-center justify-between pt-3 border-t md:col-span-2">
                  <div className="text-lg font-semibold">Total Cost</div>
                  <div className="text-xl font-bold text-indigo-600">{trip?.budgetBreakdown?.totalCost} {trip?.currency}</div>
                </div>
              </div>
            </Card>
          </div>
        );

      case 1:
        return (
          <div>
            <h3 className="mb-3 text-lg font-semibold">Traveler Details</h3>
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
              <div>
                <label className="text-sm font-medium">Full Name</label>
                <Input value={formData.name} onChange={handleInputChange('name')} placeholder="Your full name" />
              </div>
              <div>
                <label className="text-sm font-medium">Email</label>
                <Input value={formData.email} onChange={handleInputChange('email')} type="email" placeholder="you@example.com" />
              </div>
              <div className="md:col-span-2">
                <label className="text-sm font-medium">Phone Number</label>
                <Input value={formData.phone} onChange={handleInputChange('phone')} placeholder="+1 555 5555" />
              </div>
            </div>
          </div>
        );

      case 2:
        return (
          <div>
            <h3 className="mb-3 text-lg font-semibold">Payment Information</h3>
            <div className="mb-3 alert-modern">This is a demo application. No real payment will be processed.</div>
            <div className="grid grid-cols-1 gap-4">
              <div>
                <label className="text-sm font-medium">Card Number</label>
                <Input value={formData.cardNumber} onChange={handleInputChange('cardNumber')} placeholder="4111 1111 1111 1111" />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium">Expiry Date</label>
                  <Input placeholder="MM/YY" />
                </div>
                <div>
                  <label className="text-sm font-medium">CVV</label>
                  <Input placeholder="123" />
                </div>
              </div>
            </div>
          </div>
        );

      case 3:
        return (
          <div className="text-center">
            <div className="mb-4 text-6xl text-green-500">✓</div>
            <h3 className="mb-2 text-2xl font-semibold">Booking Confirmed!</h3>
            <p className="mb-4 text-gray-600">Your trip has been successfully booked. You will receive a confirmation email shortly.</p>
            <div className="flex justify-center">
              <Button onClick={() => navigate('/saved')}>View Saved Trips</Button>
            </div>
          </div>
        );

      default:
        return null;
    }
  };

  if (loading) {
    return (
      <div className="container p-6 mx-auto text-center">
        <div className="w-12 h-12 mx-auto mb-4 loading-modern" />
        <div className="text-lg font-medium">Loading trip details...</div>
      </div>
    );
  }

  if (error && !trip) {
    return (
      <div className="container p-6 mx-auto">
        <div className="alert-modern">{error}</div>
        <div className="mt-4">
          <Button intent="ghost" size="md" onClick={() => navigate(`/results/${tripId}`)}>Back to Results</Button>
        </div>
      </div>
    );
  }

  return (
    <div className="container max-w-3xl p-6 mx-auto">
      <div className="mb-4">
        <Button intent="ghost" onClick={() => navigate(`/results/${tripId}`)}>Back to Results</Button>
      </div>

      <div className="p-6 card-modern">
        <h1 className="mb-4 text-2xl font-bold text-center">Complete Your Booking</h1>

        <div className="flex justify-center gap-2 mb-6">
          {steps.map((label, idx) => (
            <div key={label} className={`px-3 py-1 rounded-full text-sm ${idx === activeStep ? 'bg-indigo-600 text-white' : 'bg-gray-200 text-gray-700'}`}>
              {label}
            </div>
          ))}
        </div>

        {error && (
          <div className="mb-4 alert-modern">{error}</div>
        )}

        <div className="min-h-[320px]">
          {renderStepContent(activeStep)}
        </div>

        {activeStep < 3 && (
          <div className="flex justify-between mt-6">
            <Button intent="ghost" onClick={handleBack} disabled={activeStep === 0}>Back</Button>
            <Button onClick={activeStep === 2 ? handleConfirmBooking : handleNext} disabled={booking}>
              {booking ? (
                <>
                  <span className="inline-block w-4 h-4 mr-2 align-middle loading-modern" />Processing...
                </>
              ) : activeStep === 2 ? (
                'Confirm Booking'
              ) : (
                'Next'
              )}
            </Button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Booking;

