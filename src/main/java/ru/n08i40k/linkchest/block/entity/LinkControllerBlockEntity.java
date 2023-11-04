package ru.n08i40k.linkchest.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ru.n08i40k.linkchest.IChestBlockEntity;
import ru.n08i40k.linkchest.IMountedStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LinkControllerBlockEntity extends RandomizableContainerBlockEntity {
    private static final int CONTAINER_SIZE = 27;
    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    private UUID uuid;

    public static final List<LinkControllerBlockEntity> LOADED_CONTROLLERS = new ArrayList<>();

    private boolean isLoaded = false;

    public LinkControllerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.LINK_CONTROLLER.get(), pPos, pBlockState);
    }

    public void unlinkAll(boolean isBreak) {
        System.out.println("Removing controller from list!");

        LOADED_CONTROLLERS.remove(this);

        // Remove chests

        System.out.println("Removing chests!");

        List<IChestBlockEntity> queueToRemoveChest = new ArrayList<>();
        for (IChestBlockEntity chestBlockEntity : IChestBlockEntity.LOADED_CHESTS) {
            if (chestBlockEntity.getLinkedControllerUuid().equals(uuid)) {
                queueToRemoveChest.add(chestBlockEntity);
            }
        }

        for (IChestBlockEntity chestBlockEntity : queueToRemoveChest) {
            if (isBreak)
                chestBlockEntity.unlinkInventory(false);
            else
                chestBlockEntity.unlinkController();
        }

        // Remove storages

        System.out.println("Removing storages!");

        List<IMountedStorage> queueToRemoveStorage = new ArrayList<>();
        for (IMountedStorage storage : IMountedStorage.LOADED_STORAGES) {
            if (storage.getLinkedControllerUuid().equals(uuid)) {
                queueToRemoveStorage.add(storage);
            }
        }

        for (IMountedStorage storage : queueToRemoveStorage) {
            if (isBreak)
                storage.unlinkInventory(false);
            else
                storage.unlinkController();
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        unlinkAll(true);
    }



    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();

        if (getLevel() != null && getLevel().isClientSide()) return;

        unlinkAll(false);
    }

    protected Component getDefaultName() {
        return Component.translatable("block.linkchest.link_controller");
    }
    protected AbstractContainerMenu createMenu(int pId, Inventory pPlayer) {
        return ChestMenu.threeRows(pId, pPlayer, this);
    }

    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        ContainerHelper.saveAllItems(pTag, this.items);
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        pTag.putUUID("controller-uuid", uuid);
    }

    @Override
    public void onLoad() {
        if (getLevel() != null && getLevel().isClientSide()) return;
        System.out.println("Link Controller onLoad function");
        loadController();
    }

    public void load(CompoundTag pTag) {
        System.out.println("Link Controller LOAD");
        super.load(pTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items);
        uuid = pTag.getUUID("controller-uuid");

        System.out.println("Link Controller load function");
        loadController();
    }

    private void loadController() {
        if (isLoaded) return;
        isLoaded = true;

        if (uuid == null) {
            System.out.println("Link Controller has no uuid! Generating...");
            uuid = UUID.randomUUID();
        }

        if (getLevel() != null && getLevel().isClientSide()) return;

        System.out.println("Load has been called! (link controller)");

        if (!LOADED_CONTROLLERS.contains(this)) {
            System.out.println("Added to loaded controllers!");
            LOADED_CONTROLLERS.add(this);
        } else {
            System.out.println("Already in loaded controllers!");
        }

        System.out.println("Size of loaded chests: " + IChestBlockEntity.LOADED_CHESTS.size());

        IChestBlockEntity.LOADED_CHESTS.removeIf(Objects::isNull);

        for (IChestBlockEntity chestBlockEntity : IChestBlockEntity.LOADED_CHESTS) {
            if (chestBlockEntity.getLinkedControllerUuid().equals(uuid)) {
                System.out.println("Required chest has been founded!");
                System.out.println("Calling inventory link...");
                chestBlockEntity.linkInventory();
            }
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }
    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }
    @Override
    protected void setItems(NonNullList<ItemStack> pItemStacks) {
        items = pItemStacks;
    }

    public NonNullList<ItemStack> getInventory() {
        return items;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, LinkControllerBlockEntity linkControllerBlockEntity) {
        if (level.isClientSide()) {
            return;
        }

    }
}
