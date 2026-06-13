package services;

import models.shared.statistics.Statistics;
import constants.LogicConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;

/**
 * Factory class responsible for generating XP gain bonuses after battles. It creates a list of possible bonuses, including fixed bonuses defined in {@link LogicConstants#FIXED_BONUSES} and randomly generated combinations of stat increases.
 * The number of bonuses generated is limited by {@link LogicConstants#BONUS_NUMBER_CHOICES}, ensuring that players have a variety of options to choose from when leveling up their Bugemon.
 * Each bonus is represented as a {@link Statistics} object, indicating the specific stat increases (HP, attack, defense, initiative) that the player can select to enhance their Bugemon.
 */
public class XPGainBonusFactory {

    private final Random random = new Random();

    /**
     * Generates a random combination of XP gain bonuses by randomly distributing a fixed number of bonus points across the available statistics (HP, attack, defense, initiative).
     * @return a random bonus (Statistics object representing the combination of stat gains)
     */
    private Statistics generateRandomCombination() {
        final Statistics.Stat[] stats = Statistics.Stat.values();
        final Statistics combination = new Statistics(0, 0, 0, 0);
        for (int i = 0; i < LogicConstants.BONUS_MAX_POINTS; i++) {
            final Statistics.Stat stat = stats[random.nextInt(stats.length)];
            final int increaseValue = switch (stat) {
                case HP -> 2;
                case ATTACK -> 1;
                case DEFENSE -> 1;
                case INITIATIVE -> 2;
            };
            combination.increaseStat(stat, increaseValue);
        }
        return combination;
    }

    /**
     * Generates a list of possible XP gain bonuses, including fixed bonuses and at least one random combination.
     * @return a list of possible bonuses to choose from, with a maximum size defined by {@link LogicConstants#BONUS_NUMBER_CHOICES}
     */
    public List<Statistics> generateBonus() {
        final List<Statistics> possibleBonus = new ArrayList<>(LogicConstants.FIXED_BONUSES);

        // We need at least one random bonus
        final int numBonusToGenerate = max(LogicConstants.BONUS_NUMBER_CHOICES - LogicConstants.FIXED_BONUSES.size(), 1);
        for (int i = 0; i<numBonusToGenerate; i++) {
            possibleBonus.add(this.generateRandomCombination());
        }

        Collections.shuffle(possibleBonus);
        return List.copyOf(possibleBonus.subList(0, LogicConstants.BONUS_NUMBER_CHOICES));
    }

}
