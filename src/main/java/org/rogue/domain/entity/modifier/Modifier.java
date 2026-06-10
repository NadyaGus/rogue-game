package org.rogue.domain.entity.modifier;

public abstract class Modifier {
    private final ModifierType type;
    private int duration;   // длительность в ходах ("-1" - постоянно, пассивный скилл)

    public Modifier(ModifierType type, int duration) {
        this.type = type;
        this.duration = duration;
    }

    public ModifierType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void reduceDuration() {
        if (duration > 0) duration--;
    }

    public boolean isExpired() {
        return duration == 0;
    }

}

