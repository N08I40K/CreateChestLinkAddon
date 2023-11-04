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
    boolean isInventoryLinked = false;

    @Unique
    private NonNullList<ItemStack> linkedInventory = null;

    @Unique
    private UUID linkedControllerUuid = null;

    @Unique
    boolean isLoaded = false;



    public UUID getLinkedControllerUuid() {
        return linkedControllerUuid;
    }

    public void setLinkedControllerUuid(UUID uuid) {
        this.linkedControllerUuid = uuid;
    }

    public boolean hasLinkedController() {
        return linkedControllerUuid != null;
    }

    @Unique
    public void createChestLinkAddon$clearInventory() {
        items = NonNullList.withSize(27, ItemStack.EMPTY);
    }

    @Override
    public void linkInventory() {
        if (!LOADED_CHESTS.contains(this)) {
//            System.out.println("Added to loaded chests!");
            LOADED_CHESTS.add(this);
        } else {
//            System.out.println("Already in loaded chests!");
        }

        LinkControllerBlockEntity.LOADED_CONTROLLERS.removeIf(Objects::isNull);

        for (LinkControllerBlockEntity linkControllerBlockEntity : LinkControllerBlockEntity.LOADED_CONTROLLERS) {
            if (linkControllerBlockEntity.getUuid().equals(linkedControllerUuid)) {
//                System.out.println("Required link controller has been founded!");
                createChestLinkAddon$clearInventory();
                linkedInventory = linkControllerBlockEntity.getInventory();
                isInventoryLinked = true;
                break;
            }
        }
    }

    @Override
    public void unlinkInventory(boolean removeFromList) {
        if (removeFromList)
            LOADED_CHESTS.remove(this);

        isInventoryLinked = false;
        linkedInventory = null;
    }

    @Override
    public void unlinkController() {
        unlinkInventory(true);

        linkedControllerUuid = null;
    }

    @Inject(method = "getItems", at = @At("HEAD"), cancellable = true)
    protected void myGetInvStackList(CallbackInfoReturnable<NonNullList<ItemStack>> cir) {
        if (!isInventoryLinked) {
            return;
        }
        if (linkedInventory == null) {
            isInventoryLinked = false;
            return;
        }
        cir.setReturnValue(linkedInventory);
        cir.cancel();
    }

    @Inject(method = "load", at = @At("RETURN"))
    public void myLoad(CompoundTag pTag, CallbackInfo cir) {
        if (isLoaded) return;
        isLoaded = true;

        if (pTag.contains("controller-uuid")) {
            linkedControllerUuid = pTag.getUUID("controller-uuid");

            if (isLoaded) {
//                System.out.println("Link inventory (chest load)!");
                linkInventory();
            }
        }
    }

    @Inject(method = "saveAdditional", at = @At("HEAD"))
    protected void mySaveAdditional(CompoundTag pTag, CallbackInfo cir) {
        if (linkedControllerUuid != null)
            pTag.putUUID("controller-uuid", linkedControllerUuid);
    }
}
