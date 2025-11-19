package org.example.model;

import java.time.LocalDateTime;

public class ReservationSummary {
    private int reservationId;
    private String eventName;
    private LocalDateTime eventDate;
    private int quantity;
    private double total;
    private LocalDateTime reservationDate;

    public ReservationSummary(int reservationId, String eventName, LocalDateTime eventDate, int quantity, double total, LocalDateTime reservationDate) {
        this.reservationId = reservationId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.quantity = quantity;
        this.total = total;
        this.reservationDate = reservationDate;
    }

    public int getReservationId() { return reservationId; }
    public String getEventName() { return eventName; }
    public LocalDateTime getEventDate() { return eventDate; }
    public int getQuantity() { return quantity; }
    public double getTotal() { return total; }
    public LocalDateTime getReservationDate() { return reservationDate; }
}
