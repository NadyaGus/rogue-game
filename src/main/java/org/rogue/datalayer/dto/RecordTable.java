package org.rogue.datalayer.dto;

import org.rogue.domain.entity.game.Statistics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RecordTable {
    private final ArrayList<Statistics> recordTable = new ArrayList<>();

    public void addResult(Statistics result) {
        recordTable.add(result);
    }

    private void sortTable() {
        recordTable.sort(Comparator.comparingInt(Statistics::getGoldCollected).reversed());
    }

    public List<Statistics> getRecordTable() {
        sortTable();
        return recordTable;
    }

    public String getInfo1(int index) {
        Statistics stat = recordTable.get(index);
        return "GOLD:" +  stat.getGoldCollected() +
                " LEVEL:" + stat.getDeepestLevel() +
                " ENEMIES:" + stat.getEnemiesDefeated() +
                " HITS DEALT:" + stat.getHitsDealt() +
                " HITS TAKEN:" + stat.getHitsTaken();
    }
    public String getInfo2(int index) {
        Statistics stat = recordTable.get(index);
        return " FOOD:" + stat.getFoodEaten() +
                " SCROLLS:" + stat.getScrollsUsed() +
                " POTION:" + stat.getPotionUsed() +
                " STEPS:" + stat.getSteps();
    }
}
