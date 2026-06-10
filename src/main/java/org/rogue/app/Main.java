package org.rogue.app;

import org.rogue.domain.service.GameEngine;
import org.rogue.presentation.presenter.GamePresenter;
import org.rogue.presentation.presenter.MenuPresenter;
import org.rogue.presentation.presenter.MenuResult;
import org.rogue.presentation.presenter.RecordsTablePresenter;
import org.rogue.presentation.view.LanternaGameView;

import java.io.IOException;

import static org.rogue.presentation.presenter.MenuResult.EXIT;
import static org.rogue.presentation.presenter.MenuResult.LOAD;

public class Main {
    /**
     * On Windows, you need to use javaw to start your application
     * or IOException will be thrown while invoking
     * DefaultTerminalFactory.createTerminal(), see #335.
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            LanternaGameView view = new LanternaGameView();
            MenuPresenter menuPresenter = new MenuPresenter(view);
            RecordsTablePresenter records = new RecordsTablePresenter(view);

            MenuResult result = null;
            while (result != EXIT && result != LOAD) {
                result = menuPresenter.showMenu();

                switch (result) {
                    case START:
                        GameEngine gameEngine = new GameEngine();
                        GamePresenter gamePresenter = new GamePresenter(view, gameEngine, records.getTable());
                        gamePresenter.startGame();
                        break;
                    case RECORDS:
                        records.showTable();
                        break;
                    case LOAD:
                        // TODO: загрузка
                        view.close();
                        break;
                    case EXIT:
                        view.close();
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}