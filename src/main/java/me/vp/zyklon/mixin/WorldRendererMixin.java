package me.vp.zyklon.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.vp.zyklon.Zyklon;
import me.vp.zyklon.event.events.WorldRenderEvent;
import me.vp.zyklon.module.modules.NoWeather;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Shadow @Nullable private ClientWorld world;

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void head(MatrixStack matrixStack, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                      LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo callback) {
		WorldRenderEvent.Pre event = new WorldRenderEvent.Pre(tickDelta, matrixStack);
		Zyklon.INSTANCE.EVENT_BUS.post(event);

		if (event.isCancelled()) {
			callback.cancel();
		}
	}

    @Inject(method = "render", at = @At("RETURN"))
	private void render_return(MatrixStack matrixStack, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
			LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo callback) {
		RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
		WorldRenderEvent.Post event = new WorldRenderEvent.Post(tickDelta, matrixStack);
		Zyklon.INSTANCE.EVENT_BUS.post(event);
	}

	@Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
	public void renderWeather(LightmapTextureManager manager, float f, double d, double e, double g, CallbackInfo ci) {
		NoWeather noWeather = (NoWeather) Zyklon.INSTANCE.moduleManager.getModule("NoWeather");

		if (noWeather != null && noWeather.isEnabled()) ci.cancel();
	}

	@Inject(method = "tickRainSplashing", at = @At("HEAD"), cancellable = true)
	public void tickRainSplashing(Camera camera, CallbackInfo ci) {
		NoWeather noWeather = (NoWeather) Zyklon.INSTANCE.moduleManager.getModule("NoWeather");

		if (noWeather != null && noWeather.isEnabled()) ci.cancel();
	}

	@Inject(method = "spawnParticle(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true)
	public void spawnParticle(ParticleEffect parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
		NoWeather noWeather = (NoWeather) Zyklon.INSTANCE.moduleManager.getModule("NoWeather");

		if (noWeather != null && noWeather.isEnabled() && (this.world.isRaining() || this.world.isThundering()) && parameters.getType().equals(ParticleTypes.DRIPPING_WATER)) {
			cir.cancel();
		}
	}
}