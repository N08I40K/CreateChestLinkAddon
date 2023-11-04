package ru.n08i40k.linkchest.mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public interface BlockEntityAccessor {
    @Accessor("remove")
    boolean remove();

    @Accessor("level")
    Level getLevel();

    @Accessor("worldPosition")
    BlockPos getWorldPosition();
}
