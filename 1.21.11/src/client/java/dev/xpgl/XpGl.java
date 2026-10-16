package dev.xpgl;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class XpGl implements ClientModInitializer {
	public static KeyMapping key;

	public void onInitializeClient() {
		Cfg.ld();
		KeyMapping k = key = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.xpglow.open", InputConstants.KEY_O, KeyMapping.Category.register(Identifier.fromNamespaceAndPath("xpglow", "main"))));
		ClientTickEvents.END_CLIENT_TICK.register(m -> {
			while (k.consumeClick()) if (m.screen == null) {
				if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("wysslib")) m.setScreen(new dev.xpgl.ui.Panel(null));
				else if (m.player != null) m.player.displayClientMessage(net.minecraft.network.chat.Component.literal("XP Glow needs Wysss Library to open its menu"), true);
			}
		});
	}
}
