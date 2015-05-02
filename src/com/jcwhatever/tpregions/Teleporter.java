package com.jcwhatever.tpregions;

import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.LeashUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.entity.EntityUtils;
import com.jcwhatever.nucleus.utils.player.PlayerStateSnapshot;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
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
        PreCon.notNull(entity, "entity");
        PreCon.notNull(destination, "destination");
        PreCon.notNull(destination.getWorld(), "destination world");

        new EntityRelations(getRootEntity(entity)).teleport(destination);
    }

    /**
     * Determine if a non-player entity is currently being teleported cross world.
     *
     * @param entity  The entity to check.
     */
    public static boolean isCrossWorldTeleporting(Entity entity) {
        PreCon.notNull(entity, "entity");

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
     * Represents a leash from a player to an entity.
     */
    private static class LeashPair {
        Player player;
        LivingEntity leashed;

        LeashPair(Player player, LivingEntity leashed) {
            this.player = player;
            this.leashed = leashed;
        }
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
                    final LinkedList<LeashPair> leashedPairs = new LinkedList<>();
                    final LinkedList<PlayerStateSnapshot> snapshots = new LinkedList<>();

                    while (relations.entity != null) {

                        Entity entity = relations.entity;

                        if (isWorldChange) {

                            if (entity instanceof Player) {
                                snapshots.add(new PlayerStateSnapshot((Player) entity));
                            } else {
                                _crossWorldTeleports.put(entity, null);
                                entities.add(entity);
                            }
                        }

                        // teleport leashed entities
                        if (entity instanceof Player) {
                            Collection<Entity> leashed = LeashUtils.getLeashed((Player) entity);

                            for (Entity leashEntity : leashed) {
                                LocationUtils.teleport(leashEntity, destination);
                                leashedPairs.add(new LeashPair((Player) entity, (LivingEntity) leashEntity));
                            }
                        }

                        LocationUtils.teleport(entity, destination);
                        relations = relations.passenger;
                    }

                    Scheduler.runTaskLater(TPRegions.getPlugin(), 2, new Runnable() {
                        @Override
                        public void run() {
                            if (hasPassenger())
                                mountAll();

                            while (!entities.isEmpty()) {
                                _crossWorldTeleports.remove(entities.remove());
                            }

                            // make sure leashes are not broken
                            while (!leashedPairs.isEmpty()) {
                                LeashPair pair = leashedPairs.remove();
                                pair.leashed.setLeashHolder(pair.player);
                            }

                            // preserve player game mode and flight
                            while (!snapshots.isEmpty()) {
                                PlayerStateSnapshot snapshot = snapshots.remove();
                                Player player = PlayerUtils.getPlayer(snapshot.getPlayerId());
                                if (player == null)
                                    continue;

                                player.setGameMode(snapshot.getGameMode());
                                player.setFlying(snapshot.isFlying());
                                player.setAllowFlight(snapshot.isFlightAllowed());
                            }
                        }
                    });
                }
            });
        }
    }
}
