package com.example.cardclub;

import com.example.cardclub.pogos.Card;
import com.example.cardclub.pogos.Player;

import java.util.ArrayList;
import java.util.List;

public class CardHandler {

    public boolean generateHand(Player player, String serverMsg) {
        List<Card> cards = new ArrayList<>();
        try {

            String[] arrOfStr = serverMsg.split(";");

            if (arrOfStr.length > 5) {
                for (int i = 0; i < arrOfStr.length; i += 2) {
                    Card card = new Card(Integer.parseInt(arrOfStr[i]), arrOfStr[i + 1]);
                    cards.add(card);
                    System.out.println(card.getSuit() + " " + card.getValue());
                }
                player.setCards(cards);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cards failed to generate");
            return false;
        }
        return false;
    }


}
