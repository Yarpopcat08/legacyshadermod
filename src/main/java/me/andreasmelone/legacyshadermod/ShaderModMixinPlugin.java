package me.andreasmelone.legacyshadermod;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ShaderModMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String[] splitPackage = mixinClassName.split("\\.");
        String p = splitPackage[splitPackage.length - 2];
        if(p.equals("vanilla")) {
            return !FabricLoader.getInstance().isModLoaded("btw") && !isMcpatcherLoaded();
        } else if(p.equals("btw")) {
            return FabricLoader.getInstance().isModLoaded("btw");
        }  else if (p.equals("mcpatcher")) {
            return isMcpatcherLoaded();
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    private static boolean isMcpatcherLoaded() {
        try {
            Class.forName("com.prupe.mcpatcher.ProfilerAPI");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
