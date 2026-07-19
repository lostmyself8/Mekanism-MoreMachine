package com.jerry.meklg.common.block;

import com.jerry.mekmm.Mekmm;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

/**
 * Removes stale large wind generator proxy blocks when their saved chunks are loaded.
 */
@EventBusSubscriber(modid = Mekmm.MOD_ID)
public final class LargeWindGeneratorProxyCleanup {

    private LargeWindGeneratorProxyCleanup() {}

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        ServerLevel level = event.getLevel() instanceof ServerLevel serverLevel ? serverLevel : null;
        if (level == null || event.isNewChunk()) {
            return;
        }
        LevelChunk chunk = event.getChunk();
        ChunkPos chunkPos = chunk.getPos();
        for (int sectionIndex = 0; sectionIndex < chunk.getSections().length; sectionIndex++) {
            LevelChunkSection section = chunk.getSections()[sectionIndex];
            if (!section.maybeHas(state -> state.getBlock() instanceof BlockLargeWindGeneratorProxy)) {
                continue;
            }
            removeInvalidProxies(level, chunkPos, level.getMinSectionY() + sectionIndex, section);
        }
    }

    private static void removeInvalidProxies(ServerLevel level, ChunkPos chunkPos, int sectionY, LevelChunkSection section) {
        int minX = chunkPos.getMinBlockX();
        int minY = sectionY << 4;
        int minZ = chunkPos.getMinBlockZ();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < LevelChunkSection.SECTION_WIDTH; x++) {
            for (int y = 0; y < LevelChunkSection.SECTION_HEIGHT; y++) {
                for (int z = 0; z < LevelChunkSection.SECTION_WIDTH; z++) {
                    BlockState state = section.getBlockState(x, y, z);
                    if (!(state.getBlock() instanceof BlockLargeWindGeneratorProxy)) {
                        continue;
                    }
                    pos.set(minX + x, minY + y, minZ + z);
                    BlockPos mainPos = BlockLargeWindGeneratorProxy.getMainBlockPos(state, pos);
                    if (mainPos == null || (level.hasChunk(mainPos.getX() >> 4, mainPos.getZ() >> 4) && !BlockLargeWindGeneratorProxy.hasValidMainBlock(level, state, pos))) {
                        BlockLargeWindGeneratorProxy.removeProxyBlock(level, pos);
                    }
                }
            }
        }
    }
}
