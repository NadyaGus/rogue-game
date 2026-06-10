package org.rogue.presentation.view;

import org.rogue.datalayer.dto.GameStateDto;
import org.rogue.datalayer.dto.RecordTableDto;
import org.rogue.presentation.input.InputCommand;
import org.rogue.presentation.presenter.GamePresenter;

public interface GameView {
    void drawMainMenu(String[] options, int selectedIndex);

    void drawGame(GameStateDto state);

    InputCommand getNextInput(GamePresenter presenter); // блокирующее получение команды

    InputCommand getNextInputFromMenu();

    void close();

    void drawRecordsTable(RecordTableDto tableDto);
}
