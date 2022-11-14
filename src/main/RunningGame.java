package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.*;

import java.lang.reflect.AnnotatedArrayType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class RunningGame {
    ArrayNode output;
    ArrayList<GameInput> games;

    DecksInput playerOneDecks;

    DecksInput playerTwoDecks;

    public RunningGame() {

    }

    public RunningGame(Input inputData, ArrayNode output) {
        // link to output
        this.output = output;

        // list of games, with its corresponding information
        this.games = new ArrayList<>(inputData.getGames());

        // objects that hold decks for player 1 and 2 (and relevant information)
        this.playerOneDecks = inputData.getPlayerOneDecks();
        this.playerTwoDecks = inputData.getPlayerTwoDecks();
    }

    public void runGame() {

        for (GameInput game : games) {
            // initialize empty table
            ArrayList<LinkedList<MinionCard>> table = new ArrayList<>(4);

            ArrayList<ActionsInput> actionsInput = game.getActions();

            // TODO: place this into a player class, instantiate 2 player obj
//            ArrayList<Card> playerOneHand = new ArrayList<>();
//            ArrayList<Card> playerTwoHand = new ArrayList<>();
//
//            // get current game decks for each player
//            ArrayList<Card> playerOneDeck = RunningGame.processDeck
//                    (playerOneDecks.getDecks().get(game.getStartGame().getPlayerOneDeckIdx()),
//                            game.getStartGame().getShuffleSeed());
//            ArrayList<Card> playerTwoDeck = RunningGame.processDeck
//                    (playerTwoDecks.getDecks().get(game.getStartGame().getPlayerOneDeckIdx()),
//                            game.getStartGame().getShuffleSeed());
//
//            int playerOneMana = 0;
//            int playerTwoMana = 0;

            for (ActionsInput command : actionsInput) {
                // process each command
            }
        }
    }

    public static ArrayList<Card> processDeck(ArrayList<CardInput> rawDeck, int shuffleSeed) {
        ArrayList<Card> processedDeck = new ArrayList<>();

        for (CardInput rawCard : rawDeck) {
            Card processedCard;

            // if card has certain name, make it object of said class
            if (rawCard.getName().equals("Goliath") || rawCard.getName().equals("Warden") ||
                    rawCard.getName().equals("Sentinel") || rawCard.getName().equals("Berserker")) {
                processedCard = new MinionCard(rawCard);
                processedDeck.add(processedCard);
            } else if (rawCard.getName().equals("The Ripper")) {
                processedCard = new Ripper(rawCard);
            } else if (rawCard.getName().equals("Miraj")) {
                processedCard = new Miraj(rawCard);
            } else if (rawCard.getName().equals("The Cursed One")) {
                processedCard = new CursedOne(rawCard);
            } else if (rawCard.getName().equals("Disciple")) {
                processedCard = new Disciple(rawCard);
            } else if (rawCard.getName().equals("Firestorm")) {
                processedCard = new FirestormEnvironmentCard(rawCard);
            } else if (rawCard.getName().equals("Winterfell")) {
                processedCard = new WinterfellEnvironmentCard(rawCard);
            } else if (rawCard.getName().equals("Heart Hound")) {
                processedCard = new HeartHoundEnvironmentCard(rawCard);
            } else {
                System.out.println("Wrong hero, bro");
            }
        }

        // shuffle the chosen deck
        Random random = new Random(shuffleSeed);
        Collections.shuffle(processedDeck, random);

        return processedDeck;
    }

}

class DebuggingCommands {
    public static void getCardsInHand() {

    }

    public static void getPlayerDeck() {

    }

    public static void getCardsOnTable() {

    }

    public static void getPlayerTurn() {

    }

    public static void getPlayerHero() {

    }

    public static void getCardAtPosition() {

    }

    public static void getPlayerMana() {

    }

    public static void getEnvironmentCardsInHand() {

    }

    public static void getFrozenCardsOnTable() {

    }
}

class Commands {

}