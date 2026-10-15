package dev.xpgl;

public class Lay {
	public boolean on = true;
	public int col = 0x7cff3a;
	public int rad = 70;
	public int str = 45;
	public int fal = 100;
	public int anim = 2;
	public int spd = 100;
	public int blend = 0;
	public int rng = 40;
	public int max = 96;

	public static Lay of(int col, int rad, int str, int anim, int rng, int max) {
		Lay l = new Lay();
		l.col = col;
		l.rad = rad;
		l.str = str;
		l.anim = anim;
		l.rng = rng;
		l.max = max;
		return l;
	}

	public void clamp() {
		col &= 0xffffff;
		rad = Math.max(10, Math.min(500, rad));
		str = Math.max(0, Math.min(500, str));
		fal = Math.max(25, Math.min(300, fal));
		anim = Math.max(0, Math.min(4, anim));
		spd = Math.max(10, Math.min(500, spd));
		blend = Math.max(0, Math.min(1, blend));
		rng = Math.max(8, Math.min(128, rng));
		max = Math.max(1, Math.min(Gk.MAX, max));
	}
}
