package ru.n08i40k.linkchest.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.n08i40k.linkchest.IChestBlockEntity;
import ru.n08i40k.linkchest.block.entity.LinkControllerBlockEntity;

import java.util.Objects;
import java.util.UUID;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin implements IChestBlockEntity {

    @Shadow
    private NonNullList<ItemStack> items;

    @Unique
    private NonNullList<ItemStack> linkedInventory = null;

    @Unique
    private UUID linkedControllerUuid = null;

    @Unique
    boolean isLoaded = false;

    @Unique
    public void clearInventory() {
        items = NonNullList.withSize(27, ItemStack.EMPTY);
    }


    // Interface functions

    @Override
    public UUID getLinkedControllerUuid() {
        return linkedControllerUuid;
    }

    @Override
    public void setLinkedControllerUuid(UUID uuid) {
        this.linkedControllerUuid = uuid;
    }

    @Override
    public boolean hasLinkedControllerUuid() {
        return linkedControllerUuid != null;
    }

    @Override
    public void linkInventory() {
        if (!LOADED_CHESTS.contains(this))
            LOADED_CHESTS.add(this);

        LinkControllerBlockEntity.LOADED_CONTROLLERS.removeIf(Objects::isNull);

        for (LinkControllerBlockEntity linkControllerBlockEntity : LinkControllerBlockEntity.LOADED_CONTROLLERS) {
            if (!linkControllerBlockEntity.getUuid().equals(linkedControllerUuid))
                continue;

            clearInventory();
            linkedInventory = linkControllerBlockEntity.getInventory();
            break;
        }
    }

    @Override
    public void unlinkInventory(boolean removeFromList) {
        if (removeFromList)
            LOADED_CHESTS.remove(this);

        linkedInventory = null;
    }

    @Override
    public void unlinkController() {
        unlinkInventory(true);

        linkedControllerUuid = null;
    }

    // Mixin functions

    @Inject(method = "getItems", at = @At("HEAD"), cancellable = true)
    protected void myGetInvStackList(CallbackInfoReturnable<NonNullList<ItemStack>> cir) {
        if (linkedInventory == null)
            return;

        cir.setReturnValue(linkedInventory);
        cir.cancel();
    }

    @Inject(method = "load", at = @At("RETURN"))
    public void myLoad(CompoundTag nbt, CallbackInfo cir) {
        if (isLoaded) return;
        isLoaded = true;

        if (!nbt.contains("controller-uuid"))
            return;

        linkedControllerUuid = nbt.getUUID("controller-uuid");
        linkInventory();
    }

    @Inject(method = "saveAdditional", at = @At("HEAD"))
    protected void mySaveAdditional(CompoundTag nbt, CallbackInfo cir) {
        if (hasLinkedControllerUuid())
            nbt.putUUID("controller-uuid", linkedControllerUuid);
    }
}
