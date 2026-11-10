package io.github.tilosophy.createbetterhammers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = CreateBetterHammers.ID, value = Dist.CLIENT)
public final class HammerTooltips {
    private HammerTooltips() {}

    @SubscribeEvent
    public static void addDetails(ItemTooltipEvent event) {
        if (!event.getItemStack().is(CreateBetterHammers.STONE_HAMMER.get())) {
            return;
        }

        String key = "tooltip." + CreateBetterHammers.ID + ".";
        if (Screen.hasShiftDown()) {
            for (String line : new String[]{"hands", "surface", "material"}) {
                event.getToolTip().add(Component.translatable(key + line).withStyle(ChatFormatting.GRAY));
            }
        } else {
            event.getToolTip().add(Component.translatable(key + "hold_shift").withStyle(ChatFormatting.GRAY));
        }
    }
}
