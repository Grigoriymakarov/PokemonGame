package dto;

/**
 * Data Transfer Object interface for exposing statistics without allowing modifications.
 *
 * <p>This interface defines a read-only contract for accessing game statistics such as
 * HP, Attack, Defense, Sp. Attack, Sp. Defense, and Speed. Implementations should
 * not expose methods to modify these statistics directly.</p>
 *
 * @see models.shared.statistics.Statistics
 */
public interface StatisticsDTO {
}
