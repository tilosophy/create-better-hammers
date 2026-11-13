package io.github.tilosophy.createbetterhammers;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class HammerItem extends Item {
    private static final int STRIKE_COOLDOWN_TICKS = 20;
    private static final TagKey<Block> WORK_SURFACES = TagKey.create(Registries.BLOCK,
        ResourceLocation.fromNamespaceAndPath(CreateBetterHammers.ID, "work_surfaces"));

    enum Tier {
        STONE("stone_materials", 64, 9, 5),
        IRON("iron_materials", 256, 10, 14),
        DIAMOND("diamond_materials", 1024, 11, 10),
        PNEUMATIC("diamond_materials", 1024, 11, 10);

        final String materialTooltip;
        final int durability;
        final int attackDamage;
        final int enchantability;

        Tier(String materialTooltip, int durability, int attackDamage, int enchantability) {
            this.materialTooltip = materialTooltip;
            this.durability = durability;
            this.attackDamage = attackDamage;
            this.enchantability = enchantability;
        }
    }

    private final Tier tier;

    HammerItem(Properties properties, Tier tier) {
        super(properties.durability(tier.durability).attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_ATTACK_DAMAGE_ID, tier.attackDamage - 1, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_ID, -3.2, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()));
        this.tier = tier;
    }

    String materialTooltip() {
        return tier.materialTooltip;
    }

    boolean isPneumatic() {
        return tier == Tier.PNEUMATIC;
    }

    private boolean handlesGenericSheets() {
        return tier == Tier.DIAMOND || isPneumatic();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (isPneumatic()) {
            return AllItems.WRENCH.get().useOn(context);
        }
        return super.useOn(context);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        spendStrikeWear(stack, attacker);
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity defender, LivingEntity attacker) {
        return true;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return tier.enchantability;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING) || enchantment.is(Enchantments.MENDING)
            || enchantment.is(Enchantments.SMITE) || enchantment.is(Enchantments.BANE_OF_ARTHROPODS);
    }

    @Override
    public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
        return supportsEnchantment(stack, enchantment) && !enchantment.is(Enchantments.MENDING);
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
        boolean obsidian = input.is(AllItems.POWDERED_OBSIDIAN.get());
        HammeringMetal metal = HammeringMetal.from(input);

        if (!level.isClientSide()) {
            SturdySheetStrike.checkTarget(player, target);
        }
        if (obsidian && surface.is(Blocks.MAGMA_BLOCK)) {
            if (!isPneumatic()) {
                return reject(player, "requires_pneumatic_hammer");
            }
            if (context.getClickedFace() != Direction.UP || !isRaisedWorksite(player, target)) {
                return reject(player, "raised_surface");
            }
            if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResult.FAIL;
            }
            if (!level.isClientSide() && !strikeSturdySheet(player, target, hammer, input)) {
                return InteractionResult.FAIL;
            }
            return confirmedStrike(level);
        }
        if (!surface.is(WORK_SURFACES)) {
            if (obsidian && handlesGenericSheets()) {
                return reject(player, "sturdy_sheet_surface");
            }
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

        if (context.getClickedFace() != Direction.UP || !isRaisedWorksite(player, target)) {
            return reject(player, "raised_surface");
        }
        if (metal != null && tier == Tier.STONE && metal.requiresIronHammer()) {
            return reject(player, "requires_iron_hammer");
        }
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            ItemStack sheet = metal == null ? HammeringMetal.findPressedSheet(level, input) : metal.sheet(level, input);
            if (metal == null && !handlesGenericSheets() && !sheet.isEmpty()) {
                return reject(player, "requires_diamond_hammer");
            }
            if (sheet.isEmpty()) {
                if (obsidian) {
                    return reject(player, "sturdy_sheet_surface");
                }
                return reject(player, metal == HammeringMetal.ZINC ? "unavailable_zinc_sheet" : "unsupported_ingot");
            }
            if (!giveSheet(player, sheet)) {
                return InteractionResult.FAIL;
            }

            input.shrink(1);
            spendStrikeWear(hammer, player);
            playStrike(player, target);
        }

        return confirmedStrike(level);
    }

    private void spendStrikeWear(ItemStack hammer, LivingEntity user) {
        if (!isPneumatic() || !PneumaticPower.spendAir(user)) {
            hammer.hurtAndBreak(1, user, EquipmentSlot.MAINHAND);
        }
    }

    private static InteractionResult confirmedStrike(Level level) {
        // The server's successful interaction sends the swing to the acting client.
        return level.isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
    }

    static boolean isRaisedWorksite(Player player, BlockPos target) {
        return player.onGround() && target.getY() == player.blockPosition().getY()
            && !target.equals(player.blockPosition());
    }

    private static boolean strikeSturdySheet(Player player, BlockPos target, ItemStack hammer, ItemStack input) {
        if (!player.hasData(SturdySheetStrike.PENDING)) {
            player.setData(SturdySheetStrike.PENDING,
                new SturdySheetStrike(player.level(), target.immutable(), hammer, input, input.getCount()));
            showMessage(player, "sturdy_sheet_first_strike");
        } else {
            if (!giveSheet(player, new ItemStack(AllItems.STURDY_SHEET.get()))) {
                return false;
            }
            player.removeData(SturdySheetStrike.PENDING);
            input.shrink(1);
            hammer.hurtAndBreak(8, player, EquipmentSlot.MAINHAND);
        }
        playStrike(player, target);
        return true;
    }

    private static boolean giveSheet(Player player, ItemStack sheet) {
        Inventory inventory = player.getInventory();
        if ((inventory.getFreeSlot() < 0 && inventory.getSlotWithRemainingSpace(sheet) < 0)
                || !inventory.add(sheet)) {
            showMessage(player, "inventory_full");
            return false;
        }
        return true;
    }

    private static void playStrike(Player player, BlockPos target) {
        // Share the cooldown so swapping hammer tiers cannot speed up pressing.
        CreateBetterHammers.HAMMERS.forEach(item -> player.getCooldowns().addCooldown(item.get(), STRIKE_COOLDOWN_TICKS));
        // The Mechanical Press itself plays this event at 0.5 volume.
        AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(player.level(), target, 0.25F, 1.0F);
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
