package org.rogue.presentation.presenter;

import org.rogue.datalayer.dto.RecordTable;
import org.rogue.datalayer.dto.RecordTableDto;
import org.rogue.presentation.input.InputCommand;
import org.rogue.presentation.view.GameView;

public class RecordsTablePresenter {
    private final GameView view;
    private final RecordTable table;

    public RecordsTablePresenter(GameView view) {
        this.view = view;
        this.table = new RecordTable();
    }

    public RecordTable getTable() {
        return table;
    }

    public void showTable() {
        RecordTableDto dto = new RecordTableDto(table);
        view.drawRecordsTable(dto);

        while (true) {
            InputCommand cmd = view.getNextInputFromMenu();
            if (cmd == InputCommand.QUIT) {
                break;
            }
        }
    }
}
