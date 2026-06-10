package org.rogue.presentation.presenter;

import org.rogue.presentation.input.InputCommand;
import org.rogue.presentation.view.GameView;

public class MenuPresenter {
    private final GameView view;
    public final String[] options = {MenuResult.START.getText(), MenuResult.RECORDS.getText(), MenuResult.EXIT.getText()};

    public MenuPresenter(GameView view) {
        this.view = view;
    }

    public MenuResult showMenu() {
        int selected = 0;

        while (true) {
            view.drawMainMenu(options, selected);
            InputCommand cmd = view.getNextInputFromMenu();

            if (cmd == InputCommand.UP) {
                selected = (selected - 1 + options.length) % options.length;
            } else if (cmd == InputCommand.DOWN) {
                selected = (selected + 1) % options.length;
            } else if (cmd == InputCommand.SELECT) {
                switch (selected) {
                    case 0:
                        return MenuResult.START;
                    case 1:
                        return MenuResult.RECORDS;
                    case 2:
                        return MenuResult.LOAD;
                    case 3:
                        return MenuResult.EXIT;
                }
            } else if (cmd == InputCommand.QUIT) {
                return MenuResult.EXIT;
            }
        }
    }
}
