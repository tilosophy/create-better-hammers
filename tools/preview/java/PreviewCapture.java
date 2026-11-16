package io.github.tilosophy.createbetterhammers.preview;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "create_better_hammers", value = Dist.CLIENT)
public final class PreviewCapture {
    private static int ticks;
    private static int loadingTicks;
    private static ItemStack item(String name, int variant) {
        var stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("create_better_hammers", name)));
        if (variant != 0) stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(variant));
        return stack;
    }
    private static void hold(String name) {
        var mc = Minecraft.getInstance();
        mc.setScreen(null);
        mc.player.getInventory().setItem(0, item(name, 0));
        mc.player.getInventory().selected = 0;
        mc.player.setYRot(0);
        mc.player.setXRot(-12);
    }
    private static void capture(String name) {
        var mc = Minecraft.getInstance();
        Screenshot.grab(mc.gameDirectory, "refined-" + name + ".png", mc.getMainRenderTarget(), c -> {});
    }
    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            if (++loadingTicks % 200 == 0) {
                System.out.println("Preview waiting on screen: " + (mc.screen == null ? "none" : mc.screen.getClass().getName()));
                capture("loading");
            }
            return;
        }
        ticks++;
        if (ticks == 40) {
            mc.options.bobView().set(false);
            mc.options.fov().set(70);
            mc.player.setPos(0.5, 65, 0.5);
            hold("pneumatic_hammer");
        }
        if (ticks == 100) capture("pneumatic-rest");
        if (ticks == 130) hold("diamond_hammer");
        if (ticks == 180) capture("diamond-rest");
        if (ticks == 210) mc.setScreen(new Review("Pneumatic Hammer", "pneumatic_hammer"));
        if (ticks == 245) capture("pneumatic-comparison");
        if (ticks == 275) mc.setScreen(new Review("Diamond Hammer", "diamond_hammer"));
        if (ticks == 310) capture("diamond-comparison");
        if (ticks == 340) mc.setScreen(new Review("Hammer family", null));
        if (ticks == 375) capture("family");
        if (ticks == 405) mc.setScreen(new Review("Handle ends and wedges", "tops"));
        if (ticks == 440) capture("handle-tops");
        if (ticks == 470) hold("iron_hammer");
        if (ticks == 520) capture("iron-rest");
        if (ticks == 550) hold("stone_hammer");
        if (ticks == 600) capture("stone-rest");
        if (ticks == 630) mc.setScreen(new Review("Iron Hammer", "iron_hammer"));
        if (ticks == 665) capture("iron-comparison");
        if (ticks == 695) mc.setScreen(new Review("Stone Hammer", "stone_hammer"));
        if (ticks == 730) capture("stone-comparison");
        if (ticks == 760) mc.setScreen(new Review("Pneumatic frame joins", "angles"));
        if (ticks == 795) capture("pneumatic-angles");
        if (ticks == 825) mc.stop();
    }
    private static final class Review extends Screen {
        private final String name;
        Review(String title, String name) { super(Component.literal(title)); this.name = name; }
        @Override public boolean isPauseScreen() { return false; }
        @Override public void render(GuiGraphics g, int mx, int my, float partial) {
            g.fill(0, 0, width, height, 0xff202830);
            g.drawCenteredString(font, title, width / 2, 12, 0xffffff);
            if ("angles".equals(name)) {
                String[] labels = {"Near side", "Above", "Far side"};
                for (int i = 0; i < 3; i++) {
                    float x = width * (i + .5f) / 3;
                    g.drawCenteredString(font, labels[i], (int)x, 30, 0xffffff);
                    draw(g, item("pneumatic_hammer", i + 2), x, height * .56f, Math.min(width / 60f, height / 25f));
                }
            } else if ("tops".equals(name)) {
                String[] tiers = {"stone", "iron", "diamond"};
                for (int i = 0; i < 3; i++) {
                    float x = width * (i + .5f) / 3;
                    g.drawCenteredString(font, tiers[i], (int)x, 30, 0xffffff);
                    draw(g, item(tiers[i] + "_hammer", 3), x, height * .56f, Math.min(width / 60f, height / 25f));
                }
            } else if (name != null) {
                g.drawCenteredString(font, "Previous", width / 4, 30, 0xffffff);
                g.drawCenteredString(font, "Refined", width * 3 / 4, 30, 0xffffff);
                draw(g, item(name, 1), width / 4f, height * .56f, Math.min(width / 42f, height / 25f));
                draw(g, item(name, 2), width * 3 / 4f, height * .56f, Math.min(width / 42f, height / 25f));
            } else {
                String[] tiers = {"stone", "iron", "diamond", "pneumatic"};
                for (int i = 0; i < 4; i++) {
                    float x = width * (i + .5f) / 4;
                    g.drawCenteredString(font, tiers[i], (int)x, 30, 0xffffff);
                    draw(g, item(tiers[i] + "_hammer", 0), x, height * .56f, Math.min(width / 75f, height / 25f));
                }
            }
        }
        private void draw(GuiGraphics g, ItemStack stack, float x, float y, float scale) {
            g.pose().pushPose();
            g.pose().translate(x, y, 0);
            g.pose().scale(scale, scale, 1);
            g.renderItem(stack, -8, -8);
            g.pose().popPose();
        }
    }
}
