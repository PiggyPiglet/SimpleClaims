package com.buuz135.simpleclaims.systems.events;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.asset.type.projectile.config.Projectile;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.ProjectileComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static java.lang.IO.println;

/**
 * Ticking system that monitors projectile entities with explosion configs.
 * Continuously tracks their positions and owners, updating the ProjectileExplosionTracker.
 * This allows us to trace explosion block damage back to the player.
 */
public class ProjectileTrackingSystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(float deltaTime, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        if (!ref.isValid()) {
            return;
        }

        ProjectileComponent projectileComponent = store.getComponent(ref, ProjectileComponent.getComponentType());
        if (projectileComponent == null) {
            return;
        }

        // Check if this projectile has an explosion config
        Projectile projectileAsset = projectileComponent.getProjectile();
        if (projectileAsset == null || projectileAsset.getExplosionConfig() == null) {
            return;
        }

        // Get the projectile's current position
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            return;
        }

        Vector3d position = transform.getPosition();

        // Try to get the creator UUID from the entity itself
        // Since ProjectileComponent stores creatorUuid as private field,
        // we need to get it from the entity's UUID tracking
        // The projectile entity itself has a UUID, and we track the mapping
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        if (uuidComponent == null) {
            return;
        }

        UUID projectileUuid = uuidComponent.getUuid();

        // Register or update this projectile's position
        // The tracker will associate this projectile with its owner
        ProjectileExplosionTracker.getInstance().updateProjectile(projectileUuid, position, ref);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        // Query for entities with ProjectileComponent
        return ProjectileComponent.getComponentType();
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(RootDependency.first());
    }
}
