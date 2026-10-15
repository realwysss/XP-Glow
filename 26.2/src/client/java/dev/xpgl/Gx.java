package dev.xpgl;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.BlendFactor;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import java.nio.ByteBuffer;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

public class Gx {
	static final int SZ = (40 + Gk.MAX * 8) * 4;
	static final RenderPipeline[] pp = new RenderPipeline[2];
	static GpuBuffer ub;

	static RenderTarget main() {
		return Minecraft.getInstance().gameRenderer.mainRenderTarget();
	}

	static int hh() {
		return main().height;
	}

	static RenderPipeline pipe(int blend) {
		if (pp[blend] == null) {
			BlendFunction bf = blend == 0 ? BlendFunction.ADDITIVE : new BlendFunction(BlendFactor.ONE, BlendFactor.ONE_MINUS_SRC_COLOR);
			pp[blend] = RenderPipeline.builder()
					.withLocation(Identifier.fromNamespaceAndPath("xpglow", "pipeline/glow" + blend))
					.withVertexShader(Identifier.fromNamespaceAndPath("xpglow", "core/glow"))
					.withFragmentShader(Identifier.fromNamespaceAndPath("xpglow", "core/glow"))
					.withBindGroupLayout(BindGroupLayout.builder().withSampler("DepthSampler").withUniform("GlowInfo", UniformType.UNIFORM_BUFFER).build())
					.withCull(false)
					.withColorTargetState(new ColorTargetState(Optional.of(bf), GpuFormat.RGBA8_UNORM, ColorTargetState.WRITE_COLOR))
					.withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
					.build();
		}
		return pp[blend];
	}

	static void draw(Matrix4f rot, Matrix4f proj, int n, float[] a, float[] b, float fal, int blend, float t) {
		if (ub == null) ub = RenderSystem.getDevice().createBuffer(() -> "xpglow ubo", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, SZ);
		RenderTarget m = main();
		try (MemoryStack st = MemoryStack.stackPush()) {
			ByteBuffer d = st.calloc(SZ);
			proj.get(0, d);
			rot.get(64, d);
			d.putFloat(128, m.width).putFloat(132, m.height).putFloat(136, fal).putFloat(140, t);
			d.putFloat(144, proj.m22()).putFloat(148, proj.m32()).putFloat(152, RenderSystem.getDevice().getDeviceInfo().isZZeroToOne() ? 1f : 0f);
			for (int i = 0; i < n; i++) {
				int o = 160 + i * 32;
				d.putFloat(o, a[i * 4]).putFloat(o + 4, a[i * 4 + 1]).putFloat(o + 8, a[i * 4 + 2]).putFloat(o + 12, a[i * 4 + 3]);
				d.putFloat(o + 16, b[i * 4]).putFloat(o + 20, b[i * 4 + 1]).putFloat(o + 24, b[i * 4 + 2]);
			}
			RenderSystem.getDevice().createCommandEncoder().writeToBuffer(ub.slice(), d);
		}
		try (RenderPass rp = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "xpglow", m.getColorTextureView(), Optional.empty())) {
			rp.setPipeline(pipe(blend));
			rp.setUniform("GlowInfo", ub);
			rp.bindTexture("DepthSampler", m.getDepthTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
			rp.draw(n * 6, 1, 0, 0);
		}
	}
}
