package dev.perfboost.mixin;

import dev.perfboost.PerfBoost;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Throttles entity ticking for entities that are far from the camera.
 *
 * <p>Entities beyond {@code config.throttleDistanceBlocks} blocks are only
 * ticked once every {@code config.throttleInterval} client ticks, reducing
 * CPU pressure without visibly affecting nearby gameplay.</p>
 *
 * <p>Compatible with your existing CCME (Concurrent Chunk Management Engine)
 * install because CCME operates on chunk-level scheduling, not entity ticking.</p>
 */
@Mixin(ClientWorld.class)
public class EntityTickThrottleMixin {

    @Inject(
            method = "tickEntity",
            at = @At("HEAD"),
            cancellable = true
    )
    private void perfboost$throttleDistantEntities(Entity entity, CallbackInfo ci) {
        if (!PerfBoost.config.entityTickThrottling) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        Vec3d playerPos = client.player.getPos();
        double distSq = entity.squaredDistanceTo(playerPos.x, playerPos.y, playerPos.z);
        double threshold = PerfBoost.config.throttleDistanceBlocks;

        if (distSq > threshold * threshold) {
            // Use the entity's id as a cheap per-entity stagger so they don't
            // all tick on the same frame, avoiding a stutter spike.
            int interval = PerfBoost.config.throttleInterval;
            long worldTime = ((ClientWorld)(Object)this).getTime();
            if ((worldTime + entity.getId()) % interval != 0) {
                ci.cancel();
            }
        }
    }
}
