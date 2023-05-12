package com.codingame.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Serializer {
    public static final String MAIN_SEPARATOR = ";";

    static public String serialize(FrameViewData frameViewData) {
        List<Object> lines = new ArrayList<>();

        frameViewData.scores
            .forEach(lines::add);
        frameViewData.messages.stream()
            .map(m -> m == null ? "" : m)
            .forEach(lines::add);
        frameViewData.beacons.stream()
            .map(array -> serializeArray(array))
            .forEach(lines::add);

        lines.add(frameViewData.events.size());
        frameViewData.events.stream()
            .flatMap(
                e -> Stream.of(
                    e.type,
                    e.animData.get(0).start,
                    e.animData.get(0).end,
                    e.playerIdx == null ? "" : e.playerIdx,
                    e.amount == null ? "" : e.amount,
                    e.cellIdx == null ? "" : e.cellIdx,
                    e.targetIdx == null ? "" : e.targetIdx,
                    e.path == null ? "" : serialize(e.path)
                )
            )
            .forEach(lines::add);

        return lines.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(MAIN_SEPARATOR));
    }

    static public String serialize(GlobalViewData globalViewData) {

        List<Object> lines = new ArrayList<>();
        lines.add(globalViewData.cells.size());
        globalViewData.cells.stream().forEach(cellData -> {
            lines.add(
                join(
                    cellData.q,
                    cellData.r,
                    cellData.richness,
                    cellData.type,
                    cellData.ants[0],
                    cellData.ants[1],
                    cellData.owner
                )
            );
        });

        return lines.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(MAIN_SEPARATOR));

    }

    static public <T> String serialize(List<T> list) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(" "));
    }

    static public String serializeArray(int[] list) {
        return Arrays.stream(list).mapToObj(String::valueOf).collect(Collectors.joining(" "));
    }

    static public String join(Object... args) {
        return Stream.of(args)
            .map(String::valueOf)
            .collect(Collectors.joining(" "));
    }

}
