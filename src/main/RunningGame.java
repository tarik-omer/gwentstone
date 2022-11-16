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
            int playerTurn = game.getStartGame().getStartingPlayer();

            // counter for rounds
            int currentRound = 1;

            // multiplier for mana gain of each player, each round
            int playerManaGain = 1;

            // each player starts with one card in hand
            playerOne.getPlayerHand().addLast(playerOne.getPlayerCurrentDeck().removeFirst());
            playerTwo.getPlayerHand().addLast(playerTwo.getPlayerCurrentDeck().removeFirst());

            for (ActionsInput command : actionsInput) {
                // process each command
                switch (command.getCommand()) {
                    // debugging commands
                    case "getCardsInHand":
                        GetPlayerHand getPlayerHand = new GetPlayerHand(command.getPlayerIdx(),
                                playerOne, playerTwo);
                        output.addPOJO(getPlayerHand);
                        break;
                    case "getPlayerDeck":
                        GetPlayerDeck getPlayerDeck = new GetPlayerDeck(command.getPlayerIdx(),
                                playerOne, playerTwo);
                        output.addPOJO(getPlayerDeck);
                        break;
                    case "getCardsOnTable":
                        GetTableCards getTableCards = new GetTableCards(table);
                        output.addPOJO(getTableCards);
                        break;
                    case "getPlayerTurn":
                        GetPlayerTurn getPlayerTurn = new GetPlayerTurn(playerTurn);
                        output.addPOJO(getPlayerTurn);
                        break;
                    case "getPlayerHero":
                        GetPlayerHero getPlayerHero = new GetPlayerHero(command.getPlayerIdx(), playerOne, playerTwo);
                        output.addPOJO(getPlayerHero);
                        break;
                    case "getCardAtPosition":
                        GetCardPosition getCardPosition = new GetCardPosition(command.getX(), command.getY(), table);
                        if (getCardPosition.getOutput() == null) {
                            // case: no card at given coordinates
                            ObjectMapper objectMapper = new ObjectMapper();
                            ObjectNode objectNode = objectMapper.createObjectNode();
                            objectNode.put("command", "getCardPosition");
                            objectNode.put("x", command.getX());
                            objectNode.put("y", command.getY());
                            objectNode.put("output", "No card at that position.");
                            output.addPOJO(objectNode);
                        } else {
                            // case: card found
                            output.addPOJO(getCardPosition);
                        }
                        break;
                    case "getPlayerMana":
                        GetPlayerMana getPlayerMana = new GetPlayerMana(playerOne, playerTwo, command.getPlayerIdx());
                        output.addPOJO(getPlayerMana);
                        break;
                    case "getEnvironmentCardsInHand":
                        GetEnvironmentCardsInHand getEnvironmentCardsInHand = new
                                GetEnvironmentCardsInHand(command.getPlayerIdx(), playerOne, playerTwo);
                        output.addPOJO(getEnvironmentCardsInHand);
                        break;
                    case "getFrozenCardsOnTable":
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
                        PlaceCard placeCard = new PlaceCard(command.getHandIdx(),
                                table, playerTurn, playerOne, playerTwo);
                        // if error is not null (something went wrong),
                        // card was not removed from hand, not added to table
                        if (placeCard.getError() != null) {
                            output.addPOJO(placeCard);
                        }
                        break;
                    case "cardUsesAttack":
                        break;
                    case "cardUsesAbility":
                        break;
                    case "useAttackHero":
                        break;
                    case "useHeroAbility":
                        break;
                    case "useEnvironmentCard":
                        UseEnvironmentCard useEnvironmentCard = new UseEnvironmentCard(command.getHandIdx(),
                                command.getAffectedRow(), playerOne, playerTwo, table, playerTurn);
                        if (useEnvironmentCard.getError() != null) {
                            output.addPOJO(useEnvironmentCard);
                        }
                        break;
                    case "endPlayerTurn":
                        // next player
                        if (playerTurn == 1) {
                            playerTurn = 2;
                        } else if (playerTurn == 2) {
                            playerTurn = 1;
                        }
                        // advance to next round if both players played
                        if (playerTurn == game.getStartGame().getStartingPlayer()) {
                            currentRound++;

                            // increment mana gain
                            if (playerManaGain < 10)
                                playerManaGain++;

                            // increase mana of each player
                            playerOne.addMana(playerManaGain);
                            playerTwo.addMana(playerManaGain);

                            // grab card from deck if deck is not empty
                            if (playerOne.getPlayerCurrentDeck().size() > 0)
                                playerOne.getPlayerHand().addLast(playerOne.getPlayerCurrentDeck().removeFirst());
                            if (playerTwo.getPlayerCurrentDeck().size() > 0)
                                playerTwo.getPlayerHand().addLast(playerTwo.getPlayerCurrentDeck().removeFirst());
                        }

                        // TODO: Unfreeze frozen cards for the player whose turn ended
                        // TODO: Implement death by Firestorm
                        // TODO: Fix useEnvironmentCard and getCardAtPosition
                        break;
                }
            }
            currentGame++;
        }
    }
}