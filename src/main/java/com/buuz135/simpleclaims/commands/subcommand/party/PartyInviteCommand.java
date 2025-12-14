package com.buuz135.simpleclaims.commands.subcommand.party;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.commands.CommandMessages;
import com.buuz135.simpleclaims.gui.PartyInfoEditGui;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AsyncCommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.server.core.command.commands.player.inventory.InventorySeeCommand.MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD;

public class PartyInviteCommand extends AsyncCommandBase {

    private RequiredArg<Player> name;

    public PartyInviteCommand() {
        super("invite", "Invites a player to your party");
        this.setPermissionGroup(GameMode.Adventure);
        this.name = this.withRequiredArg("player", "The player name", ArgTypes.PLAYER);
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        CommandSender sender = commandContext.sender();
        if (sender instanceof Player player) {
            Ref<EntityStore> ref = player.getReference();
            if (ref != null && ref.isValid()) {
                Store<EntityStore> store = ref.getStore();
                World world = store.getExternalData().getWorld();
                return CompletableFuture.runAsync(() -> {
                    PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
                    if (playerRefComponent != null) {
                        var party = ClaimManager.getInstance().getPartyFromPlayer(player);
                        if (party == null) {
                            player.sendMessage(CommandMessages.NOT_IN_A_PARTY);
                            return;
                        }
                        Player invitedPlayer = commandContext.get(this.name);
                        if (invitedPlayer == null) {
                            player.sendMessage(CommandMessages.PLAYER_NOT_FOUND);
                            return;
                        }
                        if (party.isOwnerOrMember(invitedPlayer.getUuid())) {
                            player.sendMessage(CommandMessages.PARTY_INVITE_SELF);
                            return;
                        }
                        ClaimManager.getInstance().invitePlayerToParty(invitedPlayer, party, player);
                        player.sendMessage(CommandMessages.PARTY_INVITE_SENT.param("username", invitedPlayer.getDisplayName()));
                        invitedPlayer.sendMessage(CommandMessages.PARTY_INVITE_RECEIVED.param("party_name", party.getName()).param("username", player.getDisplayName()));
                    }
                }, world);
            } else {
                commandContext.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
                return CompletableFuture.completedFuture(null);
            }
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }
}