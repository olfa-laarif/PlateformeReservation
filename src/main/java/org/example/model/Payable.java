package org.example.model;

import org.example.exception.PaiementInvalideException;

public interface Payable {
    void payer() throws PaiementInvalideException;;
}
