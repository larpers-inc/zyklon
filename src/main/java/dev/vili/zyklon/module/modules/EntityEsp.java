package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.WorldRenderEvent;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.ModeSetting;
import dev.vili.zyklon.util.EntityUtils;
import dev.vili.zyklon.util.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.lwjgl.glfw.GLFW;
import dev.vili.zyklon.eventbus.Subscribe;

import java.awt.*;

public class EntityEsp extends Module {
    public final BooleanSetting players = new BooleanSetting("Players", this, true);
    public final BooleanSetting friends = new BooleanSetting("Friends", this, true);
    public final BooleanSetting hostiles = new BooleanSetting("Hostiles", this, true);
    public final BooleanSetting animals = new BooleanSetting("Animals", this, false);
    public final BooleanSetting items = new BooleanSetting("Items", this, true);
    public final BooleanSetting projectiles = new BooleanSetting("Projectiles", this, false);
    public final BooleanSetting endCrystals = new BooleanSetting("EndCrystals", this, false);
    public final ModeSetting mode = new ModeSetting("Mode", this, "Outline", "Outline", "Glow");

    public EntityEsp() {
        super("EntityEsp", "Draws boxes around entities.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
        this.addSettings(players, friends, hostiles, animals, items, projectiles, endCrystals, mode);
    }

    @Subscribe
    public void onRender(WorldRenderEvent.Post event) {
        assert mc.world != null;
        for (Entity entity : mc.world.getEntities()) {
            if (mode.is("Outline")) {
                if (EntityUtils.isOtherServerPlayer(entity) && !EntityUtils.isFriend(entity) && players.isEnabled())
                    RenderUtils.drawOutline(event.getMatrix(), RenderUtils.smoothen(entity, entity.getBoundingBox()), new Color(255, 0, 0), 0.2f);
                else if (EntityUtils.isOtherServerPlayer(entity) && EntityUtils.isFriend(entity) && friends.isEnabled())
                    RenderUtils.drawOutline(event.getMatrix(), RenderUtils.smoothen(entity, entity.getBoundingBox()), new Color(0, 155, 0), 0.2f);
                else if (EntityUtils.isMob(entity) && hostiles.isEnabled())
                    RenderUtils.drawOutline(event.getMatrix(), RenderUtils.smoothen(entity, entity.getBoundingBox()), new Color(255, 0, 255), 0.2f);
                else if (EntityUtils.isAnimal(entity) && animals.isEnabled())
                    RenderUtils.drawOutline(event.getMatrix(), RenderUtils.smoothen(entity, entity.getBoundingBox()), new Color(0, 255, 0), 0.2f);
                else if (entity instanceof ItemEntity && items.isEnabled())
                    RenderUtils.drawOutline(event.getMatrix(), RenderUtils.smoothen(entity, entity.getBoundingBox()), new Color(255, 255, 0), 0.2f);
                else if (entity instanceof ProjectileEntity && projectiles.isEnabled())
                    RenderUtils.drawOutline(event.getMatrix(), RenderUtils.smoothen(entity, entity.getBoundingBox()), new Color(255, 255, 255), 0.2f);
                else if (entity instanceof EndCrystalEntity && endCrystals.isEnabled())
                    RenderUtils.drawOutline(event.getMatrix(), RenderUtils.smoothen(entity, entity.getBoundingBox()), new Color(50, 0, 125), 0.2f);
            }

            else if (mode.is("Glow")) {
                if (EntityUtils.isOtherServerPlayer(entity) && !EntityUtils.isFriend(entity) && players.isEnabled())
                    entity.setGlowing(true);
                else if (EntityUtils.isFriend(entity) && friends.isEnabled())
                    entity.setGlowing(true);
                else if (EntityUtils.isMob(entity) && hostiles.isEnabled())
                    entity.setGlowing(true);
                else if (EntityUtils.isAnimal(entity) && animals.isEnabled())
                    entity.setGlowing(true);
                else if (entity instanceof ProjectileEntity && projectiles.isEnabled())
                    entity.setGlowing(true);
                else if (entity instanceof ItemEntity && items.isEnabled())
                    entity.setGlowing(true);
                else if (entity instanceof EndCrystalEntity && endCrystals.isEnabled())
                    entity.setGlowing(true);
            }
        }
    }
}