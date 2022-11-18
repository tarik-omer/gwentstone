package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.Coordinates;

import java.util.ArrayList;
import java.util.LinkedList;

public class PlayerCommands {
    private ArrayList<LinkedList<MinionCard>> table;
    private Player playerOne;
    private Player playerTwo;
    private ArrayNode output;
    private GameInfo gameInfo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Singleton
    private static PlayerCommands instance = null;

    static {
        instance = new PlayerCommands();
    }

    private PlayerCommands() {

    }

    private PlayerCommands(ArrayList<LinkedList<MinionCard>> table, Player playerOne, Player playerTwo, ArrayNode output, GameInfo gameInfo) {
        this.table = table;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.output = output;
        this.gameInfo = gameInfo;
    }

    public static PlayerCommands getInstance() {
        return instance;
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

            switch (environmentCard.getName()) {
                case "Firestorm" -> {
                    ((FirestormEnvironmentCard) environmentCard).firestormEffect(table.get(command.getAffectedRow()));
                    if (gameInfo.getPlayerTurn() == 1) {
                        playerOne.getPlayerHand().remove(command.getHandIdx());
                        playerOne.useMana(environmentCard.getMana());
                    } else if (gameInfo.getPlayerTurn() == 2) {
                        playerTwo.getPlayerHand().remove(command.getHandIdx());
                        playerTwo.useMana(environmentCard.getMana());
                    }
                }
                case "Winterfell" -> {
                    ((WinterfellEnvironmentCard) environmentCard).winterfellEffect(table.get(command.getAffectedRow()));
                    if (gameInfo.getPlayerTurn() == 1) {
                        playerOne.getPlayerHand().remove(command.getHandIdx());
                        playerOne.useMana(environmentCard.getMana());
                    } else if (gameInfo.getPlayerTurn() == 2) {
                        playerTwo.getPlayerHand().remove(command.getHandIdx());
                        playerTwo.useMana(environmentCard.getMana());
                    }
                }
                case "Heart Hound" -> {
                    int err = ((HeartHoundEnvironmentCard) environmentCard).heartHoundEffect(table.get(command.getAffectedRow()),
                            table, gameInfo.getPlayerTurn());
                    // if err is 1, card was not moved because player's row is full; else, card was moved, no error
                    if (err == 1) {
                        objectNode.put("error", "Cannot steal enemy card since the player's row is full.");
                        output.addPOJO(objectNode);
                    } else if (gameInfo.getPlayerTurn() == 1) {
                        playerOne.getPlayerHand().remove(command.getHandIdx());
                        playerOne.useMana(environmentCard.getMana());
                    } else if (gameInfo.getPlayerTurn() == 2) {
                        playerTwo.getPlayerHand().remove(command.getHandIdx());
                        playerTwo.useMana(environmentCard.getMana());
                    }
                }
            }
        }
    }

    public void cardUsesAttack(ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        // prepare object node, in case of error
        objectNode.put("command", command.getCommand());

        objectNode.putPOJO("cardAttacker", new Coordinates(command.getCardAttacker().getX(),
                command.getCardAttacker().getY()));
        objectNode.putPOJO("cardAttacked", new Coordinates(command.getCardAttacked().getX(),
                command.getCardAttacked().getY()));

        // check to see whether attacked card belongs to the enemy
        if (gameInfo.getPlayerTurn() == 1 && (command.getCardAttacked().getX() == 2 ||
                command.getCardAttacked().getX() == 3)) {
            objectNode.put("error", "Attacked card does not belong to the enemy.");
            output.addPOJO(objectNode);
            return;
        } else if (gameInfo.getPlayerTurn() == 2 && (command.getCardAttacked().getX() == 1 ||
                command.getCardAttacked().getX() == 0)) {
            objectNode.put("error", "Attacked card does not belong to the enemy.");
            output.addPOJO(objectNode);
            return;
        }

        // attacked and attacker coordinates
        int attackerX = command.getCardAttacker().getX();
        int attackerY = command.getCardAttacker().getY();
        int attackedX = command.getCardAttacked().getX();
        int attackedY = command.getCardAttacked().getY();

        // attacked and attacker cards
        MinionCard attackerCard = table.get(attackerX).get(attackerY);
        MinionCard attackedCard = table.get(attackedX).get(attackedY);

        // check to see if attacker is able to attack / is frozen
        if (!attackerCard.isAbleToAttack()) {
            objectNode.put("error", "Attacker card has already attacked this turn.");
            output.addPOJO(objectNode);
            return;
        } else if (attackerCard.isFrozen()) {
            objectNode.put("error", "Attacker card is frozen.");
            output.addPOJO(objectNode);
            return;
        }

        // get front row of attacked player
        LinkedList<MinionCard> attackedFrontRow;
        if (gameInfo.getPlayerTurn() == 1)
            attackedFrontRow = table.get(1);
        else
            attackedFrontRow = table.get(2);

        // check to see if there is a tank on the enemy's side; tanks can only be on the first row
        boolean isTankOnRow = false;
        for (MinionCard minionCard : attackedFrontRow)
            if (minionCard.isTank()) {
                isTankOnRow = true;
                break;
            }

        // if there are tanks on the enemy side and the attacked card is not a tank, cannot attack
        if (isTankOnRow && !attackedCard.isTank()) {
            objectNode.put("error", "Attacked card is not of type 'Tank'.");
            output.addPOJO(objectNode);
            return;
        }

        // deal the damage
        attackedCard.loseHealth(attackerCard.getAttackDamage());

        // a card cannot attack twice in a turn
        attackerCard.setAbleToAttack(false);

        // if minion is dead, remove from table
        if (attackedCard.isDead()) {
            table.get(attackedX).remove(attackedY);
        }
    }

    public void cardUsedAbility(ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", command.getCommand());
        objectNode.putPOJO("cardAttacker", new Coordinates(command.getCardAttacker()));
        objectNode.putPOJO("cardAttacked", new Coordinates(command.getCardAttacked()));

        // attacked and attacker coordinates
        int attackerX = command.getCardAttacker().getX();
        int attackerY = command.getCardAttacker().getY();
        int attackedX = command.getCardAttacked().getX();
        int attackedY = command.getCardAttacked().getY();

        // attacked and attacker cards
        MinionCard attackerCard = table.get(attackerX).get(attackerY);
        MinionCard attackedCard = table.get(attackedX).get(attackedY);

        // attacker card must be able to attack and unfrozen
        if (attackerCard.isFrozen()) {
            objectNode.put("error", "Attacker card is frozen.");
            output.addPOJO(objectNode);
            return;
        } else if (!attackerCard.isAbleToAttack()) {
            objectNode.put("error", "Attacker card has already attacked this turn.");
            output.addPOJO(objectNode);
            return;
        }

        // if attacker card is Disciple, target must be allied, else target must be enemy
        if (gameInfo.getPlayerTurn() == 1 && attackerCard.getName().equals("Disciple") &&
                (attackedX == 0 || attackedX == 1)) {
            objectNode.put("error", "Attacked card does not belong to the current player.");
            output.addPOJO(objectNode);
            return;
        } else if (gameInfo.getPlayerTurn() == 2 && attackerCard.getName().equals("Disciple") &&
                (attackedX == 3 || attackedX == 2)) {
            objectNode.put("error", "Attacked card does not belong to the current player.");
            output.addPOJO(objectNode);
            return;
        } else if (gameInfo.getPlayerTurn() == 1 && (attackedX == 3 || attackedX == 2)) {
            objectNode.put("error", "Attacked card does not belong to the enemy.");
            output.addPOJO(objectNode);
            return;
        } else if (gameInfo.getPlayerTurn() == 2 && (attackedX == 0 || attackedX == 1)) {
            objectNode.put("error", "Attacked card does not belong to the enemy.");
            output.addPOJO(objectNode);
            return;
        }

        // must check for tanks only if target is an enemy (card != Disciple)
        if (!attackerCard.getName().equals("Disciple")) {
            // get front row of attacked player
            LinkedList<MinionCard> attackedFrontRow;
            if (gameInfo.getPlayerTurn() == 1)
                attackedFrontRow = table.get(1);
            else
                attackedFrontRow = table.get(2);

            // check to see if there is a tank on the enemy's side; tanks can only be on the first row
            boolean isTankOnRow = false;
            for (MinionCard minionCard : attackedFrontRow)
                if (minionCard.isTank()) {
                    isTankOnRow = true;
                    break;
                }

            // if there are tanks on the enemy side and the attacked card is not a tank, cannot attack
            if (isTankOnRow && !attackedCard.isTank()) {
                objectNode.put("error", "Attacked card is not of type 'Tank'.");
                output.addPOJO(objectNode);
                return;
            }
        }

        switch (attackerCard.getName()) {
            case "Disciple" ->
                // disciple ability: god's plan
                ((Disciple) attackerCard).godsPlanAbility(attackedCard);
            case "Miraj" ->
                // miraj ability: skyjack
                ((Miraj) attackerCard).skyjackAbility(attackedCard);
            case "The Cursed One" -> {
                // cursed one's ability: shapeshift
                ((CursedOne) attackerCard).shapeshiftAbility(attackedCard);
                // if the attacked card's attack damage is 0 and got swapped with his health, he will now be eliminated
                if (attackedCard.isDead())
                    table.get(attackedX).remove(attackedY);
            }
            default ->
                // ripper's ability
                ((Ripper) attackerCard).weakKneesAbility(attackedCard);
        }

        // a card can attack only once per round
        attackerCard.setAbleToAttack(false);
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
