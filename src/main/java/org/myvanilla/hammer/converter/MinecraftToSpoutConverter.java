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
package org.myvanilla.hammer.converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.myvanilla.hammer.HammerPlugin;
import org.myvanilla.hammer.com.mojang.nbt.CompoundTag;
import org.myvanilla.hammer.com.mojang.nbt.NbtIo;
import org.myvanilla.hammer.util.FileFilter;

import org.spout.api.geo.World;
import org.spout.api.util.sanitation.SafeCast;

import org.spout.vanilla.world.generator.VanillaGenerator;

public class MinecraftToSpoutConverter {
	private List<MinecraftBlock> blockList = new ArrayList<MinecraftBlock>();
	private VanillaGenerator generator;
	private int spawnX, spawnY, spawnZ;
	//Used in case of flatworld
	private int generatorHeight;
	private World world = null;
	private File folder;

	public MinecraftToSpoutConverter(String folderName, VanillaGenerator generator) throws ConverterException {
		folder = new File(HammerPlugin.getInstance().getDataFolder(), folderName);
		if (folder.exists() && folder.isDirectory() && folder.canRead()) {
			File worldInformationFile = new File(folder, "level.dat");
			if (worldInformationFile.exists() && worldInformationFile.isFile() && worldInformationFile.canRead()) {
				try {
					CompoundTag mainTag = NbtIo.read(worldInformationFile);
					if (mainTag.contains("Data")) {
						CompoundTag dataTag = mainTag.getCompound("Data");
						if (dataTag != null) {
							int mapVersion = SafeCast.toInt(dataTag.getInt("version"), 0);
							if (mapVersion == 19133) {
								spawnX = SafeCast.toInt(dataTag.getInt("SpawnX"), 0);
								spawnY = SafeCast.toInt(dataTag.getInt("SpawnY"), 0);
								spawnZ = SafeCast.toInt(dataTag.getInt("SpawnZ"), 0);

								generatorHeight = SafeCast.toString(dataTag.getInt("generatorOptions"), "").split(",").length;
								loadWorld();
							} else {
								throw new ConverterException("This converter only supports Anvil type maps!");
							}
						} else {
							throw new ConverterException("Invalid level.dat file!");
						}
					} else {
						throw new ConverterException("Invalid level.dat file!");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//TODO validate the world type (normal, nether, end)
		} else {
			throw new ConverterException("Impossible to read the folder. Did you make a mistake?");
		}
	}

	public boolean convert() {
		File regionFolder = new File(folder, "region");
		for (File file : regionFolder.listFiles(new FileFilter("*.mca"))) {
			try {
				CompoundTag mainTag = NbtIo.read(file);
				if (mainTag.contains("Level")) {

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private boolean processFile(File file) {
		return true;
	}

	private void loadWorld() {
		world = HammerPlugin.getInstance().getEngine().loadWorld(folder.getName() + "_convert", generator);
	}
}
