package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;

import java.util.ArrayList;
import java.util.LinkedList;

public class DebuggingCommands {
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
    private DebuggingCommands(ArrayList<LinkedList<MinionCard>> table, Player playerOne, Player playerTwo, ArrayNode output,
                      GameInfo gameInfo) {
        this.output = output;
        this.table = table;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.gameInfo = gameInfo;
    }

    public static DebuggingCommands getInstance() {
        return instance;
    }

    public void getCardsInHand(ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("playerIdx", command.getPlayerIdx());
        if (command.getPlayerIdx() == 1)
            objectNode.putPOJO("output", Card.getCardListCopy(playerOne.getPlayerHand()));
        else
            objectNode.putPOJO("output", Card.getCardListCopy(playerTwo.getPlayerHand()));

        output.addPOJO(objectNode);
    }

    public void getPlayerDeck(ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("playerIdx", command.getPlayerIdx());

        if (command.getPlayerIdx() == 1)
            objectNode.putPOJO("output", Card.getCardListCopy(playerOne.getPlayerCurrentDeck()));
        else
            objectNode.putPOJO("output", Card.getCardListCopy(playerTwo.getPlayerCurrentDeck()));

        output.addPOJO(objectNode);
    }

    public void getPlayerHero(ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("playerIdx", command.getPlayerIdx());

        if (command.getPlayerIdx() == 1)
            objectNode.putPOJO("output", new HeroCard(playerOne.getHeroCard()));
        else
            objectNode.putPOJO("output", new HeroCard(playerTwo.getHeroCard()));

        output.addPOJO(objectNode);
    }

    public void getPlayerTurn(ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("output", gameInfo.getPlayerTurn());

        output.addPOJO(objectNode);
    }

    public void getCardsOnTable(ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());

        // copy table
        LinkedList<LinkedList<MinionCard>> tableCopy = new LinkedList<>();
        // add each row
        for (LinkedList<MinionCard> row : table) {
            LinkedList<MinionCard> rowCopy = Card.getMinionCardListCopy(row);
            tableCopy.add(rowCopy);
        }

        objectNode.putPOJO("output", tableCopy);
        output.addPOJO(objectNode);
    }

    public void getPlayerMana(ActionsInput command) {
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

    public void getEnvironmentCardsInHand(ActionsInput command) {
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

    public void getFrozenCardsOnTable(ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());

        LinkedList<MinionCard> frozenCards = new LinkedList<>();

        for (LinkedList<MinionCard> row : table) {
            for (MinionCard card : row) {
                if (card.isFrozen())
                    frozenCards.add(new MinionCard(card));
            }
        }

        objectNode.putPOJO("output", frozenCards);
        output.addPOJO(objectNode);
    }

    public void getCardAtPosition(ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("x", command.getX());
        objectNode.put("y", command.getY());

        if (table.get(command.getX()).size() > command.getY())
            objectNode.putPOJO("output", new MinionCard(table.get(command.getX()).get(command.getY())));
        else
            objectNode.put("output", "No card available at that position.");

        output.addPOJO(objectNode);
    }

    public ArrayList<LinkedList<MinionCard>> getTable() {
        return table;
    }

    public void setTable(ArrayList<LinkedList<MinionCard>> table) {
        this.table = table;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
    }

    public ArrayNode getOutput() {
        return output;
    }

    public void setOutput(ArrayNode output) {
        this.output = output;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }
}
