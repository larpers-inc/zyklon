package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.RenderEntityEvent;
import me.vp.zyklon.event.events.WorldRenderEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import me.vp.zyklon.util.EntityUtils;
import me.vp.zyklon.util.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import org.lwjgl.glfw.GLFW;
import org.quantumclient.energy.Subscribe;

import java.awt.*;

public class EntityEsp extends Module {
    public final BooleanSetting players = new BooleanSetting("Players", this, true);
    public final BooleanSetting friends = new BooleanSetting("Friends", this, true);
    public final BooleanSetting hostiles = new BooleanSetting("Hostiles", this, true);
    public final BooleanSetting animals = new BooleanSetting("Animals", this, false);
    public final BooleanSetting items = new BooleanSetting("Items", this, true);

    public EntityEsp() {
        super("EntityEsp", "See entities trought walls and shit.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
        this.addSettings(players, friends, hostiles, animals, items);
    }

    @Subscribe
    public void onRender(WorldRenderEvent.Post event) {
        for (Entity entity : mc.world.getEntities()) {
            if (EntityUtils.isOtherServerPlayer(entity) && players.isEnabled())
                RenderUtils.draw3DBox(event.getMatrix(), entity.getBoundingBox(), new Color(255, 0, 0), 0.2f);
            else if (EntityUtils.isFriend(entity) && friends.isEnabled())
                RenderUtils.draw3DBox(event.getMatrix(), entity.getBoundingBox(), new Color(0, 155, 0), 0.2f);
            else if (EntityUtils.isMob(entity) && hostiles.isEnabled())
                RenderUtils.draw3DBox(event.getMatrix(), entity.getBoundingBox(), new Color(255, 0, 255), 0.2f);
            else if (EntityUtils.isAnimal(entity) && animals.isEnabled())
                RenderUtils.draw3DBox(event.getMatrix(), entity.getBoundingBox(), new Color(0, 255, 0), 0.2f);
            else if (entity instanceof ItemEntity && items.isEnabled()) {
                RenderUtils.draw3DBox(event.getMatrix(), entity.getBoundingBox(), new Color(255, 255, 0), 0.2f);
            }
        }
    }
}