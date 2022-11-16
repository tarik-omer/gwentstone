package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;

import java.util.ArrayList;
import java.util.LinkedList;

public class PlayerCommands {
    private ArrayList<LinkedList<MinionCard>> table;
    private Player playerOne;
    private Player playerTwo;
    private ArrayNode output;

    private GameInfo gameInfo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PlayerCommands(ArrayList<LinkedList<MinionCard>> table, Player playerOne, Player playerTwo, ArrayNode output, GameInfo gameInfo) {
        this.table = table;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.output = output;
        this.gameInfo = gameInfo;
    }

    public void placeCard(ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("handIdx", command.getHandIdx());

        Card card;

        if (gameInfo.getPlayerTurn() == 1) {
            if (playerOne.getPlayerHand().size() <= command.getHandIdx()) {
                System.out.println("You are trying to place a card you don't have");
                return;
            }
            card = playerOne.getPlayerHand().get(command.getHandIdx());
        } else {
            if (playerTwo.getPlayerHand().size() <= command.getHandIdx()) {
                System.out.println("You are trying to place a card you don't have");
                return;
            }
            card = playerTwo.getPlayerHand().get(command.getHandIdx());
        }

        if (card == null) {
            System.out.println("No more cards in deck. Be careful.");
            return;
        }

        // environment card cannot be placed on table - error
        if (card.getName().equals("Winterfell") || card.getName().equals("Heart Hound") ||
                card.getName().equals("Firestorm")) {
            objectNode.put("error", "Cannot place environment card on table.");
            output.addPOJO(objectNode);
        // not enough mana - error
        } else if ((gameInfo.getPlayerTurn() == 1 && playerOne.getMana() < card.getMana()) ||
                gameInfo.getPlayerTurn() == 2 && playerTwo.getMana() < card.getMana()) {
            objectNode.put("error", "Not enough mana to place card on table.");
            output.addPOJO(objectNode);
        } else {
            // we consider cards that must be placed on rows 1 and 2 (front rows)
            // check if rows are full
            if (Card.correspondingRow(card) == 1) {
                if ((gameInfo.getPlayerTurn() == 1 && table.get(2).size() == 5) ||
                        (gameInfo.getPlayerTurn() == 2 && table.get(1).size() == 5)) {
                    objectNode.put("error", "Cannot place card on table since row is full.");
                    output.addPOJO(objectNode);
                    // if rows are not full, place card on player's front row
                } else if (gameInfo.getPlayerTurn() == 1) {
                    table.get(2).addLast((MinionCard) playerOne.getPlayerHand().remove(command.getHandIdx()));
                    playerOne.useMana(card.getMana());
                } else if (gameInfo.getPlayerTurn() == 2) {
                    table.get(1).addLast((MinionCard)(playerTwo.getPlayerHand().remove(command.getHandIdx())));
                    playerTwo.useMana(card.getMana());
                }
                // we consider cards that must be placed on rows 0 and 3 (back rows)
                // check if rows are full
            } else if (Card.correspondingRow(card) == -1) {
                if (gameInfo.getPlayerTurn() == 1 && table.get(3).size() == 5 ||
                        gameInfo.getPlayerTurn() == 2 && table.get(0).size() == 5) {
                    objectNode.put("error", "Cannot place card on table since row is full.");
                    output.addPOJO(objectNode);
                    // if rows are not full, place card on player's back row
                } else if (gameInfo.getPlayerTurn() == 1) {
                    table.get(3).addLast((MinionCard)playerOne.getPlayerHand().remove(command.getHandIdx()));
                    playerOne.useMana(card.getMana());
                } else if (gameInfo.getPlayerTurn() == 2) {
                    table.get(0).addLast((MinionCard)playerTwo.getPlayerHand().remove(command.getHandIdx()));
                    playerTwo.useMana(card.getMana());
                }
            }
        }
    }

    public void useEnvironmentCard(ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("handIdx", command.getHandIdx());
        objectNode.put("affectedRow", command.getAffectedRow());

        // check whether card is environment card, player has enough mana and affected row is enemy's row
        if (gameInfo.getPlayerTurn() == 1 && Card.correspondingRow(playerOne.getPlayerHand().get(command.getHandIdx())) != 0) {
            objectNode.put("error", "Chosen card is not of type environment.");
            output.addPOJO(objectNode);
        } else if (gameInfo.getPlayerTurn() == 2 && Card.correspondingRow(playerTwo.getPlayerHand().get(command.getHandIdx())) != 0) {
            objectNode.put("error", "Chosen card is not of type environment.");
            output.addPOJO(objectNode);
        } else if (gameInfo.getPlayerTurn() == 1 && playerOne.getMana() < playerOne.getPlayerHand().get(command.getHandIdx()).getMana()) {
            objectNode.put("error", "Not enough mana to use environment card.");
            output.addPOJO(objectNode);
        } else if (gameInfo.getPlayerTurn() == 2 && playerTwo.getMana() < playerTwo.getPlayerHand().get(command.getHandIdx()).getMana()) {
            objectNode.put("error", "Not enough mana to use environment card.");
            output.addPOJO(objectNode);
        } else if (gameInfo.getPlayerTurn() == 1 && (command.getAffectedRow() == 2 || command.getAffectedRow() == 3)) {
            objectNode.put("error", "Chosen row does not belong to the enemy.");
            output.addPOJO(objectNode);
        } else if (gameInfo.getPlayerTurn() == 2 && (command.getAffectedRow() == 0 || command.getAffectedRow() == 1)) {
            objectNode.put("error", "Chosen row does not belong to the enemy.");
            output.addPOJO(objectNode);
        } else {
            Card environmentCard;
            if (gameInfo.getPlayerTurn() == 1)
                environmentCard = playerOne.getPlayerHand().get(command.getHandIdx());
            else
                environmentCard = playerTwo.getPlayerHand().get(command.getHandIdx());

            if (environmentCard.getName().equals("Firestorm")) {
                ((FirestormEnvironmentCard)environmentCard).firestormEffect(table.get(command.getAffectedRow()));
                if (gameInfo.getPlayerTurn() == 1) {
                    playerOne.getPlayerHand().remove(command.getHandIdx());
                    playerOne.useMana(environmentCard.getMana());
                } else if (gameInfo.getPlayerTurn() == 2) {
                    playerTwo.getPlayerHand().remove(command.getHandIdx());
                    playerTwo.useMana(environmentCard.getMana());
                }
            } else if (environmentCard.getName().equals("Winterfell")) {
                ((WinterfellEnvironmentCard)environmentCard).winterfellEffect(table.get(command.getAffectedRow()));
                if (gameInfo.getPlayerTurn() == 1) {
                    playerOne.getPlayerHand().remove(command.getHandIdx());
                    playerOne.useMana(environmentCard.getMana());
                } else if (gameInfo.getPlayerTurn() == 2) {
                    playerTwo.getPlayerHand().remove(command.getHandIdx());
                    playerTwo.useMana(environmentCard.getMana());
                }
//            } else if (environmentCard.getName().equals("Heart Hound")) {
//                int err = ((HeartHoundEnvironmentCard)environmentCard).heartHoundEffect(table.get(command.getAffectedRow()),
//                        table, gameInfo.getPlayerTurn());
//                // if err is 1, card was not moved because player's row is full; else, card was moved, no error
//                if (err == 1) {
//                    objectNode.put("error", "Cannot steal enemy card since the player's row is full.");
//                    output.addPOJO(objectNode);
//                } else if (gameInfo.getPlayerTurn() == 1) {
//                    playerOne.getPlayerHand().remove(command.getHandIdx());
//                    playerOne.useMana(environmentCard.getMana());
//                } else if (gameInfo.getPlayerTurn() == 2) {
//                    playerTwo.getPlayerHand().remove(command.getHandIdx());
//                    playerTwo.useMana(environmentCard.getMana());
//                }
            }
        }
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
