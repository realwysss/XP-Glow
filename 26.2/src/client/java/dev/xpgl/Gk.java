package dev.xpgl;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class Gk {
	static final int MAX = 256;
	static final float[] a = new float[MAX * 4];
	static final float[] b = new float[MAX * 4];
	static final float[] dd = new float[MAX];
	static int n;
	static double cx, cy, cz;
	static float fx, fy, fz, rng, t, pt, ppu;
	static Lay cur;
	public static int li;
	static boolean warm;
	public static int drawn;

	public static void add(double x, double y, double z, float size, int col, float k, int id) {
		Lay c = cur;
		float dx = (float) (x - cx), dy = (float) (y - cy), dz = (float) (z - cz);
		float d2 = dx * dx + dy * dy + dz * dz;
		if (d2 > rng * rng) return;
		float s = size * c.rad / 100f;
		float fw = dx * fx + dy * fy + dz * fz;
		if (fw < 0.1f || s * ppu < 2.5f * fw) return;
		int cap = Math.min(MAX, c.max);
		int i = n;
		if (n >= cap) {
			int w = 0;
			for (int j = 1; j < n; j++) if (dd[j] > dd[w]) w = j;
			if (dd[w] <= d2) return;
			i = w;
		} else n++;
		float d = (float) Math.sqrt(d2);
		float e = d > rng * 0.8f ? Math.max(0f, 1f - (d - rng * 0.8f) / (rng * 0.2f)) : 1f;
		float ph = id * 0.7f;
		float m = 1f, sz = s;
		int tc = col;
		switch (c.anim) {
			case 1 -> m = 0.7f + 0.3f * (float) Math.sin(t * 5f + ph);
			case 2 -> m = 0.75f + 0.25f * (float) Math.sin(t * 17f + ph) * (float) Math.sin(t * 7.3f + ph * 1.7f);
			case 3 -> tc = hue(col, t * 0.25f + ph * 0.05f);
			case 4 -> sz = s * (0.88f + 0.12f * (float) Math.sin(t * 3f + ph));
			default -> {
			}
		}
		float g = c.str / 100f * k * m * e;
		dd[i] = d2;
		a[i * 4] = dx;
		a[i * 4 + 1] = dy;
		a[i * 4 + 2] = dz;
		a[i * 4 + 3] = sz;
		b[i * 4] = (tc >> 16 & 255) / 255f * g;
		b[i * 4 + 1] = (tc >> 8 & 255) / 255f * g;
		b[i * 4 + 2] = (tc & 255) / 255f * g;
	}

	static int hue(int c, float h) {
		float r = (c >> 16 & 255) / 255f, g = (c >> 8 & 255) / 255f, b = (c & 255) / 255f;
		float mx = Math.max(r, Math.max(g, b)), mn = Math.min(r, Math.min(g, b)), d = mx - mn;
		float hh = 0f;
		if (d > 0f) {
			if (mx == r) hh = ((g - b) / d) % 6f;
			else if (mx == g) hh = (b - r) / d + 2f;
			else hh = (r - g) / d + 4f;
			hh /= 6f;
		}
		hh = (hh + h) % 1f;
		if (hh < 0f) hh += 1f;
		float s = mx == 0f ? 0f : d / mx;
		float x = hh * 6f;
		int i = (int) x;
		float f = x - i, p = mx * (1f - s), q = mx * (1f - s * f), u = mx * (1f - s * (1f - f));
		int k = i % 6;
		float rr = k == 0 || k == 5 ? mx : k == 1 ? q : k == 4 ? u : p;
		float gg = k == 1 || k == 2 ? mx : k == 0 ? u : k == 3 ? q : p;
		float bb = k == 3 || k == 4 ? mx : k == 2 ? u : k == 5 ? q : p;
		return (int) (rr * 255) << 16 | (int) (gg * 255) << 8 | (int) (bb * 255);
	}

	public static void run(Matrix4f rot, Matrix4f proj, Vec3 p, float ptk) {
		if (!warm) {
			warm = true;
			Gx.warm();
		}
		Cfg c = Cfg.c;
		drawn = 0;
		if (!c.on) return;
		cx = p.x;
		cy = p.y;
		cz = p.z;
		fx = -rot.m02();
		fy = -rot.m12();
		fz = -rot.m22();
		ppu = proj.m11() * Gx.hh() * 0.5f;
		pt = ptk;
		Lay[] ls = c.layers();
		for (int k = 0; k < ls.length; k++) {
			Lay l = ls[k];
			if (!l.on || l.str <= 0) continue;
			cur = l;
			li = k;
			rng = l.rng;
			t = (float) ((System.nanoTime() - Cfg.T0) / 1e9 * l.spd / 100.0);
			n = 0;
			Em.gather();
			drawn += n;
			if (n == 0) continue;
			Gx.draw(rot, proj, n, a, b, l.fal / 100f * 2f, l.blend, t, k);
		}
	}
}
