package dev.xpgl;

import java.util.Arrays;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CfgScr {
	static Component t(String s) {
		return Component.literal(s);
	}

	public static Screen mk(Screen p) {
		ConfigBuilder b = ConfigBuilder.create().setParentScreen(p).setTitle(t("XP Glow")).setSavingRunnable(Cfg::sv);
		ConfigEntryBuilder e = b.entryBuilder();
		Cfg c = Cfg.c;
		ConfigCategory g = b.getOrCreateCategory(t("Glow"));
		g.addEntry(e.startBooleanToggle(t("XP Glow On"), c.on).setDefaultValue(true).setTooltip(t("Turn this off to remove the glow completely. It then costs nothing.")).setSaveConsumer(v -> c.on = v).build());
		g.addEntry(e.startColorField(t("Color"), c.col).setDefaultValue(0x7cff3a).setTooltip(t("The glow color when Orb Colors is off.")).setSaveConsumer(v -> c.col = v & 0xffffff).build());
		g.addEntry(e.startBooleanToggle(t("Orb Colors"), c.oc).setDefaultValue(true).setTooltip(t("Uses the green and yellow color the orb itself shows instead of the color above.")).setSaveConsumer(v -> c.oc = v).build());
		g.addEntry(e.startIntSlider(t("Size"), c.rad, 10, 500).setDefaultValue(100).setTextGetter(v -> t(v + "%")).setTooltip(t("How big the glow is. 100% is the normal size.")).setSaveConsumer(v -> c.rad = v).build());
		g.addEntry(e.startIntSlider(t("Strength"), c.str, 0, 500).setDefaultValue(100).setTextGetter(v -> t(v + "%")).setTooltip(t("How bright the glow is. Above 100% it gets brighter than normal.")).setSaveConsumer(v -> c.str = v).build());
		g.addEntry(e.startIntSlider(t("Softness"), c.fal, 25, 300).setDefaultValue(100).setTextGetter(v -> t(v + "%")).setTooltip(t("Low makes a wide soft glow, high makes a small tight glow around the middle.")).setSaveConsumer(v -> c.fal = v).build());
		String[] bs = {"Add", "Screen"};
		g.addEntry(e.startSelector(t("Blend"), bs, bs[c.blend]).setDefaultValue(bs[0]).setTooltip(t("Add makes the glow brighter and can burn out to white. Screen is gentler and never burns out.")).setSaveConsumer(v -> c.blend = Arrays.asList(bs).indexOf(v)).build());
		ConfigCategory f = b.getOrCreateCategory(t("Effects"));
		String[] an = {"Static", "Pulse", "Flicker", "Rainbow", "Shimmer"};
		f.addEntry(e.startSelector(t("Effect"), an, an[c.anim]).setDefaultValue(an[2]).setTooltip(t("Pulse fades in and out, Flicker is quick and uneven, Rainbow cycles the color, Shimmer breathes the size.")).setSaveConsumer(v -> c.anim = Arrays.asList(an).indexOf(v)).build());
		f.addEntry(e.startIntSlider(t("Effect Speed"), c.spd, 10, 500).setDefaultValue(100).setTextGetter(v -> t(v + "%")).setTooltip(t("How fast the effect runs.")).setSaveConsumer(v -> c.spd = v).build());
		ConfigCategory p2 = b.getOrCreateCategory(t("Performance"));
		p2.addEntry(e.startIntSlider(t("Range"), c.rng, 8, 128).setDefaultValue(40).setTextGetter(v -> t(v + " blocks")).setTooltip(t("Glows further away than this are not drawn. Lower is faster.")).setSaveConsumer(v -> c.rng = v).build());
		p2.addEntry(e.startIntSlider(t("Max Glows"), c.max, 1, 256).setDefaultValue(96).setTooltip(t("The most glows drawn at once. The closest ones are kept. Lower is faster.")).setSaveConsumer(v -> c.max = v).build());
		return b.build();
	}
}
