package com.jcwhatever.bukkit.tpregions;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.entity.EntityUtils;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/*
 * 
 */
public class Teleporter {

    private static final Map<Entity, Void> _crossWorldTeleports = new WeakHashMap<>(10);

    /**
     * Handles teleporting an entity to a destination.
     *
     * <p>Teleports on next tick.</p>
     *
     * <p>Preserves entity velocity.</p>
     *
     * <p>Loads destination chunk.</p>
     *
     * <p>Teleports all vehicles/passengers and restores passenger/vehicle relationship
     * after teleporting.</p>
     *
     * @param entity       The entity to teleport.
     * @param destination  The destination location.
     */
    public static void teleport(Entity entity, Location destination) {
        PreCon.notNull(entity);
        PreCon.notNull(destination);

        new EntityRelations(getRootEntity(entity)).teleport(destination);
    }

    /**
     * Determine if a non-player entity is currently being teleported cross world.
     *
     * @param entity  The entity to check.
     */
    public static boolean isCrossWorldTeleporting(Entity entity) {
        PreCon.notNull(entity);

        return _crossWorldTeleports.containsKey(entity);
    }

    /*
     * Get the root entity in a passenger/vehicle entity relationship
     */
    private static Entity getRootEntity(Entity entity) {
        while (entity.getVehicle() != null) {
            entity = entity.getVehicle();
        }
        return entity;
    }

    /*
     * Tracks passenger/vehicle relationships and restores them
     * after transport.
     */
    private static class EntityRelations {
        Entity entity;
        Vector velocity;
        EntityRelations passenger;

        EntityRelations(@Nullable Entity entity) {

            this.entity = entity;

            if (entity != null) {
                this.velocity = entity.getVelocity();
                this.passenger = new EntityRelations(entity.getPassenger());
            }
        }

        boolean hasPassenger() {
            return passenger.entity != null;
        }

        void mountAll() {

            EntityRelations relations = this;

            Chunk chunk = relations.entity.getLocation().getChunk();

            while (relations.hasPassenger()) {

                UUID passengerId = relations.passenger.entity.getUniqueId();
                Entity passenger = EntityUtils.getEntityByUUID(chunk, passengerId);
                Entity vehicle = EntityUtils.getEntityByUUID(chunk, relations.entity.getUniqueId());

                if (passenger != null && vehicle != null)
                    vehicle.setPassenger(passenger);

                relations = relations.passenger;
            }
        }

        void dismountAll() {
            EntityRelations relations = this;

            while (relations.entity != null) {

                relations.entity.eject();
                relations = relations.passenger;
            }
        }

        void teleport(final Location destination) {

            dismountAll();

            Scheduler.runTaskLater(TPRegions.getPlugin(), new Runnable() {
                @Override
                public void run() {

                    EntityRelations relations = EntityRelations.this;

                    boolean isWorldChange = !entity.getWorld().equals(destination.getWorld());
                    final LinkedList<Entity> entities = new LinkedList<>();

                    while (relations.entity != null) {

                        if (isWorldChange && !(relations.entity instanceof Player)) {
                            _crossWorldTeleports.put(relations.entity, null);
                            entities.add(relations.entity);
                        }

                        relations.entity.teleport(destination);
                        relations = relations.passenger;
                    }

                    Scheduler.runTaskLater(TPRegions.getPlugin(), 2, new Runnable() {
                        @Override
                        public void run() {
                            if (hasPassenger())
                                mountAll();

                            entity.setVelocity(velocity);

                            while (!entities.isEmpty()) {
                                _crossWorldTeleports.remove(entities.remove());
                            }
                        }
                    });
                }
            });
        }
    }
}
