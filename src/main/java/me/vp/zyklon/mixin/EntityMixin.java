package me.vp.zyklon.mixin;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.event.events.PlayerPushedEvent;
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

    @Inject(at = { @At("HEAD") }, method = "isInvisibleTo(Lnet/minecraft/entity/player/PlayerEntity;)V", cancellable = true)
    private void isInvisibleTo(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
    	if (Zyklon.INSTANCE.moduleManager.isModuleEnabled("AntiInvis")) {
    		cir.setReturnValue(false);
        }
    }

}