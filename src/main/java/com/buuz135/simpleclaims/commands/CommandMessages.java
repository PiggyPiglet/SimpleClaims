package com.buuz135.simpleclaims.commands;

import com.hypixel.hytale.server.core.Message;

public class CommandMessages {

    public static final Message NOT_IN_A_PARTY = Message.translation("commands.errors.simpleclaims.playerNotInParty");
    public static final Message IN_A_PARTY = Message.translation("commands.errors.simpleclaims.playerInParty");

    public static final Message ALREADY_CLAIMED_BY_YOU = Message.translation("commands.errors.simpleclaims.alreadyClaimedByYou");
    public static final Message ALREADY_CLAIMED_BY_ANOTHER_PLAYER = Message.translation("commands.errors.simpleclaims.alreadyClaimedByAnotherPlayer");
    public static final Message NOT_CLAIMED = Message.translation("commands.errors.simpleclaims.notClaimed");
    public static final Message NOT_YOUR_CLAIM = Message.translation("commands.errors.simpleclaims.notYourClaim");

    public static final Message NOT_ENOUGH_CHUNKS = Message.translation("commands.errors.simpleclaims.notEnoughChunks");
    public static final Message PLAYER_NOT_FOUND = Message.translation("commands.errors.simpleclaims.playerNotFound");

    public static final Message PARTY_CREATED = Message.translation("commands.simpleclaims.partyCreated");

    public static final Message UNCLAIMED = Message.translation("commands.info.simpleclaims.unclaimed");
    public static final Message CLAIMED = Message.translation("commands.info.simpleclaims.claimed");

    public static final Message NOW_USING_PARTY = Message.translation("commands.simpleclaims.nowUsingParty");

    public static final Message ADMIN_PARTY_NOT_SELECTED = Message.translation("commands.errors.simpleclaims.admin.partyNotSelected");
    public static final Message PARTY_NOT_FOUND = Message.translation("commands.errors.simpleclaims.admin.partyNotFound");

    public static final Message PARTY_OWNER_CHANGED = Message.translation("commands.simpleclaims.admin.partyOwnerChanged");

    public static final Message PARTY_INVITE_SENT = Message.translation("commands.simpleclaims.partyInviteSent");
    public static final Message PARTY_INVITE_RECEIVED = Message.translation("commands.simpleclaims.partyInviteReceived");
    public static final Message PARTY_INVITE_JOIN = Message.translation("commands.simpleclaims.partyInviteJoined");

    public static final Message PARTY_INVITE_SELF = Message.translation("commands.simpleclaims.partyInviteSelf");
}
