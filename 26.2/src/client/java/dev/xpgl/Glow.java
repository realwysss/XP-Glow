package dev.xpgl;

import net.minecraft.resources.Identifier;

public final class Glow {
	public static final String ID = "xpglow";

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}
}
