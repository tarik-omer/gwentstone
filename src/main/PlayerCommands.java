package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.Coordinates;

import java.util.ArrayList;
import java.util.LinkedList;

public final class PlayerCommands {
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

    public static PlayerCommands getInstance() {
        return instance;
    }

    /**
     * Places a card with given index on the table, on the corresponding row.
     * Card is placed by the current player.
     * @param command   contains index of card placed from hand
     */
    public void placeCard(final ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("handIdx", command.getHandIdx());

        Card card;
        // check to see if hand index exists
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

        if (card.getName().equals("Winterfell") || card.getName().equals("Heart Hound")
                || card.getName().equals("Firestorm")) {
            // environment card cannot be placed on table - error
            objectNode.put("error", "Cannot place environment card on table.");
            output.addPOJO(objectNode);

        } else if ((gameInfo.getPlayerTurn() == 1 && playerOne.getMana() < card.getMana())
                || gameInfo.getPlayerTurn() == 2 && playerTwo.getMana() < card.getMana()) {
            // not enough mana - error
            objectNode.put("error", "Not enough mana to place card on table.");
            output.addPOJO(objectNode);

        } else {
            int handIdx = command.getHandIdx();
            // we consider cards that must be placed on rows 1 and 2 (front rows)
            // check if rows are full
            if (Card.correspondingRow(card) == 1) {
                if ((gameInfo.getPlayerTurn() == 1 && table.get(2).size() == Global.MAX_ROW_SIZE)
                        || (gameInfo.getPlayerTurn() == 2
                        && table.get(1).size() == Global.MAX_ROW_SIZE)) {
                    objectNode.put("error",
                            "Cannot place card on table since row is full.");
                    output.addPOJO(objectNode);
                    // if rows are not full, place card on player's front row
                } else if (gameInfo.getPlayerTurn() == 1) {
                    table.get(2).addLast((MinionCard)
                            playerOne.getPlayerHand().remove(handIdx));
                    playerOne.useMana(card.getMana());
                } else if (gameInfo.getPlayerTurn() == 2) {
                    table.get(1).addLast((MinionCard)
                            (playerTwo.getPlayerHand().remove(handIdx)));
                    playerTwo.useMana(card.getMana());
                }
                // we consider cards that must be placed on rows 0 and 3 (back rows)
                // check if rows are full
            } else if (Card.correspondingRow(card) == -1) {
                if (gameInfo.getPlayerTurn() == 1
                        && table.get(Global.PLAYER_ONE_BACK_ROW).size() == Global.MAX_ROW_SIZE
                        || gameInfo.getPlayerTurn() == 2
                        && table.get(Global.PLAYER_TWO_BACK_ROW).size() == Global.MAX_ROW_SIZE) {
                    objectNode.put("error",
                            "Cannot place card on table since row is full.");
                    output.addPOJO(objectNode);
                    // if rows are not full, place card on player's back row
                } else if (gameInfo.getPlayerTurn() == 1) {
                    MinionCard placedCard = (MinionCard) playerOne.getPlayerHand().remove(handIdx);
                    table.get(Global.PLAYER_ONE_BACK_ROW).addLast(placedCard);
                    playerOne.useMana(card.getMana());

                } else if (gameInfo.getPlayerTurn() == 2) {
                    MinionCard placedCard = (MinionCard) playerTwo.getPlayerHand().remove(handIdx);
                    table.get(Global.PLAYER_TWO_BACK_ROW).addLast(placedCard);
                    playerTwo.useMana(card.getMana());
                }
            }
        }
    }

    /**
     * Uses an environmental card on the table, applying its effect on a given row.
     * Card is used by the current player.
     * @param command   contains index of the used card and targeted row
     */
    public void useEnvironmentCard(final ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", command.getCommand());
        objectNode.put("handIdx", command.getHandIdx());
        objectNode.put("affectedRow", command.getAffectedRow());

        int handIdx = command.getHandIdx();

        // check whether card is environment card
        // player has enough mana and affected row is enemy's row
        if (gameInfo.getPlayerTurn() == 1
                && Card.correspondingRow(playerOne.getPlayerHand().get(handIdx)) != 0) {
            objectNode.put("error", "Chosen card is not of type environment.");
            output.addPOJO(objectNode);

        } else if (gameInfo.getPlayerTurn() == 2
                && Card.correspondingRow(playerTwo.getPlayerHand().get(handIdx)) != 0) {
            objectNode.put("error", "Chosen card is not of type environment.");
            output.addPOJO(objectNode);

        } else if (gameInfo.getPlayerTurn() == 1
                && playerOne.getMana() < playerOne.getPlayerHand().get(handIdx).getMana()) {
            objectNode.put("error", "Not enough mana to use environment card.");
            output.addPOJO(objectNode);

        } else if (gameInfo.getPlayerTurn() == 2
                && playerTwo.getMana() < playerTwo.getPlayerHand().get(handIdx).getMana()) {
            objectNode.put("error", "Not enough mana to use environment card.");
            output.addPOJO(objectNode);

        } else if (gameInfo.getPlayerTurn() == 1
                && (command.getAffectedRow() == Global.PLAYER_ONE_FRONT_ROW
                || command.getAffectedRow() == Global.PLAYER_ONE_BACK_ROW)) {
            objectNode.put("error", "Chosen row does not belong to the enemy.");
            output.addPOJO(objectNode);

        } else if (gameInfo.getPlayerTurn() == 2
                && (command.getAffectedRow() == Global.PLAYER_TWO_BACK_ROW
                || command.getAffectedRow() == Global.PLAYER_TWO_FRONT_ROW)) {
            objectNode.put("error", "Chosen row does not belong to the enemy.");
            output.addPOJO(objectNode);

        } else {
            // get environment card of current player
            Card environmentCard;
            if (gameInfo.getPlayerTurn() == 1) {
                environmentCard = playerOne.getPlayerHand().get(command.getHandIdx());
            } else {
                environmentCard = playerTwo.getPlayerHand().get(command.getHandIdx());
            }
            LinkedList<MinionCard> affectedCards = table.get(command.getAffectedRow());
            // apply effect of chosen environment card
            switch (environmentCard.getName()) {
                case "Firestorm" -> {
                    ((FirestormEnvironmentCard) environmentCard).firestormEffect(affectedCards);
                }
                case "Winterfell" -> {
                    ((WinterfellEnvironmentCard) environmentCard).winterfellEffect(affectedCards);
                }
                case "Heart Hound" -> {
                    int err = ((HeartHoundEnvironmentCard)
                            environmentCard).heartHoundEffect(affectedCards,
                            table, gameInfo.getPlayerTurn());
                    // if err is 1, card was not moved because player's row is full
                    // else, card was moved, no error
                    if (err == 1) {
                        objectNode.put("error",
                                "Cannot steal enemy card since the player's row is full.");
                        output.addPOJO(objectNode);
                        return;
                    }
                }
                default ->
                        throw new IllegalStateException("Unexpected value: "
                                + environmentCard.getName());
            }
            // consume mana and remove used card from player hand
            if (gameInfo.getPlayerTurn() == 1) {
                playerOne.getPlayerHand().remove(command.getHandIdx());
                playerOne.useMana(environmentCard.getMana());
            } else if (gameInfo.getPlayerTurn() == 2) {
                playerTwo.getPlayerHand().remove(command.getHandIdx());
                playerTwo.useMana(environmentCard.getMana());
            }
        }
    }

    /**
     * Attacks an enemy minion using an allied minion.
     * Attack is done by the current player.
     * Minion must be placed on the table.
     * @param command   contains attacker and attacked card coordinates
     */
    public void cardUsesAttack(final ActionsInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        // prepare object node, in case of error
        objectNode.put("command", command.getCommand());

        objectNode.putPOJO("cardAttacker",
                new Coordinates(command.getCardAttacker().getX(),
                command.getCardAttacker().getY()));
        objectNode.putPOJO("cardAttacked",
                new Coordinates(command.getCardAttacked().getX(),
                command.getCardAttacked().getY()));

        // check to see whether attacked card belongs to the enemy
        if (gameInfo.getPlayerTurn() == 1
                && (command.getCardAttacked().getX() == Global.PLAYER_ONE_FRONT_ROW
                || command.getCardAttacked().getX() == Global.PLAYER_ONE_BACK_ROW)) {
            objectNode.put("error", "Attacked card does not belong to the enemy.");
            output.addPOJO(objectNode);
            return;

        } else if (gameInfo.getPlayerTurn() == 2
                && (command.getCardAttacked().getX() == Global.PLAYER_TWO_FRONT_ROW
                || command.getCardAttacked().getX() == Global.PLAYER_TWO_BACK_ROW)) {
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

    /**
     * Uses the ability of an allied minion on another minion.
     * Ability is used by the current player.
     * Minion must be placed on the table.
     * @param command   contains attacker and attacked card coordinates
     */
    public void cardUsedAbility(final ActionsInput command) {
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
        if (gameInfo.getPlayerTurn() == 1 && attackerCard.getName().equals("Disciple")
                && (attackedX == Global.PLAYER_TWO_BACK_ROW
                        || attackedX == Global.PLAYER_TWO_FRONT_ROW)) {
            objectNode.put("error",
                    "Attacked card does not belong to the current player.");
            output.addPOJO(objectNode);
            return;

        } else if (gameInfo.getPlayerTurn() == 2 && attackerCard.getName().equals("Disciple")
                && (attackedX == Global.PLAYER_ONE_BACK_ROW
                || attackedX == Global.PLAYER_ONE_FRONT_ROW)) {
            objectNode.put("error",
                    "Attacked card does not belong to the current player.");
            output.addPOJO(objectNode);
            return;

        } else if (gameInfo.getPlayerTurn() == 1 && !attackerCard.getName().equals("Disciple")
                && (attackedX == Global.PLAYER_ONE_BACK_ROW
                        || attackedX == Global.PLAYER_ONE_FRONT_ROW)) {
            objectNode.put("error", "Attacked card does not belong to the enemy.");
            output.addPOJO(objectNode);
            return;

        } else if (gameInfo.getPlayerTurn() == 2 && !attackerCard.getName().equals("Disciple")
                && (attackedX == Global.PLAYER_TWO_BACK_ROW
                        || attackedX == Global.PLAYER_TWO_FRONT_ROW)) {
            objectNode.put("error", "Attacked card does not belong to the enemy.");
            output.addPOJO(objectNode);
            return;
        }

        // must check for tanks only if target is an enemy (card != Disciple)
        if (!attackerCard.getName().equals("Disciple")) {

            boolean isTankOnRow = PlayerCommands.isTankOnRow(gameInfo, table);

            // if there are tanks on the enemy side and the attacked card is not a tank
            // => cannot attack
            if (isTankOnRow && !attackedCard.isTank()) {
                objectNode.put("error", "Attacked card is not of type 'Tank'.");
                output.addPOJO(objectNode);
                return;
            }
        }

        // apply ability of attacker card on attacked card
        attackerCard.specialAbility(attackedCard);

        // check to see if minion died (cursed one case)
        if (attackedCard.isDead()) {
            table.get(attackedX).remove(attackedY);
        }
        // a card can attack only once per round
        attackerCard.setAbleToAttack(false);
    }

    /**
     * Attacks the enemy hero with an allied minion.
     * Attack is done by the current player.
     * Minion must be placed on the table.
     * @param command   contains attacker minion
     */
    public void useAttackHero(final ActionsInput command) {
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

    /**
     * Uses the ability of the current player's hero on a targeted row.
     * @param command   contains targeted row
     */
    public void useHeroAbility(final ActionsInput command) {
        // prepare output in case of error
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", command.getCommand());
        objectNode.put("affectedRow", command.getAffectedRow());

        // attacker hero card
        HeroCard heroCard;
        if (gameInfo.getPlayerTurn() == 1) {
            heroCard = playerOne.getHeroCard();
        } else {
            heroCard = playerTwo.getHeroCard();
        }
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
        if (heroCard.getName().equals("Lord Royce")
                || heroCard.getName().equals("Empress Thorina")) {
            if (gameInfo.getPlayerTurn() == 1
                    && (command.getAffectedRow() == Global.PLAYER_ONE_FRONT_ROW
                    || command.getAffectedRow() == Global.PLAYER_ONE_BACK_ROW)) {
                objectNode.put("error", "Selected row does not belong to the enemy.");
                output.addPOJO(objectNode);
                return;
            } else if (gameInfo.getPlayerTurn() == 2
                    && (command.getAffectedRow() == Global.PLAYER_TWO_BACK_ROW
                    || command.getAffectedRow() == Global.PLAYER_TWO_FRONT_ROW)) {
                objectNode.put("error", "Selected row does not belong to the enemy.");
                output.addPOJO(objectNode);
                return;
            }

        }

        // if hero has defensive ability, affected row must be an allied row
        if (heroCard.getName().equals("King Mudface")
                || heroCard.getName().equals("General Kocioraw")) {
            if (gameInfo.getPlayerTurn() == 1
                    && (command.getAffectedRow() == Global.PLAYER_TWO_FRONT_ROW
                            || command.getAffectedRow() == Global.PLAYER_TWO_BACK_ROW)) {
                objectNode.put("error",
                        "Selected row does not belong to the current player.");
                output.addPOJO(objectNode);
                return;
            } else if (gameInfo.getPlayerTurn() == 2
                    && (command.getAffectedRow() == Global.PLAYER_ONE_FRONT_ROW
                    || command.getAffectedRow() == Global.PLAYER_ONE_BACK_ROW)) {
                objectNode.put("error",
                        "Selected row does not belong to the current player.");
                output.addPOJO(objectNode);
                return;
            }
        }

        // apply ability on affected row
        heroCard.useHeroAbility(table.get(command.getAffectedRow()));

        // mark hero as unable to attack
        heroCard.setAbleToAttack(false);

        // consume player mana
        if (gameInfo.getPlayerTurn() == 1) {
            playerOne.useMana(heroCard.getMana());
        } else {
            playerTwo.useMana(heroCard.getMana());
        }
    }

    /**
     * Return whether there are any tanks on the front row of the given player's enemy
     * @param gameInfo  contains current player
     * @param table     playing table
     * @return          true if there are tanks, false if there are no tanks
     */
    public static boolean isTankOnRow(final GameInfo gameInfo,
                                      final ArrayList<LinkedList<MinionCard>> table) {
        // get front row of attacked player
        LinkedList<MinionCard> attackedFrontRow;
        if (gameInfo.getPlayerTurn() == 1) {
            attackedFrontRow = table.get(1);
        } else {
            attackedFrontRow = table.get(2);
        }
        // check to see if there is a tank on the enemy's side; tanks can only be on the first row
        boolean isTankOnRow = false;
        for (MinionCard minionCard : attackedFrontRow) {
            if (minionCard.isTank()) {
                isTankOnRow = true;
                break;
            }
        }

        return isTankOnRow;
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
