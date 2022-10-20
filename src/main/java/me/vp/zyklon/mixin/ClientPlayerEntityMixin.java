package me.vp.zyklon.mixin;

import com.mojang.authlib.GameProfile;
import me.vp.zyklon.Zyklon;
import me.vp.zyklon.event.events.*;
import me.vp.zyklon.module.modules.EntityControl;
import me.vp.zyklon.module.modules.NoSlow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    MinecraftClient mc = MinecraftClient.getInstance();
    private PlayerPacketEvent preEvent;

    @Shadow
    private ClientPlayNetworkHandler networkHandler;
	@Shadow
	private float mountJumpStrength;
	@Shadow
	private void autoJump(float dx, float dz) {}

	private ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile, null);
	}

	@Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
	private void sendMovementPackets(CallbackInfo info) {
		SendMovementPacketsEvent event = new SendMovementPacketsEvent();
		Zyklon.INSTANCE.EVENT_BUS.post(event);

		if (event.isCancelled()) {
			info.cancel();
		}
	}

	@Inject(method = "move", at = @At("HEAD"), cancellable = true)
	private void move(MovementType type, Vec3d movement, CallbackInfo info) {
		ClientMoveEvent event = new ClientMoveEvent(type, movement);
		Zyklon.INSTANCE.EVENT_BUS.post(event);

		if (event.isCancelled()) {
			info.cancel();
		} else if (!type.equals(event.getType()) || !movement.equals(event.getVec())) {
			double double_1 = this.getX();
			double double_2 = this.getZ();
			super.move(event.getType(), event.getVec());
			this.autoJump((float) (this.getX() - double_1), (float) (this.getZ() - double_2));
			info.cancel();
		}
	}

	@Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (mc.player != null && mc.world != null) {
            Zyklon.INSTANCE.moduleManager.onTick();
            TickEvent event = new TickEvent();
            Zyklon.INSTANCE.EVENT_BUS.post(event);
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), require = 0)
	private boolean tickMovement_isUsingItem(ClientPlayerEntity player) {
		NoSlow noSlow = (NoSlow) Zyklon.INSTANCE.moduleManager.getModule("NoSlow");
		if (noSlow.isEnabled() && noSlow.items.isEnabled())
			return false;

		return player.isUsingItem();
	}

	@Overwrite
	public void swingHand(Hand hand) {
		SwingHandEvent event = new SwingHandEvent(hand);
		Zyklon.INSTANCE.EVENT_BUS.post(event);

		if (!event.isCancelled()) {
			super.swingHand(event.getHand());
		}

		networkHandler.sendPacket(new HandSwingC2SPacket(hand));
	}

    @Override
	protected boolean clipAtLedge() {
		return super.clipAtLedge()
				|| Zyklon.INSTANCE.moduleManager.getModule("SafeWalk").isEnabled();
	}

	@Overwrite
	public float getMountJumpStrength() {
		EntityControl entityControl = (EntityControl) Zyklon.INSTANCE.moduleManager.getModule("EntityControl");
		return Zyklon.INSTANCE.moduleManager.isModuleEnabled("EntityControl")
				&& entityControl.maxJump.isEnabled() ? 1F : mountJumpStrength;
	}
}