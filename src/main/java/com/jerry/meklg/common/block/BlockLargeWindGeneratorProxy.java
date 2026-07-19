package com.jerry.meklg.common.block;

import com.jerry.mekmm.Mekmm;

import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeHasBounding;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.block.states.IStateFluidLoggable;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import com.jerry.meklg.common.block.attribute.AttributeLargeWindGeneratorProxy;
import com.jerry.meklg.common.content.blocktype.LargeGeneratorBlockShapes;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class BlockLargeWindGeneratorProxy extends Block implements IStateFluidLoggable {

    private static final int OFFSET_XZ_SHIFT = 5;

    public static final IntegerProperty OFFSET_X = IntegerProperty.create("offset_x", 0, 10);
    public static final IntegerProperty OFFSET_Y = IntegerProperty.create("offset_y", 0, 36);
    public static final IntegerProperty OFFSET_Z = IntegerProperty.create("offset_z", 0, 10);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final ThreadLocal<Set<BlockPos>> SUPPRESSED_REMOVALS = ThreadLocal.withInitial(HashSet::new);

    public BlockLargeWindGeneratorProxy(BlockBehaviour.Properties properties) {
        super(BlockStateHelper.applyLightLevelAdjustments(properties.dynamicShape()
                .noOcclusion()
                .isViewBlocking(BlockStateHelper.NEVER_PREDICATE)
                .pushReaction(PushReaction.BLOCK)));
        registerDefaultState(BlockStateHelper.getDefaultState(stateDefinition.any()
                .setValue(OFFSET_X, encodeHorizontalOffset(0))
                .setValue(OFFSET_Y, 0)
                .setValue(OFFSET_Z, encodeHorizontalOffset(0))
                .setValue(FACING, Direction.NORTH)));
    }

    public static BlockState applyProxyData(BlockState state, Direction facing, BlockPos offset) {
        if (!facing.getAxis().isHorizontal()) {
            throw new IllegalArgumentException("Large wind generator proxy requires a horizontal facing: " + facing);
        }
        if (!(state.getBlock() instanceof BlockLargeWindGeneratorProxy)) {
            throw new IllegalArgumentException("Expected a large wind generator proxy block state, got " + state);
        }
        return state.setValue(FACING, facing)
                .setValue(OFFSET_X, encodeHorizontalOffset(offset.getX()))
                .setValue(OFFSET_Y, offset.getY())
                .setValue(OFFSET_Z, encodeHorizontalOffset(offset.getZ()));
    }

    public static BlockPos getOffset(BlockState state) {
        return new BlockPos(decodeHorizontalOffset(state.getValue(OFFSET_X)), state.getValue(OFFSET_Y), decodeHorizontalOffset(state.getValue(OFFSET_Z)));
    }

    public static @Nullable BlockPos getMainBlockPos(BlockState state, BlockPos pos) {
        if (!(state.getBlock() instanceof BlockLargeWindGeneratorProxy)) {
            return null;
        }
        BlockPos offset = getOffset(state);
        return offset.equals(BlockPos.ZERO) ? null : pos.subtract(offset);
    }

    public static @Nullable BlockPos getMainBlockPos(BlockGetter world, BlockPos pos) {
        return getMainBlockPos(world.getBlockState(pos), pos);
    }

    public static boolean hasValidMainBlock(BlockGetter world, BlockState state, BlockPos pos) {
        return getValidMainPos(world, state, pos, false) != null;
    }

    public static void removeProxyBlock(Level level, BlockPos pos) {
        Set<BlockPos> suppressed = SUPPRESSED_REMOVALS.get();
        BlockPos immutablePos = pos.immutable();
        suppressed.add(immutablePos);
        try {
            level.removeBlock(pos, false);
        } finally {
            suppressed.remove(immutablePos);
            if (suppressed.isEmpty()) {
                SUPPRESSED_REMOVALS.remove();
            }
        }
    }

    public static <CAP> void proxyCapability(RegisterCapabilitiesEvent event, BlockCapability<CAP, @Nullable Direction> capability, Block... proxyBlocks) {
        event.registerBlock(capability, (level, pos, state, blockEntity, side) -> getOffsetCapability(level, pos, state, capability, side), proxyBlocks);
    }

    public static <CAP, CONTEXT> void alwaysProxyCapability(RegisterCapabilitiesEvent event, BlockCapability<CAP, CONTEXT> capability, Block... proxyBlocks) {
        event.registerBlock(capability, (level, pos, state, blockEntity, context) -> {
            BlockPos mainPos = getValidMainPos(level, state, pos, false);
            return mainPos == null ? null : WorldUtils.getCapability(level, capability, mainPos, context);
        }, proxyBlocks);
    }

    private static <CAP> @Nullable CAP getOffsetCapability(Level level, BlockPos pos, BlockState state,
                                                           BlockCapability<CAP, @Nullable Direction> capability, @Nullable Direction side) {
        BlockPos mainPos = getValidMainPos(level, state, pos, false);
        if (mainPos == null) {
            return null;
        }
        BlockEntity mainTile = WorldUtils.getTileEntity(level, mainPos);
        if (mainTile instanceof IBoundingBlock boundingBlock) {
            return boundingBlock.getOffsetCapability(capability, side, pos.subtract(mainPos));
        }
        warnInvalidMain(level, pos, mainPos, "Unable to proxy capability from large wind generator proxy");
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OFFSET_X, OFFSET_Y, OFFSET_Z, FACING);
        BlockStateHelper.fillBlockStateContainer(this, builder);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return null;
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }

    @Override
    protected boolean canBeReplaced(BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        BlockPos mainPos = getValidMainPos(world, state, pos, true);
        if (mainPos == null) {
            return InteractionResult.FAIL;
        }
        BlockState mainState = world.getBlockState(mainPos);
        return mainState.useWithoutItem(world, player, withHitResultForMain(hit, mainPos));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hit) {
        BlockPos mainPos = getValidMainPos(world, state, pos, true);
        if (mainPos == null) {
            return InteractionResult.FAIL;
        }
        BlockState mainState = world.getBlockState(mainPos);
        return mainState.useItemOn(stack, world, player, hand, withHitResultForMain(hit, mainPos));
    }

    private BlockHitResult withHitResultForMain(BlockHitResult hit, BlockPos mainPos) {
        Vec3 location = mainPos.getCenter().relative(hit.getDirection(), 0.5);
        if (hit.getType() == Type.MISS) {
            return BlockHitResult.miss(location, hit.getDirection(), mainPos);
        }
        return new BlockHitResult(location, hit.getDirection(), mainPos, hit.isInside());
    }

    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        world.invalidateCapabilities(pos);
        if (state.is(newState.getBlock()) || SUPPRESSED_REMOVALS.get().contains(pos)) {
            return;
        }
        BlockPos mainPos = getValidMainPos(world, state, pos, false);
        if (mainPos != null) {
            BlockState mainState = world.getBlockState(mainPos);
            if (!mainState.isAir()) {
                world.removeBlock(mainPos, false);
            }
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
        level.invalidateCapabilities(pos);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        level.invalidateCapabilities(pos);
        if (!level.isClientSide() && !hasValidMainBlock(level, state, pos)) {
            removeProxyBlock(level, pos);
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData, Player player) {
        BlockPos mainPos = getValidMainPos(world, state, pos, true);
        if (mainPos == null) {
            return ItemStack.EMPTY;
        }
        BlockState mainState = world.getBlockState(mainPos);
        return mainState.getBlock().getCloneItemStack(world, mainPos, mainState, includeData, player);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, ItemStack toolStack, boolean willHarvest,
                                       FluidState fluidState) {
        if (willHarvest) {
            return true;
        }
        BlockPos mainPos = getValidMainPos(world, state, pos, true);
        if (mainPos != null) {
            BlockState mainState = world.getBlockState(mainPos);
            if (!mainState.isAir()) {
                mainState.onDestroyedByPlayer(world, mainPos, player, toolStack, false, mainState.getFluidState());
            }
        }
        return super.onDestroyedByPlayer(state, world, pos, player, toolStack, false, fluidState);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockPos mainPos = getValidMainPos(level, state, pos, true);
        if (mainPos != null) {
            BlockState mainState = level.getBlockState(mainPos);
            if (!mainState.isAir()) {
                mainState.getBlock().playerWillDestroy(level, mainPos, mainState, player);
                return state;
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onExplosionHit(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion,
                                  BiConsumer<ItemStack, BlockPos> dropConsumer) {
        BlockPos mainPos = getValidMainPos(level, state, pos, true);
        if (mainPos == null) {
            super.onExplosionHit(state, level, pos, explosion, dropConsumer);
        } else {
            level.getBlockState(mainPos).onExplosionHit(level, mainPos, explosion, dropConsumer);
        }
    }

    @Override
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience) {
        BlockPos mainPos = getValidMainPos(level, state, pos, false);
        if (mainPos != null) {
            BlockState mainState = level.getBlockState(mainPos);
            if (!mainState.isAir()) {
                mainState.spawnAfterBreak(level, mainPos, stack, dropExperience);
            }
        }
        super.spawnAfterBreak(state, level, pos, stack, dropExperience);
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        BlockPos mainPos = getValidMainPos(world, state, pos, true);
        if (mainPos != null) {
            BlockState mainState = world.getBlockState(mainPos);
            mainState.getBlock().playerDestroy(world, player, mainPos, mainState, WorldUtils.getTileEntity(world, mainPos), stack);
        } else {
            super.playerDestroy(world, player, pos, state, blockEntity, stack);
        }
        removeProxyBlock(world, pos);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighborPos) {
        BlockPos mainPos = getValidMainPos(level, state, pos, false);
        if (mainPos != null) {
            level.getBlockState(mainPos).onNeighborChange(level, mainPos, neighborPos);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        if (!level.isClientSide()) {
            BlockPos mainPos = getValidMainPos(level, state, pos, false);
            if (mainPos == null) {
                removeProxyBlock(level, pos);
            } else {
                BlockState mainState = level.getBlockState(mainPos);
                mainState.handleNeighborChanged(level, mainPos, mainState.getBlock(), orientation, movedByPiston);
                BlockEntity mainTile = WorldUtils.getTileEntity(level, mainPos);
                if (mainTile instanceof IBoundingBlock boundingBlock) {
                    int newSignal = level.getBestNeighborSignal(pos);
                    boundingBlock.onBoundingBlockPowerChange(pos, Redstone.SIGNAL_NONE, newSignal);
                }
            }
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos, Direction direction) {
        if (!world.isClientSide()) {
            BlockPos mainPos = getValidMainPos(world, blockState, pos, false);
            if (mainPos != null && WorldUtils.getTileEntity(world, mainPos) instanceof IBoundingBlock boundingBlock && boundingBlock.supportsComparator()) {
                return boundingBlock.getBoundingComparatorSignal(pos.subtract(mainPos));
            }
        }
        return Redstone.SIGNAL_NONE;
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
        BlockPos mainPos = getValidMainPos(world, state, pos, false);
        if (mainPos == null) {
            return super.getDestroyProgress(state, player, world, pos);
        }
        return world.getBlockState(mainPos).getDestroyProgress(player, world, mainPos);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
        BlockPos mainPos = getValidMainPos(world, state, pos, false);
        if (mainPos == null) {
            return super.getExplosionResistance(state, world, pos, explosion);
        }
        return world.getBlockState(mainPos).getExplosionResistance(world, mainPos, explosion);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockPos mainPos = getValidMainPos(level, state, pos, false);
        if (mainPos == null) {
            return false;
        }
        BlockEntity mainTile = WorldUtils.getTileEntity(level, mainPos);
        return mainTile instanceof IBoundingBlock boundingBlock && boundingBlock.triggerBoundingEvent(pos.subtract(mainPos), id, param);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getProxyPartShape(state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getProxyPartShape(state);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getProxyShape(state, world, pos, context, BlockStateBase::getVisualShape);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return getFluid(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos currentPos,
                                     Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
        updateFluids(level, currentPos, state, scheduledTickAccess);
        return super.updateShape(state, level, scheduledTickAccess, currentPos, facing, facingPos, facingState, random);
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos) {
        return getProxyShape(state, world, pos, null, (mainState, level, mainPos, ignored) -> mainState.getBlockSupportShape(level, mainPos));
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return getProxyShape(state, world, pos, null, (mainState, level, mainPos, ignored) -> mainState.getInteractionShape(level, mainPos));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    private static <CONTEXT extends @Nullable CollisionContext> VoxelShape getProxyShape(BlockState state, BlockGetter world, BlockPos pos,
                                                                                         CONTEXT context, ShapeProxy<CONTEXT> proxy) {
        BlockPos mainPos = getValidMainPos(world, state, pos, false);
        if (mainPos == null) {
            return Shapes.empty();
        }
        BlockState mainState = world.getBlockState(mainPos);
        BlockPos offset = pos.subtract(mainPos);
        return proxy.getShape(mainState, world, mainPos, context).move(-offset.getX(), -offset.getY(), -offset.getZ());
    }

    private static VoxelShape getProxyPartShape(BlockState state) {
        VoxelShape shape = LargeGeneratorBlockShapes.getLargeWindGeneratorPartShape(state.getValue(FACING), getOffset(state));
        return shape == null ? Shapes.empty() : shape;
    }

    private static int encodeHorizontalOffset(int offset) {
        return offset + OFFSET_XZ_SHIFT;
    }

    private static int decodeHorizontalOffset(int offset) {
        return offset - OFFSET_XZ_SHIFT;
    }

    private static boolean isValidLargeWindMain(BlockState mainState) {
        return Attribute.get(mainState, AttributeLargeWindGeneratorProxy.class) != null ||
                Attribute.get(mainState, AttributeHasBounding.class) instanceof AttributeLargeWindGeneratorProxy;
    }

    private static @Nullable BlockPos getValidMainPos(BlockGetter world, BlockState state, BlockPos pos, boolean logFailure) {
        BlockPos mainPos = getMainBlockPos(state, pos);
        if (mainPos == null) {
            if (logFailure) {
                Mekmm.LOGGER.warn("Unable to resolve main position for large wind generator proxy at {}", pos);
            }
            return null;
        }
        BlockState mainState = world.getBlockState(mainPos);
        if (isValidLargeWindMain(mainState)) {
            return mainPos;
        }
        if (logFailure) {
            warnInvalidMain(world, pos, mainPos, "Invalid main block for large wind generator proxy");
        }
        return null;
    }

    private static void warnInvalidMain(BlockGetter world, BlockPos proxyPos, BlockPos mainPos, String message) {
        if (world instanceof Level level) {
            Mekmm.LOGGER.warn("{} at {} in {}; expected large wind generator main at {}, found {}", message, proxyPos,
                    level.dimension().identifier(), mainPos, level.getBlockState(mainPos).typeHolder().getRegisteredName());
        } else {
            Mekmm.LOGGER.warn("{} at {}; expected large wind generator main at {}", message, proxyPos, mainPos);
        }
    }

    @FunctionalInterface
    private interface ShapeProxy<CONTEXT extends @Nullable CollisionContext> {

        VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CONTEXT context);
    }
}
