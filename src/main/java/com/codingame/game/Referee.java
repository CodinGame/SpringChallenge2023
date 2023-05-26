package com.codingame.game;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.view.ViewModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Referee extends AbstractReferee {

    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private CommandManager commandManager;
    @Inject private Game game;
    @Inject private ViewModule viewModule;

    @Override
    public void init() {
        try {
            int leagueLevel = gameManager.getLeagueLevel();

            if (leagueLevel == 1) {
                Config.FORCE_SINGLE_HILL = true;
                Config.ENABLE_EGGS = false;
                Config.LOSING_ANTS_CANT_CARRY = false;
                Config.MAP_RING_COUNT_MAX = 4;
            } else if (leagueLevel == 2) {
                Config.FORCE_SINGLE_HILL = true;
                Config.LOSING_ANTS_CANT_CARRY = false;
                Config.MAP_RING_COUNT_MAX = 5;
            }
            // level 3 = interactions, big map, multiple hills
            if (leagueLevel >= 4) {
                Config.SCORES_IN_IO = true;
            }

            Config.takeFrom(gameManager.getGameParameters());

            game.init();
            sendGlobalInfo();

            gameManager.setFrameDuration(500);
            gameManager.setMaxTurns(Config.MAX_TURNS);
            gameManager.setTurnMaxTime(100);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Referee failed to initialize");
            abort();
        }
    }

    private void abort() {
        gameManager.endGame();

    }

    private void sendGlobalInfo() {
        // Give input to players
        for (Player player : gameManager.getActivePlayers()) {
            for (String line : game.getGlobalInfoFor(player)) {
                player.sendInputLine(line);
            }
        }
    }

    @Override
    public void gameTurn(int turn) {
        try {
            game.resetGameTurnData();

            if (game.isKeyFrame()) {
                // Give input to players
                for (Player player : gameManager.getActivePlayers()) {
                    for (String line : game.getCurrentFrameInfoFor(player)) {
                        player.sendInputLine(line);
                    }
                    player.execute();
                }
                // Get output from players
                handlePlayerCommands();
            }

            game.performGameUpdate(turn);

            if (gameManager.getActivePlayers().size() < 2) {
                abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Referee failed to compute turn. seed=" + gameManager.getSeed());
            abort();
        }
    }

    private void handlePlayerCommands() {

        for (Player player : gameManager.getActivePlayers()) {
            try {
                commandManager.parseCommands(player, player.getOutputs());
            } catch (TimeoutException e) {
                player.deactivate("Timeout!");
                gameManager.addToGameSummary(player.getNicknameToken() + " has not provided " + player.getExpectedOutputLines() + " lines in time");
            }
        }

    }

    @Override
    public void onEnd() {
        game.onEnd();
    }
}
