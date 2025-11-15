import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  CardActions,
  Button,
  Box,
  Chip,
  IconButton,
  Alert,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from '@mui/material';
import {
  Delete,
  Visibility,
  Download,
  FlightTakeoff,
  Hotel,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const SavedTrips = () => {
  const navigate = useNavigate();
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [deleteDialog, setDeleteDialog] = useState({ open: false, tripId: null });

  useEffect(() => {
    fetchSavedTrips();
  }, []);

  const fetchSavedTrips = async () => {
    try {
      const response = await axios.get('/api/trips');
      setTrips(response.data);
    } catch (err) {
      setError('Failed to load saved trips');
      console.error('Error fetching trips:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleViewTrip = (tripId) => {
    navigate(`/results/${tripId}`);
  };

  const handleBookTrip = (tripId) => {
    navigate(`/booking/${tripId}`);
  };

  const handleDeleteTrip = async (tripId) => {
    try {
      await axios.delete(`/api/trips/${tripId}`);
      setTrips(trips.filter(trip => trip.id !== tripId));
      setDeleteDialog({ open: false, tripId: null });
    } catch (err) {
      setError('Failed to delete trip');
      console.error('Error deleting trip:', err);
    }
  };

  const handleExportPdf = async (tripId) => {
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

  const handleExportExcel = async (tripId) => {
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
          Loading your saved trips...
        </Typography>
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Alert severity="error">{error}</Alert>
      </Container>
    );
  }

  if (trips.length === 0) {
    return (
      <Container maxWidth="lg" sx={{ py: 4, textAlign: 'center' }}>
        <FlightTakeoff sx={{ fontSize: 80, color: 'text.secondary', mb: 2 }} />
        <Typography variant="h4" gutterBottom>
          No Saved Trips Yet
        </Typography>
        <Typography variant="h6" color="text.secondary" sx={{ mb: 3 }}>
          Start planning your first vacation to see it here!
        </Typography>
        <Button
          variant="contained"
          size="large"
          onClick={() => navigate('/')}
          sx={{
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            '&:hover': {
              background: 'linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%)',
            },
          }}
        >
          Plan Your First Trip
        </Button>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h3" component="h1" gutterBottom sx={{ mb: 4 }}>
        Your Saved Trips
      </Typography>

      <Grid container spacing={3}>
        {trips.map((trip) => (
          <Grid item xs={12} md={6} lg={4} key={trip.id}>
            <Card
              sx={{
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                transition: 'transform 0.2s ease-in-out',
                '&:hover': {
                  transform: 'translateY(-4px)',
                },
              }}
            >
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography variant="h5" component="h2" gutterBottom>
                  {trip.destination}
                </Typography>
                
                <Box sx={{ display: 'flex', gap: 1, mb: 2, flexWrap: 'wrap' }}>
                  <Chip label={`${trip.startDate} - ${trip.endDate}`} size="small" color="primary" />
                  <Chip label={trip.theme} size="small" color="secondary" />
                  <Chip label={`${trip.groupSize} people`} size="small" />
                </Box>

                {trip.transportation && (
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                    <FlightTakeoff sx={{ fontSize: 16, color: 'text.secondary' }} />
                    <Typography variant="body2" color="text.secondary">
                      {trip.transportation.type} via {trip.transportation.provider}
                    </Typography>
                  </Box>
                )}

                {trip.accommodation && (
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                    <Hotel sx={{ fontSize: 16, color: 'text.secondary' }} />
                    <Typography variant="body2" color="text.secondary">
                      {trip.accommodation.name}
                    </Typography>
                  </Box>
                )}

                <Typography variant="h6" color="primary" sx={{ fontWeight: 'bold' }}>
                  {trip.budgetBreakdown?.totalCost} {trip.currency}
                </Typography>
              </CardContent>

              <CardActions sx={{ justifyContent: 'space-between', p: 2 }}>
                <Box>
                  <Button
                    size="small"
                    onClick={() => handleViewTrip(trip.id)}
                    startIcon={<Visibility />}
                  >
                    View
                  </Button>
                  <Button
                    size="small"
                    onClick={() => handleBookTrip(trip.id)}
                    variant="contained"
                    sx={{
                      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      '&:hover': {
                        background: 'linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%)',
                      },
                    }}
                  >
                    Book
                  </Button>
                </Box>
                
                <Box>
                  <IconButton
                    size="small"
                    onClick={() => handleExportPdf(trip.id)}
                    title="Export PDF"
                  >
                    <Download />
                  </IconButton>
                  <IconButton
                    size="small"
                    onClick={() => setDeleteDialog({ open: true, tripId: trip.id })}
                    title="Delete Trip"
                    color="error"
                  >
                    <Delete />
                  </IconButton>
                </Box>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={deleteDialog.open}
        onClose={() => setDeleteDialog({ open: false, tripId: null })}
      >
        <DialogTitle>Delete Trip</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to delete this trip? This action cannot be undone.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialog({ open: false, tripId: null })}>
            Cancel
          </Button>
          <Button
            onClick={() => handleDeleteTrip(deleteDialog.tripId)}
            color="error"
            variant="contained"
          >
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default SavedTrips;
