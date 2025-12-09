package com.example.miniproyecto4.model.GameSave;

import java.io.*;

/**
 * Implements serialization and deserialization of game data using Java object streams.
 * Provides functionality to persist game state to binary files.
 */
public class GameSerializer implements ISerializer {

    /**
     * Serializes game data to a file using object output stream.
     * Writes the game state in binary format to the specified file.
     *
     * @param data the game data to serialize
     * @param filename the name of the file to write to
     */
    @Override
    public void serialize(SerializableGameData data, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializes game data from a file using object input stream.
     * Reads the game state from binary format in the specified file.
     *
     * @param filename the name of the file to read from
     * @return the deserialized game data, or null if deserialization fails
     */
    @Override
    public SerializableGameData deserialize(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (SerializableGameData) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}