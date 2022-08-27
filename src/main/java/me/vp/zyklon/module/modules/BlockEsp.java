package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.WorldRenderEvent;
import me.vp.zyklon.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import org.lwjgl.glfw.GLFW;
import org.quantumclient.energy.Subscribe;

public class BlockEsp extends Module {

    public BlockEsp() {
        super("BlockEsp", "See blocks trought walls.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
    }

    @Subscribe
    public void onRender(WorldRenderEvent.Post event) {
        
    }
}
