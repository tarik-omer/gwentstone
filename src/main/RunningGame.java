package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.*;

import java.lang.reflect.AnnotatedArrayType;
import java.util.*;

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

        GameInfo gameInfo = new GameInfo(0, 0, 0, 0, 1, 1);

        // Statistics Commands use Singleton patter - class is meant to be instantiated only once
        // get Singleton instance
        StatisticsCommands statisticsCommands = StatisticsCommands.getInstance();
        // set fields
        statisticsCommands.setGameInfo(gameInfo);
        statisticsCommands.setOutput(output);

        for (GameInput game : games) {
            // initialize empty table
            ArrayList<LinkedList<MinionCard>> table = new ArrayList<>();
            table.add(new LinkedList<>());
            table.add(new LinkedList<>());
            table.add(new LinkedList<>());
            table.add(new LinkedList<>());

            // array of actions
            ArrayList<ActionsInput> actionsInput = game.getActions();

            // instantiate players one and two objects for this specific game
            Player playerOne = new Player(playerOneDecks.getDecks().get(game.getStartGame().getPlayerOneDeckIdx()),
                    1, game.getStartGame().getPlayerOneHero(), game.getStartGame().getShuffleSeed());

            Player playerTwo = new Player(playerTwoDecks.getDecks().get(game.getStartGame().getPlayerTwoDeckIdx()),
                    1, game.getStartGame().getPlayerTwoHero(), game.getStartGame().getShuffleSeed());

            // integer to keep track of whose turn it is
            gameInfo.setPlayerTurn(game.getStartGame().getStartingPlayer());

            // counter for rounds
            gameInfo.setCurrentRound(1);

            // multiplier for mana gain of each player, each round
            gameInfo.setPlayerManaGain(1);

            // each player starts with one card in hand
            playerOne.getPlayerHand().addLast(playerOne.getPlayerCurrentDeck().removeFirst());
            playerTwo.getPlayerHand().addLast(playerTwo.getPlayerCurrentDeck().removeFirst());

            // Player and Debugging Commands use Singleton design pattern - they are meant to be instantiated only once
            // get Singleton instance
            PlayerCommands playerCommands = PlayerCommands.getInstance();
            // set fields
            playerCommands.setTable(table);
            playerCommands.setGameInfo(gameInfo);
            playerCommands.setOutput(output);
            playerCommands.setPlayerOne(playerOne);
            playerCommands.setPlayerTwo(playerTwo);
            // get Singleton instance
            DebuggingCommands debuggingCommands = DebuggingCommands.getInstance();
            // set fields
            debuggingCommands.setTable(table);
            debuggingCommands.setGameInfo(gameInfo);
            debuggingCommands.setOutput(output);
            debuggingCommands.setPlayerOne(playerOne);
            debuggingCommands.setPlayerTwo(playerTwo);

            // boolean to mark end of game
            boolean gameEnded = false;

            for (ActionsInput command : actionsInput) {
                // process each command
                // debugging commands
                switch (command.getCommand()) {
                    case "getCardsInHand":
                        debuggingCommands.getCardsInHand(command);
                        break;
                    case "getPlayerDeck":
                        debuggingCommands.getPlayerDeck(command);
                        break;
                    case "getCardsOnTable":
                        debuggingCommands.getCardsOnTable(command);
                        break;
                    case "getPlayerTurn":
                        debuggingCommands.getPlayerTurn(command);
                        break;
                    case "getPlayerHero":
                        debuggingCommands.getPlayerHero(command);
                        break;
                    case "getCardAtPosition":
                        debuggingCommands.getCardAtPosition(command);
                        break;
                    case "getPlayerMana":
                        debuggingCommands.getPlayerMana(command);
                        break;
                    case "getEnvironmentCardsInHand":
                        debuggingCommands.getEnvironmentCardsInHand(command);
                        break;
                    case "getFrozenCardsOnTable":
                        debuggingCommands.getFrozenCardsOnTable(command);
                        // statistics commands
                        break;
                    case "getTotalGamesPlayed":
                        statisticsCommands.getTotalGamesPlayed();
                        break;
                    case "getPlayerOneWins":
                        statisticsCommands.getPlayerOneWins();
                        break;
                    case "getPlayerTwoWins":
                        statisticsCommands.getPlayerTwoWins();
                        break;
                }
                // if the game ended, skip player commands
                if (gameEnded)
                    continue;
                // player commands, can be executed only during an active game
                switch (command.getCommand()) {
                    // player commands
                    case "placeCard":
                        playerCommands.placeCard(command);
                        break;
                    case "cardUsesAttack":
                        playerCommands.cardUsesAttack(command);
                        break;
                    case "cardUsesAbility":
                        playerCommands.cardUsedAbility(command);
                        break;
                    case "useAttackHero":
                        playerCommands.useAttackHero(command);
                        // player one kills enemy hero
                        if (gameInfo.getPlayerTurn() == 1 && playerTwo.getHeroCard().isDead()) {
                            // display game end message
                            ObjectMapper objectMapper = new ObjectMapper();
                            ObjectNode objectNode = objectMapper.createObjectNode();
                            objectNode.put("gameEnded", "Player one killed the enemy hero.");
                            output.addPOJO(objectNode);
                            // mark game as ended
                            gameEnded = true;
                            // mark win in win counter
                            gameInfo.setPlayerOneWins(gameInfo.getPlayerOneWins() + 1);
                            // count ended game as played game
                            gameInfo.setCurrentGame(gameInfo.getCurrentGame() + 1);
                        // player two kills enemy hero
                        } else if (gameInfo.getPlayerTurn() == 2 && playerOne.getHeroCard().isDead()) {
                            // display game end message
                            ObjectMapper objectMapper = new ObjectMapper();
                            ObjectNode objectNode = objectMapper.createObjectNode();
                            objectNode.put("gameEnded", "Player two killed the enemy hero.");
                            output.addPOJO(objectNode);
                            // mark game as ended
                            gameEnded = true;
                            // mark win in win counter
                            gameInfo.setPlayerTwoWins(gameInfo.getPlayerTwoWins() + 1);
                            // count ended game as played game
                            gameInfo.setCurrentGame(gameInfo.getCurrentGame() + 1);
                        }
                        break;
                    case "useHeroAbility":
                        playerCommands.useHeroAbility(command);
                        break;
                    case "useEnvironmentCard":
                        playerCommands.useEnvironmentCard(command);
                        break;
                    case "endPlayerTurn": // clear freeze and unable to attack
                        LinkedList<MinionCard> frontRow;
                        LinkedList<MinionCard> backRow;
                        if (gameInfo.getPlayerTurn() == 1) {
                            frontRow = table.get(2);
                            backRow = table.get(3);
                        } else {
                            frontRow = table.get(1);
                            backRow = table.get(0);
                        }
                        for (MinionCard minionCard : frontRow) {
                            minionCard.setFrozen(false);
                            minionCard.setAbleToAttack(true);
                        }
                        for (MinionCard minionCard : backRow) {
                            minionCard.setFrozen(false);
                            minionCard.setAbleToAttack(true);
                        }

                        // set turn to next player and set hero to be able to attack
                        if (gameInfo.getPlayerTurn() == 1) {
                            playerOne.getHeroCard().setAbleToAttack(true);
                            gameInfo.setPlayerTurn(2);
                        } else if (gameInfo.getPlayerTurn() == 2) {
                            playerTwo.getHeroCard().setAbleToAttack(true);
                            gameInfo.setPlayerTurn(1);
                        }

                        // advance to next round if both players played
                        if (gameInfo.getPlayerTurn() == game.getStartGame().getStartingPlayer()) {
                            gameInfo.setCurrentRound(gameInfo.getCurrentRound() + 1);

                            // increment mana gain
                            if (gameInfo.getPlayerManaGain() < 10)
                                gameInfo.setPlayerManaGain(gameInfo.getPlayerManaGain() + 1);

                            // increase mana of each player
                            playerOne.addMana(gameInfo.getPlayerManaGain());
                            playerTwo.addMana(gameInfo.getPlayerManaGain());

                            // grab card from deck if deck is not empty
                            if (playerOne.getPlayerCurrentDeck().size() > 0)
                                playerOne.getPlayerHand().addLast(playerOne.getPlayerCurrentDeck().removeFirst());
                            if (playerTwo.getPlayerCurrentDeck().size() > 0)
                                playerTwo.getPlayerHand().addLast(playerTwo.getPlayerCurrentDeck().removeFirst());
                        }

                        // TODO: Add JavaDocs
                        break;
                }
            }
        }
    }
}