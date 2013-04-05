package org.myvanilla.hammer.converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.myvanilla.hammer.HammerPlugin;
import org.myvanilla.hammer.com.mojang.nbt.CompoundTag;
import org.myvanilla.hammer.com.mojang.nbt.NbtIo;

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
		return true;
	}

	private boolean processFile(File file) {
		return true;
	}

	private void loadWorld() {
		world = HammerPlugin.getInstance().getEngine().loadWorld(folder.getName() + "_convert", generator);
	}
}
