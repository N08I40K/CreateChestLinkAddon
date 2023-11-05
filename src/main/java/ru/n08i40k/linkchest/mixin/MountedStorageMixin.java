package ru.n08i40k.linkchest.mixin;

import com.simibubi.create.content.contraptions.MountedStorage;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.n08i40k.linkchest.IChestBlockEntity;
import ru.n08i40k.linkchest.IMountedStorage;
import ru.n08i40k.linkchest.block.entity.LinkControllerBlockEntity;

import java.util.Objects;
import java.util.UUID;

@Mixin(MountedStorage.class)
public class MountedStorageMixin implements IMountedStorage {
    @Unique
    private UUID linkedControllerUuid = null;

    @Shadow(remap = false)
    private BlockEntity blockEntity;

    @Shadow(remap = false)
    ItemStackHandler handler;


    // Interface functions

    public UUID getLinkedControllerUuid() {
        return linkedControllerUuid;
    }

    public void setLinkedControllerUuid(UUID uuid) {
        this.linkedControllerUuid = uuid;
    }

    private boolean hasLinkedControllerUuid() { return linkedControllerUuid != null; }

    @Override
    public void linkInventory() {
        if (!LOADED_STORAGES.contains(this))
            LOADED_STORAGES.add(this);

        LinkControllerBlockEntity.LOADED_CONTROLLERS.removeIf(Objects::isNull);

        for (LinkControllerBlockEntity linkControllerBlockEntity : LinkControllerBlockEntity.LOADED_CONTROLLERS) {
            if (linkControllerBlockEntity.getUuid().equals(linkedControllerUuid)) {
                ((ItemStackHandlerAccessor)this.handler).setStacks(linkControllerBlockEntity.getInventory());
                break;
            }
        }
    }

    @Override
    public void unlinkInventory(boolean removeFromList) {
        if (removeFromList)
            LOADED_STORAGES.remove(this);

        ((ItemStackHandlerAccessor)this.handler).setStacks(NonNullList.withSize(27, ItemStack.EMPTY));
    }

    @Override
    public void unlinkController() {
        unlinkInventory(true);

        setLinkedControllerUuid(null);
    }


    // Mixin functions

    @Inject(method = "serialize", at = @At("RETURN"), remap = false)
    public void mySerialize(CallbackInfoReturnable<CompoundTag> cir) {
        if (cir.getReturnValue() == null)
            return;

        if (!(this.blockEntity instanceof ChestBlockEntity))
            return;

        if (!hasLinkedControllerUuid())
            return;

        CompoundTag nbt = cir.getReturnValue();
        nbt.putUUID("controller-uuid", linkedControllerUuid);
    }

    @Inject(method = "deserialize", at = @At(value = "RETURN"), remap = false)
    private static void myDeserialize(CompoundTag nbt, CallbackInfoReturnable<MountedStorage> cir) {
        if (cir.getReturnValue() == null)
            return;

        if (!nbt.contains("controller-uuid"))
            return;
        IMountedStorage storage = (IMountedStorage) cir.getReturnValue();

        storage.setLinkedControllerUuid(nbt.getUUID("controller-uuid"));
        storage.linkInventory();
    }

    @Inject(method = "addStorageToWorld", at = @At("HEAD"), remap = false)
    public void myAddStorageToWorldBefore(BlockEntity be, CallbackInfo ci) {
        if (!(this.blockEntity instanceof ChestBlockEntity))
            return;

        if (!hasLinkedControllerUuid())
            return;

        unlinkInventory(true);
    }

    @Inject(method = "addStorageToWorld", at = @At("RETURN"), remap = false)
    public void myAddStorageToWorldAfter(BlockEntity be, CallbackInfo ci) {
        if (this.blockEntity instanceof ChestBlockEntity chestBlockEntity) {
            if (!hasLinkedControllerUuid())
                return;

            IChestBlockEntity customChestBlockEntity = (IChestBlockEntity) chestBlockEntity;

            customChestBlockEntity.setLinkedControllerUuid(linkedControllerUuid);
            customChestBlockEntity.linkInventory();
        }
    }

    @Inject(method = "removeStorageFromWorld", at = @At("RETURN"), remap = false)
    public void myRemoveStorageFromWorld(CallbackInfo ci) {
        if (this.blockEntity instanceof ChestBlockEntity chestBlockEntity) {
            IChestBlockEntity customChestBlockEntity = (IChestBlockEntity) chestBlockEntity;

            if (!customChestBlockEntity.hasLinkedControllerUuid())
                return;

            linkedControllerUuid = customChestBlockEntity.getLinkedControllerUuid();

            customChestBlockEntity.unlinkController();
            linkInventory();
        }
    }
}
