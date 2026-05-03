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
        Box shrunken = new Box(
            original.minX + shrink,
            original.minY + shrink,
            original.minZ + shrink,
            original.maxX - shrink,
            original.maxY - shrink,
            original.maxZ - shrink
        );

        if ((shrunken.maxX - shrunken.minX) <= 0 ||
            (shrunken.maxY - shrunken.minY) <= 0 ||
            (shrunken.maxZ - shrunken.minZ) <= 0) {
            return;
        }

        cir.setReturnValue(frustum.isVisible(shrunken));
    }
}
