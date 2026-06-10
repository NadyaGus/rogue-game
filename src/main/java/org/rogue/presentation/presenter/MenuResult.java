package org.rogue.presentation.presenter;

public enum MenuResult {
    START("START"), RECORDS("RECORDS TABLE"),
    LOAD("LOAD GAME"), EXIT("QUIT GAME");
    private final String text;

    MenuResult(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
