package me.psikuvit.wormparty.entity;

import me.psikuvit.wormparty.Utils;
import me.psikuvit.wormparty.WormParty;
import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityPig;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.GenericAttributes;
import net.minecraft.server.v1_16_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_16_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_16_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_16_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_16_R3.PathfinderGoalRandomStrollLand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Worm extends EntityPig {

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


    public Worm(Location currentLocation, int segmentCount, double segmentSpacing, double shakeAmount, double wormHealth) {
        super(EntityTypes.PIG, ((CraftWorld) currentLocation.getWorld()).getHandle());

        //Initialising worm data
        this.currentLocation = currentLocation;
        this.segments = new ArrayList<>();
        this.segmentCount = segmentCount;
        this.segmentSpacing = segmentSpacing;
        this.shakeAmount = shakeAmount;
        this.wormID = UUID.randomUUID();
        this.wormHealth = wormHealth;
        this.wormPDC = getBukkitEntity().getPersistentDataContainer();

        //Setting worm Attributes
        this.setBaby(true);
        this.setPosition(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());
        this.setSilent(true);
        this.getAttributeInstance(GenericAttributes.KNOCKBACK_RESISTANCE).setValue(100);
        initSegments();

        //Caching worm data (UUID/HP) in a pdc
        wormPDC.set(plugin.getKey(), PersistentDataType.STRING, wormID.toString());
        wormPDC.set(plugin.getKey(), PersistentDataType.DOUBLE, wormHealth);
    }

    // Handles worm movement
    @Override
    public void movementTick() {
        super.movementTick();
        currentLocation = this.getBukkitEntity().getLocation();
        moveSegments();
    }

    //Giving the worm goals
    @Override
    public void initPathfinder() {
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 4.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));

        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityPig.class, true));
    }

    //Handles worm damage
    @Override
    public boolean attackEntity(Entity entity) {
        entity.damageEntity(DamageSource.mobAttack(this), 8);
        return true;
    }

    public double getWormHealth() {
        return wormHealth;
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
            String url = (i == 0)
                    ? "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGYwM2FkOTYwOTJmM2Y3ODk5MDI0MzY3MDljZGY2OWRlNmI3MjdjMTIxYjNjMmRhZWY5ZmZhMWNjYWVkMTg2YyJ9fX0="
                    : "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2I0MzU0MTUxN2RkYTE4NjliOGMzOTBlZDRmZTViNWRmMjc4OTYxMDhlNDY2ZjAwYzE4NTdkZTdmNDJiMGUwZSJ9fX0=";

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
        this.killEntity();
        segments.forEach(org.bukkit.entity.Entity::remove);
    }
}
