1. Voici le code du projet : 
```java
public BugemonService() { this.repository = BugemonRepository.getInstance(); }
```

Cette ligne viole un principe important de Génie Logiciel : lequel ? Expliquez pourquoi.

2. Quel est l'intérêt des builders dans votre architecture (`Attack.Builder`, ...) ? 

3. À l'itération précédente, vous utilisiez des listeners associés à chaque contrôleur. Ceux-ci ont été retirés dans la version finale. Pourquoi ?

4. Dans `BugemonService`, le code fait `throw new BugemonNotFoundException(id, e)` après avoir capturé une `ElementNotFoundException` :

```java
    public TrainerBugemon createTrainerBugemon(final String id) throws BugemonNotFoundException {
        final BugemonSpecie template;
        try {
            template = this.repository.findById(id);
        } catch (ElementNotFoundException e) {
            throw new BugemonNotFoundException(id, e);
        }

        return new TrainerBugemon.Builder().withSpecie(template).build();
    }
```
À quoi servent les 2 paramètres du constructeur de `new BugemonNotFoundException(id, e)` ? Pourquoi ne pas laisser `ElementNotFoundException` remonter telle quelle ?

5. Expliquez chaque partie la ligne suivante : `abstract class BaseController<L extends BaseView.ViewListener, V extends BaseView<L, ?>>`.

6. Le projet utilise à la fois le Template Method (`BaseController`) et la Strategy (comme `RandomOffenseStrat`). Expliquez la différence fondamentale entre ces deux patterns. Dans quel cas choisiriez-vous l'un plutôt que l'autre ?

7. `CombatStateDTO` est une interface read-only. Pourquoi exposer une interface plutôt que l'objet domaine directement à la vue ? 

```java
package dto;

import models.combat.event.CombatEvent;
import models.shared.Attack;
import models.shared.Type;

import java.util.List;

public interface CombatStateDTO {
    String getPlayerName();

    String getEnemyName();

    int getPlayerHp();

    int getEnemyHp();

    int getEnemyHpMax();

    List<CombatEvent> getEvents();

    int getPlayerHpMax();

    String getPlayerSpritePath();

    String getEnemySpritePath();

    String getFloorLabel();

    String getStepLabel();

    boolean isBossFight();

    int getPlayerLevel();

    int getEnemyLevel();

    Type getPlayerType();

    Type getEnemyType();

    List<Attack> getEnemyBugemonCurrentAttacks();
}
```

8. `BugemonSpecie` et `TrainerBugemon` sont complémentaires. Expliquez ce pattern, appelé Prototype ou Flyweight. Quel est le but de cette distinction ? Pourquoi ne pas faire une seule classe `Bugemon` ?

```java
package models.shared.bugemon;

import com.google.gson.annotations.SerializedName;
import models.shared.Attack;
import models.shared.Identifiable;
import models.shared.Type;
import models.shared.statistics.Statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BugemonSpecie implements Identifiable {

    private String id;

    @SerializedName("nom")
    private String name;

    private Type type;

    @SerializedName("stats")
    private Statistics baseStats;

    private String sprite;

    private boolean starter;

    @SerializedName("attaques")
    private List<Attack> attacks;

    private BugemonSpecie(final Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.baseStats = builder.baseStats;
        this.sprite = builder.sprite;
        this.starter = builder.starter;
        this.attacks = builder.attacks != null ? new ArrayList<>(builder.attacks) : new ArrayList<>();
    }

    public BugemonSpecie() {}

    public static class Builder {
        private String id;
        private String name;
        private Type type;
        private Statistics baseStats;
        private String sprite;
        private boolean starter;
        private List<Attack> attacks;

        public Builder id(final String id) {
            this.id = id;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder type(final Type type) {
            this.type = type;
            return this;
        }

        public Builder baseStats(final Statistics baseStats) {
            this.baseStats = baseStats;
            return this;
        }

        public Builder sprite(final String sprite) {
            this.sprite = sprite;
            return this;
        }

        public Builder starter(final boolean starter) {
            this.starter = starter;
            return this;
        }

        public Builder attacks(final List<Attack> attacks) {
            this.attacks = attacks != null ? new ArrayList<>(attacks) : new ArrayList<>();
            return this;
        }

        public BugemonSpecie build() {
            if (this.id == null)       throw new NullPointerException("id is required");
            if (this.name == null)     throw new NullPointerException("name is required");
            if (this.type == null)     throw new NullPointerException("type is required");
            if (this.baseStats == null) throw new NullPointerException("baseStats is required");
            return new BugemonSpecie(this);
        }
    }

    @Override
    public String id() { return this.id; }

    public String getName() { return this.name; }

    public Type getType() { return this.type; }

    public Statistics getBaseStats() { return this.baseStats; }

    public boolean getStarter() { return this.starter; }

    public List<Attack> getPossibleAttacks() {
        return Collections.unmodifiableList(this.attacks);
    }

    public String getSprite() { return this.sprite; }

    public int getStat(final Statistics.Stat stat) {
        return this.baseStats.get(stat);
    }
}
```

```java
package models.shared.bugemon;

import dto.TrainerBugemonDTO;
import models.combat.CombatState;
import models.shared.Attack;
import models.shared.Type;
import models.shared.statistics.Statistics;
import models.shared.statistics.StatsModifier;
import models.shared.statistics.TemporaryStatsModifier;
import models.shared.xp.XP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TrainerBugemon implements TrainerBugemonDTO {
    private BugemonSpecie specie;
    private Optional<String> nickname;
    private int currentHP;
    private List<Attack> currentAttacks;
    private XP xp;
    private boolean isAlive;
    private StatsModifier levelStatsModifier;
    private StatsModifier combatStatsModifier;
    private List<TemporaryStatsModifier> temporaryStatsModifiers;

    public static class Builder {
        private BugemonSpecie specie;
        private String nickname;
        private Integer currentHP;
        private List<Attack> currentAttacks;
        private XP xp = new XP();
        private boolean isAlive = true;
        private StatsModifier combatStatsModifier;
        private List<TemporaryStatsModifier> temporaryStatsModifiers;

        public Builder withSpecie(final BugemonSpecie specie) {
            this.specie = specie;
            return this;
        }

        public Builder withNickname(final String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder withCurrentHP(final int currentHP) {
            this.currentHP = currentHP;
            return this;
        }

        public Builder withCurrentAttacks(final List<Attack> currentAttacks) {
            this.currentAttacks = new ArrayList<>(currentAttacks);
            return this;
        }

        public Builder withXPProgression(final XP xp) {
            this.xp = new XP(xp);
            return this;
        }

        public Builder withAlive(final boolean isAlive) {
            this.isAlive = isAlive;
            return this;
        }

        public Builder withCombatStatsModifier(final StatsModifier combatStatsModifier) {
            this.combatStatsModifier = combatStatsModifier;
            return this;
        }

        public Builder withTemporaryStatsModifiers(final List<TemporaryStatsModifier> temporaryStatsModifiers) {
            this.temporaryStatsModifiers = new ArrayList<>(temporaryStatsModifiers);
            return this;
        }

        private void validateHPModifiers() {
            if (this.combatStatsModifier != null && this.combatStatsModifier.get(Statistics.Stat.HP) != 0) {
                throw new AmbiguousHPModifierException(
                    "Combat stat modifier contains HP modifications."
                );
            }

            if (this.temporaryStatsModifiers != null) {
                for (final TemporaryStatsModifier modifier : this.temporaryStatsModifiers) {
                    if (modifier.get(Statistics.Stat.HP) != 0) {
                        throw new AmbiguousHPModifierException(
                            "Temporary stat modifier contains HP modifications."
                        );
                    }
                }
            }
        }

        public TrainerBugemon build() {
            validateHPModifiers();

            final TrainerBugemon bugemon = new TrainerBugemon();
            bugemon.specie = this.specie;
            bugemon.nickname = Optional.ofNullable(this.nickname);
            bugemon.currentAttacks = (this.currentAttacks != null)
                    ? new ArrayList<>(this.currentAttacks)
                    : new ArrayList<>(this.specie.getPossibleAttacks());
            bugemon.xp = this.xp;
            bugemon.isAlive = this.isAlive;
            bugemon.levelStatsModifier = new StatsModifier();
            bugemon.combatStatsModifier = (this.combatStatsModifier != null) ? this.combatStatsModifier : new StatsModifier();
            bugemon.temporaryStatsModifiers = (this.temporaryStatsModifiers != null)
                    ? new ArrayList<>(this.temporaryStatsModifiers)
                    : new ArrayList<>();

            final int maxHP = this.specie.getStat(Statistics.Stat.HP);
            bugemon.currentHP = (this.currentHP != null) ? this.currentHP : maxHP;

            return bugemon;
        }
    }

    public int getCurrentHP() {
        return this.currentHP;
    }

    public int getLevel() {
        return this.xp.getLevel();
    }

    public int getXP() {
        return this.xp.getCurrentXP();
    }

    public int getXPToNextLevel() {
        return this.xp.getXPToNextLevel();
    }

    public String getName() {
        return this.specie.getName();
    }

    public String getDisplayName() {
        return this.nickname.orElse(this.specie.getName());
    }

    public List<Attack> getCurrentAttacks() {
        return Collections.unmodifiableList(this.currentAttacks);
    }

    public BugemonSpecie getSpecie() {
        return this.specie;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public int getMaxHp() {
        return this.getStatistics().getHp();
    }

    public String getSprite() {
        return this.specie.getSprite();
    }

    public Statistics getStatistics() {
        final Statistics stats = new Statistics(this.specie.getBaseStats());

        stats.add(this.levelStatsModifier);
        stats.add(this.combatStatsModifier);
        for (final TemporaryStatsModifier modifier : this.temporaryStatsModifiers) {
            stats.add(modifier);
        }

        return stats;
    }

    public int getStat(Statistics.Stat stat) {
        return this.getStatistics().get(stat);
    }

    public boolean hasAttack(Attack attack) {
        return this.currentAttacks.contains(attack);
    }

    public void setAlive(final boolean alive) {
        this.isAlive = alive;
        this.currentHP = (this.isAlive) ? this.getStatistics().getHp() : 0;
    }

    public void applyDamage(final int damage) {
        int effectiveDamage = damage;
        if (effectiveDamage >= this.currentHP) {
            effectiveDamage = this.currentHP;
        }
        this.currentHP -= effectiveDamage;
        if (this.currentHP <= 0) {
            this.isAlive = false;
        }
    }

    public void heal(final int healAmount) {
        this.currentHP += healAmount;
        final int maxHp = this.getMaxHp();
        if (this.currentHP > maxHp) {
            this.currentHP = maxHp;
        }
    }

    public void resetMaluses() {
        this.combatStatsModifier.resetMaluses();
    }

    public void resetForCombat() {
        this.currentHP = this.getMaxHp();
        this.isAlive = true;
        this.combatStatsModifier = new StatsModifier();
        this.resetTemporaryModifiers();
    }

    public void increaseCombatStat(final Statistics.Stat stat, final int modifier) {
        this.combatStatsModifier.increaseStat(stat, modifier);
    }

    public void addLevelStats(final Statistics stat) {
        this.levelStatsModifier.add(stat);
    }

    public void addTemporaryModifier(final TemporaryStatsModifier modifier) {
        this.temporaryStatsModifiers.add(modifier);
    }

    public void registerTimeTick() {
        for (final TemporaryStatsModifier modifier : this.temporaryStatsModifiers) {
            modifier.shortenRemainingTime(1);
        }
        this.temporaryStatsModifiers.removeIf(m -> !m.isActive());
    }

    public void resetTemporaryModifiers() {
        this.temporaryStatsModifiers = new ArrayList<>();
    }

    public void resetProgression() {
        this.xp = new XP();
        this.levelStatsModifier = new StatsModifier();
        this.resetForCombat();
    }

    public void gainXP(final int amount) {
        final int levelBefore = this.getLevel();
        this.xp.addXP(amount);
        final int levelAfter = this.getLevel();

        if (levelAfter > levelBefore) {
            this.currentHP = this.getMaxHp();
        }
    }

    public Type getSpecieType() {
        return this.getSpecie().getType();
    }

    public boolean isSpecie(BugemonSpecie specie) {
        return this.specie.equals(specie);
    }
}
```


9. Dans le projet, `BugemonServiceTest` charge réellement les données JSON via `BugemonRepository.getInstance().buildDataBase()`. Listez les problèmes que cela engendre. Proposez une alternative avec Mockito.

```java
package services;

import models.exceptions.BugemonNotFoundException;
import models.shared.bugemon.TrainerBugemon;
import org.junit.Before;
import org.junit.Test;
import repositories.AttackRepository;
import repositories.BugemonRepository;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

public class BugemonServiceTest {

    private BugemonService bugemonService;

    private static final String ID = "florachu";

    @Before
    public void setUp() throws IOException {
        BugemonRepository.getInstance().buildDataBase();

        bugemonService = new BugemonService();
    }

    @Test
    public void createTrainerBugemon_validId_returnsNonNullBugemon() throws BugemonNotFoundException {
        TrainerBugemon bugemon = bugemonService.createTrainerBugemon(ID);
        assertNotNull("The created Bugemon should not be null", bugemon);
    }

    @Test
    public void createTrainerBugemon_twoCalls_returnDifferentInstances() throws BugemonNotFoundException {
        TrainerBugemon first = bugemonService.createTrainerBugemon(ID);
        TrainerBugemon second = bugemonService.createTrainerBugemon(ID);

        assertNotSame("Two calls should return different instances", first, second);
    }
}
```

10. Le projet définit 17 exceptions métier spécifiques au lieu d'utiliser `IllegalArgumentException`. Quel est l'intérêt de `TeamFullException` versus `IllegalStateException("Team is full")` ? Quand une exception `Exception` est-elle préférable à une exception `RuntimeException` ?

