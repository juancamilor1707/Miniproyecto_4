package com.example.miniproyecto4.model.GameSave;

/**
 * Interface defining serialization and deserialization operations for game data.
 * Provides methods to persist game state to files and restore it.
 */
public interface ISerializer {

    /**
     * Serializes game data to a file.
     * Writes the game state to persistent storage.
     *
     * @param data the game data to serialize
     * @param filename the name of the file to write to
     */
    void serialize(SerializableGameData data, String filename);

    /**
     * Deserializes game data from a file.
     * Reads and restores the game state from persistent storage.
     *
     * @param filename the name of the file to read from
     * @return the deserialized game data, or null if deserialization fails
     */
    SerializableGameData deserialize(String filename);
}