package main;

public class GameInfo {
    private int playerTurn;
    private int currentGame;
    private int playerOneWins;
    private int playerTwoWins;
    private int playerManaGain;

    private int currentRound;

    public GameInfo(int playerTurn, int currentGame, int playerOneWins, int playerTwoWins, int playerManaGain, int currentRound) {
        this.playerTurn = playerTurn;
        this.currentGame = currentGame;
        this.playerOneWins = playerOneWins;
        this.playerTwoWins = playerTwoWins;
        this.playerManaGain = playerManaGain;
        this.currentRound = currentRound;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    public int getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(int currentGame) {
        this.currentGame = currentGame;
    }

    public int getPlayerOneWins() {
        return playerOneWins;
    }

    public void setPlayerOneWins(int playerOneWins) {
        this.playerOneWins = playerOneWins;
    }

    public int getPlayerTwoWins() {
        return playerTwoWins;
    }

    public void setPlayerTwoWins(int playerTwoWins) {
        this.playerTwoWins = playerTwoWins;
    }

    public int getPlayerManaGain() {
        return playerManaGain;
    }

    public void setPlayerManaGain(int playerManaGain) {
        this.playerManaGain = playerManaGain;
    }
}
