package io.github.tilosophy.createbetterhammers;

import net.minecraft.world.item.CreativeModeTabs;
import java.util.List;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(CreateBetterHammers.ID)
public final class CreateBetterHammers {
    public static final String ID = "create_better_hammers";

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ID);
    public static final DeferredItem<HammerItem> STONE_HAMMER = registerHammer("stone_hammer", HammerItem.Tier.STONE);
    public static final DeferredItem<HammerItem> IRON_HAMMER = registerHammer("iron_hammer", HammerItem.Tier.IRON);
    public static final DeferredItem<HammerItem> DIAMOND_HAMMER = registerHammer("diamond_hammer", HammerItem.Tier.DIAMOND);
    public static final DeferredItem<HammerItem> PNEUMATIC_HAMMER = registerHammer("pneumatic_hammer", HammerItem.Tier.PNEUMATIC);
    static final List<DeferredItem<HammerItem>> HAMMERS = List.of(STONE_HAMMER, IRON_HAMMER, DIAMOND_HAMMER, PNEUMATIC_HAMMER);

    private static DeferredItem<HammerItem> registerHammer(String name, HammerItem.Tier tier) {
        return ITEMS.registerItem(name, properties -> new HammerItem(properties, tier));
    }

    public CreateBetterHammers(IEventBus modBus) {
        ITEMS.register(modBus);
        SturdySheetStrike.ATTACHMENTS.register(modBus);
        modBus.addListener(this::addCreativeTabContents);
    }

    private void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            HAMMERS.forEach(event::accept);
        }
    }
}
