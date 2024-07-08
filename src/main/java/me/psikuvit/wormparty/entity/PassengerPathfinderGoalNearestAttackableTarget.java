package me.psikuvit.wormparty.entity;

import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.PathfinderGoalNearestAttackableTarget;

public class PassengerPathfinderGoalNearestAttackableTarget<T extends EntityLiving> extends PathfinderGoalNearestAttackableTarget<T> {

    private final Worm worm;

    public PassengerPathfinderGoalNearestAttackableTarget(Worm worm, Class<T> targetClass, boolean checkSight) {
        super(worm, targetClass, checkSight);
        this.worm = worm;
    }

    @Override
    public boolean a() {
        if (this.e != null && worm.getPassengers().contains(this.e)) {
            return false;
        }
        return super.a();
    }
}
