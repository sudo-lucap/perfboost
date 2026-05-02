package dev.perfboost.mixin;

import dev.perfboost.PerfBoost;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects into {@code EntityRenderer#shouldRender} and replaces the entity's
 * bounding box with a slightly shrunken version before the frustum test.
 * This achieves the "aggressive frustum cull" feature with minimal invasiveness.
 */
@Mixin(EntityRenderer.class)
public abstract class ShouldRenderMixin<T extends Entity> {

    @Inject(
            method = "shouldRender",
            at = @At("HEAD"),
            cancellable = true
    )
    private void perfboost$aggressiveFrustumCull(
            T entity,
            Frustum frustum,
            double camX, double camY, double camZ,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!PerfBoost.config.aggressiveFrustumCull) return;

        double shrink = PerfBoost.config.frustumShrinkBlocks;
        if (shrink <= 0) return;

        Box original = entity.getVisibilityBoundingBox();
        Box shrunken = original.shrink(shrink);

        // If shrunken box is degenerate, fall back to default behaviour
        if (shrunken.getXLength() <= 0 || shrunken.getYLength() <= 0 || shrunken.getZLength() <= 0) {
            return;
        }

        boolean visible = frustum.isVisible(shrunken);
        cir.setReturnValue(visible);
    }
}
