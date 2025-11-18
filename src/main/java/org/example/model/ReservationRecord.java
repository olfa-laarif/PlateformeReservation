package org.example.model;

import java.time.LocalDateTime;

public class ReservationRecord {
    private int reservationId;
    private String eventName;
    private LocalDateTime eventDate;
    private String categoryName;
    private int placeId;
    private int quantity;
    private double total;
    private LocalDateTime reservationDate;

    public ReservationRecord(int reservationId, String eventName, LocalDateTime eventDate, String categoryName, int placeId, int quantity, double total, LocalDateTime reservationDate) {
        this.reservationId = reservationId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.categoryName = categoryName;
        this.placeId = placeId;
        this.quantity = quantity;
        this.total = total;
        this.reservationDate = reservationDate;
    }

    public int getReservationId() { return reservationId; }
    public String getEventName() { return eventName; }
    public LocalDateTime getEventDate() { return eventDate; }
    public String getCategoryName() { return categoryName; }
    public int getPlaceId() { return placeId; }
    public int getQuantity() { return quantity; }
    public double getTotal() { return total; }
    public LocalDateTime getReservationDate() { return reservationDate; }
}
