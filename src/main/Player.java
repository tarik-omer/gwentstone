package main;

import fileio.CardInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Player {
    private int mana;

    private HeroCard heroCard;

    private LinkedList<Card> playerHand;

    private LinkedList<Card> playerCurrentDeck;

    public Player (ArrayList<CardInput> playerRawDeck, int mana, CardInput heroCard, int shuffleSeed) {
        this.playerHand = new LinkedList<>();
        this.playerCurrentDeck = Player.processDeck(playerRawDeck, shuffleSeed);
        this.mana = mana;
        // TODO: implement hero choice
        this.heroCard = null;
    }

    public int useManaOnCard (Card usedCard) {
        if (usedCard.getMana() < this.getMana()) {
            this.setMana(this.getMana() - usedCard.getMana());
            return 1;
        } else {
            return 0;
        }
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public HeroCard getHeroCard() {
        return heroCard;
    }

    public void setHeroCard(HeroCard heroCard) {
        this.heroCard = heroCard;
    }

    public LinkedList<Card> getPlayerHand() {
        return playerHand;
    }

    public void setPlayerHand(LinkedList<Card> playerHand) {
        this.playerHand = playerHand;
    }

    public LinkedList<Card> getPlayerCurrentDeck() {
        return playerCurrentDeck;
    }

    public void setPlayerCurrentDeck(LinkedList<Card> playerCurrentDeck) {
        this.playerCurrentDeck = playerCurrentDeck;
    }

    public static LinkedList<Card> processDeck(ArrayList<CardInput> rawDeck, int shuffleSeed) {
        LinkedList<Card> processedDeck = new LinkedList<>();

        for (CardInput rawCard : rawDeck) {
            Card processedCard;

            // if card has certain name, make it object of said class
            if (rawCard.getName().equals("Goliath") || rawCard.getName().equals("Warden") ||
                    rawCard.getName().equals("Sentinel") || rawCard.getName().equals("Berserker")) {
                processedCard = new MinionCard(rawCard);
                processedDeck.add(processedCard);
            } else if (rawCard.getName().equals("The Ripper")) {
                processedCard = new Ripper(rawCard);
                processedDeck.add(processedCard);
            } else if (rawCard.getName().equals("Miraj")) {
                processedCard = new Miraj(rawCard);
                processedDeck.add(processedCard);
            } else if (rawCard.getName().equals("The Cursed One")) {
                processedCard = new CursedOne(rawCard);
                processedDeck.add(processedCard);
            } else if (rawCard.getName().equals("Disciple")) {
                processedCard = new Disciple(rawCard);
                processedDeck.add(processedCard);
            } else if (rawCard.getName().equals("Firestorm")) {
                processedCard = new FirestormEnvironmentCard(rawCard);
                processedDeck.add(processedCard);
            } else if (rawCard.getName().equals("Winterfell")) {
                processedCard = new WinterfellEnvironmentCard(rawCard);
                processedDeck.add(processedCard);
            } else if (rawCard.getName().equals("Heart Hound")) {
                processedCard = new HeartHoundEnvironmentCard(rawCard);
                processedDeck.add(processedCard);
            } else {
                System.out.println("Wrong hero");
            }
        }

        // list is reversed, bring order of cards to normal
        Collections.reverse(processedDeck);
        // shuffle order of cards, based on seed
        Random random = new Random(shuffleSeed);
        Collections.shuffle(processedDeck, random);

        return processedDeck;
    }
}
