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
        int playerOneWins = 0;
        int playerTwoWins = 0;
        int currentGame = 1;

        GameInfo gameInfo = new GameInfo(0, 1, 0, 0, 1, 1);

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

            DebuggingCommands debuggingCommands = new DebuggingCommands(table, playerOne, playerTwo, output, gameInfo);
            PlayerCommands playerCommands = new PlayerCommands(table, playerOne, playerTwo, output, gameInfo);

            for (ActionsInput command : actionsInput) {
                // process each command
                switch (command.getCommand()) {
                    // debugging commands
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
                        break;
                    // statistics commands
                    case "getTotalGamesPlayed":
                        break;
                    case "getPlayerOneWins":
                        break;
                    case "getPlayerTwoWins":
                        break;
                    // player commands
                    case "placeCard":
                        playerCommands.placeCard(command);
                        break;
                    case "cardUsesAttack":
                        playerCommands.cardUsesAttack(command);
                        break;
                    case "cardUsesAbility":
                        break;
                    case "useAttackHero":
                        break;
                    case "useHeroAbility":
                        break;
                    case "useEnvironmentCard":
                        playerCommands.useEnvironmentCard(command);
                        break;
                    case "endPlayerTurn":
                        // next player
                        if (gameInfo.getPlayerTurn() == 1) {
                            gameInfo.setPlayerTurn(2);
                        } else if (gameInfo.getPlayerTurn() == 2) {
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

                        // TODO: Unfreeze frozen cards for the player whose turn ended
                        // TODO: Set is able to attack (both here and in useAttackCard in PlayerCommands
                        // TODO: Convert DebuggingCommands and PlayerCommands to Singleton!!!
                        break;
                }
            }
            currentGame++;
        }
    }
}