package ru.n08i40k.linkchest.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackHandler.class)
public interface ItemStackHandlerAccessor {
    @Accessor(value = "stacks", remap = false)
    NonNullList<ItemStack> getStacks();

    @Accessor(value = "stacks", remap = false)
    void setStacks(NonNullList<ItemStack> stacks);
}
