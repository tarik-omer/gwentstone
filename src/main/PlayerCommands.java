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
                }
                case "Winterfell" -> {
                    ((WinterfellEnvironmentCard) environmentCard).winterfellEffect(table.get(command.getAffectedRow()));
                }
                case "Heart Hound" -> {
                    int err = ((HeartHoundEnvironmentCard) environmentCard).heartHoundEffect(table.get(command.getAffectedRow()),
                            table, gameInfo.getPlayerTurn());
                    // if err is 1, card was not moved because player's row is full; else, card was moved, no error
                    if (err == 1) {
                        objectNode.put("error", "Cannot steal enemy card since the player's row is full.");
                        output.addPOJO(objectNode);
                        return;
                    }
                }
            }
            if (gameInfo.getPlayerTurn() == 1) {
                playerOne.getPlayerHand().remove(command.getHandIdx());
                playerOne.useMana(environmentCard.getMana());
            } else if (gameInfo.getPlayerTurn() == 2) {
                playerTwo.getPlayerHand().remove(command.getHandIdx());
                playerTwo.useMana(environmentCard.getMana());
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

        // check to see if there are any tanks on front row
        boolean isTankOnRow = PlayerCommands.isTankOnRow(gameInfo, table);

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
        SpecialMinionCard attackerCard = (SpecialMinionCard) table.get(attackerX).get(attackerY);
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
        } else if (gameInfo.getPlayerTurn() == 1 && !attackerCard.getName().equals("Disciple") &&
                (attackedX == 3 || attackedX == 2)) {
            objectNode.put("error", "Attacked card does not belong to the enemy.");
            output.addPOJO(objectNode);
            return;
        } else if (gameInfo.getPlayerTurn() == 2 && !attackerCard.getName().equals("Disciple") &&
                (attackedX == 0 || attackedX == 1)) {
            objectNode.put("error", "Attacked card does not belong to the enemy.");
            output.addPOJO(objectNode);
            return;
        }

        // must check for tanks only if target is an enemy (card != Disciple)
        if (!attackerCard.getName().equals("Disciple")) {

            boolean isTankOnRow = PlayerCommands.isTankOnRow(gameInfo, table);

            // if there are tanks on the enemy side and the attacked card is not a tank, cannot attack
            if (isTankOnRow && !attackedCard.isTank()) {
                objectNode.put("error", "Attacked card is not of type 'Tank'.");
                output.addPOJO(objectNode);
                return;
            }
        }

        // apply ability of attacker card on attacked card
        attackerCard.specialAbility(attackedCard);

        // check to see if minion died (cursed one case)
        if (attackedCard.isDead())
            table.get(attackedX).remove(attackedY);

        // a card can attack only once per round
        attackerCard.setAbleToAttack(false);
    }

    public void useAttackHero(ActionsInput command) {
        // prepare output in case of error
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", command.getCommand());
        objectNode.putPOJO("cardAttacker", new Coordinates(command.getCardAttacker()));

        // attacker coordinate
        int attackerX = command.getCardAttacker().getX();
        int attackerY = command.getCardAttacker().getY();

        // attacker card
        MinionCard attackerCard = table.get(attackerX).get(attackerY);

        // attacker must be able to attack and unfrozen
        if (attackerCard.isFrozen()) {
            objectNode.put("error", "Attacker card is frozen.");
            output.addPOJO(objectNode);
            return;
        } else if (!attackerCard.isAbleToAttack()) {
            objectNode.put("error", "Attacker card has already attacked this turn.");
            output.addPOJO(objectNode);
            return;
        }

        // check to see if there are any tanks on front row of attacked player
        boolean isTankOnRow = PlayerCommands.isTankOnRow(gameInfo, table);

        // player must attack tanks first
        if (isTankOnRow) {
            objectNode.put("error", "Attacked card is not of type 'Tank'.");
            output.addPOJO(objectNode);
            return;
        }

        // attacked hero card
        HeroCard heroCard;
        if (gameInfo.getPlayerTurn() == 1) {
            heroCard = playerTwo.getHeroCard();
        } else {
            heroCard = playerOne.getHeroCard();
        }

        // deal the damage
        heroCard.loseHealth(attackerCard.getAttackDamage());

        // mark attacker as unable to attack
        attackerCard.setAbleToAttack(false);

        // we check to see if hero card died in main, so we can control the for loop
    }

    public void useHeroAbility(ActionsInput command) {
        // prepare output in case of error
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", command.getCommand());
        objectNode.put("affectedRow", command.getAffectedRow());

        // attacker hero card
        HeroCard heroCard;
        if (gameInfo.getPlayerTurn() == 1)
            heroCard = playerOne.getHeroCard();
        else
            heroCard = playerTwo.getHeroCard();

        // player must have enough mana to use hero ability
        if (gameInfo.getPlayerTurn() == 1 && playerOne.getMana() < heroCard.getMana()) {
            objectNode.put("error", "Not enough mana to use hero's ability.");
            output.addPOJO(objectNode);
            return;
        } else if (gameInfo.getPlayerTurn() == 2 && playerTwo.getMana() < heroCard.getMana()) {
            objectNode.put("error", "Not enough mana to use hero's ability.");
            output.addPOJO(objectNode);
            return;
        }

        // hero must be able to attack (only once per round)
        if (!heroCard.isAbleToAttack()) {
            objectNode.put("error", "Hero has already attacked this turn.");
            output.addPOJO(objectNode);
            return;
        }

        // if hero has aggressive ability, affected row must be an enemy row
        if (heroCard.getName().equals("Lord Royce") || heroCard.getName().equals("Empress Thorina")) {
            if (gameInfo.getPlayerTurn() == 1 && (command.getAffectedRow() == 2 || command.getAffectedRow() == 3)) {
                objectNode.put("error", "Selected row does not belong to the enemy.");
                output.addPOJO(objectNode);
                return;
            } else if (gameInfo.getPlayerTurn() == 2 && (command.getAffectedRow() == 0 || command.getAffectedRow() == 1)) {
                objectNode.put("error", "Selected row does not belong to the enemy.");
                output.addPOJO(objectNode);
                return;
            }

        }

        // if hero has defensive ability, affected row must be an allied row
        if (heroCard.getName().equals("King Mudface") || heroCard.getName().equals("General Kocioraw")) {
            if (gameInfo.getPlayerTurn() == 1 && (command.getAffectedRow() == 1 || command.getAffectedRow() == 0)) {
                objectNode.put("error", "Selected row does not belong to the current player.");
                output.addPOJO(objectNode);
                return;
            } else if (gameInfo.getPlayerTurn() == 2 && (command.getAffectedRow() == 2 || command.getAffectedRow() == 3)) {
                objectNode.put("error", "Selected row does not belong to the current player.");
                output.addPOJO(objectNode);
                return;
            }
        }

        // apply ability on affected row
        heroCard.useHeroAbility(table.get(command.getAffectedRow()));

        // mark hero as unable to attack
        heroCard.setAbleToAttack(false);

        // consume player mana
        if (gameInfo.getPlayerTurn() == 1)
            playerOne.useMana(heroCard.getMana());
        else
            playerTwo.useMana(heroCard.getMana());

    }

    public static boolean isTankOnRow(GameInfo gameInfo, ArrayList<LinkedList<MinionCard>> table) {
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

        return isTankOnRow;
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
