package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import static dev.vili.zyklon.command.CommandManager.prefix;

public class HClipCmd extends Command {
    public HClipCmd() {
        super("hclip", "Clip horizontally.", "hclip <amount>", "");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 0) {
            ZLogger.error("Usage: " + prefix + "hclip <amount>");
            return;
        }

        Vec3d forward = Vec3d.fromPolar(0, mc.player.getYaw()).normalize();
        if (mc.player.hasVehicle()) {
            Entity vehicle = mc.player.getVehicle();
            vehicle.setPosition(vehicle.getX() + forward.x * Integer.parseInt(args[0]), vehicle.getY(), vehicle.getZ() + forward.z * Integer.parseInt(args[0]));
        }
        mc.player.setPosition(mc.player.getX() + forward.x * Integer.parseInt(args[0]), mc.player.getY(), mc.player.getZ() + forward.z * Integer.parseInt(args[0]));
    }
}
