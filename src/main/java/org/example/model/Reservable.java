package org.example.model;


import org.example.exception.PlacesInsuffisantesException;

public interface Reservable {
    void reserver(int nombreTickets) throws PlacesInsuffisantesException;

}
