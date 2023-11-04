package ru.n08i40k.linkchest.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.n08i40k.linkchest.LinkChest;
import ru.n08i40k.linkchest.block.custom.LinkControllerBlock;
import ru.n08i40k.linkchest.item.ModCreativeModTab;
import ru.n08i40k.linkchest.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LinkChest.MOD_ID);

    public static final RegistryObject<Block> LINK_CONTROLLER =
            registerBlock(
                    "link_controller",
                    () -> new LinkControllerBlock(BlockBehaviour.Properties.of(Material.STONE).strength(1.f)),
                    new Item.Properties().stacksTo(1).fireResistant(),
                    ModCreativeModTab.MOD_TAB
            );

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        return registerBlock(name, block, new Item.Properties(), tab);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, Item.Properties itemProperties, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);

        registerBlockItem(name, toReturn, itemProperties, tab);

        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, Item.Properties itemProperties, CreativeModeTab tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), itemProperties.tab(tab)));
    }


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
