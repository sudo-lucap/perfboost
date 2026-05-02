package dev.perfboost.mixin;

import dev.perfboost.PerfBoost;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Probabilistically drops particle spawn calls when the client is below the
 * target FPS.  This is a lightweight alternative to Minecraft's built-in
 * "Minimal" particle setting – it runs at all particle quality levels and
 * scales proportionally with how far below the FPS target the client is,
 * rather than being binary.
 *
 * <p>Compatible with your existing Dynamic FPS mod because Dynamic FPS acts
 * when the window is unfocused; this limiter runs continuously based on
 * live FPS data.</p>
 */
@Mixin(ParticleManager.class)
public class ParticleLimiterMixin {

    @Inject(
            method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void perfboost$limitParticles(
            ParticleEffect parameters,
            double x, double y, double z,
            double vx, double vy, double vz,
            CallbackInfo ci
    ) {
        if (!PerfBoost.config.particleLimiter) return;
        if (PerfBoost.fpsTracker == null || !PerfBoost.fpsTracker.isLowFps()) return;

        float keep = PerfBoost.config.particleKeepFraction;
        if (ThreadLocalRandom.current().nextFloat() > keep) {
            ci.cancel();
        }
    }
}
