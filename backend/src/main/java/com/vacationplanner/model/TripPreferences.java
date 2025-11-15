package com.vacationplanner.model;

import java.time.LocalDate;

/**
 * Represents user preferences for planning a trip.
 * Contains all the input parameters provided by the user.
 */
public class TripPreferences {
    private double budget;
    private String currency;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String theme;
    private int groupSize;
    private String startingPoint;
    private TripPlan.BudgetBreakdown budgetBreakdown;

    /**
     * Constructor for TripPreferences.
     *
     * @param budget The total budget for the trip
     * @param currency The currency of the budget (e.g., USD, EUR)
     * @param startDate The start date of the trip
     * @param endDate The end date of the trip
     * @param theme The preferred theme of the trip (e.g., Adventure, Relaxation)
     * @param groupSize The size of the traveling group (e.g., Solo, Couple, Family)
     * @param startingPoint The starting location for the trip
     */
    public TripPreferences(double budget, String currency, LocalDate startDate, LocalDate endDate, 
                          String theme, int groupSize, String startingPoint) {
        this.budget = budget;
        this.currency = currency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.theme = theme;
        this.groupSize = groupSize;
        this.startingPoint = startingPoint;
    }

    /**
     * Constructor including destination.
     */
    public TripPreferences(double budget, String currency, String destination, LocalDate startDate, LocalDate endDate,
                          String theme, int groupSize, String startingPoint) {
        this.budget = budget;
        this.currency = currency;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.theme = theme;
        this.groupSize = groupSize;
        this.startingPoint = startingPoint;
    }

    /**
     * Default constructor for TripPreferences.
     */
    public TripPreferences() {
        // Default constructor
    }

    /**
     * Calculate the duration of the trip in days.
     *
     * @return The number of days between start and end dates (inclusive)
     */
    public int getTripDurationDays() {
        return (int) (endDate.toEpochDay() - startDate.toEpochDay() + 1);
    }

    /**
     * Alias for getTripDurationDays used by tests.
     *
     * @return trip duration in days
     */
    public int getTripDuration() {
        return getTripDurationDays();
    }

    /**
     * Get the budget per day for the trip.
     *
     * @return The budget divided by the number of days
     */
    public double getBudgetPerDay() {
        int days = getTripDurationDays();
        return days > 0 ? budget / days : 0;
    }

    // Getters and setters

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    /**
     * Get the user-specified destination, if any.
     *
     * @return destination string or null
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Set the destination preference.
     *
     * @param destination the destination string
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    /**
     * Get the budget breakdown for the trip.
     *
     * @return The budget breakdown
     */
    public TripPlan.BudgetBreakdown getBudgetBreakdown() {
        if (budgetBreakdown == null) {
            budgetBreakdown = new TripPlan.BudgetBreakdown(budget);
            budgetBreakdown.setCurrency(currency);
        }
        return budgetBreakdown;
    }
    
    /**
     * Set the budget breakdown for the trip.
     *
     * @param budgetBreakdown The budget breakdown to set
     */
    public void setBudgetBreakdown(TripPlan.BudgetBreakdown budgetBreakdown) {
        this.budgetBreakdown = budgetBreakdown;
    }

    @Override
    public String toString() {
        return "TripPreferences{" +
                "budget=" + budget +
                ", currency='" + currency + '\'' +
                ", destination='" + destination + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", theme='" + theme + '\'' +
                ", groupSize='" + groupSize + '\'' +
                ", startingPoint='" + startingPoint + '\'' +
                '}';
    }
}