package models.shared;

/**
 * Interface for entities that have a unique identifier.
 *
 * <p>Classes implementing this interface must provide a unique ID for each instance,
 * enabling consistent identification and retrieval of entities across the application.</p>
 *
 * @see repositories.Repository
 */
public interface Identifiable {
    /**
     * Returns the unique identifier for this entity.
     *
     * @return the unique ID as a String
     */
    String id();
}
