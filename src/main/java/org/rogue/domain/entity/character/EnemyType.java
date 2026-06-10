package org.rogue.domain.entity.character;

import org.rogue.domain.entity.modifier.Modifier;
import org.rogue.domain.entity.modifier.skill.*;
import org.rogue.domain.entity.modifier.effect.*;

import java.util.List;

import static org.rogue.domain.entity.character.EnemyParams.*;

public enum EnemyType {
    ZOMBIE(HP_MED, STR_MED, AGL_LOW, HOST_MED),
    VAMPIRE(HP_MED, STR_MED, AGL_MED, HOST_HIGH),
    GHOST(HP_LOW, STR_LOW, AGL_MED, HOST_LOW),
    OGRE(HP_HIGH, STR_HIGH, AGL_LOW, HOST_MED),
    SNAKE_MAGE(HP_MED, STR_MED, AGL_HIGH, HOST_HIGH),
    MIMIC(HP_MED, STR_LOW, AGL_MED, HOST_LOW);

    private final int baseHealth;
    private final int baseStrength;
    private final int baseAgility;
    private final double baseHostility;

    EnemyType(int baseHealth, int baseStrength, int baseAgility, double baseHostility) {
        this.baseHealth = baseHealth;
        this.baseStrength = baseStrength;
        this.baseAgility = baseAgility;
        this.baseHostility = baseHostility;
    }

    /**
     * Возвращает список базовых модификаторов (скиллов) для данного типа врага.
     * Модификаторы создаются с предопределёнными параметрами.
     */
    public List<Modifier> getDefaultModifiers() {
        return switch (this) {
            // пассивный скилл:
            // 25% шанс наложить сон на цель, продолжительностью 1 ход
            case SNAKE_MAGE -> List.of(new SleepStrikeSkill(25, 1));

            // пассивный скилл:
            // 100% шанс при нанесении урона накладывает на себя CounterAttackEffect (отдыхает следующий ход),
            // CounterAttackEffect: 1 контратака при получении урона, если владелец эффекта не отдыхает
            // CounterAttackEffect накладывается как пассивный эффект и исчезает только при начале контратаке(-1)
            case OGRE -> List.of(new CounterAbilitySkill(100, 1, -1));

            case VAMPIRE -> List.of(
                    // пассивный скилл:
                    // 100% шанс нанести дополнительный урон, зависящий от MaxHealth (10%) цели
                    new VampiricClawSkill(100, 10),
                    // временный эффект:
                    // уворачивается от первой атаки (после этого эффект пропадает)
                    new EvasionEffect(-1,1)
            );
            // у остальных врагов по умолчанию нет особых скиллов и эффектов
            default -> List.of();
        };
    }


    public int getBaseHealth() {
        return baseHealth;
    }

    public int getBaseStrength() {
        return baseStrength;
    }

    public int getBaseAgility() {
        return baseAgility;
    }

    public double getBaseHostility() {
        return baseHostility;
    }

}
