package io.github.tilosophy.createbetterhammers;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@EventBusSubscriber(modid = CreateBetterHammers.ID)
public record SturdySheetStrike(Level level, BlockPos target, ItemStack hammer, ItemStack powder, int powderCount) {
    static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CreateBetterHammers.ID);

    // Unsaved and server-side. No attachment exists until the player makes the first strike.
    static final Supplier<AttachmentType<SturdySheetStrike>> PENDING =
        ATTACHMENTS.register("pending_sturdy_sheet_strike",
            () -> AttachmentType.<SturdySheetStrike>builder(() -> null).build());

    static void checkTarget(Player player, BlockPos clicked) {
        var pending = player.getExistingDataOrNull(PENDING);
        if (pending != null && (!pending.target.equals(clicked) || !pending.canContinue(player))) {
            player.removeData(PENDING);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        var pending = player.getExistingDataOrNull(PENDING);
        if (pending != null && !pending.canContinue(player)) {
            player.removeData(PENDING);
        }
    }

    private boolean canContinue(Player player) {
        return player.isAlive() && player.level() == level
            && player.getMainHandItem() == hammer && !hammer.isEmpty()
            && player.getOffhandItem() == powder && powder.getCount() == powderCount
            && HammerItem.isRaisedWorksite(player, target) && player.canInteractWithBlock(target, 0)
            && level.getBlockState(target).is(Blocks.MAGMA_BLOCK);
    }
}
