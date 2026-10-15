package dev.xpgl.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import dev.xpgl.Gk;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LvRdMxn {
	@Inject(method = "render", at = @At("TAIL"))
	private void glw(GraphicsResourceAllocator a, DeltaTracker d, boolean b, CameraRenderState c, Matrix4fc mv, GpuBufferSlice f, Vector4f fog, boolean sky, CallbackInfo ci) {
		Gk.run(c.viewRotationMatrix, c.projectionMatrix, c.pos, d.getGameTimeDeltaPartialTick(false));
	}
}
