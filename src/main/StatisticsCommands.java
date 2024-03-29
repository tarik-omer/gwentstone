package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class StatisticsCommands {
    private GameInfo gameInfo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ArrayNode output;

    // Singleton
    private static StatisticsCommands instance = null;

    private StatisticsCommands() {

    }

    public static StatisticsCommands getInstance() {
        return instance;
    }

    static {
        instance = new StatisticsCommands();
    }

    /**
     * Displays the total number of games played.
     */
    public void getTotalGamesPlayed() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "getTotalGamesPlayed");
        objectNode.put("output", gameInfo.getCurrentGame());
        output.addPOJO(objectNode);
    }

    /**
     * Displays the total number of games player one won.
     */
    public void getPlayerOneWins() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "getPlayerOneWins");
        objectNode.put("output", gameInfo.getPlayerOneWins());
        output.addPOJO(objectNode);
    }

    /**
     * Displays the total number of games player two won.
     */
    public void getPlayerTwoWins() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "getPlayerTwoWins");
        objectNode.put("output", gameInfo.getPlayerTwoWins());
        output.addPOJO(objectNode);
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(final GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public ArrayNode getOutput() {
        return output;
    }

    public void setOutput(final ArrayNode output) {
        this.output = output;
    }
}
