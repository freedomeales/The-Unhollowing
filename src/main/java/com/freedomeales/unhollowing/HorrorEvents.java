package com.freedomeales.unhollowing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mod.EventBusSubscriber(modid = UnhollowingMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@SuppressWarnings("null")
public final class HorrorEvents {
    private static final Map<BlockPos, Long> ILLUSION_BLOCKS = new HashMap<>();

    private HorrorEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide
                || !(event.player instanceof net.minecraft.server.level.ServerPlayer player)) {
            return;
        }

        updateIllusions(player);
        updateStillMobs(player);
        if (player.tickCount % 20 != 0) {
            return;
        }

        int depth = Math.max(0, 48 - player.blockPosition().getY());
        boolean underground = player.getY() <= 48.0D;
        boolean dark = player.level().getMaxLocalRawBrightness(player.blockPosition()) <= 7;
        if (underground && dark) {
            extinguishTorchBehindPlayer(player);
            distortNearbyAnimals(player, depth);
        }
        if (!underground || !dark || player.getRandom().nextInt(4) != 0) {
            return;
        }

        if (!player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(32.0D),
                other -> other != player).isEmpty()
                || !player.level().getEntitiesOfClass(WatcherEntity.class, player.getBoundingBox().inflate(48.0D),
                        watcher -> watcher.isAlive()).isEmpty()) {
            return;
        }

        player.level().playSound(null, player.blockPosition(), SoundEvents.AMBIENT_CAVE.value(), SoundSource.AMBIENT,
            0.7F, 0.42F + player.getRandom().nextFloat() * 0.16F);
        if (depth > 20 && player.getRandom().nextBoolean()) {
            player.level().playSound(null, player.blockPosition(), SoundEvents.WARDEN_TENDRIL_CLICKS,
                SoundSource.AMBIENT, 0.35F, 0.55F);
        }

        BlockPos spawnPos = findWatcherPosition(player);
        if (spawnPos == null) {
            return;
        }

        WatcherEntity watcher = UnhollowingMod.WATCHER.get().create(player.level());
        if (watcher == null) {
            return;
        }
        watcher.moveTo(spawnPos, player.getYRot() + 180.0F, 0.0F);
        player.level().addFreshEntity(watcher);
        if (depth > 35) {
            spawnFalseStructure(player);
            giveNameBook(player);
        }
    }

    private static void updateStillMobs(net.minecraft.server.level.ServerPlayer player) {
        for (Mob mob : player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(12.0D),
            mob -> !(mob instanceof WatcherEntity))) {
            CompoundTag data = mob.getPersistentData();
            int stillTicks = data.getInt("unhollowing_still_ticks");
            if (stillTicks > 0) {
                data.putInt("unhollowing_still_ticks", stillTicks - 1);
                mob.setDeltaMovement(Vec3.ZERO);
                mob.getLookControl().setLookAt(player, 30.0F, 30.0F);
            } else if (player.tickCount % 100 == 0 && player.getRandom().nextInt(8) == 0) {
                data.putInt("unhollowing_still_ticks", 60);
            }
        }
    }

    private static void extinguishTorchBehindPlayer(net.minecraft.server.level.ServerPlayer player) {
        if (player.getRandom().nextInt(5) != 0) {
            return;
        }
        BlockPos origin = player.blockPosition();
        Vec3 look = player.getLookAngle();
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-6, -3, -6), origin.offset(6, 3, 6))) {
            BlockState state = player.level().getBlockState(pos);
            if (!isTorch(state.getBlock())) {
                continue;
            }
            Vec3 toTorch = Vec3.atCenterOf(pos).subtract(player.getEyePosition());
            if (look.dot(toTorch.normalize()) < -0.35D) {
                player.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                player.level().playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 0.8F);
                return;
            }
        }
    }

    private static boolean isTorch(net.minecraft.world.level.block.Block block) {
        return block == Blocks.TORCH || block == Blocks.WALL_TORCH || block == Blocks.SOUL_TORCH
                || block == Blocks.SOUL_WALL_TORCH;
    }

    private static void distortNearbyAnimals(net.minecraft.server.level.ServerPlayer player, int depth) {
        if (depth < 12 || player.getRandom().nextInt(10) != 0) {
            return;
        }
        for (Animal animal : player.level().getEntitiesOfClass(Animal.class, player.getBoundingBox().inflate(16.0D),
                Animal::isAlive)) {
            animal.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120, 0, false, false));
            animal.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false));
            animal.getPersistentData().putBoolean("unhollowing_distorted", true);
            animal.getLookControl().setLookAt(player, 20.0F, 20.0F);
            break;
        }
    }

    private static void spawnFalseStructure(net.minecraft.server.level.ServerPlayer player) {
        BlockPos base = player.blockPosition().relative(Direction.NORTH, 16);
        if (!player.level().isEmptyBlock(base) || !player.level().isEmptyBlock(base.above())) {
            return;
        }
        for (int height = 0; height < 3; height++) {
            BlockPos pos = base.above(height);
            player.level().setBlock(pos, UnhollowingMod.BLACKBARK.get().defaultBlockState(), 3);
            ILLUSION_BLOCKS.put(pos.immutable(), player.level().getGameTime() + 120L);
        }
    }

    private static void updateIllusions(net.minecraft.server.level.ServerPlayer player) {
        Iterator<Map.Entry<BlockPos, Long>> iterator = ILLUSION_BLOCKS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Long> entry = iterator.next();
            if (entry.getValue() <= player.level().getGameTime()
                    || player.distanceToSqr(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ()) < 25.0D) {
                if (player.level().getBlockState(entry.getKey()).is(UnhollowingMod.BLACKBARK.get())) {
                    player.level().removeBlock(entry.getKey(), false);
                }
                iterator.remove();
            }
        }
    }

    private static void giveNameBook(net.minecraft.server.level.ServerPlayer player) {
        if (player.getPersistentData().getBoolean("unhollowing_name_book") || player.getRandom().nextInt(8) != 0) {
            return;
        }
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        CompoundTag tag = book.getOrCreateTag();
        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf(net.minecraft.network.chat.Component.Serializer.toJson(
            net.minecraft.network.chat.Component.literal(
                "You are not alone, " + player.getName().getString() + ".\n\nDo not answer when it calls."))));
        tag.put("pages", pages);
        tag.putString("title", "A Familiar Hand");
        tag.putString("author", player.getName().getString());
        player.getInventory().add(book);
        player.getPersistentData().putBoolean("unhollowing_name_book", true);
    }

    private static BlockPos findWatcherPosition(net.minecraft.server.level.ServerPlayer player) {
        double backwardX = -player.getLookAngle().x * 12.0D;
        double backwardZ = -player.getLookAngle().z * 12.0D;
        BlockPos candidate = BlockPos.containing(player.getX() + backwardX, player.getY(), player.getZ() + backwardZ);
        BlockState feet = player.level().getBlockState(candidate);
        BlockState head = player.level().getBlockState(candidate.above());
        if (feet.isAir() && head.isAir()
            && player.level().getBlockState(candidate.below()).isSolidRender(player.level(), candidate.below())) {
            return candidate;
        }
        return null;
    }
}