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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.myvanilla.hammer.ConvertBlock;
import org.myvanilla.hammer.Converter;
import org.myvanilla.hammer.MapMetadata;
import org.spout.api.util.sanitation.SafeCast;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.util.NBTMapper;

public class MinecraftConverter extends Converter {

	private MinecraftMapMetadata metaData;
	private File worldInformation;

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
				
				CompoundMap dataTag = (CompoundMap) SafeCast.toGeneric(CompoundMap.class,NBTMapper.toTagValue(level.get("Data")), null);
				if (dataTag != null) {
					int mapVersion = SafeCast.toInt(NBTMapper.toTagValue(dataTag.get("version")), 0);
					if (mapVersion == 19133) {
						metaData = new MinecraftMapMetadata(SafeCast.toInt(NBTMapper.toTagValue(dataTag.get("SpawnX")),0), SafeCast.toInt(NBTMapper.toTagValue(dataTag.get("SpawnY")),0), SafeCast.toInt(NBTMapper.toTagValue(dataTag.get("SpawnZ")),0), SafeCast.toString(NBTMapper.toTagValue(dataTag.get("generatorName")),""));
					}
				}
			}
		}
		return metaData;
	}

	@Override
	public List<ConvertBlock> getBlockList() {
		List<ConvertBlock> blockList = new ArrayList<ConvertBlock>();
		if (metaData == null) {
			throw new IllegalArgumentException("Metadata isin't loaded!");
		}
		// TODO: Find how we read chunks.
		return blockList;
	}

}
