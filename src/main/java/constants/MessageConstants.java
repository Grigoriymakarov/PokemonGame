package constants;

/**
 * Centralized error and informative messages.
 */
public final class MessageConstants {
    private MessageConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Combat Errors
    public static final String ERROR_KO_SWITCH = "Votre Bugémon est K.O. Vous devez d'abord choisir un remplaçant !";
    public static final String ERROR_FORFEIT_KO = "Vous devez d'abord choisir un remplaçant pour votre Bugémon K.O. !";
    public static final String ERROR_UNKNOWN_ATTACK = "Attaque inconnue : %s";
    public static final String ERROR_REWARD_CONTROLLER = "Erreur lors de la création du contrôleur de récompense";

    // Inventory Errors
    public static final String ERROR_NO_ITEMS = "Aucun objet disponible";
    public static final String ERROR_LOAD_INVENTORY = "Impossible de charger l'inventaire";

    // Team Selection Errors
    public static final String ERROR_SAVE_TEAM = "Erreur lors de la sauvegarde";
    public static final String ERROR_LOAD_TEAMS = "Erreur lors du chargement des équipes";
    public static final String ERROR_LOAD_TEAM = "Impossible de charger l'équipe sélectionnée";
    public static final String ERROR_INVALID_TEAM_NAME = "Nom d'équipe invalide";
    public static final String ERROR_TEAM_FULL = "Impossible d'ajouter %s. L'équipe est pleine (max 6)";
    public static final String ERROR_TEAM_ALREADY_HAS_SPECIE = "Impossible d'ajouter %s. L'équipe contient déjà un Bugémon de cette espèce.";
    public static final String ERROR_CANNOT_ADD_BUGEMON_TO_TEAM = "Impossible d'ajouter %s à l'équipe.";
    public static final String ERROR_REMOVE_BUGEMON = "Impossible de retirer %s de l'équipe.";
    public static final String ERROR_TEAM_EMPTY = "Vous devez avoir au moins 1 Bugémon dans votre équipe !";
    public static final String ERROR_TEAM_INVALID = "L'équipe n'est pas valide";
    public static final String ERROR_TEAM_ILLEGAL_NAME = " (nom illégal)";
    public static final String ERROR_TEAM_NAME_ALREADY_USED = " (ce nom est déjà utilisé)";
    public static final String ERROR_TEAM_EMPTY_PARENTHESIZED = " (l'équipe doit contenir au moins 1 Bugémon)";

    // Other Errors
    public static final String ERROR_NAVIGATION = "Navigation error";
    public static final String ERROR_NO_MODE_SELECTED = "Tu dois choisir un mode avant de pouvoir jouer !";
    public static final String ERROR_TEAM_SERVICE_LOAD = "Impossible d'initialiser le service d'équipe";
    public static final String ERROR_SPRITE_LOAD = "Sprite image could not be loaded";

    // Combat Logs
    public static final String LOG_USE_ACTION = "%s utilise %s !";
    public static final String LOG_SUPER_EFFECTIVE = "C'est super efficace !";
    public static final String LOG_NOT_VERY_EFFECTIVE = "C'est peu efficace...";
    public static final String LOG_CRITICAL_HIT = "Coup critique !";
    public static final String LOG_DAMAGE_DEALT = "%s perd %d PV.";
    public static final String LOG_SWITCH = "%s se retire. %s entre en combat !";
    public static final String LOG_KO = "%s est K.O. !";
    public static final String LOG_SWITCH_ENTER = "%s est entré en combat !";

    // Item Effects
    public static final String LOG_ITEM_HEAL = "%s récupère %d PV.";
    public static final String LOG_ITEM_STAT_BOOST    = "%s augmente %s de %d.";
    public static final String LOG_ITEM_STAT_DECREASE = "%s diminue %s de %d.";
    public static final String LOG_ITEM_RESET_MALUS = "%s se sent mieux !";

    // Bonus Formatting
    public static final String BONUS_HP = "+%d PV ";
    public static final String BONUS_ATK = "+%d ATK ";
    public static final String BONUS_DEF = "+%d DEF ";
    public static final String BONUS_INIT = "+%d INIT ";
}
