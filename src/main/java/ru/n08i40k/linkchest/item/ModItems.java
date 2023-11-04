package ru.n08i40k.linkchest.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.n08i40k.linkchest.LinkChest;
import ru.n08i40k.linkchest.item.custom.LinkStickItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LinkChest.MOD_ID);

    public static final RegistryObject<Item> LINK_STICK = ITEMS.register("link_stick", () -> new LinkStickItem(
            new Item.Properties().tab(ModCreativeModTab.MOD_TAB).stacksTo(1)
    ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
