package com.freedomeales.unhollowing;

import javax.annotation.Nonnull;
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
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings("null")
public final class WatcherEntity extends Monster {
    public WatcherEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 45.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.ATTACK_DAMAGE, 90.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.55D);
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
    public void tick() {
        super.tick();
        if (level().isClientSide || tickCount % 20 != 0 || !(getTarget() instanceof ServerPlayer player)) {
            return;
        }

        if (player.isSprinting()) {
            player.getPersistentData().putInt("unhollowing_sprint_habit", 1200);
        }

        if (distanceTo(player) <= 4.5D && tickCount % 40 == 0) {
            breakInFrontOfPlayer(player);
        }
        int sprintHabit = player.getPersistentData().getInt("unhollowing_sprint_habit");
        if (sprintHabit > 0) {
            player.getPersistentData().putInt("unhollowing_sprint_habit", sprintHabit - 20);
        }

        double distance = distanceTo(player);
        if (distance <= 12.0D && hasLineOfSight(player) && player.getRandom().nextInt(3) == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
            level().playSound(null, blockPosition(), SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.0F, 0.45F);
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
        net.minecraft.core.BlockPos target = blockPosition().relative(getDirection());
        BlockState state = level().getBlockState(target);
        float hardness = state.getDestroySpeed(level(), target);
        if (!state.isAir() && hardness >= 0.0F && hardness <= 3.0F
                && state.getBlock() != Blocks.BEDROCK && state.getBlock() != Blocks.WATER
                && state.getBlock() != Blocks.LAVA) {
            level().destroyBlock(target, true, this);
            level().playSound(null, target, UnhollowingMod.FORGOTTEN_CALL.get(), SoundSource.HOSTILE, 0.45F, 0.55F);
        }
    }
}