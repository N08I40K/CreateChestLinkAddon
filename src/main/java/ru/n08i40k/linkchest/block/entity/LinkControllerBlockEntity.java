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
        if (!isLoaded)
            return;
        isLoaded = false;

        LOADED_CONTROLLERS.remove(this);

        // Remove chests

        List<IChestBlockEntity> queueToRemoveChest = new ArrayList<>();
        for (IChestBlockEntity chestBlockEntity : IChestBlockEntity.LOADED_CHESTS) {
            if (chestBlockEntity.getLinkedControllerUuid().equals(uuid)) {
                queueToRemoveChest.add(chestBlockEntity);
            }
        }

        for (IChestBlockEntity chestBlockEntity : queueToRemoveChest) {
            if (isBreak)
                chestBlockEntity.unlinkController();
            else
                chestBlockEntity.unlinkInventory(false);
        }

        // Remove storages

        List<IMountedStorage> queueToRemoveStorage = new ArrayList<>();
        for (IMountedStorage storage : IMountedStorage.LOADED_STORAGES) {
            if (storage.getLinkedControllerUuid().equals(uuid)) {
                queueToRemoveStorage.add(storage);
            }
        }

        for (IMountedStorage storage : queueToRemoveStorage) {
            if (isBreak)
                storage.unlinkController();
            else
                storage.unlinkInventory(false);
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

        loadController();
    }

    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items);
        uuid = pTag.getUUID("controller-uuid");

        loadController();
    }

    private void loadController() {
        if (isLoaded) return;
        isLoaded = true;

        if (uuid == null)
            uuid = UUID.randomUUID();


        if (getLevel() != null && getLevel().isClientSide()) return;

        if (!LOADED_CONTROLLERS.contains(this))
            LOADED_CONTROLLERS.add(this);

        IChestBlockEntity.LOADED_CHESTS.removeIf(Objects::isNull);

        for (IChestBlockEntity chestBlockEntity : IChestBlockEntity.LOADED_CHESTS) {
            if (chestBlockEntity.getLinkedControllerUuid().equals(uuid)) {
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
}
