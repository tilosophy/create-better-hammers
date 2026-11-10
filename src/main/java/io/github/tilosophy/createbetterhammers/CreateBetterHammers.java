package io.github.tilosophy.createbetterhammers;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(CreateBetterHammers.ID)
public final class CreateBetterHammers {
    public static final String ID = "create_better_hammers";

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ID);
    public static final DeferredItem<StoneHammerItem> STONE_HAMMER =
        ITEMS.registerItem("stone_hammer", StoneHammerItem::new, new Item.Properties().durability(64));

    public CreateBetterHammers(IEventBus modBus) {
        ITEMS.register(modBus);
        modBus.addListener(this::addCreativeTabContents);
    }

    private void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            event.accept(STONE_HAMMER);
        }
    }
}
