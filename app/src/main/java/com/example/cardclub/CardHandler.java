package com.example.cardclub;

import com.example.cardclub.pogos.Card;

import java.util.ArrayList;
import java.util.List;

public class CardHandler {

    public List<Card> generateHand(String serverMsg) {
        try {
            List<Card> cards = new ArrayList<>();

            String[] arrOfStr = serverMsg.split(";");

            for (int i = 0; i > arrOfStr.length; i += 2) {
                Card card = new Card(Integer.parseInt(arrOfStr[i]), arrOfStr[i + 1]);
                cards.add(card);
            }

            return cards;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
