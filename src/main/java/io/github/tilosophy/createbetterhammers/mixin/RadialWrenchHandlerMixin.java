package io.github.tilosophy.createbetterhammers.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchHandler;
import io.github.tilosophy.createbetterhammers.CreateBetterHammers;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RadialWrenchHandler.class, remap = false)
abstract class RadialWrenchHandlerMixin {
    @ModifyExpressionValue(method = "onKeyInput", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private static Item createBetterHammers$acceptPneumaticHammer(Item item) {
        return item == CreateBetterHammers.PNEUMATIC_HAMMER.get() ? AllItems.WRENCH.get() : item;
    }
}
