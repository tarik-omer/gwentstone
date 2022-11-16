package main;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.LinkedList;
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ActionsOutput {
    private String command;

    private String error;

    public ActionsOutput(String command, String error) {
        this.command = command;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}

class GetPlayerHand extends ActionsOutput {
    private int playerIdx;
    private LinkedList<Card> output;

    public int getPlayerIdx() {
        return playerIdx;
    }

    public void setPlayerIdx(int playerIdx) {
        this.playerIdx = playerIdx;
    }

    public LinkedList<Card> getOutput() {
        return output;
    }

    public void setOutput(LinkedList<Card> output) {
        this.output = output;
    }

    public GetPlayerHand(int playerIdx, Player playerOne, Player playerTwo) {
        super("getCardsInHand", null);

        if (playerIdx == 1) {
            output = new LinkedList<>(playerOne.getPlayerHand());
        } else if (playerIdx == 2) {
            output = new LinkedList<>(playerTwo.getPlayerHand());
        }
        this.playerIdx = playerIdx;
    }
}

class GetPlayerDeck extends ActionsOutput {
    private int playerIdx;
    private LinkedList<Card> output;

    public int getPlayerIdx() {
        return playerIdx;
    }

    public void setPlayerIdx(int playerIdx) {
        this.playerIdx = playerIdx;
    }

    public LinkedList<Card> getOutput() {
        return output;
    }

    public void setOutput(LinkedList<Card> output) {
        this.output = output;
    }

    public GetPlayerDeck(int playerIdx, Player playerOne, Player playerTwo) {
        super("getPlayerDeck", null);

        if (playerIdx == 1) {
            this.output = new LinkedList<>(playerOne.getPlayerCurrentDeck());
        } else if (playerIdx == 2) {
            this.output = new LinkedList<>(playerTwo.getPlayerCurrentDeck());
        }

        this.playerIdx = playerIdx;
    }
}

class GetTableCards extends ActionsOutput {
    private LinkedList<LinkedList<MinionCard>> output;

    public GetTableCards(ArrayList<LinkedList<MinionCard>> table) {
        super("getCardsOnTable", null);
        // table copy
        this.output = new LinkedList<>();
        // add each row
        this.output.add(new LinkedList<>(table.get(0)));
        this.output.add(new LinkedList<>(table.get(1)));
        this.output.add(new LinkedList<>(table.get(2)));
        this.output.add(new LinkedList<>(table.get(3)));

    }

    public LinkedList<LinkedList<MinionCard>> getOutput() {
        return output;
    }

    public void setOutput(LinkedList<LinkedList<MinionCard>> output) {
        this.output = output;
    }
}

class GetPlayerTurn extends ActionsOutput {
    private int output;

    public GetPlayerTurn(int playerTurn) {
        super("getPlayerTurn", null);
        this.output = playerTurn;
    }

    public int getOutput() {
        return output;
    }

    public void setOutput(int output) {
        this.output = output;
    }
}

class GetPlayerHero extends ActionsOutput {
    private int playerIdx;
    private HeroCard output;

    public GetPlayerHero(int playerIdx, Player playerOne, Player playerTwo) {
        super("getPlayerHero", null);
        this.playerIdx = playerIdx;

        if (playerIdx == 1) {
            this.output = playerOne.getHeroCard();
        } else if (playerIdx == 2) {
            this.output = playerTwo.getHeroCard();
        }
    }

    public int getPlayerIdx() {
        return playerIdx;
    }

    public void setPlayerIdx(int playerIdx) {
        this.playerIdx = playerIdx;
    }

    public HeroCard getOutput() {
        return output;
    }

    public void setOutput(HeroCard output) {
        this.output = output;
    }
}

class GetCardPosition extends ActionsOutput {
    private int x;
    private int y;
    private MinionCard output;

    public GetCardPosition(int x, int y, ArrayList<LinkedList<MinionCard>> table) {
        super("getCardAtPosition", null);
        this.x = x;
        this.y = y;

        if (table.get(y).size() > x) {
            this.output = table.get(y).get(x);
        } else {
            // no card at this position
            this.output = null;
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public MinionCard getOutput() {
        return output;
    }

    public void setOutput(MinionCard output) {
        this.output = output;
    }
}

class GetPlayerMana extends ActionsOutput {
    private int playerIdx;
    private int output;

    public GetPlayerMana(Player playerOne, Player playerTwo, int playerIdx) {
        super("getPlayerMana", null);
        this.playerIdx = playerIdx;

        if (playerIdx == 1) {
            this.output = playerOne.getMana();
        } else {
            this.output = playerTwo.getMana();
        }
    }

    public int getPlayerIdx() {
        return playerIdx;
    }

    public void setPlayerIdx(int playerIdx) {
        this.playerIdx = playerIdx;
    }

    public int getOutput() {
        return output;
    }

    public void setOutput(int output) {
        this.output = output;
    }
}

class GetEnvironmentCardsInHand extends ActionsOutput {
    private int playerIdx;
    LinkedList<Card> output;

    public GetEnvironmentCardsInHand(int playerIdx, Player playerOne, Player playerTwo) {
        super("getEnvironmentCardsInHand", null);
        this.playerIdx = playerIdx;

        this.output = new LinkedList<>();

        if (playerIdx == 1) {
            for (Card card : playerOne.getPlayerHand()) {
                if (Card.correspondingRow(card) == 0) {
                    this.output.add(card);
                }
            }
        } else if (playerIdx == 2) {
            for (Card card : playerTwo.getPlayerHand()) {
                if (Card.correspondingRow(card) == 0) {
                    this.output.add(card);
                }
            }
        }
    }

    public int getPlayerIdx() {
        return playerIdx;
    }

    public void setPlayerIdx(int playerIdx) {
        this.playerIdx = playerIdx;
    }

    public LinkedList<Card> getOutput() {
        return output;
    }

    public void setOutput(LinkedList<Card> output) {
        this.output = output;
    }
}

class GetFrozenCardsOnTable extends ActionsOutput {
    LinkedList<Card> output;

    public GetFrozenCardsOnTable(String command, String error, LinkedList<Card> output) {
        super(command, error);
        this.output = output;
    }
}

class PlaceCard extends ActionsOutput {
    private int handIdx;
    public PlaceCard(int handIdx, ArrayList<LinkedList<MinionCard>> table, int playerTurn,
                     Player playerOne, Player playerTwo) {
        super("placeCard", null);
        this.handIdx = handIdx;

        Card card;

        if (playerTurn == 1) {
            if (playerOne.getPlayerHand().size() <= handIdx) {
                System.out.println("You are trying to place a card you don't have");
                this.setError("You are trying to place a card you don't have");
                return;
            }
            card = playerOne.getPlayerHand().get(handIdx);
        } else {
            if (playerTwo.getPlayerHand().size() <= handIdx) {
                System.out.println("You are trying to place a card you don't have");
                this.setError("You are trying to place a card you don't have");
                return;
            }
            card = playerTwo.getPlayerHand().get(handIdx);
        }

        if (card == null) {
            this.setError("No cards in hand left.");
            return;
        }

        // environment card cannot be placed on table - error
        if (card.getName().equals("Winterfell") || card.getName().equals("Heart Hound") ||
                card.getName().equals("Firestorm")) {
            this.setError("Cannot place environment card on table.");
        // not enough mana - error
        } else if ((playerTurn == 1 && playerOne.getMana() < card.getMana()) ||
                playerTurn == 2 && playerTwo.getMana() < card.getMana()) {
            this.setError("Not enough mana to place card on table.");
        } else {
            // we consider cards that must be placed on rows 1 and 2 (front rows)
            // check if rows are full
            if (Card.correspondingRow(card) == 1) {
                if ((playerTurn == 1 && table.get(2).size() == 5) ||
                    (playerTurn == 2 && table.get(1).size() == 5)) {
                    this.setError("Cannot place card on table since row is full.");
                // if rows are not full, place card on player's front row
                } else if (playerTurn == 1) {
                    table.get(2).addLast((MinionCard) playerOne.getPlayerHand().remove(handIdx));
                    playerOne.useMana(card.getMana());
                } else if (playerTurn == 2) {
                    table.get(1).addLast((MinionCard)(playerTwo.getPlayerHand().remove(handIdx)));
                    playerTwo.useMana(card.getMana());
                }
            // we consider cards that must be placed on rows 0 and 3 (back rows)
            // check if rows are full
            } else if (Card.correspondingRow(card) == -1) {
                if (playerTurn == 1 && table.get(3).size() == 5 ||
                        playerTurn == 2 && table.get(0).size() == 5) {
                    this.setError("Cannot place card on table since row is full.");
                // if rows are not full, place card on player's back row
                } else if (playerTurn == 1) {
                    table.get(3).addLast((MinionCard)playerOne.getPlayerHand().remove(handIdx));
                    playerOne.useMana(card.getMana());
                } else if (playerTurn == 2) {
                    table.get(0).addLast((MinionCard)playerTwo.getPlayerHand().remove(handIdx));
                    playerTwo.useMana(card.getMana());
                }
            }
        }
    }

    public int getHandIdx() {
        return handIdx;
    }

    public void setHandIdx(int handIdx) {
        this.handIdx = handIdx;
    }
}

class UseEnvironmentCard extends ActionsOutput {

    private int handIdx;
    private int affectedRow;
    public UseEnvironmentCard(int handIdx, int affectedRow, Player playerOne, Player playerTwo,
                              ArrayList<LinkedList<MinionCard>> table, int playerTurn) {
        super("useEnvironmentCard", null);
        this.affectedRow = affectedRow;
        this.handIdx = handIdx;

        // check whether card is environment card, player has enough mana and affected row is enemy's row
        if (playerTurn == 1 && MinionCard.correspondingRow(playerOne.getPlayerHand().get(handIdx)) != 0) {
            this.setError("Chosen card is not of type environment.");
        } else if (playerTurn == 2 && MinionCard.correspondingRow(playerTwo.getPlayerHand().get(handIdx)) != 0) {
            this.setError("Chosen card is not of type environment.");
        } else if (playerTurn == 1 && playerOne.getMana() < playerOne.getPlayerHand().get(handIdx).getMana()) {
            this.setError("Not enough mana to use environment card.");
        } else if (playerTurn == 2 && playerTwo.getMana() < playerTwo.getPlayerHand().get(handIdx).getMana()) {
            this.setError("Not enough mana to use environment card.");
        } else if (playerTurn == 1 && (affectedRow == 2 || affectedRow == 3)) {
            this.setError("Chosen row does not belong to the enemy.");
        } else if (playerTurn == 2 && (affectedRow == 0 || affectedRow == 1)) {
            this.setError("Chosen row does not belong to the enemy.");
        } else if (playerTurn == 1) {
            Card environmentCard = playerOne.getPlayerHand().get(handIdx);
            if (environmentCard.getName().equals("Firestorm")) {
                ((FirestormEnvironmentCard)environmentCard).firestormEffect(table.get(affectedRow));
            } else if (environmentCard.getName().equals("Winterfell")) {
                ((WinterfellEnvironmentCard)environmentCard).winterfellEffect(table.get(affectedRow));
            } else if (environmentCard.getName().equals("Heart Hound")) {
                int err = ((HeartHoundEnvironmentCard)environmentCard).heartHoundEffect(table.get(affectedRow),
                        table, playerTurn);
                // if err is 1, card was not moved because player's row is full; else, card was moved, no error
                if (err == 1) {
                    this.setError("Cannot steal enemy card since the player's row is full.");
                }
            }
        }
    }

    public int getHandIdx() {
        return handIdx;
    }

    public void setHandIdx(int handIdx) {
        this.handIdx = handIdx;
    }

    public int getAffectedRow() {
        return affectedRow;
    }

    public void setAffectedRow(int affectedRow) {
        this.affectedRow = affectedRow;
    }
}