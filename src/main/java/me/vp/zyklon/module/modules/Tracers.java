package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.WorldRenderEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import me.vp.zyklon.util.EntityUtils;
import me.vp.zyklon.util.RenderUtils;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import me.vp.zyklon.eventbus.Subscribe;

import java.awt.*;

public class Tracers extends Module {
    public final BooleanSetting players = new BooleanSetting("Players", this, true);
    public final BooleanSetting friends = new BooleanSetting("Friends", this, true);
    public final BooleanSetting hostiles = new BooleanSetting("Hostiles", this, true);
    public final BooleanSetting animals = new BooleanSetting("Animals", this, false);
    public final BooleanSetting items = new BooleanSetting("Items", this, true);
    public final BooleanSetting endCrystals = new BooleanSetting("EndCrystals", this, false);

    public Tracers() {
        super("Tracers", "Draws lines to entities positions.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
        this.addSettings(players, friends, hostiles, animals, items, endCrystals);
    }

    @Subscribe
    public void onRender(WorldRenderEvent.Post event) {
        for (Entity entity : mc.world.getEntities()) {
            Camera camera = mc.gameRenderer.getCamera();
            Vec3d start = new Vec3d(0, 0, 1)
                    .rotateX(-(float) Math.toRadians(camera.getPitch()))
                    .rotateY(-(float) Math.toRadians(camera.getYaw()));
            Vec3d end = RenderUtils.smoothen(entity).add(0, entity.getStandingEyeHeight(), 0);

            if (EntityUtils.isOtherServerPlayer(entity) && players.isEnabled())
                RenderUtils.draw3DLine(event.getMatrix(), start, end, new Color(255, 0, 0));
            else if (EntityUtils.isFriend(entity) && friends.isEnabled())
                RenderUtils.draw3DLine(event.getMatrix(), start, end, new Color(0, 155, 0));
            else if (EntityUtils.isMob(entity) && hostiles.isEnabled())
                RenderUtils.draw3DLine(event.getMatrix(), start, end, new Color(255, 0, 255));
            else if (EntityUtils.isAnimal(entity) && animals.isEnabled())
                RenderUtils.draw3DLine(event.getMatrix(), start, end, new Color(0, 255, 0));
            else if (entity instanceof ItemEntity && items.isEnabled())
                RenderUtils.draw3DLine(event.getMatrix(), start, end, new Color(255, 255, 0));
            else if (entity instanceof EndCrystalEntity && endCrystals.isEnabled())
                RenderUtils.draw3DLine(event.getMatrix(), start, end, new Color(50, 0, 125));
        }
    }

}