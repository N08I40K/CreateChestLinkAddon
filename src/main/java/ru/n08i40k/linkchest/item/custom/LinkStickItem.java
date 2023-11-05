package ru.n08i40k.linkchest.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.n08i40k.linkchest.IChestBlockEntity;
import ru.n08i40k.linkchest.block.custom.LinkControllerBlock;
import ru.n08i40k.linkchest.block.entity.LinkControllerBlockEntity;

import java.util.UUID;

public class LinkStickItem extends Item {
    public LinkStickItem(Properties properties) {
        super(properties);
    }

    private void setLinkedControllerUuid(ItemStack itemStack, UUID uuid) {
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putUUID("controller-uuid", uuid);
        itemStack.setTag(tag);
    }

    @Nullable
    public UUID getLinkedControllerUuid(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();

        return tag.contains("controller-uuid") ? tag.getUUID("controller-uuid") : null;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        InteractionResult result = super.useOn(context);

        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (player == null || player.isSpectator() || level.isClientSide())
            return result;

        Block block = level.getBlockState(context.getClickedPos()).getBlock();

        if (block instanceof LinkControllerBlock) {
            LinkControllerBlockEntity linkControllerBlockEntity =
                    (LinkControllerBlockEntity) level.getBlockEntity(context.getClickedPos());

            if (linkControllerBlockEntity == null)
                return InteractionResult.FAIL;

            UUID uuid = linkControllerBlockEntity.getUuid();

            setLinkedControllerUuid(context.getItemInHand(), uuid);

            context.getPlayer().sendSystemMessage(
                    Component.literal("Stick linked to controller with uuid: " + uuid.toString()));
        }

        if (block instanceof ChestBlock chestBlock) {
            IChestBlockEntity chestBlockEntity = (IChestBlockEntity) chestBlock.blockEntityType().getBlockEntity(level, context.getClickedPos());

            if (chestBlockEntity == null)
                return InteractionResult.FAIL;

            UUID uuid = getLinkedControllerUuid(context.getItemInHand());

            if (uuid == null)
                return InteractionResult.FAIL;

            chestBlockEntity.setLinkedControllerUuid(uuid);

            chestBlockEntity.unlinkInventory(true);
            chestBlockEntity.linkInventory();
        }

        player.getCooldowns().addCooldown(this, 10);

        return InteractionResult.FAIL;
    }
}
