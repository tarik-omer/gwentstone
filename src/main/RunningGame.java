package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.*;

import java.util.ArrayList;

public class RunningGame {
    ArrayNode output;
    ArrayList<GameInput> games;

    ArrayList<DecksInput> playerOneDecks;

    ArrayList<DecksInput> getPlayerTwoDecks;

    CardInput[][] gameTable = new CardInput[4][5];

    Player playerOne;
    Player playerTwo;

    public RunningGame() {

    }

    public RunningGame(Input inputData, ArrayNode output) {
        // constructor that copies games and player data
        this.output = output;
        games = new ArrayList<>(inputData.getGames());
        playerOne = new Player(inputData.getPlayerOneDecks());
        playerTwo = new Player(inputData.getPlayerTwoDecks());
    }

    public void runGame() {
        for (GameInput game : games) {
            StartGameInput startGameInput = game.getStartGame();
            ArrayList<ActionsInput> actionsInput = game.getActions();
        }
    }

}

class DebuggingCommands {

}

class Commands {

}