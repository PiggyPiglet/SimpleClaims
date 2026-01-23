package com.buuz135.simpleclaims.systems.events;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.system.WorldEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static java.lang.IO.println;

/**
 * Global event system that catches DamageBlockEvent invocations without entity context.
 * This is used for projectile explosions and other non-player block damage.
 */
public class GlobalDamageBlockEventSystem extends WorldEventSystem<EntityStore, DamageBlockEvent> {

    public GlobalDamageBlockEventSystem() {
        super(DamageBlockEvent.class);
    }

    @Override
    public void handle(@Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull DamageBlockEvent event) {
        println("GlobalDamageBlockEventSystem: handling global damage block event at " + event.getTargetBlock());

        // Try to find the owner of a nearby projectile
        UUID playerUUID = ProjectileExplosionTracker.getInstance().findExplosionOwner(event.getTargetBlock(), store);

        if (playerUUID == null) {
            // No nearby projectile found, this might be from another source
            // For safety, we should still protect claimed chunks
            println("GlobalDamageBlockEventSystem: no nearby projectile found, denying in all claimed chunks");

            // Check if the block is in a claimed chunk
            String worldName = store.getExternalData().getWorld().getName();
            int x = event.getTargetBlock().getX();
            int z = event.getTargetBlock().getZ();

            // If it's claimed by anyone, deny it (since we don't know who's doing it)
            if (!ClaimManager.getInstance().isAllowedToInteract(null, worldName, x, z, PartyInfo::isBlockBreakEnabled)) {
                println("GlobalDamageBlockEventSystem: block is in claimed chunk, cancelling");
                event.setCancelled(true);
            }
            return;
        }

        println("GlobalDamageBlockEventSystem: found projectile owner: " + playerUUID);

        // Check if the player is allowed to break blocks in this chunk
        String worldName = store.getExternalData().getWorld().getName();
        int x = event.getTargetBlock().getX();
        int z = event.getTargetBlock().getZ();

        if (!ClaimManager.getInstance().isAllowedToInteract(playerUUID, worldName, x, z, PartyInfo::isBlockBreakEnabled)) {
            println("GlobalDamageBlockEventSystem: player " + playerUUID + " is not allowed to break blocks here, cancelling");
            event.setCancelled(true);
        } else {
            println("GlobalDamageBlockEventSystem: player " + playerUUID + " is allowed to break blocks here");
        }
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(RootDependency.first());
    }
}
