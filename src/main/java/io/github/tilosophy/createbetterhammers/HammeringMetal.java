package io.github.tilosophy.createbetterhammers;

import java.util.function.Supplier;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

enum HammeringMetal {
    IRON("iron", false, () -> AllItems.IRON_SHEET.asStack()),
    COPPER("copper", false, () -> AllItems.COPPER_SHEET.asStack()),
    ZINC("zinc", false, null),
    BRASS("brass", true, () -> AllItems.BRASS_SHEET.asStack()),
    GOLD("gold", true, () -> AllItems.GOLDEN_SHEET.asStack());

    private static final TagKey<Item> ZINC_PLATES = commonTag("plates/zinc");
    private static final TagKey<Item> ZINC_ITEM_PLATES = commonTag("item/plates/zinc");

    private final TagKey<Item> ingots;
    private final boolean requiresIronHammer;
    private final Supplier<ItemStack> createSheet;

    HammeringMetal(String material, boolean requiresIronHammer, @Nullable Supplier<ItemStack> createSheet) {
        this.ingots = commonTag("ingots/" + material);
        this.requiresIronHammer = requiresIronHammer;
        this.createSheet = createSheet;
    }

    @Nullable
    static HammeringMetal from(ItemStack input) {
        for (HammeringMetal metal : values()) {
            if (input.is(metal.ingots)) {
                return metal;
            }
        }
        return null;
    }

    boolean requiresIronHammer() {
        return requiresIronHammer;
    }

    ItemStack sheet(Level level, ItemStack input) {
        return createSheet != null ? createSheet.get() : findZincSheet(level, input);
    }

    private static ItemStack findZincSheet(Level level, ItemStack input) {
        RecipeType<PressingRecipe> pressing = AllRecipeTypes.PRESSING.getType();
        var ingredient = new SingleRecipeInput(input.copyWithCount(1));
        ItemStack sheet = ItemStack.EMPTY;

        for (var entry : level.getRecipeManager().getAllRecipesFor(pressing)) {
            PressingRecipe recipe = entry.value();
            var outputs = recipe.getRollableResults();
            if (outputs.size() != 1 || outputs.getFirst().getChance() != 1.0F
                    || !recipe.matches(ingredient, level)) {
                continue;
            }

            ItemStack candidate = outputs.getFirst().getStack();
            if (candidate.getCount() != 1 || !isZincPlate(candidate)) {
                continue;
            }
            if (!sheet.isEmpty() && !ItemStack.isSameItemSameComponents(sheet, candidate)) {
                return ItemStack.EMPTY;
            }
            sheet = candidate;
        }
        return sheet.copy();
    }

    private static boolean isZincPlate(ItemStack stack) {
        String name = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        return stack.is(ZINC_PLATES) || stack.is(ZINC_ITEM_PLATES)
            || name.equals("zinc_sheet") || name.equals("zinc_plate");
    }

    private static TagKey<Item> commonTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
    }
}
