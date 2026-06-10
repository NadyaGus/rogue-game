package org.rogue.domain.service.attackService;


import org.rogue.domain.entity.modifier.Modifier;
import org.rogue.domain.entity.character.Character;
import org.rogue.domain.entity.modifier.effect.*;
import org.rogue.domain.entity.modifier.skill.*;

import java.util.ArrayList;
import java.util.List;

//private static final int BASE_HIT_CHANCE = 90;

/*
    Перед началом хода Character, проверяем, есть ли у него SleepEffect/EvasionEffect isExpired() = TRUE, если есть то удаляем их,
    проверяем есть ли у него такие Modifier как: SleepEffect (isExpired() = FALSE), CounterAttackEffect(с restFlag == TRUE).
    Если есть, то пропускаем ход(движение/атака) и:
        - уменьшаем duration у SleepEffect -1 (IF dur==0, то удаляем эффект)
        - переключаем флаг у CounterAttackEffect(restFlag == FALSE), но не удаляем эффект, т.к. он удалится после контратаки, когда на него напали
    Если нет таких Modifier, то:
        - ходим по карте или
        - запускаем CombatEngine.processAttack(attacker, defender)
 */
public class CombatEngine {
    public static void processAttack(Character attacker, Character defender) {
        AttackData data = new AttackData(attacker, defender);

        // Проверяет, уклонился ли защитник от атаки, на основе разницы в ловкости.
        checkEvasion(data);

        // Если из-за разницы в ловкости атака не отменена (!isEvaded()):
        if (!data.isEvaded()) {
            applyAttackerModifiers(data); // (удар вампира, наложение контратаки у огра)
            applyDefenderModifiers(data); // (уворот у вампира)
        }

        // Если из-за модификаторов атака не отменена (!isEvaded()):
        if (!data.isEvaded()) {
            int damage = data.getFinalDamage();
            defender.takeDamage(damage);
            System.out.println(attacker.getName() + " deals " + damage + " damage to the " + defender.getName() + "\n" +
                    defender.getName() + " HP: " + defender.getHealth() + " / " + defender.getMaxHealth() + "\n");
            applyPostDamageEffects(data); // (наложение сна, сброс сна из-за получения урона)
        }
    }


    /**
     * Проверяет, уклонился ли защитник от атаки, на основе разницы в ловкости.
     * <p>
     * Базовый шанс попадания = 90%. Итоговый шанс рассчитывается по формуле:
     * <pre>
     * шанс = 90 + (ловкость атакующего - ловкость защитника)
     * </pre>
     * Полученное значение ограничивается диапазоном [0, 100]. Затем генерируется случайное число от 0 до 100.
     * Если оно превышает итоговый шанс, атака считается промахнувшейся – в объекте {@code data}
     * устанавливается флаг {@code evaded = true}.
     *
     * @param data объект {@link AttackData}, содержащий информацию об атакующем и защитнике,
     *             а также флаг уклонения, который может быть изменён
     */
    private static void checkEvasion(AttackData data) {
//        int chanceOfHitting = BASE_HIT_CHANCE + (data.getAttacker().getAgility() - data.getDefender().getAgility());
        int chanceOfHitting = 90 + (data.getAttacker().getAgility() - data.getDefender().getAgility());
        chanceOfHitting = Math.max(0, Math.min(100, chanceOfHitting));
        int roll = (int) (Math.random() * 100);
        if (roll > chanceOfHitting) {
            data.setEvaded(true);
            System.out.println(data.getDefender().getName() + " dodges the blow using agility.\n");
        }
    }


    // 1. Применяем модификаторы атакующего (активные способности)
    // 1. Применяем модификаторы атакующего (активные способности)
    private static void applyAttackerModifiers(AttackData data) {
        List<Modifier> modifiersCopy = new ArrayList<>(data.getAttacker().getModifiers());

        for (Modifier mod : modifiersCopy) {
            switch (mod) {
                case VampiricClawSkill skill -> handleVampiricClaw(data, skill);
                case CounterAbilitySkill skill -> handleCounterAbility(data, skill);
                default -> { /* другие типы модификаторов игнорируются */ }
            }
        }
    }

    // применяем к атаке доп урон, если прокнул шанс:
    private static void handleVampiricClaw(AttackData data, VampiricClawSkill skill) {
        int roll = (int) (Math.random() * 100);
        if (roll < skill.getChance()) {
            int additionalAttack = data.getDefender().getMaxHealth() * skill.getPercentOfEnemyMaxHealth() / 100;
            data.setFinalDamage(data.getBaseDamage() + additionalAttack);
            System.out.println(data.getAttacker().getName() + " applied " + additionalAttack +
                    " extra damage to his attack using " + skill.getType() + " skill");
        }
    }

    // Накладываем эффект контратаки на атакующего если еще такого эффекта нет и прокнул шанс :
    private static void handleCounterAbility(AttackData data, CounterAbilitySkill skill) {
        int roll = (int) (Math.random() * 100);
        if (roll < skill.getChance()) {
            for (Modifier mod : data.getAttacker().getModifiers()) {
                if (mod instanceof CounterAttackEffect effect) {
                    effect.setRestFlag(true);
                    System.out.println(data.getAttacker().getName() + " already has " + effect.getType() + " so we change the rest-flag to true");
                    return;
                }
            }
            CounterAttackEffect effect = new CounterAttackEffect(skill.getTimeOfCounterAttack(), skill.getNumOfCounterAttack());
            data.getAttacker().addModifier(effect);
            System.out.println(data.getAttacker().getName() + " during attack, get " + effect.getType() +
                    " effect to themselves for the next " + skill.getNumOfCounterAttack() + " attacks." +
                    " He will rest for the next turn.");
        }
    }


    // 2. Применяем модификаторы защитника (эффекты защиты/уклонения)
    // 2. Применяем модификаторы защитника (эффекты защиты/уклонения)
    private static void applyDefenderModifiers(AttackData data) {
        List<Modifier> modifiersCopy = new ArrayList<>(data.getDefender().getModifiers());
        for (Modifier mod : modifiersCopy) {
            switch (mod) {
                case EvasionEffect effect -> handleEvasionEffect(data, effect);
                default -> {
                }
            }
        }
    }

    // Отменяем атаку, т.к. защитник увернулся от атаки за счет EvasionEffect
    private static void handleEvasionEffect(AttackData data, EvasionEffect effect) {
        // todo: ДОБАВИТЬ КОРРЕКТНОЕ УДАЛЕНИЕ ЭФФЕКТА!
        if (!effect.isExpired()) {
            data.setEvaded(true);
            effect.reduceDodge();
            System.out.println(data.getDefender().getName() + " dodges the blow using " + effect.getType() + " effect\n");
        }
    }


    // 3. Применяем модификаторы только после получения урона (успешная атака)
    // 3. Применяем модификаторы только после получения урона (успешная атака)
    private static void applyPostDamageEffects(AttackData data) {
        // обработка модификаторов у защитника:
        if (data.getDefender().isAlive()) {
            for (Modifier mod : data.getDefender().getModifiers()) {
                switch (mod) {
                    case SleepEffect effect -> handleSleepEffect(data, effect);
                    case CounterAttackEffect effect -> handleCounterAttackEffect(data, effect);
                    default -> { /* другие типы модификаторов игнорируются */ }
                }
            }
        }
        // обработка модификаторов у атакующего:
        if (data.getAttacker().isAlive()) {
            for (Modifier mod : data.getAttacker().getModifiers()) {
                switch (mod) {
                    case SleepStrikeSkill skill -> handleSleepStrike(data, skill);
                    default -> { /* другие типы модификаторов игнорируются */ }
                }
            }
        }
    }

    // Сбрасываем слип у защитника:
    private static void handleSleepEffect(AttackData data, SleepEffect effect) {
        effect.setDuration(0);
        System.out.println(data.getDefender().getName() + " wakes up from the hit and loses " + effect.getType() + " effect\n");
    }

    // Защитник контратакует атакующего:
    private static void handleCounterAttackEffect(AttackData data, CounterAttackEffect effect) {
        if (!effect.isRestFlag()) {
            Character nowCounterAttacker = data.getDefender();
            Character nowDefender = data.getAttacker();
            int damage = nowCounterAttacker.getStrength();
            nowDefender.takeDamage(damage);
            effect.reduceNumOfCounterAttack();
            System.out.println(nowCounterAttacker.getName() + " kontratakoval " + nowDefender.getName() + " i nanes " +
                    damage + " damage.");
        }
    }

    // Накладываем слип на защитника, если прокнул шанс:
    private static void handleSleepStrike(AttackData data, SleepStrikeSkill skill) {
        int roll = (int) (Math.random() * 100);
        if (roll < skill.getChance()) {
            SleepEffect effect = new SleepEffect(skill.getSleepDuration());
            data.getDefender().addModifier(effect);
            System.out.println(data.getDefender().getName() + " fell asleep for " + skill.getSleepDuration() + " steps\n");
        }
    }


}



