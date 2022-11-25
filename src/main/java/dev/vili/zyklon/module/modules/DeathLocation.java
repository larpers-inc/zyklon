package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.event.events.PacketEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class DeathLocation extends Module {

    public DeathLocation() {
        super("DeathLocation", "Send your death location in chat.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
    }

    private final File MainDirectory = new File(MinecraftClient.getInstance().runDirectory, Zyklon.name);
    private final File file = new File(MainDirectory, "deaths.txt");

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof HealthUpdateS2CPacket packet) {
            if (packet.getHealth() > 0.0F) return;
            int x = (int) mc.player.getPos().getX();
            int y = (int) mc.player.getPos().getY();
            int z = (int) mc.player.getPos().getZ();
            ZLogger.info(("You died at " + x + ", " + y + ", " + z + " in the " + getDimension()));

            // save death location to file
            try {
                if (!file.exists()) file.createNewFile();
                PrintWriter writer = new PrintWriter(file);
                // new line for every death
                writer.println("You died at " + x + ", " + y + ", " + z + " in the " + getDimension() + "\n");
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getDimension() {
        if (mc.world.getDimension().respawnAnchorWorks())
            return "Nether";
        else
            return "Overworld";
    }
}