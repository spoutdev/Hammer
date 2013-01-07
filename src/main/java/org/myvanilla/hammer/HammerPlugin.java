/*
 * This file is part of Hammer.
 *
 * Copyright (c) 2012-2013, Spout LLC <http://www.spout.org/>
 * Hammer is licensed under the Spout License Version 1.
 *
 * Hammer is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Hammer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.myvanilla.hammer;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.myvanilla.hammer.configuration.HammerConfiguration;
import org.myvanilla.hammer.minecraft.MinecraftConverter;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.geo.World;
import org.spout.api.material.BlockMaterial;
import org.spout.api.plugin.CommonPlugin;
import org.spout.vanilla.plugin.material.VanillaMaterials;
import org.spout.vanilla.plugin.world.generator.VanillaGenerator;
import org.spout.vanilla.plugin.world.generator.VanillaGenerators;

public class HammerPlugin extends CommonPlugin {

	private World newWorld = null;
	private Converter converter;
	private Converters converterType;

	@Override
	public void onEnable() {

		// Load the Configuration file
		try {
			new HammerConfiguration(getDataFolder()).load();
		} catch (ConfigurationException e1) {
			getLogger().log(Level.SEVERE, "Impossible to load Hammer configuration file!", e1);
			this.getPluginLoader().disablePlugin(this);
			return;
		}

		if (HammerConfiguration.START.getBoolean()) {
			// Checks in the configuration file if the converter selected exists.
			try {
				converterType = Converters.valueOf(HammerConfiguration.CONVERTER.getString().toUpperCase());
			} catch (IllegalArgumentException e) {
				getLogger().severe("There's no converter with the name: " + HammerConfiguration.CONVERTER.getString() + "! Disabling plugin");
				this.getPluginLoader().disablePlugin(this);
				return;
			}

			getLogger().info("Loading Hammer (Map converter to Vanilla). Please wait...");

			// We load the generator from the configuration file.
			VanillaGenerator generator = VanillaGenerators.byName(HammerConfiguration.GENERATOR.getString());
			if (generator == null) {
				getLogger().severe(HammerConfiguration.GENERATOR.getString() + " Vanilla generator not found! Impossible to continue!");
				this.getPluginLoader().disablePlugin(this);
				return;
			}

			try {
				if (converterType.equals(Converters.MINECRAFT)) {
					converter = new MinecraftConverter(new File(getDataFolder(), HammerConfiguration.FOLDERNAME.getString()));
				}
			} catch (InstantiationException e) {
				getLogger().log(Level.SEVERE, "Impossible to load the converter!", e);
				this.getPluginLoader().disablePlugin(this);
				return;
			}

			//MapMetadata metadata = converter.getMapMetadata();
			// We use a random UUID to be sure we don't override a folder.
			newWorld = getEngine().loadWorld("convertWorld" + UUID.randomUUID().toString().replace("-", "").substring(0, 5), generator);
			List<ConvertBlock> blockList = converter.getBlockList();
			int i = 0;
			for (ConvertBlock block : blockList) {
				//System.out.println("BlockID:" + block.getId());
				BlockMaterial blockMaterial = null;
				if (block.getId() == 0) {
					blockMaterial = VanillaMaterials.AIR;
				} else {
					blockMaterial = (BlockMaterial) VanillaMaterials.getMaterial(block.getId());
					if (block.getId() == 35) {
						System.out.println("I FOUND WOOL!!!");
					}
				}
				
				newWorld.getBlock(block.getX(), block.getY(), block.getZ()).setMaterial(blockMaterial);
				i++;
				if (i % 10 == 0) {
					//System.out.println(i + " of " + blockList.size());
				}
			}
		} else {
			getLogger().warning("Hammer converter not started. Please configure it through the config.yml file!");
		}
		
	}

	@Override
	public void onDisable() {
		getLogger().info("Hammer stopped!");
	}

}
