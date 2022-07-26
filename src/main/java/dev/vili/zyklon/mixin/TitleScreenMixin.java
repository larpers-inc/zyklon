package dev.vili.zyklon.mixin;


import dev.vili.zyklon.Zyklon;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String watermark = Zyklon.name + " " + Zyklon.version + " | vili.dev";
        String userName = "Logged in as " + Zyklon.mc.getSession().getUsername();
        MutableText drawText = Text.literal("");
        int hue = MathHelper.floor((System.currentTimeMillis() % 5000L) / 5000.0F * 360.0F);
        for (char c: watermark.toCharArray()) {
            int finalHue = hue;
            drawText.append(Text.literal(Character.toString(c)).styled(s -> s.withColor(MathHelper.hsvToRgb(finalHue / 360.0F, 1.0F, 1.0F))));
            hue += 100 / watermark.length();
            if (hue >= 360) hue %= 360;
        }
        Zyklon.mc.textRenderer.draw(matrices, drawText, 1, 1, 0);
        Zyklon.mc.textRenderer.draw(matrices, userName, 1, 10, Formatting.LIGHT_PURPLE.getColorValue());
        ci.cancel();
    }
}
