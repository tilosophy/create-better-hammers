package io.github.tilosophy.createbetterhammers;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateBetterHammers.ID)
public final class PneumaticPower {
    private static final int USES_PER_STANDARD_TANK = 200;
    private static final AttributeModifier SPEED_BOOST = new AttributeModifier(
        ResourceLocation.fromNamespaceAndPath(CreateBetterHammers.ID, "pneumatic_attack_speed"),
        0.2, AttributeModifier.Operation.ADD_VALUE);

    private PneumaticPower() {}

    static boolean spendAir(LivingEntity user) {
        return !user.level().isClientSide() && BacktankUtil.canAbsorbDamage(user, USES_PER_STANDARD_TANK);
    }

    @SubscribeEvent
    public static void updateAttackSpeed(PlayerTickEvent.Post event) {
        var player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        var speed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (speed == null) {
            return;
        }

        boolean boosted = speed.hasModifier(SPEED_BOOST.id());
        boolean powered = player.isAlive() && player.getMainHandItem().is(CreateBetterHammers.PNEUMATIC_HAMMER.get())
            && !BacktankUtil.getAllWithAir(player).isEmpty();
        if (powered && !boosted) {
            speed.addTransientModifier(SPEED_BOOST);
        } else if (!powered && boosted) {
            speed.removeModifier(SPEED_BOOST.id());
        }
    }
}
