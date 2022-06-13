package com.example.cardclub.pogos;

import java.util.List;

public class Player {

    private final String userName;
    private String playerName;
    private List<Card> playerHand;

    /**
     * Constructor for Human class
     *
     * @param Username   Login name of user
     * @param PlayerName public name of user
     */
    public Player(String Username, String PlayerName) {
        this.playerName = PlayerName;
        this.userName = Username;
    }

    /**
     * Method to get the player's username
     *
     * @return String name
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Method to get the player's player name
     *
     * @return String name
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * Method to set a player's cards
     *
     * @param newCards new set of cards
     */
    public void setCards(List<Card> newCards) {
        this.playerHand = newCards;
    }

    /**
     * Method to get a player's cards
     *
     * @return list of player's cards
     */
    public List<Card> getCards() {
        return this.playerHand;
    }

    /**
     * Method to set the public playerName of user
     *
     * @param name name to set
     */
    public void setPlayerName(String name) {
        playerName = name;
    }


}
