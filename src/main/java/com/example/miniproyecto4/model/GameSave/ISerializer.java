package com.example.miniproyecto4.model.GameSave;

public interface ISerializer {

    void serialize(SerializableGameData data, String filename);

    SerializableGameData deserialize(String filename);
}