package io.github.tilosophy.createbetterhammers.preview;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(modid = "create_better_hammers")
public final class PreviewWorld {
    @SubscribeEvent
    public static void started(ServerStartedEvent event) {
        var server = event.getServer();
        if (!server.isDedicatedServer()) return;
        var level = server.overworld();
        for (int x = -12; x <= 12; x++) for (int z = -12; z <= 12; z++)
            level.setBlockAndUpdate(new BlockPos(x, 64, z), Blocks.SMOOTH_STONE.defaultBlockState());
        level.setDefaultSpawnPos(new BlockPos(0, 65, 0), 0);
        level.setDayTime(6000);
        level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, server);
        level.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, server);
        server.halt(false);
    }
}
