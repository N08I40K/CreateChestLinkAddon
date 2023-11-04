package ru.n08i40k.linkchest.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.n08i40k.linkchest.block.ModBlocks;

import static ru.n08i40k.linkchest.LinkChest.MOD_ID;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<BlockEntityType<LinkControllerBlockEntity>> LINK_CONTROLLER =
            BLOCK_ENTITIES.register("link_controller", () ->
                    BlockEntityType.Builder.of(LinkControllerBlockEntity::new,
                            ModBlocks.LINK_CONTROLLER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
