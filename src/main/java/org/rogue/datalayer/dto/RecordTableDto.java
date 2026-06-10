package org.rogue.datalayer.dto;

import org.rogue.domain.entity.game.Statistics;

import java.util.ArrayList;
import java.util.List;

public class RecordTableDto {
    public final List<String> table;

    public RecordTableDto(RecordTable table) {
        List<String> tableStr = new ArrayList<>();
        List<Statistics> records = table.getRecordTable();

        for (int i = 0; i < records.size(); i++) {
            tableStr.add(table.getInfo1(i));
            tableStr.add(table.getInfo2(i));
        }

        this.table = tableStr;
    }

}
