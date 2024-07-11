package me.psikuvit.wormparty.entity;

import me.psikuvit.wormparty.Utils;
import me.psikuvit.wormparty.WormParty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Worm extends Pig {

    private final List<ArmorStand> segments;
    private final int segmentCount;
    private final double segmentSpacing;
    private Location currentLocation;
    private final Random rnd = new Random();
    private final double shakeAmount;
    private final double wormHealth;
    private final UUID wormID;
    private final PersistentDataContainer wormPDC;
    private final WormParty plugin = WormParty.getPlugin(WormParty.class);

    public Worm(Location currentLocation, int segmentCount, double wormHealth) {
        super(EntityType.PIG, ((CraftWorld) currentLocation.getWorld()).getHandle());

        //Initialising worm data
        this.currentLocation = currentLocation;
        this.segments = new ArrayList<>();
        this.segmentCount = segmentCount;
        this.segmentSpacing = 0.5;
        this.shakeAmount = 0.2;
        this.wormID = UUID.randomUUID();
        this.wormHealth = wormHealth;
        this.wormPDC = getBukkitEntity().getPersistentDataContainer();

        //Setting worm Attributes
        this.setBaby(true);
        this.setPos(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());
        this.setSilent(true);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(100);
        initSegments();

        //Caching worm data (UUID/HP) in a pdc
        wormPDC.set(plugin.getKey(), PersistentDataType.STRING, wormID.toString());
        wormPDC.set(plugin.getKey(), PersistentDataType.DOUBLE, wormHealth);
    }

    // Handles worm movement
    @Override
    public void aiStep() {
        super.aiStep();
        currentLocation = this.getBukkitEntity().getLocation();
        moveSegments();
    }

    //Giving the worm goals
    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));

        this.targetSelector.addGoal(2, new PassengerPathfinderGoalNearestAttackableTarget<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new PassengerPathfinderGoalNearestAttackableTarget<>(this, Pig.class, true));
    }

    //Handles worm damage
    @Override
    public boolean canAttack(LivingEntity target) {
        return target instanceof Player;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean flag = super.doHurtTarget(entity);
        if (flag) {
            entity.hurt(DamageSource.mobAttack(this), 8);
        }
        return flag;
    }

    public PersistentDataContainer getWormPDC() {
        return wormPDC;
    }

    public UUID getWormID() {
        return wormID;
    }

    public void initSegments() {
        Vector direction = currentLocation.clone().getDirection().normalize();
        direction.setY(0);
        for (int i = 0; i <= segmentCount; i++) {
            Location spawnLocation = currentLocation.clone().subtract(direction.clone().multiply(i * segmentSpacing));
            spawnLocation.subtract(0, 0.3, 0);
            ArmorStand segment = currentLocation.getWorld().spawn(spawnLocation, ArmorStand.class);
            segment.setBasePlate(false);
            segment.setSmall(true);
            segment.setInvisible(true);
            String url = (i == 0) ? plugin.getConfigUtils().headTexture() : plugin.getConfigUtils().segmentTexture();

            segment.getEquipment().setHelmet(Utils.getCustomSkull(url));

            PersistentDataContainer pdc = segment.getPersistentDataContainer();
            pdc.set(plugin.getKey(), PersistentDataType.STRING, wormID.toString());

            segments.add(segment);
        }
    }

    private void moveSegments() {
        double shakeConstant = shakeAmount / 2;

        if (!segments.isEmpty()) {
            ArmorStand headSegment = segments.get(0);
            headSegment.teleport(currentLocation.clone().subtract(0, 0.3, 0));
        }
        for (int i = 1; i < segments.size(); i++) {
            ArmorStand currentSegment = segments.get(i);
            ArmorStand previousSegment = segments.get(i - 1);

            // Get the location of the previous segment
            Location previousLocation = previousSegment.getLocation().clone();

            // Calculate the offset for the current segment based on the previous segment's position
            Vector offset = previousLocation.getDirection().normalize().clone().multiply(segmentSpacing);
            offset.setY(0);
            Location tpLocation = previousLocation.subtract(offset);

            tpLocation.add(rnd.nextDouble() * shakeAmount - shakeConstant, 0, rnd.nextDouble() * shakeAmount - shakeConstant);

            // Smoothly interpolate to the new location
            Location currentLocation = currentSegment.getLocation();
            Vector direction = tpLocation.toVector().subtract(currentLocation.toVector()).normalize();

            double distance = currentLocation.distance(tpLocation);
            double moveAmount = Math.min(distance, segmentSpacing); // Limit the movement amount to segmentSpacing

            Location nextStep = currentLocation.add(direction.multiply(moveAmount));

            // Interpolating the position for smooth movement
            currentSegment.teleport(nextStep);

            // Interpolating the rotation for smooth turning
            float currentYaw = currentSegment.getLocation().getYaw();
            float targetYaw = previousSegment.getLocation().getYaw();
            float interpolatedYaw = Utils.interpolateYaw(currentYaw, targetYaw, 0.1f); // Adjust the interpolation factor as needed

            // Set the segment to the interpolated rotation
            currentSegment.setRotation(interpolatedYaw, currentSegment.getLocation().getPitch());
        }
    }

    public void killWorm() {
        this.kill();
        segments.forEach(org.bukkit.entity.Entity::remove);
        currentLocation.getWorld().dropItemNaturally(currentLocation, Utils.randomReward());
    }
}
