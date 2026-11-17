package io.github.tilosophy.createbetterhammers.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import io.github.tilosophy.createbetterhammers.CreateBetterHammers;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WrenchItem.class, remap = false)
abstract class WrenchItemMixin {
    @ModifyExpressionValue(method = "wrenchInstaKillsMinecarts", at = @At(value = "INVOKE",
        target = "Lcom/tterrag/registrate/util/entry/ItemEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean createBetterHammers$acceptPneumaticHammer(boolean isWrench, AttackEntityEvent event) {
        return isWrench || event.getEntity().getMainHandItem().is(CreateBetterHammers.PNEUMATIC_HAMMER.get());
    }
}
