package dev.perfboost.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.client.render.entity.EntityRenderer")
public abstract class ShouldRenderMixin<T extends Entity> {
    // Frustum culling disabled - incompatible descriptor
}
