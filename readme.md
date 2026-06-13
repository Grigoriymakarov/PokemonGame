# Project Bugemon 🪲🥊

Bugemon is a strategic combat game inspired by creature capture games.  
Players take on the role of a trainer who assembles a team of Bugemons to face opponents in turn-based battles.
Two game modes are available:
- **Free Combat**, where the player can fight against random opponents.
- The **NO Tower**, where the player navigates through a series of floors from 2 to 9, with several boss battles on its path.
In this mode, XP is earned after each battle, allowing players to level up their Bugemons and increase their stats.

To be the best trainer, player can build teams and save their best ones (or any, really), need to consider their Bugemon's strenght and weaknesses (stats and types),
and mind to use objects.

Gotta beat them all! Bugemon!

### Running the Iteration Executable
The JAR file is provided with its dependencies. To run it, use the following command:
```bash
java -jar infof307-FINAL.jar
```
The application will create a `.bugemondb` directory where local information (teams, etc.) will be saved.
Please keep this folder accessible in the directory from which the JAR is executed, otherwise your data will be reset!

---

# Implemented Features/User stories

## Story 1 – Assemble a Team

Players can assemble a team of 1 to 6 Bugemons via a dedicated interface.

Features:
- Display of available Bugemons (name + sprite).
- Adding a Bugemon to the team.
- Removing a Bugemon from the team.
- Prevention of selecting the same Bugemon multiple times.
- Bugemon information is loaded from the `bugemons.json` file.

---

## Story 2 – Save and Load a Team

Players can save a team by giving it a name and load a previously saved team.

Features:
- Saving a team with a custom name from the team assembly menu.
- Loading a saved team from the "Manage My Teams" menu.
- Saved teams are stored locally in `.bugemondb/savedteams.json`.
- Each player has their own saved teams.

---

## Stories 4 (automatic combat), 5 (combat UI), 6 (damage calculation), 8 (attack effect) and 10 (item effect) – Free Combat

After creating a team, a battle is automatically launched against a randomly generated opposing team.

### Mechanics:
- Each team has the same number of Bugemons.
- The first Bugemon from each team enters the battle.
- In each turn:
  - The player chooses its turn action (attack, item use, switch Bugemon or forfeit)
  - The enemy's Bugemon chooses a random attack from its 3 available attacks.
  - Priority is determined by the Bugemon's initiative stat. If both Bugemons have the same speed, a coin flip determines the order.

#### Damage Calculation:
- The damage dealt by an attack is calculated using the following:
  - Attack factor = (Attacker's Attack Stat + 100) / 100
  - Defense factor = 100 / (Defender's Defense Stat + 100)
  - Attack Power is the base power of the attack.
  - Type Effectiveness is determined by the attack's type and the defender's type(s)
  - Critical factor = 1.5 if the attack is a critical hit, otherwise 1.0 (10% chance for a critical hit)
- Damage = (Attack factor) * (Defense factor) * (Attack Power) * (Type Effectiveness) * (Critical factor)
- HP are updated accordingly

#### K.O. Management:
- When a Bugemon reaches 0 HP, it is removed from the battle.
- On the enemy's side, the next available Bugemon from the team is sent out.
- On the player's side, the player can choose which Bugemon to send out next.
- If a team has no more available Bugemons, the trainer loses the battle.

### Battle Interface:
- Display of active Bugemon sprites.
- Bugemon names.
- Health bars.
- Messages describing combat actions.
- Buttons for player actions (attack, item use, switch Bugemon, forfeit).
  - Indicator of attack type (Pyro/Aqua/Flora/Litho) and predicted effectiveness.
  - Display of available items and their effects when hovering over them.
  - Display of available Bugemons in the player's team when switching, along with their HP.

At the end of the battle, the game displays **VICTORY** or **DEFEAT** and returns to the main menu.

---

## Story 7 – Experience Points and Leveling Up
Bugemons gain experience points (XP) after each victorious battle, based on the number of enemies defeated, the stage (floor and type). It is shared equally among the Bugemons that participated in the battle. 

Each Bugemon starts at level 1. To reach the next level, it must accumulate a certain amount of experience (XP) that increases with each level:
- Level 𝑛 → (𝑛 + 1): (50 + 50(𝑛 − 1)) XP

When a Bugemon reaches the required threshold, it levels up. Upon leveling up:
- Its HP is automatically restored to maximum.
- The player chooses a bonus from 3 randomly proposed options:
  - +20 maximum HP
  - +10 Attack
  - +10 Defense
  - +20 Initiative
  - Randomly generated combinations; the total must equal 10 points, and 1 point corresponds
to 2 HP or 2 Initiative or 1 Attack or 1 Defense (e.g., +5 Attack and +5 Defense)

Bugemon levels are displayed next to their names in all menus and combat screens.


---

## Story 9 – NO Tower Structure

The NO Tower consists of 8 floors (NO2 - NO9). Each floor follows a fixed structure with mandatory battles and a floor boss. A Final Boss awaits at the very end.

Mechanics:
- Structure of each floor: 4 mandatory battles, including one floor boss.
- Opposing teams are randomly generated with the same number of Bugemons as the player's team.
- Bugemon health points are fully restored between each battle.
- Defeating a floor boss unlocks the next floor.
- In case of defeat, the player is returned to the main menu.

Interface:
- Victory screen displaying Bugemon details after each battle.
- Defeat screen with options to retry or return to the main menu.
- Main menu with access to normal combat and the NO Tower.

---

# Technologies Used

- **Java 25**
- **Maven**

---

# Compilation and Execution

## Compile the project
```bash
mvn clean compile
```

## Run the application with Maven
```bash
mvn exec:java
```

## Run unit tests
```bash
mvn test
```

## Create the JAR
```bash
mvn package
```

---

# Project Structure

- **src/main/java**: Main project source code.
- **src/main/resources**: Game data files and resources.
- **src/test/java**: Unit tests.
- **team/**: Project management documents (stories, meetings, planning, etc.).

---

# Team

Project developed by:

- Chakroune Sanae
- Fattah Amal
- Luypaert Alexandre
- Makarov Grigorii
- Meyer Guillaume
- Sarkees Avoo Mariam
- Saxemard Mattiah
- Sylla Faamara
- Yaakoub Salma
- Youssouf Elias
