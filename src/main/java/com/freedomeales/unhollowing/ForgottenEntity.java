package com.freedomeales.unhollowing;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;

@SuppressWarnings("null")
public final class ForgottenEntity extends Monster {
    
    private int apparitionCooldown = 0;
    private Player targetPlayer = null;
    private boolean isPhasing = false;
    private int cornerPeekTimer = 0;
    private Vec3 cornerPeekPosition = null;

    public ForgottenEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        setPersistenceRequired();
    }

    // Tall — 3.5 blocks
    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return 3.2F;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 32.0F));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        
        // Target alone players
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<Player>(this, Player.class, 
            false, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 40.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.ATTACK_DAMAGE, 6.0D)
            .add(Attributes.FOLLOW_RANGE, 64.0D)
            .add(Attributes.ATTACK_KNOCKBACK, 0.5D);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            return;
        }

        // Handle apparition visibility
        handleApparition();
        
        // Handle corner peeking (rare, atmospheric effect)
        updateCornerPeekBehavior();
        
        // Tracking behavior
        if (this.getTarget() instanceof ServerPlayer player) {
            targetPlayer = player;
            
            // LEARN SPRINT HABITS — NEVER FORGET
            if (player.isSprinting()) {
                int habit = player.getPersistentData().getInt("unhollowing_forgotten_habit");
                habit += 25;
                player.getPersistentData().putInt("unhollowing_forgotten_habit", habit);
            }
            
            // Adapt speed based on learned habits
            updateLearnedSpeed(player);
            
            // Random chat messages
            if (this.random.nextInt(400) == 0 && !player.isSpectator()) {
                sendForgottenMessage(player);
            }
            
            // Apply nausea if staring (eyes glowing)
            if (shouldHaveGlowingEyes() && isStaringAtPlayer(player)) {
                player.addEffect(new MobEffectInstance(
                    MobEffects.CONFUSION, 60, 1, false, false));
            }
            
            // Occasionally break nearby soft blocks
            if (this.random.nextInt(60) == 0) {
                breakNearbySoftBlocks();
            }
        }
    }

    /**
     * Handle rare apparition/disappearance for atmosphere
     */
    private void handleApparition() {
        if (apparitionCooldown > 0) {
            apparitionCooldown--;
        }

        if (isPhasing() && this.random.nextInt(100) == 0 && apparitionCooldown == 0) {
            // Rarely appear
            setPhasing(false);
            apparitionCooldown = 200; // 10 seconds before next change
        } else if (!isPhasing() && this.random.nextInt(120) == 0 && apparitionCooldown == 0) {
            // Disappear after appearing
            setPhasing(true);
            apparitionCooldown = 100; // Reappear soon
        }
    }

    /**
     * Should have glowing eyes (nighttime)
     */
    private boolean shouldHaveGlowingEyes() {
        return this.level().isNight();
    }

    /**
     * Check if entity is staring at player (looking direction)
     */
    private boolean isStaringAtPlayer(Player player) {
        Vec3 toPlayer = player.getEyePosition().subtract(this.getEyePosition());
        Vec3 looking = this.getLookAngle();
        
        double dotProduct = looking.dot(toPlayer.normalize());
        return dotProduct > 0.9D; // Looking mostly at the player
    }

    /**
     * Send creepy chat message
     */
    private void sendForgottenMessage(ServerPlayer player) {
        String[] messages = {
            "You were forgotten... like me...",
            "Why do you wander alone?",
            "I remember when I was like you...",
            "The darkness remembers everything...",
            "You will forget too...",
            "We are forgotten..."
        };
        
        String message = messages[this.random.nextInt(messages.length)];
        net.minecraft.network.chat.Component component = 
            net.minecraft.network.chat.Component.literal(message)
                .withStyle(net.minecraft.ChatFormatting.DARK_GRAY, 
                    net.minecraft.ChatFormatting.ITALIC);
        
        player.displayClientMessage(component, true); // Action bar message
    }

    /**
     * Update movement speed based on learned sprint habits
     * The Forgotten learns and adapts to player behavior — NEVER FORGETS
     */
    private void updateLearnedSpeed(ServerPlayer player) {
        int learnedHabit = player.getPersistentData().getInt("unhollowing_forgotten_habit");
        
        double baseSpeed = 0.25D; // Base speed
        
        if (learnedHabit > 1000) {
            baseSpeed = 0.45D; // Very learned - becomes much faster
        } else if (learnedHabit > 600) {
            baseSpeed = 0.40D; // Well learned
        } else if (learnedHabit > 300) {
            baseSpeed = 0.32D; // Starting to learn
        } else if (learnedHabit > 100) {
            baseSpeed = 0.28D; // Beginning to adapt
        }
        
        // Apply the learned speed
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(baseSpeed);
    }

    /**
     * Update corner peeking behavior - rare, atmospheric effect
     */
    private void updateCornerPeekBehavior() {
        if (cornerPeekTimer > 0) {
            cornerPeekTimer--;
            
            // Teleport to corner peek position for rare appearance
            if (cornerPeekTimer == 10 && cornerPeekPosition != null) {
                this.moveTo(cornerPeekPosition.x, cornerPeekPosition.y, cornerPeekPosition.z,
                    this.getYRot(), this.getXRot());
                setPhasing(false); // Briefly visible
            } else if (cornerPeekTimer == 0) {
                setPhasing(true); // Disappear after peeking
            }
        } else if (this.random.nextInt(400) == 0 && targetPlayer != null) {
            // Rare chance to peek around corner
            Vec3 playerLook = targetPlayer.getLookAngle().normalize();
            Vec3 peekPos = this.position().add(playerLook.scale(3)).add(0, 0.5, 0);
            
            cornerPeekPosition = peekPos;
            cornerPeekTimer = 60; // 3 seconds
        }
    }

    /**
     * Break nearby soft blocks (for horror effect)
     */
    private void breakNearbySoftBlocks() {
        BlockPos center = this.blockPosition();
        
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = this.level().getBlockState(pos);
                    
                    // Break soft blocks: grass, leaves, vines, cobwebs, etc.
                    if (isSoftBlock(state)) {
                        this.level().destroyBlock(pos, true);
                        return; // Only break one block per tick
                    }
                }
            }
        }
    }

    /**
     * Check if block is soft (should be breakable by Forgotten)
     */
    private boolean isSoftBlock(BlockState state) {
        // Check for replaceable blocks like flowers, tall grass, etc.
        return state.getBlock() == Blocks.GRASS ||
               state.getBlock() == Blocks.TALL_GRASS ||
               state.getBlock() == Blocks.SEAGRASS ||
               state.getBlock() == Blocks.COBWEB ||
               state.getBlock() == Blocks.POPPY ||
               state.getBlock() == Blocks.DANDELION ||
               state.getBlock() == Blocks.DEAD_BUSH ||
               state.getBlock() == Blocks.VINE;
    }

    /**
     * Make entity (in)visible
     */
    public void setPhasing(boolean phasing) {
        this.isPhasing = phasing;
        if (!phasing) {
            this.setInvisible(true);
        } else {
            this.setInvisible(false);
        }
    }

    /**
     * Check if entity is phasing
     */
    public boolean isPhasing() {
        return this.isPhasing;
    }

    /**
     * Play the Forgotten's call sound
     */
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.random.nextInt(4) == 0 && shouldHaveGlowingEyes()) {
            return UnhollowingMod.FORGOTTEN_CALL.get();
        }
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) {
        return UnhollowingMod.FORGOTTEN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return UnhollowingMod.FORGOTTEN_DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsPhasing", isPhasing());
        if (targetPlayer != null) {
            tag.putString("TargetPlayer", targetPlayer.getName().getString());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setPhasing(tag.getBoolean("IsPhasing"));
    }
}
