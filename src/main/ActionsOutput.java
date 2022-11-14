package main;

import fileio.CardInput;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;

public abstract class ActionsOutput {
    private String command;
    private String error = null;

    public ActionsOutput(String command, String error) {
        this.command = command;
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

    public GetTableCards(String command, LinkedList<Card> output) {
        super(command, null);
        this.output = output;
    }
}

class GetPlayerTurn extends ActionsOutput {
    private int output;

    public GetPlayerTurn(String command, String error, int output) {
        super(command, error);
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

    public GetPlayerMana(String command, String error, int playerIdx) {
        super(command, error);
        this.playerIdx = playerIdx;
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
