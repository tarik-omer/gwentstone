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
        this.heroCard = Player.processHero(heroCard);
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

    public void addMana (int addedMana) {
        this.setMana(this.getMana() + addedMana);
    }

    public void useMana (int usedMana) {
        this.setMana(this.getMana() - usedMana);
        if (this.getMana() < 0)
            System.out.println("Error - somewhere you let mana turn to negative values.");
    }

    public static LinkedList<Card> processDeck(ArrayList<CardInput> rawDeck, int shuffleSeed) {
        LinkedList<Card> processedDeck = new LinkedList<>();

        for (CardInput rawCard : rawDeck) {
            Card processedCard;

            // if card has certain name, make it object of said type
            switch (rawCard.getName()) {
                case "Goliath", "Warden", "Sentinel", "Berserker" -> {
                    processedCard = new MinionCard(rawCard);
                    processedDeck.add(processedCard);
                }
                case "The Ripper" -> {
                    processedCard = new Ripper(rawCard);
                    processedDeck.add(processedCard);
                }
                case "Miraj" -> {
                    processedCard = new Miraj(rawCard);
                    processedDeck.add(processedCard);
                }
                case "The Cursed One" -> {
                    processedCard = new CursedOne(rawCard);
                    processedDeck.add(processedCard);
                }
                case "Disciple" -> {
                    processedCard = new Disciple(rawCard);
                    processedDeck.add(processedCard);
                }
                case "Firestorm" -> {
                    processedCard = new FirestormEnvironmentCard(rawCard);
                    processedDeck.add(processedCard);
                }
                case "Winterfell" -> {
                    processedCard = new WinterfellEnvironmentCard(rawCard);
                    processedDeck.add(processedCard);
                }
                case "Heart Hound" -> {
                    processedCard = new HeartHoundEnvironmentCard(rawCard);
                    processedDeck.add(processedCard);
                }
                default -> System.out.println("Wrong card.");
            }
        }

        // shuffle order of cards, based on seed
        Random random = new Random(shuffleSeed);
        Collections.shuffle(processedDeck, random);

        return processedDeck;
    }

    public static HeroCard processHero(CardInput rawHero) {
        HeroCard heroCard;

        // based on hero name, create corresponding hero object
        switch (rawHero.getName()) {
            case "Lord Royce" -> heroCard = new LordRoyce(rawHero);
            case "Empress Thorina" -> heroCard = new EmpressThorina(rawHero);
            case "King Mudface" -> heroCard = new KingMudface(rawHero);
            case "General Kocioraw" -> heroCard = new GeneralKocioraw(rawHero);
            default -> {
                System.out.println("Wrong hero");
                heroCard = null;
            }
        }
        return heroCard;
    }
}
