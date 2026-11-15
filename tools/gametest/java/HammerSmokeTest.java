package io.github.tilosophy.createbetterhammers;

import java.util.List;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CreateBetterHammers.ID)
@PrefixGameTestTemplate(false)
public class HammerSmokeTest {
    @GameTest(template = "empty")
    public static void progression(GameTestHelper helper) {
        var level = helper.getLevel();
        var feet = helper.absolutePos(new BlockPos(1, 1, 1));
        var target = feet.east();
        var player = new Player(level, feet, 0, new GameProfile(UUID.randomUUID(), "HammerTest")) {
            @Override public boolean isCreative() { return false; }
            @Override public boolean isSpectator() { return false; }
        };
        player.setPos(feet.getX() + 0.5, feet.getY(), feet.getZ() + 0.5);
        player.setOnGround(true);
        var stone = new ItemStack(CreateBetterHammers.STONE_HAMMER.get());
        var iron = new ItemStack(CreateBetterHammers.IRON_HAMMER.get());
        var diamond = new ItemStack(CreateBetterHammers.DIAMOND_HAMMER.get());
        helper.assertTrue(stone.getMaxDamage() == 64 && iron.getMaxDamage() == 256 && diamond.getMaxDamage() == 1024, "Correct tier durability");
        var ironSheet = AllItems.IRON_SHEET.get();

        level.setBlockAndUpdate(target, Blocks.STONE.defaultBlockState());
        var first = new ItemStack(Items.IRON_INGOT, 2);
        helper.assertTrue(strike(player, stone, first, target, Direction.UP).consumesAction(), "First raised stone strike works");
        helper.assertTrue(!strike(player, iron, first, target, Direction.UP).consumesAction(), "Tier switch cannot bypass cooldown");
        helper.assertTrue(!strike(player, diamond, first, target, Direction.UP).consumesAction(), "Diamond shares the pressing cooldown");
        tickCooldown(player, 19);
        helper.assertTrue(!strike(player, stone, first, target, Direction.UP).consumesAction(), "19 ticks is too soon");
        helper.assertTrue(first.getCount() == 1 && stone.getDamageValue() == 1 && iron.getDamageValue() == 0
            && player.getInventory().countItem(ironSheet) == 1, "Rejected repeats do not consume or produce");
        tickCooldown(player, 1);

        level.setBlockAndUpdate(target, Blocks.SMOOTH_STONE.defaultBlockState());
        assertConversion(helper, player, stone, target, Items.COPPER_INGOT, AllItems.COPPER_SHEET.get());
        level.setBlockAndUpdate(target.below(), Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(target, Blocks.ANVIL.defaultBlockState());
        var brass = new ItemStack(AllItems.BRASS_INGOT.get());
        helper.assertTrue(strike(player, stone, brass, target, Direction.UP) == InteractionResult.FAIL
            && !player.getCooldowns().isOnCooldown(stone.getItem()) && brass.getCount() == 1, "Stone rejects brass without a cooldown");
        var gold = new ItemStack(Items.GOLD_INGOT);
        helper.assertTrue(strike(player, stone, gold, target, Direction.UP) == InteractionResult.FAIL, "Stone rejects gold");
        assertConversion(helper, player, iron, target, AllItems.BRASS_INGOT.get(), AllItems.BRASS_SHEET.get());
        assertConversion(helper, player, iron, target, Items.GOLD_INGOT, AllItems.GOLDEN_SHEET.get());
        assertConversion(helper, player, iron, target, Items.IRON_INGOT, ironSheet);
        assertConversion(helper, player, iron, target, Items.COPPER_INGOT, AllItems.COPPER_SHEET.get());
        helper.assertTrue(strike(player, iron, ItemStack.EMPTY, target, Direction.UP) == InteractionResult.PASS, "Empty offhand preserves anvil interaction");
        helper.assertTrue(strike(player, stone, first, target, Direction.WEST) == InteractionResult.FAIL, "Side rejected");
        helper.assertTrue(strike(player, stone, first, target.below(), Direction.UP) == InteractionResult.FAIL, "Floor rejected");

        level.setBlockAndUpdate(target, Blocks.DIRT.defaultBlockState());
        helper.assertTrue(strike(player, stone, first, target, Direction.UP) == InteractionResult.PASS, "Dirt does not hammer");
        level.setBlockAndUpdate(target, Blocks.STONE.defaultBlockState());
        var zinc = new ItemStack(AllItems.ZINC_INGOT.get());
        var pressPos = target.above(2);
        level.setBlockAndUpdate(pressPos, com.simibubi.create.AllBlocks.MECHANICAL_PRESS.get().defaultBlockState());
        var press = (com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity) level.getBlockEntity(pressPos);
        var recipe = press.getRecipe(zinc);
        if (Boolean.getBoolean("hammerSmoke.withDeco")) {
            net.minecraft.world.item.crafting.RecipeType<com.simibubi.create.content.kinetics.press.PressingRecipe> pressing =
                com.simibubi.create.AllRecipeTypes.PRESSING.getType();
            var input = new net.minecraft.world.item.crafting.SingleRecipeInput(zinc.copy());
            var sheets = level.getRecipeManager().getAllRecipesFor(pressing).stream()
                .filter(entry -> entry.value().matches(input, level))
                .flatMap(entry -> entry.value().getRollableResults().stream())
                .map(output -> BuiltInRegistries.ITEM.getKey(output.getStack().getItem()).toString())
                .toList();
            helper.assertTrue(sheets.contains("createaddition:zinc_sheet") && (Boolean.getBoolean("hammerSmoke.additionOnly") || sheets.contains("createdeco:zinc_sheet")),
                "Both zinc sheet recipes are loaded");
            var pressOutput = recipe.orElseThrow().value().getRollableResults().getFirst().getStack().getItem();
            System.out.println("ALPHA3 PRESS OUTPUT: " + BuiltInRegistries.ITEM.getKey(pressOutput));
            assertConversion(helper, player, stone, target, AllItems.ZINC_INGOT.get(), pressOutput);
            assertConversion(helper, player, iron, target, AllItems.ZINC_INGOT.get(), pressOutput);
            assertConversion(helper, player, diamond, target, AllItems.ZINC_INGOT.get(), pressOutput);
            var electrum = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("createaddition", "electrum_ingot"));
            var electrumSheet = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("createaddition", "electrum_sheet"));
            var inputElectrum = new ItemStack(electrum);
            helper.assertTrue(strike(player, iron, inputElectrum, target, Direction.UP) == InteractionResult.FAIL
                && inputElectrum.getCount() == 1, "Iron leaves electrum intact");
            assertConversion(helper, player, diamond, target, electrum, electrumSheet);
        } else {
            helper.assertTrue(recipe.isEmpty(), "Create alone has no zinc recipe");
            helper.assertTrue(strike(player, stone, zinc, target, Direction.UP) == InteractionResult.FAIL
                && zinc.getCount() == 1 && !player.getCooldowns().isOnCooldown(stone.getItem()),
                "Missing zinc recipe preserves input");
        }

        var pulp = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("create", "pulp"));
        var cardboard = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("create", "cardboard"));
        assertConversion(helper, player, diamond, target, pulp, cardboard);
        var sugarCane = new ItemStack(Items.SUGAR_CANE);
        helper.assertTrue(strike(player, diamond, sugarCane, target, Direction.UP) == InteractionResult.FAIL
            && sugarCane.getCount() == 1 && !player.getCooldowns().isOnCooldown(diamond.getItem()),
            "Diamond rejects ordinary paper and preserves its input");

        for (int slot = 1; slot < 36; slot++) {
            player.getInventory().setItem(slot, new ItemStack(Items.COBBLESTONE, 64));
        }
        helper.assertTrue(strike(player, stone, first, target, Direction.UP) == InteractionResult.FAIL && first.getCount() == 1
            && !player.getCooldowns().isOnCooldown(stone.getItem()), "Full inventory does not consume input or trigger cooldown");
        player.getInventory().setItem(1, new ItemStack(ironSheet, 63));
        stone.setDamageValue(63);
        helper.assertTrue(strike(player, stone, first, target, Direction.UP).consumesAction() && first.isEmpty() && stone.isEmpty()
            && player.getInventory().countItem(ironSheet) == 64, "Last strike merges sheet and breaks hammer");

        assertRecipe(helper, "stone_hammer", Items.COBBLESTONE);
        assertRecipe(helper, "iron_hammer", Items.IRON_INGOT);
        assertRecipe(helper, "diamond_hammer", Items.DIAMOND);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void combat(GameTestHelper helper) {
        var level = helper.getLevel();
        var feet = helper.absolutePos(new BlockPos(1, 1, 1));
        class TestPlayer extends Player {
            TestPlayer() {
                super(level, feet, 0, new GameProfile(UUID.randomUUID(), "HammerCombat"));
                setOnGround(true);
            }
            @Override public boolean isCreative() { return false; }
            @Override public boolean isSpectator() { return false; }
            void charge() { attackStrengthTicker = 25; }
            void blockHit(LivingEntity attacker) { blockUsingShield(attacker); }
        }
        var player = new TestPlayer();
        var defender = new TestPlayer();
        var enchantments = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        int damage = 9;
        for (var item : CreateBetterHammers.HAMMERS) {
            var hammer = new ItemStack(item.get());
            player.setItemInHand(InteractionHand.MAIN_HAND, hammer);
            hammer.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) ->
                player.getAttribute(attribute).addOrUpdateTransientModifier(modifier));
            helper.assertTrue(Math.abs(player.getCurrentItemAttackStrengthDelay() - 25) < 0.01, "25-tick recharge");
            var cow = EntityType.COW.create(level);
            cow.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30);
            cow.setHealth(30);
            player.charge();
            player.attack(cow);
            helper.assertTrue(cow.getHealth() == 30 - damage, "Correct fully charged hit for " + item.getId() + ": " + cow.getHealth());
            helper.assertTrue(hammer.getDamageValue() == 1 && player.getAttackStrengthScale(0) == 0,
                "Landed hit uses one durability and resets attack strength");
            defender.getCooldowns().removeCooldown(Items.SHIELD);
            defender.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.SHIELD));
            defender.startUsingItem(InteractionHand.OFF_HAND);
            defender.blockHit(player);
            helper.assertTrue(defender.getCooldowns().isOnCooldown(Items.SHIELD), "Axe-style shield disable");

            for (var key : List.of(Enchantments.UNBREAKING, Enchantments.MENDING, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS)) {
                var enchantment = enchantments.getHolderOrThrow(key);
                helper.assertTrue(hammer.supportsEnchantment(enchantment), "Supported enchantment " + key);
                helper.assertTrue(hammer.isPrimaryItemFor(enchantment) == !key.equals(Enchantments.MENDING), "Correct table eligibility " + key);
            }
            for (var key : List.of(Enchantments.BREACH, Enchantments.DENSITY, Enchantments.WIND_BURST, Enchantments.SHARPNESS)) {
                helper.assertTrue(!hammer.supportsEnchantment(enchantments.getHolderOrThrow(key)), "Excluded enchantment " + key);
            }
            damage = Math.min(11, damage + 1);
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void pneumatic(GameTestHelper helper) {
        var level = helper.getLevel();
        var feet = helper.absolutePos(new BlockPos(1, 1, 1));
        var target = feet.east();
        class PneumaticPlayer extends Player {
            PneumaticPlayer() {
                super(level, feet, 0, new GameProfile(UUID.randomUUID(), "PneumaticTest"));
                setPos(Vec3.atBottomCenterOf(feet));
                setOnGround(true);
            }
            @Override public boolean isCreative() { return false; }
            @Override public boolean isSpectator() { return false; }
            void charge() { attackStrengthTicker = 25; }
            Component lastMessage;
            @Override public void displayClientMessage(Component message, boolean overlay) { lastMessage = message; }
        }
        var player = new PneumaticPlayer();
        var hammer = new ItemStack(CreateBetterHammers.PNEUMATIC_HAMMER.get());
        var diamond = new ItemStack(CreateBetterHammers.DIAMOND_HAMMER.get());
        var iron = new ItemStack(CreateBetterHammers.IRON_HAMMER.get());
        var goldSheet = AllItems.GOLDEN_SHEET.asStack();
        var ironSheet = AllItems.IRON_SHEET.asStack();
        var cog = AllBlocks.COGWHEEL.asStack();
        var wrench = AllItems.WRENCH.asStack();
        var key = ResourceLocation.fromNamespaceAndPath(CreateBetterHammers.ID, "pneumatic_hammer");
        var recipe = (ShapedRecipe) level.getRecipeManager().byKey(key).orElseThrow().value();
        helper.assertTrue(recipe.matches(CraftingInput.of(3, 3,
            List.of(goldSheet, ironSheet, goldSheet, cog, diamond, cog, ItemStack.EMPTY, wrench, ItemStack.EMPTY)), level),
            "Approved Pneumatic crafting pattern");
        helper.assertTrue(hammer.is(net.neoforged.neoforge.common.Tags.Items.TOOLS_WRENCH), "Shared wrench tag");

        level.setBlockAndUpdate(target, AllBlocks.GEARBOX.getDefaultState());
        var original = level.getBlockState(target);
        player.setItemInHand(InteractionHand.MAIN_HAND, hammer);
        var hit = new BlockHitResult(Vec3.atCenterOf(target), Direction.NORTH, target, false);
        var event = new net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock(
            player, InteractionHand.MAIN_HAND, target, hit);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
        helper.assertTrue(event.isCanceled() && level.getBlockState(target) != original, "Create rotates tagged hammer");
        player.setShiftKeyDown(true);
        event = new net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock(
            player, InteractionHand.MAIN_HAND, target, hit);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
        helper.assertTrue(event.isCanceled() && level.isEmptyBlock(target), "Create sneak-picks up machine");
        player.setShiftKeyDown(false);

        var cart = EntityType.MINECART.create(level);
        cart.setPos(Vec3.atBottomCenterOf(target));
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(
            new net.neoforged.neoforge.event.entity.player.AttackEntityEvent(player, cart));
        helper.assertTrue(cart.isRemoved(), "Pneumatic Hammer matches Create's minecart hit");

        level.setBlockAndUpdate(target, Blocks.STONE.defaultBlockState());
        var powder = new ItemStack(AllItems.POWDERED_OBSIDIAN.get(), 3);
        helper.assertTrue(strike(player, hammer, powder, target, Direction.UP) == InteractionResult.FAIL
            && !player.getCooldowns().isOnCooldown(hammer.getItem()), "Wrong surface rejects without cooldown");
        helper.assertTrue(strike(player, iron, AllItems.PULP.asStack(), target, Direction.UP) == InteractionResult.FAIL
            && ((net.minecraft.network.chat.contents.TranslatableContents) player.lastMessage.getContents()).getKey().endsWith("requires_diamond_hammer"),
            "A compatible generic recipe reports the tier requirement");
        helper.assertTrue(strike(player, iron, new ItemStack(Items.SUGAR_CANE), target, Direction.UP) == InteractionResult.FAIL
            && ((net.minecraft.network.chat.contents.TranslatableContents) player.lastMessage.getContents()).getKey().endsWith("unsupported_ingot"),
            "Unsupported ingredients keep the generic error");
        level.setBlockAndUpdate(target, Blocks.MAGMA_BLOCK.defaultBlockState());
        helper.assertTrue(strike(player, diamond, powder, target, Direction.UP) == InteractionResult.FAIL, "Diamond cannot craft Sturdy Sheets");

        var tank = AllItems.COPPER_BACKTANK.asStack();
        tank.set(com.simibubi.create.AllDataComponents.BACKTANK_AIR, 100);
        player.setItemSlot(EquipmentSlot.CHEST, tank);
        player.setItemInHand(InteractionHand.MAIN_HAND, hammer);
        hammer.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) ->
            player.getAttribute(attribute).addOrUpdateTransientModifier(modifier));
        var tick = new net.neoforged.neoforge.event.tick.PlayerTickEvent.Post(player);
        PneumaticPower.updatePower(tick);
        helper.assertTrue(Math.abs(player.getCurrentItemAttackStrengthDelay() - 20) < 0.01, "Tank gives 20-tick combat recovery");
        for (int i = 0; i < 5; i++) PneumaticPower.updatePower(tick);
        helper.assertTrue(player.getAttributeValue(Attributes.ATTACK_DAMAGE) == 12, "Repeated ticks do not stack damage");
        player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
        PneumaticPower.updatePower(tick);
        helper.assertTrue(player.getAttributeValue(Attributes.ATTACK_DAMAGE) == 11
            && Math.abs(player.getCurrentItemAttackStrengthDelay() - 25) < .01, "Removing tank clears both boosts");
        player.setItemSlot(EquipmentSlot.CHEST, tank);
        player.setItemInHand(InteractionHand.MAIN_HAND, diamond);
        PneumaticPower.updatePower(tick);
        helper.assertTrue(player.getAttributeValue(Attributes.ATTACK_DAMAGE) == 11, "Other hammers receive no powered damage");
        player.setItemInHand(InteractionHand.MAIN_HAND, hammer);
        PneumaticPower.updatePower(tick);
        var cow = EntityType.COW.create(level);
        cow.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30);
        cow.setHealth(30);
        player.charge();
        player.attack(cow);
        int afterHit = com.simibubi.create.content.equipment.armor.BacktankUtil.getAir(tank);
        helper.assertTrue(cow.getHealth() == 18 && afterHit < 100 && hammer.getDamageValue() == 0, "12 powered damage and combat air use");
        helper.assertTrue(strike(player, hammer, powder, target, Direction.UP).shouldSwing()
            && powder.getCount() == 3 && hammer.getDamageValue() == 0, "First strike confirms swing without wear");
        helper.assertTrue(((net.minecraft.network.chat.contents.TranslatableContents) player.lastMessage.getContents())
            .getKey().endsWith("sturdy_sheet_first_strike"), "First strike shows progress");
        tickCooldown(player, 19);
        helper.assertTrue(strike(player, hammer, powder, target, Direction.UP) == InteractionResult.FAIL, "Early repeat blocked");
        tickCooldown(player, 1);
        helper.assertTrue(strike(player, hammer, powder, target, Direction.UP).shouldSwing()
            && powder.getCount() == 2 && hammer.getDamageValue() == 8
            && player.getInventory().countItem(AllItems.STURDY_SHEET.get()) == 1
            && com.simibubi.create.content.equipment.armor.BacktankUtil.getAir(tank) == afterHit, "Sturdy sheet spends durability only");
        helper.assertTrue(((net.minecraft.network.chat.contents.TranslatableContents) player.lastMessage.getContents())
            .getKey().endsWith("sturdy_sheet_complete"), "Second strike replaces progress with completion");
        tickCooldown(player, 20);
        strike(player, hammer, powder, target, Direction.UP);
        player.setItemInHand(InteractionHand.MAIN_HAND, diamond);
        SturdySheetStrike.onPlayerTick(tick);
        helper.assertTrue(!player.hasData(SturdySheetStrike.PENDING), "Switching tool interrupts");
        tickCooldown(player, 20);

        var capacityPlayer = new PneumaticPlayer();
        var capacityHammer = new ItemStack(CreateBetterHammers.PNEUMATIC_HAMMER.get());
        var capacityPowder = AllItems.POWDERED_OBSIDIAN.asStack();
        strike(capacityPlayer, capacityHammer, capacityPowder, target, Direction.UP);
        tickCooldown(capacityPlayer, 20);
        for (int slot = 0; slot < 36; slot++) capacityPlayer.getInventory().setItem(slot, new ItemStack(Items.COBBLESTONE, 64));
        helper.assertTrue(strike(capacityPlayer, capacityHammer, capacityPowder, target, Direction.UP) == InteractionResult.FAIL
            && capacityPowder.getCount() == 1 && capacityHammer.getDamageValue() == 0
            && ((net.minecraft.network.chat.contents.TranslatableContents) capacityPlayer.lastMessage.getContents()).getKey().endsWith("inventory_full"),
            "Rejected second strike never claims completion or consumes material");

        level.setBlockAndUpdate(target, Blocks.STONE.defaultBlockState());
        helper.assertTrue(strike(player, hammer, new ItemStack(Items.IRON_INGOT), target, Direction.UP).shouldSwing()
            && hammer.getDamageValue() == 8 && com.simibubi.create.content.equipment.armor.BacktankUtil.getAir(tank) < afterHit,
            "Normal sheet uses air");
        tickCooldown(player, 20);
        tank.set(com.simibubi.create.AllDataComponents.BACKTANK_AIR, 0);
        PneumaticPower.updatePower(tick);
        helper.assertTrue(Math.abs(player.getCurrentItemAttackStrengthDelay() - 25) < 0.01, "Empty tank restores 25-tick recovery");
        helper.assertTrue(player.getAttributeValue(Attributes.ATTACK_DAMAGE) == 11, "Empty tank clears damage boost");
        assertConversion(helper, player, hammer, target, Items.IRON_INGOT, AllItems.IRON_SHEET.get());
        helper.succeed();
    }

    private static void assertRecipe(GameTestHelper helper, String id, Item material) {
        var level = helper.getLevel();
        var key = ResourceLocation.fromNamespaceAndPath(CreateBetterHammers.ID, id);
        var recipe = (ShapedRecipe) level.getRecipeManager().byKey(key).orElseThrow().value();
        var mat = new ItemStack(material);
        var stick = new ItemStack(Items.STICK);
        var empty = ItemStack.EMPTY;
        helper.assertTrue(recipe.matches(CraftingInput.of(3, 3, List.of(mat, mat, mat, mat, stick, mat, empty, stick, empty)), level),
            "New recipe pattern accepted: " + id);
        helper.assertTrue(!recipe.matches(CraftingInput.of(3, 3, List.of(mat, mat, mat, mat, mat, mat, empty, stick, empty)), level),
            "Old recipe pattern rejected: " + id);
    }

    private static void assertConversion(GameTestHelper helper, Player player, ItemStack hammer, BlockPos target, Item input, Item output) {
        var ingot = new ItemStack(input);
        int before = player.getInventory().countItem(output);
        int damage = hammer.getDamageValue();
        helper.assertTrue(strike(player, hammer, ingot, target, Direction.UP).consumesAction()
            && ingot.isEmpty() && hammer.getDamageValue() == damage + 1
            && player.getInventory().countItem(output) == before + 1, "Successful conversion: " + input);
        tickCooldown(player, 20);
    }

    private static void tickCooldown(Player player, int ticks) {
        for (int i = 0; i < ticks; i++) {
            player.getCooldowns().tick();
        }
    }

    private static InteractionResult strike(Player player, ItemStack hammer, ItemStack offhand, BlockPos pos, Direction face) {
        player.setItemInHand(InteractionHand.MAIN_HAND, hammer);
        player.setItemInHand(InteractionHand.OFF_HAND, offhand);
        var hit = new BlockHitResult(Vec3.atCenterOf(pos), face, pos, false);
        return ((HammerItem) hammer.getItem()).onItemUseFirst(hammer, new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
    }
}
