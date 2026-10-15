package dev.xpgl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class Cfg {
	public boolean on = true;
	public int col = 0x7cff3a;
	public int rad = 100;
	public int str = 100;
	public int fal = 100;
	public int anim = 2;
	public int spd = 100;
	public int blend = 0;
	public int rng = 40;
	public int max = 96;
	public boolean oc = true;

	public static final long T0 = System.nanoTime();
	static final Gson G = new GsonBuilder().setPrettyPrinting().create();
	static final Path P = FabricLoader.getInstance().getConfigDir().resolve("xpglow.json");
	public static Cfg c = new Cfg();

	public static void ld() {
		try {
			if (Files.exists(P)) {
				Cfg n = G.fromJson(Files.readString(P), Cfg.class);
				if (n != null) c = n;
			}
		} catch (Exception e) {
			c = new Cfg();
		}
		fx();
	}

	public static void fx() {
		c.col &= 0xffffff;
		c.rad = Math.max(10, Math.min(500, c.rad));
		c.str = Math.max(0, Math.min(500, c.str));
		c.fal = Math.max(25, Math.min(300, c.fal));
		c.anim = Math.max(0, Math.min(4, c.anim));
		c.spd = Math.max(10, Math.min(500, c.spd));
		c.blend = Math.max(0, Math.min(1, c.blend));
		c.rng = Math.max(8, Math.min(128, c.rng));
		c.max = Math.max(1, Math.min(Gk.MAX, c.max));
	}

	public static void sv() {
		fx();
		try {
			Files.writeString(P, G.toJson(c));
		} catch (Exception e) {
		}
	}
}
