/*
package com.github.lumin.modules.impl.player;

import com.github.lumin.modules.Category;
import com.github.lumin.modules.Module;
import com.github.lumin.settings.impl.BoolSetting;
import com.github.lumin.settings.impl.ColorSetting;
import com.github.lumin.settings.impl.IntSetting;
import com.github.lumin.settings.impl.ModeSetting;
import com.github.lumin.utils.math.MathUtils;
import com.github.lumin.utils.player.MoveUtils;
import com.github.lumin.utils.rotation.RaytraceUtils;
import com.github.lumin.utils.rotation.RotationUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector2f;

import java.awt.*;

public class Scaffold extends Module {

    public static final Scaffold INSTANCE = new Scaffold();

    public Scaffold() {
        super("Scaffold", "自动搭路", "Automatically place block under your feet", "紫薇", Category.PLAYER);
    }

    private final ModeSetting mode = modeSetting("Mode", "模式", "Telly", new String[]{"GodBridge", "Telly"});
    private final ModeSetting swapMode = modeSetting("Swap Mode", "切换模式", "Normal", new String[]{"None", "Normal", "InvSwitch", "Silent"});
    private final BoolSetting swapBack = boolSetting("SwapBack", "停用还原", true, () -> swapMode.is("Normal"));
    private final BoolSetting swingHand = boolSetting("Swing Hand", "挥手", true);
    private final IntSetting tellyTick = intSetting("Telly Tick", "Telly延迟", 0, 0, 8, 1, () -> mode.is("Telly"));
    private final BoolSetting keepY = boolSetting("Keep Y", "保持Y轴", true, () -> mode.is("Telly"));
    private final IntSetting rotationSpeed = intSetting("Rotation Speed", "旋转速度", 10, 1, 10, 1);
    private final IntSetting rotationBackSpeed = intSetting("Rotation Back Speed", "回转速度", 10, 0, 10, 1, () -> mode.is(Mode.Telly));
    private final BoolSetting sideCheck = boolSetting("Strict Side", "严格放置面", false);
    private final BoolSetting moveFix = boolSetting("Movement Fix", "移动修复", true);
    private final BoolSetting safeWalk = boolSetting("Safe Walk", "安全行走", true);

    private final BoolSetting render = boolSetting("Render", "渲染", true);
    private final BoolSetting fade = boolSetting("Fade", "变淡", false, render::getValue);
    private final BoolSetting shrink = boolSetting("Shrink", "收缩", true, render::getValue);
    private final ColorSetting sideColor = colorSetting("Side Color", "侧面颜色", new Color(255, 183, 197, 100), render::getValue);
    private final ColorSetting lineColor = colorSetting("Line Color", "线条颜色", new Color(255, 105, 180), render::getValue);

    private static final double[] placeOffsets = new double[]{
            0.03125, 0.09375, 0.15625, 0.21875, 0.28125, 0.34375, 0.40625, 0.46875, 0.53125, 0.59375, 0.65625, 0.71875, 0.78125, 0.84375, 0.90625, 0.96875
    };

    private int yLevel;
    private int airTicks;

    private boolean swapped;
    private boolean invSwapped;
    private boolean shouldSwapBack;

    private BlockInfo blockInfo;

    @Override
    public String getSuffix() {
        return mode.get().name();
    }

    @Override
    protected void onEnable() {
        blockInfo = null;
        swapped = false;
        invSwapped = false;
        shouldSwapBack = false;
    }

    @Override
    protected void onDisable() {
        blockInfo = null;
        if (shouldSwapBack) {
            InvUtil.swapBack();
        }
    }

    @EventHandler
    private void onMouse(MouseClickEvent event) {
        if (mc.currentScreen != null) return;
        if (event.getButton() == InputUtil.GLFW_MOUSE_BUTTON_LEFT || event.getButton() == InputUtil.GLFW_MOUSE_BUTTON_RIGHT) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMotion(MotionEvent e) {
        if (e.getType() == EventType.PRE && safeWalk.get() && mode.is(Mode.GodBridge)) {
            mc.options.sneakKey.setPressed(mc.player.isOnGround() && SafeWalk.isOnBlockEdge(0.3F));
        }
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        updateBlockInfo();

        MovementFix movementFix = moveFix.get() ? MovementFix.NORMAL : MovementFix.OFF;
        if (mode.is(Mode.Telly)) {
            if (mc.player.isOnGround()) {
                yLevel = MathHelper.floor(mc.player.getY()) - 1;
                airTicks = 0;
                blockInfo = null;
                Rotation rotation = new Rotation(mc.player.getYaw(), mc.player.getPitch());
                Managers.ROTATION.setRotations(rotation, rotationBackSpeed.get(), movementFix);
            } else {
                if (airTicks >= tellyTick.get() && blockInfo != null) {
                    FindItemResult item = findItem();
                    if (item.found()) {
                        Managers.ROTATION.setRotations(getRotation(blockInfo), rotationSpeed.get(), movementFix);
                        place(item);
                    }
                }
                airTicks++;
            }
        } else if (blockInfo != null) {
            FindItemResult item = findItem();
            if (item.found()) {
                Managers.ROTATION.setRotations(getRotation(blockInfo), rotationSpeed.get(), movementFix);
                place(item);
            }
        }

        switch (swapMode.get()) {
            case Silent -> {
                if (swapped) {
                    swapped = false;
                    InvUtil.swapBack();
                }
            }
            case InvSwitch -> {
                if (invSwapped) {
                    invSwapped = false;
                    InvUtil.invSwapBack();
                }
            }
        }
    }

    @EventHandler
    private void onStrafe(StrafeEvent event) {
        if (nullCheck()) return;
        if (mc.player.isOnGround() && MoveUtil.isMoving() && mode.is(Mode.Telly) && !mc.options.jumpKey.isPressed()) {
            mc.player.jump();
        }
    }

    private int getYLevel() {
        if (keepY.get() && !mc.options.jumpKey.isPressed() && MoveUtil.isMoving() && mode.is(Mode.Telly) && mc.player.fallDistance <= 0.25) {
            return yLevel;
        } else {
            return Mth.floor(mc.player.getY()) - 1;
        }
    }

    public static Vec3 getVec3(BlockPos pos, Direction face) {
        double x = (double) pos.getX() + 0.5;
        double y = (double) pos.getY() + 0.5;
        double z = (double) pos.getZ() + 0.5;
        if (face == Direction.UP || face == Direction.DOWN) {
            x += MathUtils.getRandom(0.3, -0.3);
            z += MathUtil.getRandom(0.3, -0.3);
        } else {
            y += MathUtil.getRandom(0.3, -0.3);
        }
        if (face == Direction.WEST || face == Direction.EAST) {
            z += MathUtil.getRandom(0.3, -0.3);
        }
        if (face == Direction.SOUTH || face == Direction.NORTH) {
            x += MathUtil.getRandom(0.3, -0.3);
        }
        return new Vec3(x, y, z);
    }

    private boolean validItem(ItemStack itemStack, BlockPos pos) {
        if (!(itemStack.getItem() instanceof BlockItem blockItem)) return false;

        Block block = blockItem.getBlock();

        if (block instanceof TntBlock) return false;

        if (!Block.isShapeFullCube(block.getDefaultState().getCollisionShape(mc.world, pos))) return false;
        return !(block instanceof FallingBlock) || !FallingBlock.canFallThrough(mc.world.getBlockState(pos));
    }

    private FindItemResult findItem() {
        switch (swapMode.get()) {
            case None -> {
                if (InvUtil.testInOffHand(itemStack -> validItem(itemStack, blockInfo.position))) {
                    return new FindItemResult(SlotUtil.OFFHAND, mc.player.getOffHandStack().getCount(), mc.player.getOffHandStack().getMaxCount());
                }
                if (InvUtil.testInMainHand(itemStack -> validItem(itemStack, blockInfo.position))) {
                    return new FindItemResult(mc.player.getInventory().getSelectedSlot(), mc.player.getMainHandStack().getCount(), mc.player.getMainHandStack().getMaxCount());
                }
                return new FindItemResult(-1, 0, 0);
            }
            case InvSwitch -> {
                return InvUtil.find(itemStack -> validItem(itemStack, blockInfo.position));
            }
            default -> {
                return InvUtil.findInHotbar(itemStack -> validItem(itemStack, blockInfo.position));
            }
        }
    }

    private void place(FindItemResult item) {
        if (!onAir()) return;
        if (!BlockUtil.canPlaceAt(blockInfo.blockPos)) return;

        switch (swapMode.get()) {
            case Normal -> {
                boolean should = swapBack.get();
                InvUtil.swap(item.slot(), should);
                shouldSwapBack = should;
            }
            case Silent -> swapped = InvUtil.swap(item.slot(), true);
            case InvSwitch -> invSwapped = InvUtil.invSwap(item.slot());
        }

        boolean hasRotated = RaytraceUtil.overBlock(Managers.ROTATION.getRotation(), blockInfo.dir, blockInfo.position, sideCheck.get());
        if (hasRotated) {
            ActionResult result = mc.interactionManager.interactBlock(mc.player, item.getHand(), new BlockHitResult(blockInfo.hitVec, blockInfo.dir, blockInfo.position, false));
            if (result.isAccepted()) {
                if (swingHand.get()) {
                    mc.player.swing(item.getHand());
                } else {
                    mc.getConnection().send(new ServerboundSwingPacket(item.getHand()));
                }
            }

            if (render.get()) {
                Managers.RENDER.add(blockInfo.blockPos, sideColor.get(), lineColor.get(), fade.get(), shrink.get());
            }
        }
    }

    private void updateBlockInfo() {
        Vec3 baseVec = mc.player.getEyePosition();
        BlockPos base = BlockPos.containing(baseVec.x, getYLevel(), baseVec.z);
        int baseX = base.getX();
        int baseZ = base.getZ();
        if (mc.level.getBlockState(base).entityCanStandOn(mc.level, base, mc.player)) return;
        if (checkBlock(baseVec, base)) {
            return;
        }
        for (int d = 1; d <= 6; d++) {
            if (checkBlock(baseVec, new BlockPos(baseX, getYLevel() - d, baseZ))) {
                return;
            }
            for (int x = 0; x <= d; x++) {
                for (int z = 0; z <= d - x; z++) {
                    int y = d - x - z;
                    for (int rev1 = 0; rev1 <= 1; rev1++) {
                        for (int rev2 = 0; rev2 <= 1; rev2++) {
                            if (checkBlock(baseVec, new BlockPos(baseX + (rev1 == 0 ? x : -x), getYLevel() - y, baseZ + (rev2 == 0 ? z : -z)))) {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean checkBlock(Vec3 baseVec, BlockPos pos) {
        if (!(mc.level.getBlockState(pos).getBlock() instanceof AirBlock) */
/*&& !(mc.level.getBlockState(pos).getBlock() instanceof FluidBlock)*//*
) {
            return false;
        }

        Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        for (Direction dir : Direction.values()) {
            Vec3 hit = center.add(new Vec3(dir.getUnitVec3i()).scale(0.5));
            Vec3i baseBlock = pos.offset(dir.getUnitVec3i());
            BlockPos baseBlockPos = new BlockPos(baseBlock.getX(), baseBlock.getY(), baseBlock.getZ());

            if (!mc.level.getBlockState(baseBlockPos).canBeReplaced()) continue;

            Vec3 relevant = hit.subtract(baseVec);
            if (relevant.lengthSqr() <= 4.5 * 4.5 && relevant.dot(new Vec3(dir.getUnitVec3i())) >= 0) {
                if (dir.getOpposite() == Direction.UP && mode.is("GodBridge") && MoveUtils.isMoving() && !mc.options.keyJump.isDown()) {
                    continue;
                }
                blockInfo = new BlockInfo(pos, new BlockPos(baseBlock), dir.getOpposite());
                return true;
            }
        }
        return false;
    }

    private Vector2f getRotation(BlockInfo blockCache) {
        if (onAir()) {
            return RotationUtils.calculate(blockCache.position, blockCache.dir);
        }

        double[] x = placeOffsets;
        double[] y = placeOffsets;
        double[] z = placeOffsets;

        BlockState state = mc.level.getBlockState(blockCache.position);
        VoxelShape shape = state.getCollisionShape(mc.level, blockCache.position);
        if (shape.isEmpty()) return RotationUtils.calculate(blockCache.position.getCenter());

        AABB box = shape.bounds();

        switch (blockCache.dir) {
            case NORTH -> z = new double[]{box.minZ};
            case EAST -> x = new double[]{box.maxX};
            case SOUTH -> z = new double[]{box.maxZ};
            case WEST -> x = new double[]{box.minX};
            case DOWN -> y = new double[]{box.minY};
            case UP -> y = new double[]{box.maxY};
        }

        float bestYaw = -1000.0F;
        float bestPitch = -1000.0F;
        float bestDiff = Float.MAX_VALUE;
        Vec3 bestHitVec = null;

        float baseYaw = Mth.wrapDegrees(mc.player.getYRot() - 180);
        float basePitch = mc.player.getXRot();

        for (double dx : x) {
            for (double dy : y) {
                for (double dz : z) {

                    double finalX = blockCache.position.getX() + dx;
                    double finalY = blockCache.position.getY() + dy;
                    double finalZ = blockCache.position.getZ() + dz;

                    if (x.length > 1) finalX = blockCache.position.getX() + box.minX + dx * (box.maxX - box.minX);
                    if (y.length > 1) finalY = blockCache.position.getY() + box.minY + dy * (box.maxY - box.minY);
                    if (z.length > 1) finalZ = blockCache.position.getZ() + box.minZ + dz * (box.maxZ - box.minZ);

                    Vec3 hitVec = new Vec3(finalX, finalY, finalZ);

                    Vector2f rotation = RotationUtils.calculate(hitVec);
                    float totalDiff = Math.abs(rotation.x - baseYaw) + Math.abs(rotation.y - basePitch);

                    if (totalDiff < bestDiff) {
                        boolean overBlock = RaytraceUtils.overBlock(rotation, blockCache.dir, blockCache.position, sideCheck.getValue());

                        if (overBlock) {
                            bestYaw = rotation.x;
                            bestPitch = rotation.y;
                            bestDiff = totalDiff;
                            bestHitVec = hitVec;
                        }
                    }
                }
            }
        }

        if (bestYaw != -1000.0F) {
            blockCache.hitVec = bestHitVec;
            return new Vector2f(bestYaw, bestPitch);
        }

        return RotationUtils.calculate(blockCache.position.getCenter());
    }

    private boolean onAir() {
        Vec3 baseVec = mc.player.getEyePosition();
        BlockPos base = BlockPos.containing(baseVec.x, getYLevel(), baseVec.z);
        return mc.level.getBlockState(base).getBlock() instanceof AirBlock || mc.level.getBlockState(base).getBlock() instanceof WaterlilyBlock;
    }

    private static class BlockInfo {
        private final BlockPos blockPos;
        private final BlockPos position;
        private final Direction dir;
        private Vec3 hitVec;

        public BlockInfo(BlockPos blockPos, BlockPos position, Direction dir) {
            this.blockPos = blockPos;
            this.position = position;
            this.dir = dir;
            this.hitVec = new Vec3(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5)
                    .add(new Vec3(dir.getUnitVec3i()).scale(0.5));
        }
    }
    
}
*/
