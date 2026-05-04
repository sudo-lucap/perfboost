package dev.perfboost.mixin;

import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientWorld.class)
public class EntityTickThrottleMixin {
    // Disabled - method signature incompatible with 1.21.11
}
