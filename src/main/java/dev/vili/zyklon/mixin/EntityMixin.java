package dev.vili.zyklon.mixin;

import dev.vili.zyklon.module.modules.EntityEsp;
import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.event.events.PlayerPushedEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

@Mixin(Entity.class)
public class EntityMixin {

    @ModifyArgs(method = "pushAwayFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
	private void pushAwayFrom_addVelocity(Args args) {
		PlayerPushedEvent event = new PlayerPushedEvent(args.get(0), args.get(1), args.get(2));
		Zyklon.INSTANCE.EVENT_BUS.post(event);

		args.set(0, event.getPushX());
		args.set(1, event.getPushY());
		args.set(2, event.getPushZ());
	}


    @Inject(at = { @At("HEAD") }, method = "isInvisibleTo", cancellable = true)
    private void isInvisibleTo(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
    	if (Zyklon.INSTANCE.moduleManager.isModuleEnabled("AntiInvis")) {
    		cir.setReturnValue(false);
        }
    }

	@Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
	private void overrideIsGlowing(CallbackInfoReturnable<Boolean> cir) {
		EntityEsp entityEsp = (EntityEsp) Zyklon.INSTANCE.moduleManager.getModule("EntityEsp");
		if (entityEsp.isEnabled() && entityEsp.mode.is("Glow"))
			cir.setReturnValue(true);
		else cir.setReturnValue(false);
	}

	@Inject(method = "getTeamColorValue", at = @At("RETURN"), cancellable = true)
	private void overrideTeamColor(CallbackInfoReturnable<Integer> cir) {
		EntityEsp entityEsp = (EntityEsp) Zyklon.INSTANCE.moduleManager.getModule("EntityEsp");

		if (entityEsp.isEnabled() && entityEsp.mode.is("Glow")) {
			if (entityEsp.players.isEnabled()) cir.setReturnValue(new Color(255, 0, 0).getRGB());
			else if (entityEsp.hostiles.isEnabled()) cir.setReturnValue(new Color(255, 0, 255).getRGB());
			else if (entityEsp.animals.isEnabled()) cir.setReturnValue(new Color(0, 255, 0).getRGB());
			else if (entityEsp.friends.isEnabled()) cir.setReturnValue(new Color(0, 255, 227).getRGB());
			else if (entityEsp.items.isEnabled()) cir.setReturnValue(new Color(255, 255, 0).getRGB());
			else if (entityEsp.projectiles.isEnabled()) cir.setReturnValue(new Color(255, 255, 255).getRGB());
			else if (entityEsp.endCrystals.isEnabled()) cir.setReturnValue(new Color(50, 0, 125).getRGB());
		}
		else cir.setReturnValue(0xFFFFFF);
	}
}