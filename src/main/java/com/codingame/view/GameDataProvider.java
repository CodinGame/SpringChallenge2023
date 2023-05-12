package com.codingame.view;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.codingame.game.Cell;
import com.codingame.game.CellType;
import com.codingame.game.CubeCoord;
import com.codingame.game.Game;
import com.codingame.game.Player;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GameDataProvider {
    @Inject private Game game;
    @Inject private MultiplayerGameManager<Player> gameManager;

    public GlobalViewData getGlobalData() {
        GlobalViewData data = new GlobalViewData();
        data.cells = game.getBoardCoords()
            .stream()
            .map(entry -> {
                CubeCoord coord = entry;
                Cell cell = game.getBoard().get(coord);

                CellData cellData = new CellData();
                cellData.q = coord.getX();
                cellData.r = coord.getZ();
                cellData.richness = cell.getRichness();
                cellData.index = cell.getIndex();
                cellData.owner = Optional.ofNullable(cell.getAnthill())
                    .map(ah -> ah.getIndex())
                    .orElse(-1);
                cellData.type = cell.getType() == CellType.EGG ? 1 : 2;
                cellData.ants = cell.getAnts();
                return cellData;
            })
            .collect(Collectors.toList());

        return data;
    }

    private List<int[]> collectCellData(BiFunction<Cell, Player, Integer> getter) {
        return gameManager.getPlayers().stream()
            .map(player -> {
                return collectCellData(cell -> getter.apply(cell, player));
            })
            .collect(Collectors.toList());
    }

    private int[] collectCellData(Function<Cell, Integer> getter) {
        return game.getBoardCoords().stream()
            .mapToInt(coord -> {
                Cell cell = game.getBoard().get(coord);
                return getter.apply(cell);
            })
            .toArray();

    }

    public FrameViewData getCurrentFrameData() {
        FrameViewData data = new FrameViewData();

        data.messages = gameManager.getPlayers().stream()
            .map(player -> player.getMessage())
            .collect(Collectors.toList());
        data.beacons = collectCellData((cell, player) -> cell.getBeaconPower(player));

        data.scores = gameManager.getPlayers().stream()
            .map(Player::getPoints)
            .collect(Collectors.toList());

        data.events = game.getViewerEvents();

        return data;
    }

}
