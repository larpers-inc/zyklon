package dev.vili.zyklon.mixin;

import com.mojang.authlib.GameProfile;
import dev.vili.zyklon.event.events.ClientMoveEvent;
import dev.vili.zyklon.event.events.SendMovementPacketsEvent;
import dev.vili.zyklon.event.events.SwingHandEvent;
import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.module.modules.*;
import dev.vili.zyklon.Zyklon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    MinecraftClient mc = MinecraftClient.getInstance();
	private Screen tempCurrentScreen;
    @Shadow
    private ClientPlayNetworkHandler networkHandler;
	@Shadow
	private float mountJumpStrength;
	@Shadow
	private void autoJump(float dx, float dz) {}

	private ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
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

	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", opcode = Opcodes.GETFIELD,
			ordinal = 0), method = {"updateNausea()V"})
	private void beforeUpdateNausea(CallbackInfo ci) {
		if (Zyklon.INSTANCE.moduleManager.getModule("PortalGui").isEnabled()) {
			tempCurrentScreen = mc.currentScreen;
			mc.currentScreen = null;
		}
	}

	@Inject(method = "updateNausea", at = @At("HEAD"), cancellable = true)
	public void updateNausea(CallbackInfo ci) {
		NoOverlay noOverlay = (NoOverlay) Zyklon.INSTANCE.moduleManager.getModule("NoOverlay");

		if (noOverlay != null && noOverlay.portal.isEnabled()) ci.cancel();
	}

	@Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
	public void isCamera(CallbackInfoReturnable<Boolean> cir) {
		Freecam freecam = (Freecam) Zyklon.INSTANCE.moduleManager.getModule("Freecam");

		if (freecam != null && freecam.isEnabled()) cir.setReturnValue(true);
	}

	@Override
	protected float getJumpVelocity() {
		HighJump highJump = (HighJump) Zyklon.INSTANCE.moduleManager.getModule("HighJump");
		if (highJump.isEnabled()) return (float) (super.getJumpVelocity() + highJump.strength.getValue());
		return super.getJumpVelocity();
	}
}