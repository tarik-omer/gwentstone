package main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import fileio.CardInput;

import javax.swing.*;
import java.awt.*;
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
            output = playerOne.getPlayerHand();
        } else if (playerIdx == 2) {
            output = playerTwo.getPlayerHand();
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
            this.output = playerOne.getPlayerCurrentDeck();
        } else if (playerIdx == 2) {
            this.output = playerTwo.getPlayerCurrentDeck();
        }

        this.playerIdx = playerIdx;
    }
}

class GetTableCards extends ActionsOutput {
    private LinkedList<Card> output;

    public GetTableCards(ArrayList<LinkedList<MinionCard>> table) {
        super("getCardsOnTable", null);
        this.output = new LinkedList<>();

        for (LinkedList<MinionCard> row : table) {
            for (MinionCard card : row) {
                this.output.push(card);
            }
        }
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
    private Card output;

    public GetCardPosition(String command, String error, int x) {
        super(command, error);
        this.x = x;
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

    public GetEnvironmentCardsInHand(String command, String error, int playerIdx) {
        super(command, error);
        this.playerIdx = playerIdx;
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
            if (card.getName().equals("The Ripper") ||
                card.getName().equals("Miraj") ||
                card.getName().equals("Goliath") ||
                card.getName().equals("Warden")) {
                if ((playerTurn == 1 && table.get(2).size() == 5) ||
                    (playerTurn == 2 && table.get(1).size() == 5)) {
                    this.setError("Cannot place card on table since row is full.");
                // if rows are not full, place card on player's front row
                } else if (playerTurn == 1) {
                    table.get(2).addLast((MinionCard)playerOne.getPlayerHand().remove(handIdx));
                    playerOne.useMana(card.getMana());
                } else if (playerTurn == 2) {
                    table.get(1).addLast((MinionCard)playerTwo.getPlayerHand().remove(handIdx));
                    playerTwo.useMana(card.getMana());
                }
            // we consider cards that must be placed on rows 0 and 3 (back rows)
            // check if rows are full
            } else if (card.getName().equals("Sentinel") ||
                    card.getName().equals("Berserker") ||
                    card.getName().equals("The Cursed One") ||
                    card.getName().equals("Disciple")) {
                if (playerTurn == 1 && table.get(2).size() == 5 ||
                        playerTurn == 2 && table.get(1).size() == 5) {
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
