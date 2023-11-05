package ru.n08i40k.linkchest.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import ru.n08i40k.linkchest.block.entity.LinkControllerBlockEntity;
import ru.n08i40k.linkchest.block.entity.ModBlockEntities;

public class LinkControllerBlock extends BaseEntityBlock {
    public LinkControllerBlock(Properties properties) {
        super(properties);
    }

    private void onDestroy(LevelAccessor levelAccessor) {
//        if (!levelAccessor.isClientSide()) {
//            levelAccessor.getServer().sendSystemMessage(Component.literal("Bye :("));
//        }
    }

    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos pos, BlockState state) {
        onDestroy(levelAccessor);
        super.destroy(levelAccessor, pos, state);
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        onDestroy(level);
        super.wasExploded(level, pos, explosion);
    }

    /* BlockEntity */

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof LinkControllerBlockEntity) {
                player.openMenu((LinkControllerBlockEntity)blockentity);
                player.awardStat(Stats.OPEN_CHEST);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof Container) {
                Containers.dropContents(level, pos, (Container)blockentity);
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LinkControllerBlockEntity(pos, state);
    }
}
