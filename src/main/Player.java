package main;

import fileio.CardInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public final class Player {
    private int mana;

    private HeroCard heroCard;

    private LinkedList<Card> playerHand;

    private LinkedList<Card> playerCurrentDeck;

    Player(final ArrayList<CardInput> playerRawDeck, final int mana,
                  final CardInput heroCard, final int shuffleSeed) {
        this.playerHand = new LinkedList<>();
        this.playerCurrentDeck = Player.processDeck(playerRawDeck, shuffleSeed);
        this.mana = mana;
        this.heroCard = Player.processHero(heroCard);
    }

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public HeroCard getHeroCard() {
        return heroCard;
    }

    public void setHeroCard(final HeroCard heroCard) {
        this.heroCard = heroCard;
    }

    public LinkedList<Card> getPlayerHand() {
        return playerHand;
    }

    public void setPlayerHand(final LinkedList<Card> playerHand) {
        this.playerHand = playerHand;
    }

    public LinkedList<Card> getPlayerCurrentDeck() {
        return playerCurrentDeck;
    }

    public void setPlayerCurrentDeck(final LinkedList<Card> playerCurrentDeck) {
        this.playerCurrentDeck = playerCurrentDeck;
    }

    /**
     * Adds mana to the available mana of the player.
     * @param addedMana     amount of mana added to the player
     */
    public void addMana(final int addedMana) {
        this.setMana(this.getMana() + addedMana);
    }

    /**
     * Consumes some available mana of the player
     * @param usedMana      amount of mana consumed from the player
     */
    public void useMana(final int usedMana) {
        this.setMana(this.getMana() - usedMana);
        if (this.getMana() < 0) {
            System.out.println("Error - somewhere you let mana turn to negative values.");
        }
    }
    /**
     * Returns a list of processed (to custom classes) cards, that is shuffled with the given seed.
     * @param rawDeck       raw input card list
     * @param shuffleSeed   seed for the shuffle
     * @return              processed shuffled card list
     */
    public static LinkedList<Card> processDeck(final ArrayList<CardInput> rawDeck,
                                               final int shuffleSeed) {
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

    /**
     * Returns processed (to custom classes) hero card
     * @param rawHero   raw input hero card
     * @return          processed (to HeroCard types) hero card
     */
    public static HeroCard processHero(final CardInput rawHero) {
        // based on hero name, create corresponding hero object
        switch (rawHero.getName()) {
            case "Lord Royce" -> {
                return new LordRoyce(rawHero);
            }
            case "Empress Thorina" -> {
                return new EmpressThorina(rawHero);
            }
            case "King Mudface" -> {
                return new KingMudface(rawHero);
            }
            case "General Kocioraw" -> {
                return new GeneralKocioraw(rawHero);
            }
            default -> {
                System.out.println("Wrong hero");
                return null;
            }
        }
    }
}
