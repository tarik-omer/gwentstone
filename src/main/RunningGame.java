package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.GameInput;
import fileio.DecksInput;
import fileio.Input;
import java.util.ArrayList;
import java.util.LinkedList;
public final class RunningGame {
    private ArrayNode output;
    private ArrayList<GameInput> games;

    private DecksInput playerOneDecks;

    private DecksInput playerTwoDecks;

    public RunningGame() {

    }

    public RunningGame(final Input inputData, final ArrayNode output) {
        // link to output
        this.output = output;

        // list of games, with its corresponding information
        this.games = new ArrayList<>(inputData.getGames());

        // objects that hold decks for player 1 and 2 (and relevant information)
        this.playerOneDecks = inputData.getPlayerOneDecks();
        this.playerTwoDecks = inputData.getPlayerTwoDecks();
    }

    /**
     * Method that runs the entire game.
     * Iterates through input, considering player one and two decks,
     * playing through all given games.
     */
    public void runGame() {
        GameInfo gameInfo = new GameInfo(0, 0, 0, 0, 1, 1);
        // Statistics Commands use Singleton patter - class is meant to be instantiated only once
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
            int playerOneDeckIdx = game.getStartGame().getPlayerOneDeckIdx();
            Player playerOne = new Player(playerOneDecks.getDecks().get(playerOneDeckIdx),
                    1, game.getStartGame().getPlayerOneHero(),
                    game.getStartGame().getShuffleSeed());
            int playerTwoDeckIdx = game.getStartGame().getPlayerTwoDeckIdx();
            Player playerTwo = new Player(playerTwoDecks.getDecks().get(playerTwoDeckIdx),
                    1, game.getStartGame().getPlayerTwoHero(),
                    game.getStartGame().getShuffleSeed());
            // integer to keep track of whose turn it is
            gameInfo.setPlayerTurn(game.getStartGame().getStartingPlayer());
            // counter for rounds
            gameInfo.setCurrentRound(1);
            // multiplier for mana gain of each player, each round
            gameInfo.setPlayerManaGain(1);
            // each player starts with one card in hand
            playerOne.getPlayerHand().addLast(playerOne.getPlayerCurrentDeck().removeFirst());
            playerTwo.getPlayerHand().addLast(playerTwo.getPlayerCurrentDeck().removeFirst());
            // Player and Debugging Commands use Singleton design pattern
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
            // process each command
            for (ActionsInput command : actionsInput) {
                // debugging commands
                switch (command.getCommand()) {
                    case "getCardsInHand" -> debuggingCommands.getCardsInHand(command);
                    case "getPlayerDeck" -> debuggingCommands.getPlayerDeck(command);
                    case "getCardsOnTable" -> debuggingCommands.getCardsOnTable(command);
                    case "getPlayerTurn" -> debuggingCommands.getPlayerTurn(command);
                    case "getPlayerHero" -> debuggingCommands.getPlayerHero(command);
                    case "getCardAtPosition" -> debuggingCommands.getCardAtPosition(command);
                    case "getPlayerMana" -> debuggingCommands.getPlayerMana(command);
                    case "getEnvironmentCardsInHand" ->
                            debuggingCommands.getEnvironmentCardsInHand(command);
                    case "getFrozenCardsOnTable" ->
                            debuggingCommands.getFrozenCardsOnTable(command);
                    // statistics commands
                    case "getTotalGamesPlayed" -> statisticsCommands.getTotalGamesPlayed();
                    case "getPlayerOneWins" -> statisticsCommands.getPlayerOneWins();
                    case "getPlayerTwoWins" -> statisticsCommands.getPlayerTwoWins();
                    // player commands
                    case "placeCard" -> playerCommands.placeCard(command);
                    case "cardUsesAttack" -> playerCommands.cardUsesAttack(command);
                    case "cardUsesAbility" -> playerCommands.cardUsedAbility(command);
                    case "useAttackHero" -> {
                        playerCommands.useAttackHero(command);
                        // player one kills enemy hero
                        if (gameInfo.getPlayerTurn() == 1 && playerTwo.getHeroCard().isDead()) {
                            // display game end message
                            ObjectMapper objectMapper = new ObjectMapper();
                            ObjectNode objectNode = objectMapper.createObjectNode();
                            objectNode.put("gameEnded",
                                    "Player one killed the enemy hero.");
                            output.addPOJO(objectNode);
                            // mark win in win counter
                            gameInfo.setPlayerOneWins(gameInfo.getPlayerOneWins() + 1);
                            // count ended game as played game
                            gameInfo.setCurrentGame(gameInfo.getCurrentGame() + 1);
                            // player two kills enemy hero
                        } else if (gameInfo.getPlayerTurn() == 2
                                && playerOne.getHeroCard().isDead()) {
                            // display game end message
                            ObjectMapper objectMapper = new ObjectMapper();
                            ObjectNode objectNode = objectMapper.createObjectNode();
                            objectNode.put("gameEnded",
                                    "Player two killed the enemy hero.");
                            output.addPOJO(objectNode);
                            // mark win in win counter
                            gameInfo.setPlayerTwoWins(gameInfo.getPlayerTwoWins() + 1);
                            // count ended game as played game
                            gameInfo.setCurrentGame(gameInfo.getCurrentGame() + 1);
                        }
                    }
                    case "useHeroAbility" -> playerCommands.useHeroAbility(command);
                    case "useEnvironmentCard" -> playerCommands.useEnvironmentCard(command);
                    case "endPlayerTurn" -> {
                        // clear freeze and unable to attack
                        LinkedList<MinionCard> frontRow;
                        LinkedList<MinionCard> backRow;
                        if (gameInfo.getPlayerTurn() == 1) {
                            frontRow = table.get(Global.PLAYER_ONE_FRONT_ROW);
                            backRow = table.get(Global.PLAYER_ONE_BACK_ROW);
                        } else {
                            frontRow = table.get(Global.PLAYER_TWO_FRONT_ROW);
                            backRow = table.get(Global.PLAYER_TWO_BACK_ROW);
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
                            if (gameInfo.getPlayerManaGain() < Global.MAX_MANA_GAIN) {
                                gameInfo.setPlayerManaGain(gameInfo.getPlayerManaGain() + 1);
                            }
                            // increase mana of each player
                            playerOne.addMana(gameInfo.getPlayerManaGain());
                            playerTwo.addMana(gameInfo.getPlayerManaGain());

                            // grab card from deck if deck is not empty
                            if (playerOne.getPlayerCurrentDeck().size() > 0) {
                                Card grabbedCard = playerOne.getPlayerCurrentDeck().removeFirst();
                                playerOne.getPlayerHand().addLast(grabbedCard);
                            }
                            if (playerTwo.getPlayerCurrentDeck().size() > 0) {
                                Card grabbedCard = playerTwo.getPlayerCurrentDeck().removeFirst();
                                playerTwo.getPlayerHand().addLast(grabbedCard);
                            }
                        }
                    }
                    default ->
                            throw new IllegalStateException("Unexpected value: "
                                    + command.getCommand());
                }
            }
        }
    }
}
