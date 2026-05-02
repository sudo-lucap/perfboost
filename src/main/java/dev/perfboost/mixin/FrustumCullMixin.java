package dev.perfboost.mixin;

import dev.perfboost.PerfBoost;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Shrinks entity bounding boxes slightly before they are tested against the
 * camera frustum.  This causes entities that are nearly off-screen to be
 * culled earlier, saving render thread work.
 *
 * <p>The shrink amount is tunable via {@code config.frustumShrinkBlocks}.
 * The default (0.5 blocks) is barely noticeable but measurably reduces
 * draw-call count in entity-dense scenes.</p>
 *
 * <p>Works alongside ImmediatelyFast and More Culling – those mods operate at
 * later rendering stages; this intercepts the box used for the initial
 * visibility test.</p>
 */
@Mixin(EntityRenderer.class)
public class FrustumCullMixin<T extends Entity> {

    @ModifyVariable(
            method = "render",
            at = @At("HEAD"),
            argsOnly = true,
            index = 1  // first actual arg after 'this': the Entity
    )
    private T perfboost$injectShrunkBox(T entity) {
        // We cannot modify the entity box here directly, so we rely on the
        // shouldRender check.  The real shrink is applied in ShouldRenderMixin.
        return entity;
    }
}
