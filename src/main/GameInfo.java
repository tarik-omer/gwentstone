package main;

public final class GameInfo {
    private int playerTurn;
    private int currentGame;
    private int playerOneWins;
    private int playerTwoWins;
    private int playerManaGain;
    private int currentRound;

    public GameInfo(final int playerTurn, final int currentGame,
                    final int playerOneWins, final int playerTwoWins,
                    final int playerManaGain, final int currentRound) {
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

    public void setCurrentRound(final int currentRound) {
        this.currentRound = currentRound;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(final int playerTurn) {
        this.playerTurn = playerTurn;
    }

    public int getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(final int currentGame) {
        this.currentGame = currentGame;
    }

    public int getPlayerOneWins() {
        return playerOneWins;
    }

    public void setPlayerOneWins(final int playerOneWins) {
        this.playerOneWins = playerOneWins;
    }

    public int getPlayerTwoWins() {
        return playerTwoWins;
    }

    public void setPlayerTwoWins(final int playerTwoWins) {
        this.playerTwoWins = playerTwoWins;
    }

    public int getPlayerManaGain() {
        return playerManaGain;
    }

    public void setPlayerManaGain(final int playerManaGain) {
        this.playerManaGain = playerManaGain;
    }
}
