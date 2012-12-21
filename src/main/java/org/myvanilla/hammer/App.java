/*
 * This file is part of Hammer.
 *
 * Copyright (c) 2012-2012, Spout LLC <http://www.spout.org/>
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.FloatTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.Tag;
import org.spout.nbt.StringTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

import org.myvanilla.hammer.minecraft.MinecraftWorld;
import org.myvanilla.hammer.util.FileFilter;

public class App {
	private static MinecraftWorld world;
	private static File spoutFolder;

	public static void main(String[] args) {
		File worldFolder = new File(args[0]);
		if (worldFolder.exists() && worldFolder.isDirectory()) {
			// Start level.dat checkup and world initialization
			System.out.println("Reading level.dat file....");
			if (readLevelDatMc(worldFolder)) {
				if (world != null) { // Just a check to be sure
					System.out.println("World " + world.getName() + " loaded!");
					if (createWorldDatSpout()) {
						System.out.println("Spout world.dat created!");
						if (convertToSpout()) {
						}
					}
				}
			}
		} else {
			System.out.println("This is not a world folder!");
		}
	}

	// Step 1
	public static boolean readLevelDatMc(File worldFolder) {
		boolean result = false;
		File worldInformation = new File(worldFolder, "level.dat");
		if (worldInformation.exists() && worldInformation.isFile()) {
			CompoundTag data = null;
			try {
				NBTInputStream in = new NBTInputStream(new FileInputStream(worldInformation));
				data = (CompoundTag) in.readTag();
				in.close();
			} catch (IOException e) {
				// TODO: Auto-generated catch block
				e.printStackTrace();
			}
			HashMap<String, Tag> level = new HashMap<String, Tag>();
			level.putAll(data.getValue());
			if (level.containsKey("Data")) {
				sendUpdate();
				CompoundMap dataTag = (CompoundMap) level.get("Data").getValue();
				if (((IntTag) dataTag.get("version")).getValue().equals(19133)) {
					sendUpdate();
					world = new MinecraftWorld(worldFolder, ((StringTag) dataTag.get("LevelName")).getValue(), ((StringTag) dataTag.get("generatorName")).getValue(), ((IntTag)dataTag.get("GameType")).getValue(), ((LongTag) dataTag.get("RandomSeed")).getValue(), ((IntTag) dataTag.get("SpawnX")).getValue(), ((IntTag) dataTag.get("SpawnY")).getValue(), ((IntTag) dataTag.get("SpawnZ")).getValue());
					if (world.isValid()) {
						sendUpdate();
						result = true;
					} else {
						System.out.println("Invalid world");
					}
				} else {
					System.out.println("This system is only compatible with 1.3 maps");
				}
			} else {
				System.out.println("Invalid level.dat file");
			}
		} else {
			System.out.println("The level.dat file doesn't exist!");
		}
		return result;
	}

	// Step 2
	public static boolean createWorldDatSpout() {
		boolean result = false;
		CompoundMap worldTags = new CompoundMap();

		// World version 1
		worldTags.put(new ByteTag("version", (byte)2));
		worldTags.put(new LongTag("seed", world.getSeed()));
		worldTags.put(new StringTag("generator","VanillaNormal"));
		worldTags.put(new LongTag("UUID_lsb", new Random().nextLong()));
		worldTags.put(new LongTag("UUID_msb", new Random().nextLong()));

		//DataMap map = new DataMap(new GenericDatatableMap());
		//map.put("weather", "CLEAR");
		//map.put("game_mode", world.getGamemode());
		//map.put("difficulty", "NORMAL");
		//map.put("dimension", "NORMAL");
		//worldTags.put(new ByteArrayTag("extra_data", map.getRawMap().compress()));
		worldTags.put(new LongTag("age", 0));

		// World version 2
		ArrayList<FloatTag> spawn = new ArrayList<FloatTag>();
		spawn.add(new FloatTag("px", world.getSpawnX()));
		spawn.add(new FloatTag("py", world.getSpawnY()));
		spawn.add(new FloatTag("pz", world.getSpawnZ()));

		// TODO: Actually do something about those?
		spawn.add(new FloatTag("rw", 0.0f));
		spawn.add(new FloatTag("rx", 0.0f));
		spawn.add(new FloatTag("ry", 0.0f));
		spawn.add(new FloatTag("rz", 0.0f));
		spawn.add(new FloatTag("sx", 1.0f));
		spawn.add(new FloatTag("sy", 1.0f));
		spawn.add(new FloatTag("sz", 1.0f));
		worldTags.put(new ListTag<FloatTag>("spawn_position", FloatTag.class, spawn));
		CompoundTag worldTag = new CompoundTag(world.getName(), worldTags);
		sendUpdate();

		// Let's create the new folder
		NBTOutputStream os = null;
		try {
			// TODO: Fix, doesn't create at the good spot
			spoutFolder = new File(world.getWorldFolder().getParentFile(), "SpoutWorld");
			spoutFolder.mkdirs();
			sendUpdate();
			os = new NBTOutputStream(new DataOutputStream(new FileOutputStream(new File(spoutFolder, "world.dat"))), false);
			os.writeTag(worldTag);
			result = true;
		} catch (IOException e) {
			System.out.println("Error saving world data for " + world.toString() + ". Reason:" + e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ignore) {
				}
			}
		}

		return result;
	}

	public static boolean convertToSpout() {
		boolean result = false;
		File regionFolder = new File(world.getWorldFolder(), "region");
		if (regionFolder.exists() && regionFolder.isDirectory()) {
			File[] regionFiles = regionFolder.listFiles(new FileFilter(".mca"));
			for (int i = 0; i < regionFiles.length; i++) {
			}
		} else {
			System.out.println("Region folder doesn't exist!");
		}
		return result;
	}

	public static void sendUpdate() {
		System.out.println(".");
	}
}
