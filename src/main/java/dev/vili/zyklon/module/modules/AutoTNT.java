package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.NumberSetting;
import dev.vili.zyklon.util.InventoryUtils;
import dev.vili.zyklon.util.WorldUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public class AutoTNT extends Module {
    public final NumberSetting range = new NumberSetting("Range", this, 2, 1, 5, 1);

    private IntList blacklist = new IntArrayList();
    public AutoTNT() {
        super("AutoTNT", "Automatically places TNT.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
        this.addSettings(range);
    }

    @Override
    public void onDisable() {
        blacklist.clear();
    }

    @Subscribe
    public void onTick(TickEvent event) {
        int tntSlot = InventoryUtils.getSlot(true, i -> mc.player.getInventory().getStack(i).getItem() == Items.TNT);
        if (tntSlot == -1)
            return;

        int dist = (int) range.getValue();
        for (int i = -3; i < 4; i++)  {
            for (int j = -3; j < 4; j++)  {
                int x = (int) mc.player.getX() - (int) mc.player.getX() % dist - i * dist;
                int z = (int) mc.player.getZ() - (int) mc.player.getZ() % dist - j * dist;

                boolean skip = false;
                for (int l = 0; l < blacklist.size(); l += 2) {
                    if (x == blacklist.getInt(l) && z == blacklist.getInt(l + 1)) {
                        skip = true;
                        break;
                    }
                }

                if (skip) continue;

                for (int k = -3; k < 4; k++) {
                    int y = (int) mc.player.getY() + k;
                    if (mc.player.squaredDistanceTo(x + 0.5, y + 0.5, z + 0.5) < 4.25
                            && WorldUtils.placeBlock(new BlockPos(x, y, z), tntSlot, 0, false, false, true)) {
                        blacklist.add(x);
                        blacklist.add(z);
                        return;
                    }
                }
            }
        }
    }
}
