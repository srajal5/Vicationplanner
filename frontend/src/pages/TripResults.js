import React, { useState, useEffect } from 'react';
import {
  Container,
  Paper,
  Typography,
  Button,
  Grid,
  Box,
  Card,
  CardContent,
  Chip,
  Divider,
  Tab,
  Tabs,
  Alert,
  CircularProgress,
  IconButton,
} from '@mui/material';
import {
  ArrowBack,
  BookmarkBorder,
  Bookmark,
  FlightTakeoff,
  Hotel,
  Attractions,
  Map,
  Download,
  Share,
} from '@mui/icons-material';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const TripResults = () => {
  const { tripId } = useParams();
  const navigate = useNavigate();
  const [trip, setTrip] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [tabValue, setTabValue] = useState(0);
  const [saved, setSaved] = useState(false);

  const fetchTrip = async () => {
    try {
      const response = await axios.get(`/api/trips/${tripId}`);
      setTrip(response.data);
    } catch (err) {
      setError('Failed to load trip details');
      console.error('Error fetching trip:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTrip();
  }, [tripId]);

  const handleSaveTrip = async () => {
    try {
      await axios.post(`/api/trips/${tripId}/save`);
      setSaved(true);
    } catch (err) {
      console.error('Error saving trip:', err);
    }
  };

  const handleBookNow = () => {
    navigate(`/booking/${tripId}`);
  };

  const handleExportPdf = async () => {
    try {
      const response = await axios.get(`/api/export/pdf/${tripId}`, {
        responseType: 'blob',
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `trip-plan-${tripId}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error('Error exporting PDF:', err);
    }
  };

  const handleExportExcel = async () => {
    try {
      const response = await axios.get(`/api/export/excel/${tripId}`, {
        responseType: 'blob',
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `trip-plan-${tripId}.xlsx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error('Error exporting Excel:', err);
    }
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 4, textAlign: 'center' }}>
        <CircularProgress size={60} />
        <Typography variant="h6" sx={{ mt: 2 }}>
          Loading trip details...
        </Typography>
      </Container>
    );
  }

  if (error || !trip) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Alert severity="error">{error}</Alert>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/')}
          sx={{ mt: 2 }}
        >
          Back to Planning
        </Button>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      {/* Header */}
      <Box sx={{ mb: 4 }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/')}
          sx={{ mb: 2 }}
        >
          Back to Planning
        </Button>
        
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 3 }}>
          <Box>
            <Typography variant="h3" component="h1" gutterBottom>
              {trip.destination}
            </Typography>
            <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
              <Chip label={`${trip.startDate} - ${trip.endDate}`} color="primary" />
              <Chip label={trip.theme} color="secondary" />
              <Chip label={`${trip.groupSize} people`} />
            </Box>
          </Box>
          
          <Box sx={{ display: 'flex', gap: 1 }}>
            <IconButton onClick={handleSaveTrip} color={saved ? 'primary' : 'default'}>
              {saved ? <Bookmark /> : <BookmarkBorder />}
            </IconButton>
            <IconButton>
              <Share />
            </IconButton>
            <Button
              variant="outlined"
              startIcon={<Download />}
              onClick={handleExportPdf}
            >
              PDF
            </Button>
            <Button
              variant="outlined"
              startIcon={<Download />}
              onClick={handleExportExcel}
            >
              Excel
            </Button>
            <Button
              variant="contained"
              size="large"
              onClick={handleBookNow}
              sx={{
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                '&:hover': {
                  background: 'linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%)',
                },
              }}
            >
              Book Now
            </Button>
          </Box>
        </Box>
      </Box>

      {/* Tabs */}
      <Paper sx={{ mb: 3 }}>
        <Tabs
          value={tabValue}
          onChange={(e, newValue) => setTabValue(newValue)}
          variant="fullWidth"
        >
          <Tab icon={<FlightTakeoff />} label="Overview" />
          <Tab icon={<Attractions />} label="Itinerary" />
          <Tab icon={<Map />} label="Map" />
        </Tabs>
      </Paper>

      {/* Tab Content */}
      {tabValue === 0 && (
        <Grid container spacing={3}>
          {/* Transportation */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <FlightTakeoff color="primary" />
                  Transportation
                </Typography>
                {trip.transportation && (
                  <Box>
                    <Typography variant="body2" color="text.secondary">
                      {trip.transportation.type} • {trip.transportation.provider}
                    </Typography>
                    <Typography variant="body1" sx={{ mt: 1 }}>
                      {trip.transportation.origin} → {trip.transportation.destination}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Departure: {trip.transportation.departureDate}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Return: {trip.transportation.returnDate}
                    </Typography>
                    <Typography variant="h6" color="primary" sx={{ mt: 1 }}>
                      {trip.transportation.cost} {trip.currency}
                    </Typography>
                  </Box>
                )}
              </CardContent>
            </Card>
          </Grid>

          {/* Accommodation */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Hotel color="primary" />
                  Accommodation
                </Typography>
                {trip.accommodation && (
                  <Box>
                    <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                      {trip.accommodation.name}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {trip.accommodation.type} • ⭐ {trip.accommodation.rating}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {trip.accommodation.location}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Check-in: {trip.accommodation.checkInDate}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Check-out: {trip.accommodation.checkOutDate}
                    </Typography>
                    <Typography variant="h6" color="primary" sx={{ mt: 1 }}>
                      {trip.accommodation.cost} {trip.currency}
                    </Typography>
                  </Box>
                )}
              </CardContent>
            </Card>
          </Grid>

          {/* Budget Breakdown */}
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Budget Breakdown
                </Typography>
                {trip.budgetBreakdown && (
                  <Grid container spacing={2}>
                    <Grid item xs={6} sm={3}>
                      <Box sx={{ textAlign: 'center' }}>
                        <Typography variant="body2" color="text.secondary">
                          Transportation
                        </Typography>
                        <Typography variant="h6">
                          {trip.budgetBreakdown.transportationCost} {trip.currency}
                        </Typography>
                      </Box>
                    </Grid>
                    <Grid item xs={6} sm={3}>
                      <Box sx={{ textAlign: 'center' }}>
                        <Typography variant="body2" color="text.secondary">
                          Accommodation
                        </Typography>
                        <Typography variant="h6">
                          {trip.budgetBreakdown.accommodationCost} {trip.currency}
                        </Typography>
                      </Box>
                    </Grid>
                    <Grid item xs={6} sm={3}>
                      <Box sx={{ textAlign: 'center' }}>
                        <Typography variant="body2" color="text.secondary">
                          Activities
                        </Typography>
                        <Typography variant="h6">
                          {trip.budgetBreakdown.activitiesCost} {trip.currency}
                        </Typography>
                      </Box>
                    </Grid>
                    <Grid item xs={6} sm={3}>
                      <Box sx={{ textAlign: 'center' }}>
                        <Typography variant="body2" color="text.secondary">
                          Total
                        </Typography>
                        <Typography variant="h5" color="primary" sx={{ fontWeight: 'bold' }}>
                          {trip.budgetBreakdown.totalCost} {trip.currency}
                        </Typography>
                      </Box>
                    </Grid>
                  </Grid>
                )}
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {tabValue === 1 && (
        <Paper sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            Daily Itinerary
          </Typography>
          {trip.dailyItineraries && trip.dailyItineraries.length > 0 ? (
            trip.dailyItineraries.map((day, index) => (
              <Box key={index} sx={{ mb: 3 }}>
                <Typography variant="h6" color="primary" gutterBottom>
                  Day {index + 1} - {day.date}
                </Typography>
                {day.activities && day.activities.map((activity, actIndex) => (
                  <Card key={actIndex} variant="outlined" sx={{ mb: 2, ml: 2 }}>
                    <CardContent>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <Box>
                          <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                            {activity.time} - {activity.name}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            {activity.description}
                          </Typography>
                          <Typography variant="body2" color="primary" sx={{ mt: 1 }}>
                            {activity.cost} {trip.currency}
                          </Typography>
                        </Box>
                        <Chip label={activity.type} size="small" color="secondary" />
                      </Box>
                    </CardContent>
                  </Card>
                ))}
                <Divider sx={{ mt: 2 }} />
              </Box>
            ))
          ) : (
            <Typography color="text.secondary">
              No itinerary available for this trip.
            </Typography>
          )}
        </Paper>
      )}

      {tabValue === 2 && (
        <Paper sx={{ p: 3, textAlign: 'center' }}>
          <Typography variant="h6" gutterBottom>
            Interactive Map
          </Typography>
          <Typography color="text.secondary">
            Map visualization will be displayed here showing your trip route and points of interest.
          </Typography>
        </Paper>
      )}
    </Container>
  );
};

export default TripResults;
