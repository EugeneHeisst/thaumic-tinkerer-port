package dev.eugheist.ttmodern;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Vector3f;


/** Original build164 summon call: one FXEssentiaTrail per pedestal, count20/scale20.
 * Homing/billboard adaptation follows the compatible base port; no base files are changed.
 */
public final class SummonFx {
    static int received;
    public static void handle(dev.eugheist.tc4modern.InfusionContent.FxPayload p){
        received++;var mc=Minecraft.getInstance();if(mc.level==null)return;
        var from=Vec3.atCenterOf(BlockPos.containing(p.x0(),p.y0(),p.z0()));
        var to=Vec3.atCenterOf(BlockPos.containing(p.x1(),p.y1(),p.z1()));
        var camera=mc.getCameraEntity();int range=mc.options.graphicsMode().get()==net.minecraft.client.GraphicsStatus.FAST?32:64;
        if(camera!=null&&camera.distanceToSqr(from)>range*range)return;
        mc.particleEngine.add(new EssentiaTrail(mc.level,from,to,20,p.data(),20f));
    }
    static abstract class Homing extends TextureSheetParticle {
        final Vec3 target;
        Homing(ClientLevel level, Vec3 start, Vec3 target) { super(level, start.x, start.y, start.z); this.target = target; hasPhysics = true; }
        @Override public void move(double dx, double dy, double dz) {
            double ox = dx, oy = dy, oz = dz;
            if (hasPhysics && (dx != 0 || dy != 0 || dz != 0) && dx * dx + dy * dy + dz * dz < 10000) {
                var v = Entity.collideBoundingBox(null, new Vec3(dx, dy, dz), getBoundingBox(), level, java.util.List.of());
                dx = v.x; dy = v.y; dz = v.z;
            }
            if (dx != 0 || dy != 0 || dz != 0) { setBoundingBox(getBoundingBox().move(dx, dy, dz)); setLocationFromBoundingbox(); }
            onGround = oy != dy && oy < 0;
            if (ox != dx) xd = 0;
            if (oy != dy) yd = 0;
            if (oz != dz) zd = 0;
        }
        /** pushOutOfBlocks: inside any non-air block (no liquid) leave through the closest face whose neighbour is not a full cube. */
        boolean pushOut(boolean requireFullCube) {
            var pos = BlockPos.containing(x, y, z);
            var state = level.getBlockState(pos);
            if (state.isAir() || !state.getFluidState().isEmpty()) return false;
            if (requireFullCube && !state.isCollisionShapeFullBlock(level, pos)) return false;
            // DEVIATION: the original pushes out of any non-air block and overwrites the velocity every tick, so a bit inside
            // the pillar model above its 0..0.5 collision box fought the homing and hovered there ("stuck in the pillars").
            // Only push when actually inside collision geometry; collisions then slide the bits around the pillars.
            var shape = state.getCollisionShape(level, pos);
            if (shape.isEmpty() || !shape.bounds().move(pos).contains(x, y, z)) return false;
            double fx = x - pos.getX(), fy = y - pos.getY(), fz = z - pos.getZ(), nearest = 9999;
            Direction face = null;
            for (var d : new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}) {
                var n = pos.relative(d);
                if (level.getBlockState(n).isCollisionShapeFullBlock(level, n)) continue;
                double dist = switch (d) { case WEST -> fx; case EAST -> 1 - fx; case DOWN -> fy; case UP -> 1 - fy; case NORTH -> fz; case SOUTH -> 1 - fz; };
                if (dist < nearest) { nearest = dist; face = d; }
            }
            float speed = random.nextFloat() * .05f + .025f, other = (random.nextFloat() - random.nextFloat()) * .1f;
            if (face != null) {
                xd = face.getAxis() == Direction.Axis.X ? speed * face.getStepX() : other;
                yd = face.getAxis() == Direction.Axis.Y ? speed * face.getStepY() : other;
                zd = face.getAxis() == Direction.Axis.Z ? speed * face.getStepZ() : other;
            }
            return true;
        }
        /** EntityFX billboard with the original corner order and fullbright (240) light. */
        void quad(VertexConsumer out, Camera camera, float partial, float size, float u0, float u1, float v0, float v1, float alpha) {
            var eye = camera.getPosition();
            float cx = (float) (Mth.lerp(partial, xo, x) - eye.x), cy = (float) (Mth.lerp(partial, yo, y) - eye.y), cz = (float) (Mth.lerp(partial, zo, z) - eye.z);
            float[][] corners = {{-1, -1, u1, v1}, {-1, 1, u1, v0}, {1, 1, u0, v0}, {1, -1, u0, v1}};
            for (var c : corners) {
                var o = new Vector3f(-c[0] * size, c[1] * size, 0).rotate(camera.rotation());
                out.addVertex(cx + o.x, cy + o.y, cz + o.z).setUv(c[2], c[3]).setColor(rCol, gCol, bCol, alpha).setLight(0xF000F0);
            }
        }
    }
    static final class EssentiaTrail extends Homing {
        private final int count;
        private float scale;
        EssentiaTrail(ClientLevel level, Vec3 start, Vec3 target, int count, int color, float scale) {
            super(level, start, target);
            this.count = count;
            this.scale = (Mth.sin(count / 2f) * .1f + 1) * scale;
            int base = Math.max(1, (int) (start.distanceTo(target) * 30));
            lifetime = base / 2 + random.nextInt(base);
            xd = Mth.sin(count / 4f) * .015f + random.nextGaussian() * .002f;
            yd = .1f + Mth.sin(count / 3f) * .01f;
            zd = Mth.sin(count / 2f) * .015f + random.nextGaussian() * .002f;
            float r = (color >> 16 & 255) / 255f, g = (color >> 8 & 255) / 255f, b = (color & 255) / 255f;
            setColor(r - r * .2f + random.nextFloat() * r * .2f, g - g * .2f + random.nextFloat() * g * .2f, b - b * .2f + random.nextFloat() * b * .2f);
            gravity = .2f;

        }
        @Override public void tick() {
            xo = x; yo = y; zo = z;
            if (age++ >= lifetime) { remove(); return; }
            yd += .01 * gravity; // original adds "gravity" upwards
            pushOut(true);
            move(xd, yd, zd);
            xd = Mth.clamp((float) (xd * .985), -.05f, .05f);
            yd = Mth.clamp((float) (yd * .985), -.05f, .05f);
            zd = Mth.clamp((float) (zd * .985), -.05f, .05f);
            double dx = target.x - x, dy = target.y - y, dz = target.z - z, len = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (len < 2) scale *= .98f;
            if (scale < .2f) { remove(); return; }
            if (len > 0) { double acc = .01 / Math.min(1, len); xd += dx / len * acc; yd += dy / len * acc; zd += dz / len * acc; }
        }
        @Override public ParticleRenderType getRenderType() { return TRANSLUCENT; }
        @Override public void render(VertexConsumer out, Camera camera, float partial) {
            float s = Mth.sin((age - count) / 5f) * .25f + 1;
            quad(out, camera, partial, .1f * scale * s, .5625f, .625f, .0625f, .125f, .5f);
        }
    }

    static final ParticleRenderType TRANSLUCENT = new ParticleRenderType() {
        @Override public BufferBuilder begin(Tesselator tessellator, net.minecraft.client.renderer.texture.TextureManager textures) {
            RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tc4modern","textures/misc/particles.png"));
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 771);
            RenderSystem.depthMask(false);
            return tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }
    };
}
