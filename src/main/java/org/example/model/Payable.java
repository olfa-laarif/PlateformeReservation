package org.example.model;

import org.example.exception.PaiementInvalideException;

public interface Payable {
    int effectuerPaiement() throws PaiementInvalideException;;
}
