package dev.vili.zyklon.mixin;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.module.modules.AutoSteal;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/* Credits to Wurst client */
@Mixin(GenericContainerScreen.class)
public abstract class ContainerScreenMixin extends HandledScreen<GenericContainerScreenHandler> implements ScreenHandlerProvider<GenericContainerScreenHandler> {
    @Shadow
    @Final
    private int rows;

    private final AutoSteal autoSteal = (AutoSteal) Zyklon.INSTANCE.moduleManager.getModule("AutoSteal");
    private int mode;

    public ContainerScreenMixin(GenericContainerScreenHandler container, PlayerInventory playerInventory, Text name) {
        super(container, playerInventory, name);
    }

    @Override
    protected void init() {
        super.init();

        if (autoSteal.buttons.isEnabled()) {
            addDrawableChild(ButtonWidget.builder(Text.of("Steal"), (buttonWidget) -> steal()).dimensions(x + backgroundWidth - 108, y + 4,
                    50, 12).build());
            addDrawableChild(ButtonWidget.builder(Text.of("Store"), (buttonWidget) -> store()).dimensions(x + backgroundWidth - 54, y + 4,
                    50, 12).build());
        }

        if (autoSteal.isEnabled()) steal();
    }

    private void steal() {
        runInThread(() -> shiftClickSlots(0, rows * 9, 1));
    }

    private void store() {
        runInThread(() -> shiftClickSlots(rows * 9, rows * 9 + 44, 2));
    }

    private void runInThread(Runnable r)
    {
        new Thread(() -> {
            try {r.run();}
            catch(Exception e) {
                ZLogger.logger.error("Error while running in thread: " + e.getMessage());
            }
        }).start();
    }

    private void shiftClickSlots(int from, int to, int mode) {
        this.mode = mode;

        for (int i = from; i < to; i++) {
            Slot slot = handler.slots.get(i);
            if(slot.getStack().isEmpty())
                continue;

            waitForDelay();
            if (this.mode != mode || client.currentScreen == null)
                break;

            onMouseClick(slot, slot.id, 0, SlotActionType.QUICK_MOVE);
        }
    }

    private void waitForDelay() {
        try {
            Thread.sleep((long) autoSteal.delay.getValue());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
