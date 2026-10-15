package dev.xpgl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.phys.Vec3;

public class Em {
	public static void gather() {
		Minecraft m = Minecraft.getInstance();
		if (m.level == null) return;
		for (Entity e : m.level.entitiesForRendering()) {
			if (!(e instanceof ExperienceOrb) || e.isRemoved()) continue;
			Vec3 p = e.getPosition(Gk.pt);
			Gk.add(p.x, p.y + e.getBbHeight() * 0.5, p.z, 0.8f, Cfg.c.oc ? orb(e.tickCount + Gk.pt) : Gk.cur.col, 1f, e.getId());
		}
	}

	static int orb(float t) {
		float h = t / 4f;
		int r = (int) ((Mth.sin(h) + 1f) * 0.5f * 255f);
		int b = (int) ((Mth.sin(h + 4.1887903f) + 1f) * 0.1f * 255f);
		return r << 16 | 255 << 8 | b;
	}
}
