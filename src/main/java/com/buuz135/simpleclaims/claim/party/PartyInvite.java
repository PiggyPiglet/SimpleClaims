package com.buuz135.simpleclaims.claim.party;

import java.util.UUID;

public record PartyInvite(UUID recipient, UUID sender, UUID party) {
}
