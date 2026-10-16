package dev.xpgl.ui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xpgl.Frost;
import dev.xpgl.Gpu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2f;

public final class Panel extends Screen implements Canvas {
    private final Screen parent;
    private final Menu menu;
    private GuiGraphics g;

    public Panel(Screen parent) {
        super(Component.literal("XP Glow"));
        this.parent = parent;
        this.menu = new Menu(this::back, () -> minecraft == null ? null : minecraft.keyboardHandler.getClipboard());
    }

    private void back() {
        minecraft.setScreen(parent);
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
        if (minecraft.level == null) renderPanorama(g, delta);
        Frost.request();
        g.blurBeforeThisStratum();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        this.g = g;
        menu.draw(this, width, height, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent e, boolean twice) {
        return menu.click(e.x(), e.y(), e.button()) || super.mouseClicked(e, twice);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent e) {
        return menu.release(e.x(), e.y(), e.button()) || super.mouseReleased(e);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
        return menu.dragged(e.x(), e.y()) || super.mouseDragged(e, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double sx, double sy) {
        return menu.scroll(x, y, sy != 0 ? sy : sx) || super.mouseScrolled(x, y, sx, sy);
    }

    @Override
    public boolean keyPressed(KeyEvent e) {
        if (dev.xpgl.XpGl.key != null && dev.xpgl.XpGl.key.matches(e) && !menu.typing()) {
            onClose();
            return true;
        }
        int action = switch (e.key()) {
            case InputConstants.KEY_BACKSPACE -> Menu.BACK;
            case InputConstants.KEY_RETURN, InputConstants.KEY_NUMPADENTER -> Menu.ENTER;
            case InputConstants.KEY_ESCAPE -> Menu.ESCAPE;
            case InputConstants.KEY_TAB -> Menu.TAB;
            case InputConstants.KEY_V -> e.hasControlDown() ? Menu.PASTE : 0;
            case InputConstants.KEY_C -> e.hasControlDown() ? Menu.COPY : 0;
            default -> 0;
        };
        if (action != 0 && menu.key(action)) return true;
        return super.keyPressed(e);
    }

    @Override
    public boolean charTyped(CharacterEvent e) {
        return menu.type(e.codepoint()) || super.charTyped(e);
    }

    @Override
    public void onClose() {
        menu.save();
        back();
    }

    @Override
    public void fill(float x, float y, float w, float h, float r, int top, int bottom) {
        quad(Gpu.ui(0), x, y, w, h, code(r, h), 0, top, bottom);
    }

    @Override
    public void ring(float x, float y, float w, float h, float r, float t, int top, int bottom) {
        quad(Gpu.ui(1), x, y, w, h, code(r, h), code(t, h), top, bottom);
    }

    @Override
    public void shadow(float x, float y, float w, float h, float r, float blur, int argb) {
        float eh = h + blur * 2;
        quad(Gpu.ui(2), x - blur, y - blur, w + blur * 2, eh, code(r, eh), code(blur, eh), argb, argb);
    }

    @Override
    public void hue(float x, float y, float w, float h, float r, int argb) {
        quad(Gpu.ui(3), x, y, w, h, code(r, h), 0, argb, argb);
    }

    @Override
    public void sv(float x, float y, float w, float h, float r, int argb) {
        quad(Gpu.ui(4), x, y, w, h, code(r, h), 0, argb, argb);
    }

    @Override
    public void text(String s, float x, float y, int argb) {
        if ((argb >>> 24) < 4) return;
        g.pose().pushMatrix();
        g.pose().translate(x, y);
        g.drawString(font, s, 0, 0, argb, false);
        g.pose().popMatrix();
    }

    @Override
    public int width(String s) {
        return font.width(s);
    }

    private final java.util.Map<String, net.minecraft.world.entity.Entity> ents = new java.util.HashMap<>();

    @Override
    public void item(String id, float x, float y, float size) {
        net.minecraft.world.item.Item it = id.equals("anchor") ? net.minecraft.world.item.Items.RESPAWN_ANCHOR : id.equals("glowstone") ? net.minecraft.world.item.Items.GLOWSTONE : net.minecraft.world.item.Items.END_CRYSTAL;
        g.pose().pushMatrix();
        g.pose().translate(x, y);
        g.pose().scale(size / 16f, size / 16f);
        g.renderItem(new net.minecraft.world.item.ItemStack(it), 0, 0);
        g.pose().popMatrix();
    }

    @Override
    public boolean player(float x, float y, float w, float h) {
        if (minecraft.player == null) return false;
        float sw = (float) Math.sin(System.nanoTime() / 1.1e9) * 90f;
        net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventoryFollowsMouse(g, (int) x, (int) y, (int) (x + w), (int) (y + h), (int) (h * 0.42f), 0.0625f, x + w / 2f + sw, y + h * 0.3f, minecraft.player);
        return true;
    }

    @Override
    public void hurt() {
        if (minecraft.player != null) {
            minecraft.player.hurtDuration = 10;
            minecraft.player.hurtTime = 10;
        }
    }

    private net.minecraft.world.entity.Entity mkEnt(String kind, int n) {
        net.minecraft.world.level.Level lv = minecraft.level;
        if (kind.equals("crystal")) {
            net.minecraft.world.entity.boss.enderdragon.EndCrystal c = new net.minecraft.world.entity.boss.enderdragon.EndCrystal(lv, 0, 0, 0);
            c.setShowBottom(true);
            return c;
        }
        if (kind.equals("xp")) return new net.minecraft.world.entity.ExperienceOrb(lv, 0, 0, 0, n);
        return null;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean ent(String kind, int n, float x, float y, float w, float h, float yaw, float age) {
        if (minecraft.level == null) return false;
        try {
            net.minecraft.world.entity.Entity e = ents.computeIfAbsent(kind + n, k -> mkEnt(kind, n));
            if (e == null) return false;
            net.minecraft.client.renderer.entity.EntityRenderer r = minecraft.getEntityRenderDispatcher().getRenderer(e);
            net.minecraft.client.renderer.entity.state.EntityRenderState st = r.createRenderState(e, 1f);
            st.lightCoords = 0xF000F0;
            st.shadowPieces.clear();
            st.outlineColor = 0;
            st.ageInTicks = age;
            org.joml.Quaternionf q = new org.joml.Quaternionf().rotateZ((float) Math.PI).rotateY(yaw);
            org.joml.Quaternionf cam = new org.joml.Quaternionf().rotateX(0.4f);
            q.mul(cam);
            g.submitEntityRenderState(st, h * (kind.equals("xp") ? 4.2f : 0.3f), new org.joml.Vector3f(0f, st.boundingBoxHeight / 2f + (kind.equals("xp") ? 0f : 0.15f), 0f), q, cam, (int) x, (int) y, (int) (x + w), (int) (y + h));
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private void face(String atlas, String name, float[] p, int col) {
        net.minecraft.client.renderer.texture.TextureAtlas at = minecraft.getAtlasManager().getAtlasOrThrow(Identifier.withDefaultNamespace(atlas));
        net.minecraft.client.renderer.texture.TextureAtlasSprite sp = at.getSprite(Identifier.withDefaultNamespace(name));
        Matrix3x2f pose = new Matrix3x2f(g.pose()).mul(new Matrix3x2f(p[2] - p[0], p[3] - p[1], p[6] - p[0], p[7] - p[1], p[0], p[1]));
        g.guiRenderState.submitGuiElement(new Quad(RenderPipelines.GUI_TEXTURED, TextureSetup.singleTexture(at.getTextureView(), at.getSampler()), pose, sp.getU0(), sp.getU1(), sp.getV0(), sp.getV1(), col, col, new ScreenRectangle(0, 0, 1, 1).transformMaxBounds(pose)));
    }

    @Override
    public boolean blk(String kind, int n, float cx, float cy, float s, float yaw) {
        if (!kind.equals("anchor")) return false;
        try {
            float c = (float) Math.cos(yaw), sn = (float) Math.sin(yaw);
            float ca = 0.8191f, sa = 0.5736f;
            float[][] cor = new float[8][];
            for (int i = 0; i < 8; i++) {
                float X = (i & 1) == 0 ? -0.5f : 0.5f, Y = (i & 2) == 0 ? -0.5f : 0.5f, Z = (i & 4) == 0 ? -0.5f : 0.5f;
                float rx = X * c + Z * sn, rz = -X * sn + Z * c;
                cor[i] = new float[]{cx + rx * s, cy - (Y * ca - rz * sa) * s};
            }
            int[][] fs = {{2, 3, 7, 6}, {3, 2, 0, 1}, {6, 7, 5, 4}, {2, 6, 4, 0}, {7, 3, 1, 5}};
            float[][] nr = {{0, 1, 0}, {0, 0, -1}, {0, 0, 1}, {-1, 0, 0}, {1, 0, 0}};
            String side = "block/respawn_anchor_side" + n;
            String[] nm = {n > 0 ? "block/respawn_anchor_top" : "block/respawn_anchor_top_off", side, side, side, side};
            for (int k = 0; k < 5; k++) {
                float nz = -nr[k][0] * sn + nr[k][2] * c;
                if (nr[k][1] * sa + nz * ca <= 0.01f) continue;
                int[] f = fs[k];
                int sh = k == 0 ? 0xFFFFFFFF : Math.abs(nr[k][0]) > 0 ? 0xFFA8A8A8 : 0xFFD2D2D2;
                face("blocks", nm[k], new float[]{cor[f[0]][0], cor[f[0]][1], cor[f[1]][0], cor[f[1]][1], cor[f[2]][0], cor[f[2]][1], cor[f[3]][0], cor[f[3]][1]}, sh);
            }
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public void spr(String name, float x, float y, float size, int argb) {
        try {
            net.minecraft.client.renderer.texture.TextureAtlas at = minecraft.getAtlasManager().getAtlasOrThrow(Identifier.withDefaultNamespace("particles"));
            net.minecraft.client.renderer.texture.TextureAtlasSprite sp = at.getSprite(Identifier.withDefaultNamespace(name));
            Matrix3x2f pose = new Matrix3x2f(g.pose()).translate(x, y).scale(size, size);
            g.guiRenderState.submitGuiElement(new Quad(RenderPipelines.GUI_TEXTURED, TextureSetup.singleTexture(at.getTextureView(), at.getSampler()), pose, sp.getU0(), sp.getU1(), sp.getV0(), sp.getV1(), argb, argb, new ScreenRectangle(0, 0, 1, 1).transformMaxBounds(pose)));
        } catch (Throwable t) {
        }
    }

    private void quad(RenderPipeline p, float x, float y, float w, float h, int cu, int cv, int top, int bottom) {
        if (w <= 0 || h <= 0 || ((top | bottom) >>> 24) == 0) return;
        element(p, TextureSetup.noTexture(), x, y, w, h, 4 * cu + 1, 4 * cu + 3, 4 * cv + 1, 4 * cv + 3, top, bottom);
    }

    private void element(RenderPipeline p, TextureSetup t, float x, float y, float w, float h, float u0, float u1, float v0, float v1, int top, int bottom) {
        Matrix3x2f pose = new Matrix3x2f(g.pose()).translate(x, y).scale(w, h);
        g.guiRenderState.submitGuiElement(new Quad(p, t, pose, u0, u1, v0, v1, top, bottom, new ScreenRectangle(0, 0, 1, 1).transformMaxBounds(pose)));
    }

    private static int code(float v, float h) {
        return Math.max(0, Math.min(4000, Math.round(v / Math.max(h, 0.001f) * 500f)));
    }

    private record Quad(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, float u0, float u1, float v0, float v1,
                        int top, int bottom, ScreenRectangle bounds) implements GuiElementRenderState {
        @Override
        public void buildVertices(VertexConsumer c) {
            c.addVertexWith2DPose(pose, 0, 0).setUv(u0, v0).setColor(top);
            c.addVertexWith2DPose(pose, 0, 1).setUv(u0, v1).setColor(bottom);
            c.addVertexWith2DPose(pose, 1, 1).setUv(u1, v1).setColor(bottom);
            c.addVertexWith2DPose(pose, 1, 0).setUv(u1, v0).setColor(top);
        }

        @Override
        public ScreenRectangle scissorArea() {
            return null;
        }
    }
}
