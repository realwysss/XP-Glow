package dev.xpgl.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import dev.xpgl.Gk;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LvRdMxn {
	@Inject(method = "renderLevel", at = @At("TAIL"))
	private void glw(GraphicsResourceAllocator a, DeltaTracker d, boolean b, Camera cam, Matrix4f rot, Matrix4f proj, Matrix4f cull, GpuBufferSlice f, Vector4f fog, boolean sky, CallbackInfo ci) {
		Gk.run(rot, proj, cam.position(), d.getGameTimeDeltaPartialTick(false));
	}
}
