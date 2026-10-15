package dev.xpgl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

public class Gx {
	static final int SZ = mk();
	static final RenderPipeline[] pp = new RenderPipeline[2];
	static MappableRingBuffer ub;

	static int mk() {
		Std140SizeCalculator s = new Std140SizeCalculator().putMat4f().putMat4f().putVec4().putVec4();
		for (int i = 0; i < Gk.MAX * 2; i++) s.putVec4();
		return s.get();
	}

	static int hh() {
		return Minecraft.getInstance().getMainRenderTarget().height;
	}

	static RenderPipeline pipe(int blend) {
		if (pp[blend] == null) {
			BlendFunction bf = blend == 0 ? BlendFunction.ADDITIVE : new BlendFunction(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_COLOR);
			pp[blend] = RenderPipeline.builder()
					.withLocation(Identifier.fromNamespaceAndPath("xpglow", "pipeline/glow" + blend))
					.withVertexShader(Identifier.fromNamespaceAndPath("xpglow", "core/glow"))
					.withFragmentShader(Identifier.fromNamespaceAndPath("xpglow", "core/glow"))
					.withSampler("DepthSampler")
					.withUniform("GlowInfo", UniformType.UNIFORM_BUFFER)
					.withCull(false)
					.withColorTargetState(new ColorTargetState(Optional.of(bf), ColorTargetState.WRITE_COLOR))
					.withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES)
					.build();
		}
		return pp[blend];
	}

	static void draw(Matrix4f rot, Matrix4f proj, int n, float[] a, float[] b, float fal, int blend, float t) {
		if (ub == null) ub = new MappableRingBuffer(() -> "xpglow ubo", 130, SZ);
		RenderTarget m = Minecraft.getInstance().getMainRenderTarget();
		CommandEncoder ce = RenderSystem.getDevice().createCommandEncoder();
		try (GpuBuffer.MappedView mv = ce.mapBuffer(ub.currentBuffer(), false, true)) {
			Std140Builder s = Std140Builder.intoBuffer(mv.data());
			s.putMat4f(proj).putMat4f(rot).putVec4(m.width, m.height, fal, t).putVec4(proj.m22(), proj.m32(), RenderSystem.getDevice().isZZeroToOne() ? 1f : 0f, 0f);
			for (int i = 0; i < n; i++) s.putVec4(a[i * 4], a[i * 4 + 1], a[i * 4 + 2], a[i * 4 + 3]).putVec4(b[i * 4], b[i * 4 + 1], b[i * 4 + 2], 0f);
			for (int i = n; i < Gk.MAX; i++) s.putVec4(0f, 0f, 0f, 0f).putVec4(0f, 0f, 0f, 0f);
		}
		try (RenderPass rp = ce.createRenderPass(() -> "xpglow", m.getColorTextureView(), OptionalInt.empty())) {
			rp.setPipeline(pipe(blend));
			rp.setUniform("GlowInfo", ub.currentBuffer());
			rp.bindTexture("DepthSampler", m.getDepthTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
			rp.draw(0, n * 6);
		}
		ub.rotate();
	}
}
