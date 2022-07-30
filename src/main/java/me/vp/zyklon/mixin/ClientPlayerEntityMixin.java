package me.vp.zyklon.mixin;

import com.mojang.authlib.GameProfile;
import me.vp.zyklon.Zyklon;
import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.module.modules.NoSlow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    MinecraftClient mc = MinecraftClient.getInstance();

    private ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile, null);
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

    @Override
	protected boolean clipAtLedge() {
		return super.clipAtLedge()
				|| Zyklon.INSTANCE.moduleManager.getModule("SafeWalk").isEnabled();
	}
}