package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.WorldRenderEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import me.vp.zyklon.util.RenderUtils;
import me.vp.zyklon.util.WorldUtils;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.quantumclient.energy.Subscribe;

import java.awt.*;

public class BlockEsp extends Module {
    public final BooleanSetting chests = new BooleanSetting("Chests", this, true);
    public final BooleanSetting echests = new BooleanSetting("Echests", this, true);
    public final BooleanSetting shulkers = new BooleanSetting("Shulkers", this, true);
    public final BooleanSetting furnaces = new BooleanSetting("Furnaces", this, true);
    public final BooleanSetting brewingStands = new BooleanSetting("Brewingstands", this, true);
    public final BooleanSetting dispensers = new BooleanSetting("Dispensers", this, false);
    public final BooleanSetting hoppers = new BooleanSetting("Hoppers", this, false);
    public final BooleanSetting spawners = new BooleanSetting("Spawners", this, false);
    public final BooleanSetting beds = new BooleanSetting("Beds", this, false);

    public BlockEsp() {
        super("BlockEsp", "See blocks trought walls.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
        this.addSettings(chests, echests, shulkers, furnaces, brewingStands, dispensers, hoppers, spawners, beds);
    }

    @Subscribe
    public void onRender(WorldRenderEvent.Post event) {
        for (BlockEntity blockEntity : WorldUtils.getBlockEntities()) {
            if (blockEntity instanceof ChestBlockEntity && chests.isEnabled() || blockEntity instanceof BarrelBlockEntity && chests.isEnabled())
                RenderUtils.draw3DBox(event.getMatrix(), new Box(blockEntity.getPos()), new Color(255, 195, 0), 0.2f);
            else if (blockEntity instanceof EnderChestBlockEntity && echests.isEnabled())
                RenderUtils.draw3DBox(event.getMatrix(), new Box(blockEntity.getPos()), new Color(67, 0, 100), 0.2f);
            else if (blockEntity instanceof ShulkerBoxBlockEntity && shulkers.isEnabled())
                RenderUtils.draw3DBox(event.getMatrix(), new Box(blockEntity.getPos()), new Color(255, 255, 255), 0.2f);
            else if (blockEntity instanceof AbstractFurnaceBlockEntity && furnaces.isEnabled())
                RenderUtils.draw3DBox(event.getMatrix(), new Box(blockEntity.getPos()), new Color(67, 67, 67), 0.2f);
            else if (blockEntity instanceof BrewingStandBlockEntity && brewingStands.isEnabled())
                RenderUtils.draw3DBox(event.getMatrix(), new Box(blockEntity.getPos()), new Color(255, 135, 0), 0.2f);
            else if (blockEntity instanceof DispenserBlockEntity && dispensers.isEnabled())
                RenderUtils.draw3DBox(event.getMatrix(), new Box(blockEntity.getPos()), new Color(140, 0, 0), 0.2f);
            else if (blockEntity instanceof HopperBlockEntity && hoppers.isEnabled())
                RenderUtils.draw3DBox(event.getMatrix(), new Box(blockEntity.getPos()), new Color(255, 0, 59), 0.2f);
            else if (blockEntity instanceof MobSpawnerBlockEntity && spawners.isEnabled())
                RenderUtils.draw3DBox(event.getMatrix(), new Box(blockEntity.getPos()), new Color(255, 0, 255), 0.2f);
            else if (blockEntity instanceof BedBlockEntity && beds.isEnabled())
                RenderUtils.draw3DBox(event.getMatrix(), new Box(blockEntity.getPos()), new Color(144, 0, 0), 0.2f);
        }
    }
}
