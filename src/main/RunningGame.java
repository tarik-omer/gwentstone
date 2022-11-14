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

            Player playerOne = new Player(playerOneDecks.getDecks().get(game.getStartGame().getPlayerOneDeckIdx()),
                    0, game.getStartGame().getPlayerOneHero(), game.getStartGame().getShuffleSeed());

            Player playerTwo = new Player(playerTwoDecks.getDecks().get(game.getStartGame().getPlayerTwoDeckIdx()),
                    0, game.getStartGame().getPlayerTwoHero(), game.getStartGame().getShuffleSeed());

            output.addPOJO(playerOne.getPlayerCurrentDeck());

            for (ActionsInput command : actionsInput) {
                // process each command
            }
        }
    }
}