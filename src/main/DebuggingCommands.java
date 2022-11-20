package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;

import java.util.ArrayList;
import java.util.LinkedList;

public final class DebuggingCommands {
    private ArrayList<LinkedList<MinionCard>> table;
    private Player playerOne;
    private Player playerTwo;
    private ArrayNode output;

    private GameInfo gameInfo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static DebuggingCommands instance = null;

    static {
        instance = new DebuggingCommands();
    }
    private DebuggingCommands() {

    }
    private DebuggingCommands(final ArrayList<LinkedList<MinionCard>> table, final Player playerOne,
                              final Player playerTwo, final ArrayNode output,
                              final GameInfo gameInfo) {
        this.output = output;
        this.table = table;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.gameInfo = gameInfo;
    }

    public static DebuggingCommands getInstance() {
        return instance;
    }

    /**
     * Displays the cards in the hand of a given player
     * @param command   contains index of desired player hand
     */
    public void getCardsInHand(final ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("playerIdx", command.getPlayerIdx());
        if (command.getPlayerIdx() == 1) {
            objectNode.putPOJO("output",
                    Card.getCardListCopy(playerOne.getPlayerHand()));
        } else {
            objectNode.putPOJO("output",
                    Card.getCardListCopy(playerTwo.getPlayerHand()));
        }
        output.addPOJO(objectNode);
    }

    /**
     * Displays the deck of a given player.
     * @param command   contains index of desired player deck
     */
    public void getPlayerDeck(final ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("playerIdx", command.getPlayerIdx());

        if (command.getPlayerIdx() == 1) {
            objectNode.putPOJO("output",
                    Card.getCardListCopy(playerOne.getPlayerCurrentDeck()));
        } else {
            objectNode.putPOJO("output",
                    Card.getCardListCopy(playerTwo.getPlayerCurrentDeck()));
        }
        output.addPOJO(objectNode);
    }

    /**
     * Displays the hero card of a given player
     * @param command   contains index of desired player hero
     */
    public void getPlayerHero(final ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("playerIdx", command.getPlayerIdx());

        if (command.getPlayerIdx() == 1) {
            objectNode.putPOJO("output",
                    new HeroCard(playerOne.getHeroCard()));
        } else {
            objectNode.putPOJO("output",
                    new HeroCard(playerTwo.getHeroCard()));
        }
        output.addPOJO(objectNode);
    }

    /**
     * Displays player whose turn it currently is.
     * @param command   contains command name
     */
    public void getPlayerTurn(final ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("output", gameInfo.getPlayerTurn());

        output.addPOJO(objectNode);
    }

    /**
     * Displays cards that are currently placed on the table.
     * @param command   contains command name
     */
    public void getCardsOnTable(final ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());

        // copy table
        LinkedList<LinkedList<MinionCard>> tableCopy = new LinkedList<>();
        // add each row - must be deep-copied
        for (LinkedList<MinionCard> row : table) {
            LinkedList<MinionCard> rowCopy = Card.getMinionCardListCopy(row);
            tableCopy.add(rowCopy);
        }

        objectNode.putPOJO("output", tableCopy);
        output.addPOJO(objectNode);
    }

    /**
     * Displays current mana of given player
     * @param command   contains index of desired player mana
     */
    public void getPlayerMana(final ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("playerIdx", command.getPlayerIdx());


        if (command.getPlayerIdx() == 1) {
            objectNode.put("output", playerOne.getMana());
        } else if (command.getPlayerIdx() == 2) {
            objectNode.put("output", playerTwo.getMana());
        }

        output.addPOJO(objectNode);
    }

    /**
     * Displays environment-type cards in the hand of given player
     * @param command   contains index of desired player environment cards
     */
    public void getEnvironmentCardsInHand(final ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("playerIdx", command.getPlayerIdx());

        LinkedList<Card> environmentCardList = new LinkedList<>();

        if (command.getPlayerIdx() == 1) {
            for (Card card : playerOne.getPlayerHand()) {
                if (Card.correspondingRow(card) == 0) {
                    environmentCardList.add(card);
                }
            }
        } else if (command.getPlayerIdx() == 2) {
            for (Card card : playerTwo.getPlayerHand()) {
                if (Card.correspondingRow(card) == 0) {
                    environmentCardList.add(card);
                }
            }
        }

        objectNode.putPOJO("output", environmentCardList);
        output.addPOJO(objectNode);
    }

    /**
     * Displays frozen cards placed on the table
     * @param command   contains command name
     */
    public void getFrozenCardsOnTable(final ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());

        LinkedList<MinionCard> frozenCards = new LinkedList<>();

        for (LinkedList<MinionCard> row : table) {
            for (MinionCard card : row) {
                if (card.isFrozen()) {
                    frozenCards.add(new MinionCard(card));
                }
            }
        }

        objectNode.putPOJO("output", frozenCards);
        output.addPOJO(objectNode);
    }

    /**
     * Displays information of the card at the given coordinates.
     * @param command   contains desired card coordinates
     */
    public void getCardAtPosition(final ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        int x = command.getX();
        int y = command.getY();

        objectNode.put("command", command.getCommand());
        objectNode.put("x", x);
        objectNode.put("y", y);

        if (table.get(command.getX()).size() > command.getY()) {
            objectNode.putPOJO("output",
                    new MinionCard(table.get(command.getX()).get(command.getY())));
        } else {
            objectNode.put("output", "No card available at that position.");
        }
        output.addPOJO(objectNode);
    }

    public ArrayList<LinkedList<MinionCard>> getTable() {
        return table;
    }

    public void setTable(final ArrayList<LinkedList<MinionCard>> table) {
        this.table = table;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(final Player playerOne) {
        this.playerOne = playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(final Player playerTwo) {
        this.playerTwo = playerTwo;
    }

    public ArrayNode getOutput() {
        return output;
    }

    public void setOutput(final ArrayNode output) {
        this.output = output;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(final GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }
}
