package ru.n08i40k.linkchest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public interface IChestBlockEntity {
    static List<IChestBlockEntity> LOADED_CHESTS = new ArrayList<>();

    UUID getLinkedControllerUuid();
    void setLinkedControllerUuid(UUID uuid);
    boolean hasLinkedControllerUuid();

    void linkInventory();
    void unlinkInventory(boolean removeFromList);
    void unlinkController();
}