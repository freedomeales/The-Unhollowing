package com.freedomeales.unhollowing;

import javax.annotation.Nonnull;


import java.util.List;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;


@SuppressWarnings("null")
public final class WatcherEntity extends Monster {

    public WatcherEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        setPersistenceRequired();
    }

    // Full 3-block eye height
    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return 2.8F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.40D)
                .add(Attributes.FOLLOW_RANGE, 800.0D)
                .add(Attributes.ATTACK_DAMAGE, 120.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));
        goalSelector.addGoal(7, new RandomStrollGoal(this, 0.7D));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 16.0F));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
public void tick()
 {
        super.tick();

        Level level = level();

        // --- AUTO-SUMMON WHEN ALONE (3 MINUTES) ---
        if (!level.isClientSide && level.players().size() == 1) {

            Player solo = level.players().get(0);

            int timer = solo.getPersistentData().getInt("unhollowing_alone_timer");
            timer++;
            solo.getPersistentData().putInt("unhollowing_alone_timer", timer);

            if (timer >= 3600) { // 3 minutes
                solo.getPersistentData().putInt("unhollowing_alone_timer", 0);

              WatcherEntity watcher = new WatcherEntity(
        UnhollowingEntities.WATCHER.get(),
        level()
);



                watcher.moveTo(
                        solo.getX() + 8,
                        solo.getY(),
                        solo.getZ() + 8,
                        level.random.nextFloat() * 360F,
                        0
                );

                level.addFreshEntity(watcher);
            }
        }

        // --- MULTIPLAYER WATCHING BEHAVIOR ---
        List<Player> nearbyPlayers = level.getEntitiesOfClass(
                Player.class,
                getBoundingBox().inflate(40.0D)
        );

        if (nearbyPlayers.size() >= 2) {

            setTarget(null);

            if (tickCount % 40 == 0) {
                double dx = (random.nextDouble() - 0.5) * 30;
                double dz = (random.nextDouble() - 0.5) * 30;
                getNavigation().moveTo(getX() + dx, getY(), getZ() + dz, 0.6D);
            }

            Player p = nearbyPlayers.get(0);
            int habit = p.getPersistentData().getInt("unhollowing_sprint_habit");
            habit += 10;
            p.getPersistentData().putInt("unhollowing_sprint_habit", habit);

            return;
        }

        // --- FASTER LEARNING, NEVER FORGET ---
        if (getTarget() instanceof Player player && player.isSprinting()) {
            int habit = player.getPersistentData().getInt("unhollowing_sprint_habit");
            habit += 30;
            player.getPersistentData().putInt("unhollowing_sprint_habit", habit);
        }

        // --- ORIGINAL HORROR LOGIC BELOW (kept exactly as you wrote it) ---

        Player player = level.getNearestPlayer(this, 800);
        if (player == null) return;

        if (player.isSprinting()) {
            player.getPersistentData().putInt("unhollowing_sprint_habit", 1200);
        }

        if (distanceTo(player) <= 4.5D && tickCount % 40 == 0) {
            breakInFrontOfPlayer((ServerPlayer) player);
        }

        int sprintHabit = player.getPersistentData().getInt("unhollowing_sprint_habit");

        // NEVER FORGET → decay removed

        double learnedSpeed = 0.40D;

        if (sprintHabit > 800) {
            learnedSpeed = 0.55D;
        } else if (sprintHabit > 400) {
            learnedSpeed = 0.48D;
        } else if (sprintHabit > 200) {
            learnedSpeed = 0.44D;
        }

        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(learnedSpeed);

        double distance = distanceTo(player);
        if (distance <= 12.0D && hasLineOfSight(player) && player.getRandom().nextInt(3) == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
            level.playSound(null, blockPosition(), SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.0F, 0.45F);
        }

        if (sprintHabit > 0 && distance > 12.0D) {
            setTarget(player);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return UnhollowingMod.FORGOTTEN_CALL.get();
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull net.minecraft.world.damagesource.DamageSource source) {
        return UnhollowingMod.FORGOTTEN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return UnhollowingMod.FORGOTTEN_DEATH.get();
    }

    private void breakInFrontOfPlayer(ServerPlayer player) {

        BlockPos target = player.blockPosition().relative(player.getDirection());
        BlockState state = level().getBlockState(target);
        float hardness = state.getDestroySpeed(level(), target);

        if (!state.isAir()
                && hardness >= 0.0F && hardness <= 3.0F
                && state.getBlock() != Blocks.BEDROCK
                && state.getBlock() != Blocks.WATER
                && state.getBlock() != Blocks.LAVA) {

            level().destroyBlock(target, true, player);

            level().playSound(null, target, UnhollowingMod.FORGOTTEN_CALL.get(), SoundSource.HOSTILE, 0.45F, 0.55F);

            String lastBroken = level().registryAccess()
        .registryOrThrow(net.minecraft.core.registries.Registries.BLOCK)
        .getKey(state.getBlock())
        .toString();


            player.getPersistentData().putString("unhollowing_last_broken_block", lastBroken);
        }
    }
}
