package ru.n08i40k.linkchest;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import ru.n08i40k.linkchest.block.ModBlocks;
import ru.n08i40k.linkchest.block.entity.ModBlockEntities;
import ru.n08i40k.linkchest.item.ModItems;

@Mod(LinkChest.MOD_ID)
public class LinkChest
{
    public static final String MOD_ID = "linkchest";

    public LinkChest()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);

        ModBlocks.register(eventBus);
        ModItems.register(eventBus);
        ModBlockEntities.register(eventBus);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel().isClientSide()) return;

        for (BlockPos pos : event.getChunk().getBlockEntitiesPos()) {
            BlockEntity blockEntity = event.getChunk().getBlockEntity(pos);

            if (blockEntity instanceof IChestBlockEntity chestBlockEntity)
                chestBlockEntity.unlinkInventory(true);
        }
    }
}
