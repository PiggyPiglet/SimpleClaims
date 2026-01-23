package com.buuz135.simpleclaims.systems.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.ProjectileComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.IO.println;

/**
 * Tracks active projectile entities that have explosion configs.
 * When block damage occurs, we check for nearby projectiles and extract their owner.
 * This allows us to determine which player caused explosion block damage.
 */
public class ProjectileExplosionTracker {
    private static final ProjectileExplosionTracker INSTANCE = new ProjectileExplosionTracker();

    // Maximum distance (in blocks) to consider a projectile as the cause of block damage
    private static final double MAX_DISTANCE = 10.0;

    // Track projectiles by their UUID
    private final Map<UUID, ProjectileInfo> activeProjectiles = new ConcurrentHashMap<>();

    private ProjectileExplosionTracker() {
    }

    public static ProjectileExplosionTracker getInstance() {
        return INSTANCE;
    }

    /**
     * Updates or registers a projectile's current position.
     */
    public void updateProjectile(UUID projectileUuid, Vector3d position, Ref<EntityStore> ref) {
        activeProjectiles.put(projectileUuid, new ProjectileInfo(position.clone(), ref, System.currentTimeMillis()));

        // Clean up stale projectiles (older than 30 seconds)
        cleanupStaleProjectiles();
    }

    /**
     * Finds the owner of a projectile near the given block position, with store access.
     * This version can properly extract the owner UUID.
     */
    @Nullable
    public UUID findExplosionOwner(Vector3i blockPosition, Store<EntityStore> store) {
        Vector3d blockPos = new Vector3d(blockPosition.x + 0.5, blockPosition.y + 0.5, blockPosition.z + 0.5);

        ProjectileInfo nearestProjectile = null;
        double nearestDistance = Double.MAX_VALUE;

        // Find the nearest projectile
        for (Map.Entry<UUID, ProjectileInfo> entry : activeProjectiles.entrySet()) {
            ProjectileInfo projectile = entry.getValue();

            // Check distance
            double distance = projectile.position.distanceTo(blockPos);
            if (distance <= MAX_DISTANCE && distance < nearestDistance) {
                nearestDistance = distance;
                nearestProjectile = projectile;
            }
        }

        if (nearestProjectile != null) {
            println("ProjectileExplosionTracker: Found projectile within " + nearestDistance + " blocks of block");

            // Extract the owner UUID from the projectile entity using reflection
            if (nearestProjectile.ref.isValid()) {
                ProjectileComponent projectileComponent = store.getComponent(nearestProjectile.ref, ProjectileComponent.getComponentType());
                if (projectileComponent != null) {
                    // Use reflection to access the private creatorUuid field
                    try {
                        java.lang.reflect.Field creatorUuidField = ProjectileComponent.class.getDeclaredField("creatorUuid");
                        creatorUuidField.setAccessible(true);
                        UUID creatorUuid = (UUID) creatorUuidField.get(projectileComponent);

                        if (creatorUuid != null) {
                            println("ProjectileExplosionTracker: Found creator UUID via reflection: " + creatorUuid);
                            return creatorUuid;
                        } else {
                            println("ProjectileExplosionTracker: Creator UUID is null");
                        }
                    } catch (Exception e) {
                        println("ProjectileExplosionTracker: Failed to access creatorUuid via reflection: " + e.getMessage());
                    }
                }
            }
        }

        println("ProjectileExplosionTracker: Could not find explosion owner");
        return null;
    }

    /**
     * Removes projectiles that are older than 30 seconds (likely despawned or exploded).
     */
    private void cleanupStaleProjectiles() {
        long currentTime = System.currentTimeMillis();
        long STALE_TIMEOUT_MS = 30000; // 30 seconds

        activeProjectiles.entrySet().removeIf(entry ->
                currentTime - entry.getValue().timestamp > STALE_TIMEOUT_MS
        );
    }

    private static class ProjectileInfo {
        final Vector3d position;
        final Ref<EntityStore> ref;
        final long timestamp;

        ProjectileInfo(Vector3d position, Ref<EntityStore> ref, long timestamp) {
            this.position = position;
            this.ref = ref;
            this.timestamp = timestamp;
        }
    }
}
