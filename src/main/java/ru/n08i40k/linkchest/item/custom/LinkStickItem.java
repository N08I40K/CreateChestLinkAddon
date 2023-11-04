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

        if (player != null && !player.isSpectator() && !level.isClientSide()) {
            Block block = level.getBlockState(context.getClickedPos()).getBlock();

            if (block instanceof LinkControllerBlock linkControllerBlock) {
//                System.out.println("Clicked on Link Controller!");

                LinkControllerBlockEntity linkControllerBlockEntity =
                        (LinkControllerBlockEntity) level.getBlockEntity(context.getClickedPos());

                if (linkControllerBlockEntity != null) {
//                    System.out.println("Has Link Controller block entity!");

                    ItemStack itemStack = context.getItemInHand();

                    UUID uuid = linkControllerBlockEntity.getUuid();
                    setLinkedControllerUuid(itemStack, uuid);

                    context.getPlayer().sendSystemMessage(
                            Component.literal("Stick linked to controller with uuid: " + uuid.toString()));
                }
            }

            if (block instanceof ChestBlock chestBlock) {
//                System.out.println("Clicked on Chest!");

                ChestBlockEntity chestBlockEntity = chestBlock.blockEntityType().getBlockEntity(level, context.getClickedPos());

                if (chestBlockEntity != null) {
//                    System.out.println("Has Chest block entity!");

                    if (chestBlockEntity instanceof IChestBlockEntity customEntity) {
//                        if (customEntity.getLinkedControllerUuid() == null) {
//                            System.out.println("Chest doesn't have controller uuid!");
//                        } else {
//                            System.out.println("Chest controller uuid: " + customEntity.getLinkedControllerUuid().toString());
//                        }

                        UUID uuid = getLinkedControllerUuid(context.getItemInHand());

                        if (uuid == null) {
//                            System.out.println("Link Stick doesn't have controller uuid!");
                        } else {
//                            System.out.println("Link Stick controller uuid: " + uuid);

                            customEntity.setLinkedControllerUuid(uuid);

                            customEntity.unlinkInventory(true);
                            customEntity.linkInventory();

//                            System.out.println("New Chest controller uuid: " + customEntity.getLinkedControllerUuid().toString());
                        }
                    }
                }
            }


            player.getCooldowns().addCooldown(this, 10);

            return InteractionResult.FAIL;
        }

        return result;
    }
}
