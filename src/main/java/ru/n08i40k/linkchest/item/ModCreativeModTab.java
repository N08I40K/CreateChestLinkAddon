package ru.n08i40k.linkchest.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static ru.n08i40k.linkchest.LinkChest.MOD_ID;

public class ModCreativeModTab {
    public static final CreativeModeTab MOD_TAB = new CreativeModeTab(MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.LINK_STICK.get());
        }
    };
}
