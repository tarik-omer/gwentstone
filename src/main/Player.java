package main;

import fileio.CardInput;
import fileio.DecksInput;

import java.util.ArrayList;

public class Player {

    int nrCardInDeck;

    int nrDecks;

    ArrayList<ArrayList<CardInput>> decks;

    CardInput heroCard = null;

    public Player(DecksInput decksInput) {
        this.nrDecks = decksInput.getNrDecks();
        this.nrCardInDeck = decksInput.getNrCardsInDeck();
        this.decks = decksInput.getDecks();
    }
}
