package me.vp.zyklon.module;

import java.util.ArrayList;
import java.util.List;

import me.vp.zyklon.event.events.KeyPressEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.modules.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModuleManager {
    public ArrayList<Module> modules;

    public ModuleManager() {
        modules = new ArrayList<>();

        modules.add(new AirPlace());
        modules.add(new AntiHunger());
        modules.add(new AntiInvis());
        modules.add(new ArrowDodge());
        modules.add(new Aura());
        modules.add(new AutoCritical());
        modules.add(new AutoEat());
        modules.add(new AutoFish());
        modules.add(new AutoIgnite());
        modules.add(new AutoParkour());
        modules.add(new AutoRespawn());
        modules.add(new AutoWalk());
        modules.add(new BlockEsp());
        modules.add(new BlockHighlight());
        modules.add(new BreadCrumbs());
        modules.add(new CameraClip());
        modules.add(new Clickgui());
        modules.add(new DeathLocation());
        modules.add(new EntityControl());
        modules.add(new EntityEsp());
        modules.add(new FakePlayer());
        modules.add(new Fly());
        modules.add(new Freecam());
        modules.add(new Fullbright());
        modules.add(new GameWindowTitle());
        modules.add(new Hud());
        modules.add(new Jesus());
        modules.add(new Magnet());
        modules.add(new MiddleClickFriend());
        modules.add(new NoFall());
        modules.add(new NoFog());
        modules.add(new NoOverlay());
        modules.add(new NoRender());
        modules.add(new NoSlow());
        modules.add(new NoSwing());
        modules.add(new NoWeather());
        modules.add(new Nuker());
        modules.add(new PacketFly());
        modules.add(new PortalGui());
        modules.add(new Reach());
        modules.add(new SafeWalk());
        modules.add(new SkinBlinker());
        modules.add(new Sneak());
        modules.add(new Speed());
        modules.add(new Spider());
        modules.add(new Sprint());
        modules.add(new Step());
        modules.add(new Timer());
        modules.add(new ToggleInfo());
        modules.add(new ToggleSound());
        modules.add(new Tracers());
        modules.add(new Twerk());
        modules.add(new UnfocusedCPU());
        modules.add(new VanillaSpoof());
        modules.add(new Velocity());
        modules.add(new VisualRange());
        modules.add(new XCarry());
        modules.add(new XRay());
    }

    public boolean isModuleEnabled(String name) {
        Module m = modules.stream().filter(mm -> mm.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        return m.isEnabled();
    }

    public Module getModule(String name) {
        for (Module m : this.modules) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public List<Module> getEnabledModules() {
        List<Module> modules = new ArrayList<>();

        for (Module m : modules) {
            if (m.isEnabled())
                modules.add(m);
        }
        return modules;
    }

    public List<Module> getModulesByCategory(Module.Category c) {
        List<Module> modules = new ArrayList<>();

        for (Module m : modules) {
            if (m.getCategory() == c)
                modules.add(m);
        }
        return modules;
    }

    public void onTick() {
        modules.stream().filter(Module::isEnabled).forEach(Module::onTick);
    }

    // for key binds (called in MixinKeyboard).
    @Subscribe
    public void onKeyPress(KeyPressEvent event) {
        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_F3))
            return;

        modules.stream().filter(m -> m.getKey() == event.getKey()).forEach(Module::toggle);
    }

}
