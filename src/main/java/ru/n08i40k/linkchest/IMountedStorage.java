package ru.n08i40k.linkchest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public interface IMountedStorage {
    static List<IMountedStorage> LOADED_STORAGES = new ArrayList<>();

    UUID getLinkedControllerUuid();

    void setLinkedControllerUuid(UUID uuid);

    void linkInventory();
    void unlinkInventory(boolean removeFromList);
    void unlinkController();
}