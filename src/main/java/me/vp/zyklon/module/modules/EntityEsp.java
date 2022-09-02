package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.WorldRenderEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import me.vp.zyklon.util.EntityUtils;
import me.vp.zyklon.util.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.lwjgl.glfw.GLFW;
import me.vp.zyklon.eventbus.Subscribe;

import java.awt.*;

public class EntityEsp extends Module {
    public final BooleanSetting players = new BooleanSetting("Players", this, true);
    public final BooleanSetting friends = new BooleanSetting("Friends", this, true);
    public final BooleanSetting hostiles = new BooleanSetting("Hostiles", this, true);
    public final BooleanSetting animals = new BooleanSetting("Animals", this, false);
    public final BooleanSetting items = new BooleanSetting("Items", this, true);
    public final BooleanSetting endCrystals = new BooleanSetting("EndCrystals", this, false);

    public EntityEsp() {
        super("EntityEsp", "Draws boxes around entities.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
        this.addSettings(players, friends, hostiles, animals, items, endCrystals);
    }

    @Subscribe
    public void onRender(WorldRenderEvent.Post event) {
        for (Entity entity : mc.world.getEntities()) {
            if (EntityUtils.isOtherServerPlayer(entity) && players.isEnabled())
                RenderUtils.drawOutline(event.getMatrix(), RenderUtils.smoothen(entity, entity.getBoundingBox()), new Color(255, 0, 0), 0.2f);
            else if (EntityUtils.isFriend(entity) && friends.isEnabled())
                RenderUtils.drawOutline(event.getMatrix(), RenderUtils.smoothen(entity, entity.getBoundingBox()), new Color(0, 155, 0), 0.2f);
            else if (EntityUtils.isMob(entity) && hostiles.isEnabled())
                RenderUtils.drawOutline(event.getMatrix(), RenderUtils.smoothen(entity, entity.getBoundingBox()), new Color(255, 0, 255), 0.2f);
            else if (EntityUtils.isAnimal(entity) && animals.isEnabled())
                RenderUtils.drawOutline(event.getMatrix(), RenderUtils.smoothen(entity, entity.getBoundingBox()), new Color(0, 255, 0), 0.2f);
            else if (entity instanceof ItemEntity && items.isEnabled())
                RenderUtils.drawOutline(event.getMatrix(), RenderUtils.smoothen(entity, entity.getBoundingBox()), new Color(255, 255, 0), 0.2f);
            else if (entity instanceof EndCrystalEntity && endCrystals.isEnabled())
                RenderUtils.drawOutline(event.getMatrix(), RenderUtils.smoothen(entity, entity.getBoundingBox()), new Color(50, 0, 125), 0.2f);
        }
    }
}