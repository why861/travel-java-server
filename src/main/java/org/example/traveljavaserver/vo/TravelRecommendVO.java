package org.example.traveljavaserver.vo;

import java.util.List;

import lombok.Data;

@Data
public class TravelRecommendVO {
    private Boolean success;
    private String city;
    private Integer days;
    private Double totalBudget;
    private List<DailyItinerary> dailyItinerary;
    private BudgetBreakdown budgetBreakdown;
    private List<String> tips;
    private List<String> warnings;
    private String error;
    private String rawResponse;

    @Data
    public static class DailyItinerary {
        private Integer day;
        private String date;
        private Timeslot morning;
        private Timeslot afternoon;
        private Timeslot evening;
    }

    @Data
    public static class Timeslot {
        private String spot;
        private String duration;
        private String transportation;
        private String description;
        private String ticket;
    }

    @Data
    public static class BudgetBreakdown {
        private Double accommodation;
        private Double food;
        private Double transportation;
        private Double tickets;
        private Double other;

    }

}
