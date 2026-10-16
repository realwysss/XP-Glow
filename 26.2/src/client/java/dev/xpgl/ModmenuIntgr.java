package dev.xpgl;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModmenuIntgr implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return p -> net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("wysslib") ? new dev.xpgl.ui.Panel(p) : new net.minecraft.client.gui.screens.AlertScreen(() -> net.minecraft.client.Minecraft.getInstance().gui.setScreen(p), net.minecraft.network.chat.Component.literal("XP Glow"), net.minecraft.network.chat.Component.literal("Install Wysss Library to open this menu."));
	}
}
