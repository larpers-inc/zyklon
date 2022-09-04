package me.vp.zyklon.mixin;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.event.events.EntityControlEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractHorseEntity.class, PigEntity.class, StriderEntity.class})
public class LlamaPigStriderEntityMixin extends AnimalEntity {

    private LlamaPigStriderEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "isSaddled", at = @At("HEAD"), cancellable = true)
    private void isSaddled(CallbackInfoReturnable<Boolean> info) {
        EntityControlEvent event = new EntityControlEvent();
        Zyklon.INSTANCE.EVENT_BUS.post(event);

        if (event.canBeControlled() != null) {
            info.setReturnValue(event.canBeControlled());
        }
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
}
