/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.myvanilla.hammer;

import org.spout.api.geo.World;
import org.spout.api.plugin.CommonPlugin;
import org.spout.vanilla.world.generator.VanillaGenerator;
import org.spout.vanilla.world.generator.VanillaGenerators;

/**
 *
 * @author William
 */
public class HammerPlugin extends CommonPlugin {

    private World newWorld = null;
    @Override
    public void onEnable() {
        getLogger().info("Loading Hammer (Minecraft map to Spout map converter. Please wait...");
        VanillaGenerator generator = VanillaGenerators.byName("normal");
        if (generator == null) {
            getLogger().severe("Normal Vanilla generator not found! Impossible to continue!");
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        World world = getEngine().loadWorld("convertWorld", generator);
        
        
    }

    @Override
    public void onDisable() {
        getLogger().info("Hammer stopped!");
    }
    
}
