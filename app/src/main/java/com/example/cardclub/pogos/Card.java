package com.example.cardclub.pogos;

/***
 * Object class for a Card
 */
public class Card {
    /**
     * Fields for Card
     */
    private final int value;
    private final String suit;

    /**
     * Constructor for Card class
     *  @param value value of card
     * @param suit  suit of card
     */
    public Card(int value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    /**
     * Method to get the card's value
     *
     * @return int value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Method to get the card's suit
     *
     * @return String suit
     */
    public String getSuit() {
        return this.suit;
    }
}