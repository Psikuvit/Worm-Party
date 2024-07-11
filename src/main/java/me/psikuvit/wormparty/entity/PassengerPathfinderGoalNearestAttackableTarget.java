package me.psikuvit.wormparty.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class PassengerPathfinderGoalNearestAttackableTarget<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    private final Worm worm;

    public PassengerPathfinderGoalNearestAttackableTarget(Worm worm, Class<T> targetClass, boolean checkSight) {
        super(worm, targetClass, checkSight);
        this.worm = worm;
    }

    @Override
    public boolean canUse() {
        if (this.target != null && worm.getPassengers().contains(this.target)) {
            return false;
        }
        return super.canUse();
    }
}
