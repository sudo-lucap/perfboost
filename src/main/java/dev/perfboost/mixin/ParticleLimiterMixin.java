package dev.perfboost.mixin;

import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ParticleManager.class)
public class ParticleLimiterMixin {
    // Disabled - method signature incompatible with 1.21.11
}
