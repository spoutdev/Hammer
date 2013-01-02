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
package org.myvanilla.hammer.minecraft;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.myvanilla.hammer.ConvertBlock;
import org.myvanilla.hammer.Converter;
import org.myvanilla.hammer.MapMetadata;
import org.myvanilla.hammer.minecraft.com.mojang.nbt.CompoundTag;
import org.myvanilla.hammer.minecraft.com.mojang.nbt.ListTag;
import org.myvanilla.hammer.minecraft.com.mojang.nbt.NbtIo;
import org.myvanilla.hammer.minecraft.com.mojang.nbt.Tag;
import org.myvanilla.hammer.util.FileFilter;
import org.spout.api.util.sanitation.SafeCast;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.util.NBTMapper;


public class MinecraftConverter extends Converter {

	private MinecraftMapMetadata metaData;
	private File worldInformation;
	private static final int VERSION_GZIP = 1;
	private static final int VERSION_DEFLATE = 2;
    
	public MinecraftConverter(File folder) throws InstantiationException {
		super(folder);
		worldInformation = new File(this.folder, "level.dat");
		if (!worldInformation.exists() || !worldInformation.canRead() || !worldInformation.isFile()) {
			throw new InstantiationException("Impossible to read the level.dat file. Is it a minecraft world?");
		}
	}

	@Override
	public MapMetadata getMapMetadata() {
		if (metaData == null) {
			CompoundTag data = null;
			try {
				data = NbtIo.read(worldInformation);
			} catch (IOException e) {
				// TODO: Auto-generated catch block
				e.printStackTrace();
			}
			
			if (data.contains("Data")) {
				CompoundTag dataTag = data.getCompound("Data");
				if (dataTag != null) {
					int mapVersion = SafeCast.toInt(dataTag.getInt("version"), 0);
					if (mapVersion == 19133) {
						metaData = new MinecraftMapMetadata(SafeCast.toInt(dataTag.getInt("SpawnX"), 0), SafeCast.toInt(dataTag.getInt("SpawnY"), 0),
								SafeCast.toInt(dataTag.getInt("SpawnZ"), 0), SafeCast.toString(dataTag.getInt("generatorName"), ""));
					}
				}
			}
		}
		return metaData;
	}

	@Override
	public List<ConvertBlock> getBlockList() {
		List<ConvertBlock> blockList = new ArrayList<ConvertBlock>();
		DataInputStream in;
		HashMap<String, Tag> chunk;
		CompoundTag tag = null;
		//if (metaData == null) {
		//	throw new IllegalArgumentException("Metadata isin't loaded!");
		//}
		File regionFolder = new File(folder, "region");
		if (!regionFolder.isDirectory()) {
			throw new IllegalArgumentException("The region folder doesn't exist!");
		}
		File[] regionFiles = regionFolder.listFiles(new FileFilter(".mca"));
		for (File regionFile : regionFiles) {
			System.out.println("Opening file: " + regionFile);
			in = null;
			tag = null;
			chunk = new HashMap<String, Tag>();
			try {
				RegionFile region = new RegionFile(regionFile);
				
				// We open the file.
				/*RandomAccessFile file = new RandomAccessFile(regionFile, "r");
				int offset = getOffset(x, z);

				int sectorNumber = offset >> 8;
	            int numSectors = offset & 0xFF;

	            if (sectorNumber + numSectors > sectorFree.size()) {
	                debugln("READ", x, z, "invalid sector");
	                return null;
	            }
				// We read the length of the byte array containing the level information.
				int length = file.readInt();
				byte[] data = new byte[length - 1];
				file.read(data);

				// We get the type of compression used.
				byte version = file.readByte();

				if (version == VERSION_GZIP) {
					in = new NBTInputStream(region.getChunkDataInputStream(x, z));
					
				} else if (version == VERSION_DEFLATE) {
					in = new NBTInputStream(new InflaterInputStream(new ByteArrayInputStream(data)));
				}*/
				System.out.println(regionFile.getName());
				for (int x = 0; x < 32; x++) {
	                for (int z = 0; z < 32; z++) {
	                	if (region.hasChunk(x, z)) {
	                		in = region.getChunkDataInputStream(x, z);
	                		break;
	                	}
	                }
				}
				//in = new NBTInputStream(region.getChunkDataInputStream(0,0));
				tag = (CompoundTag) NbtIo.read(in);
				in.close();
				
					

				if (tag.contains("Level")) {
					//Yay! file looks valid. Let's continue.
					CompoundTag levelTag = tag.getCompound("Level");
					int xPos = SafeCast.toInt(levelTag.get("xPos"), 0) * 16;
					int zPos = SafeCast.toInt(levelTag.get("zPos"), 0) * 16;
					ListTag<CompoundTag> sections = (ListTag<CompoundTag>) levelTag.getList("Sections");
					for (int i = 0; i < sections.size(); i++) {
						CompoundTag compoundTag = sections.get(i);
						byte yPos = compoundTag.getByte("Y");
						byte[] blocks = SafeCast.toByteArray(compoundTag.getByteArray("Blocks"), new byte[0]);
						for (int yBase = 0; yBase < (128 / 16); yBase++) {
							for (int x = 0; x < 16; x++) {
				                for (int y = 0; y < 16; y++) {
				                    for (int z = 0; z < 16; z++) {
				                        byte block = blocks[(y << 8) | (z << 4) | x];
				                        blockList.add(new ConvertBlock(xPos + x,y * 16,zPos + z ,block, (byte) 0));
				                    }
				                }
							}
						}
					}
				}
				System.out.println("Amount of blocks:" + blockList.size());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return blockList;
	}

}
