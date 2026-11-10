package io.github.tilosophy.createbetterhammers;

import com.simibubi.create.AllSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class HammerItem extends Item {
    private static final int STRIKE_COOLDOWN_TICKS = 20;
    private static final TagKey<Block> WORK_SURFACES = TagKey.create(Registries.BLOCK,
        ResourceLocation.fromNamespaceAndPath(CreateBetterHammers.ID, "work_surfaces"));

    private final boolean ironTier;

    public HammerItem(Properties properties, boolean ironTier) {
        super(properties);
        this.ironTier = ironTier;
    }

    String materialTooltip() {
        return ironTier ? "iron_materials" : "stone_materials";
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack hammer, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || context.getHand() != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos target = context.getClickedPos();
        ItemStack input = player.getOffhandItem();
        var surface = level.getBlockState(target);
        HammeringMetal metal = HammeringMetal.from(input);

        if (!surface.is(WORK_SURFACES)) {
            if (metal != null) {
                showMessage(player, "surface");
            }
            return InteractionResult.PASS;
        }
        if (input.isEmpty()) {
            // Preserve the usual repair menu when the player is only holding a hammer.
            if (!surface.is(Blocks.ANVIL)) {
                showMessage(player, "offhand");
            }
            return InteractionResult.PASS;
        }

        if (context.getClickedFace() != Direction.UP || !player.onGround()
                || target.getY() != player.blockPosition().getY()
                || target.equals(player.blockPosition())) {
            return reject(player, "raised_surface");
        }
        if (metal == null) {
            return reject(player, "unsupported_ingot");
        }
        if (metal.requiresIronHammer() && !ironTier) {
            return reject(player, "requires_iron_hammer");
        }
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            ItemStack sheet = metal.sheet(level, input);
            if (sheet.isEmpty()) {
                return reject(player, "unavailable_zinc_sheet");
            }
            Inventory inventory = player.getInventory();
            if ((inventory.getFreeSlot() < 0 && inventory.getSlotWithRemainingSpace(sheet) < 0)
                    || !inventory.add(sheet)) {
                return reject(player, "inventory_full");
            }

            input.shrink(1);
            hammer.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            // Share the cooldown so swapping hammer tiers cannot speed up pressing.
            player.getCooldowns().addCooldown(CreateBetterHammers.STONE_HAMMER.get(), STRIKE_COOLDOWN_TICKS);
            player.getCooldowns().addCooldown(CreateBetterHammers.IRON_HAMMER.get(), STRIKE_COOLDOWN_TICKS);
            // The Mechanical Press itself plays this event at 0.5 volume.
            AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(level, target, 0.25F, 1.0F);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static InteractionResult reject(Player player, String reason) {
        showMessage(player, reason);
        return InteractionResult.FAIL;
    }

    private static void showMessage(Player player, String reason) {
        if (!player.level().isClientSide()) {
            player.displayClientMessage(
                Component.translatable("message." + CreateBetterHammers.ID + "." + reason), true);
        }
    }
}
