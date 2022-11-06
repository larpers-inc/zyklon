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

@Mixin(Entity.class)
public class EntityMixin {

    @ModifyArgs(method = "pushAwayFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
	private void pushAwayFrom_addVelocity(Args args) {
		if ((Object) this == MinecraftClient.getInstance().player) {
            PlayerPushedEvent event = new PlayerPushedEvent(args.get(0), args.get(1), args.get(2));
			Zyklon.INSTANCE.EVENT_BUS.post(event);

			args.set(0, event.getPushX());
			args.set(1, event.getPushY());
			args.set(2, event.getPushZ());
		}
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
		if (entityEsp.mode.is("Glow"))
			cir.setReturnValue(true);
		else cir.setReturnValue(false);
	}

}