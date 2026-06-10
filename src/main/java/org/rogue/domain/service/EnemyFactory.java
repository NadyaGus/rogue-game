package org.rogue.domain.service;

import org.rogue.domain.entity.character.*;
import org.rogue.domain.entity.modifier.Modifier;

import java.awt.*;
import java.util.Random;

/**
 * Фабрика для создания врагов с учётом текущего уровня-сложности (difficult) подземелья.
 * Настройка характеристик масштабирования.
 */
public class EnemyFactory {
    private static final char[] DISGUISE_SYMBOLS = {'F', 'P', 'S', 'W'}; // Еда, зелье, свиток, оружие
    private static final Random RANDOM = new Random();
    private static final EnemyType[] ENEMY_TYPES = EnemyType.values();

    private EnemyFactory() {
    }

    public static Enemy createRandomEnemy(Point spawnPoint, int difficult) {
        EnemyType type = ENEMY_TYPES[RANDOM.nextInt(ENEMY_TYPES.length)];
        return createEnemy(type, spawnPoint, difficult);
//        return createEnemy(EnemyType.MIMIC, spawnPoint, difficult);
    }

    public static Enemy createEnemy(EnemyType type, Point spawnPoint, int difficult) {
        int health = scaleHealth(type.getBaseHealth(), difficult);
        int strength = scaleStrength(type.getBaseStrength(), difficult);
        int agility = scaleAgility(type.getBaseAgility(), difficult);
        double hostility = scaleHostility(type.getBaseHostility());

        Enemy enemy = switch (type) {
            case ZOMBIE -> new Zombie(health, strength, agility, spawnPoint, hostility);
            case VAMPIRE -> new Vampire(health, strength, agility, spawnPoint, hostility);
            case GHOST -> new Ghost(health, strength, agility, spawnPoint, hostility);
            case OGRE -> new Ogre(health, strength, agility, spawnPoint, hostility);
            case SNAKE_MAGE -> new SnakeMage(health, strength, agility, spawnPoint, hostility);
            case MIMIC -> {
                char disguise = DISGUISE_SYMBOLS[RANDOM.nextInt(DISGUISE_SYMBOLS.length)];
                yield new Mimic(health, strength, agility, spawnPoint, hostility, disguise);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип врага: " + type);
        };
        // Добавляем базовые модификаторы (скиллы, эффекты)
        for (Modifier mod : type.getDefaultModifiers()) {
            enemy.addModifier(mod);
        }
        return enemy;
    }


    private static int scaleHealth(int base, int difficult) {
        return base + (difficult - 1) * 2;
    }

    private static int scaleStrength(int base, int difficult) {
        return base + (difficult - 1);
    }

    private static int scaleAgility(int base, int difficult) {
        return base + (difficult - 1);
    }

    private static double scaleHostility(double base) {
        return base;
    }
}
