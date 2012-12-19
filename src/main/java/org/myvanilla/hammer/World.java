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

import java.io.File;

public class World {
	private int spawnX, spawnY, spawnZ, gamemode;
	private String name, generator;
	private long seed = 0;
	private File worldFolder;

	public World (File worldFolder, String name, String generator, int gamemode, long seed, int spawnX, int spawnY, int spawnZ) {
		this.worldFolder = worldFolder;
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.spawnZ = spawnZ;
		this.name = name;
		this.generator = generator;
		this.seed = seed;
	}

	public int getSpawnX() {
		return spawnX;
	}

	public int getSpawnY() {
		return spawnY;
	}

	public int getSpawnZ() {
		return spawnZ;
	}

	public String getName() {
		return name;
	}

	public String getGenerator() {
		return generator;
	}

	public long getSeed() {
		return seed;
	}

	public boolean isValid() {
		if (name != null && !name.isEmpty() && generator != null && !generator.isEmpty() && seed != 0) {
			return true;
		}
		return false;
	}

	public String getGamemode() {
		String result = "";
		if (gamemode == 0) {
			result = "SURVIVAL";
		} else if (gamemode == 1) {
			result = "CREATIVE";
		} else if (gamemode == 2) {
			result = "ADVENTURE";
		} else {
			result = "SURVIVAL";
		}
		return result;
	}

	public File getWorldFolder() {
		return worldFolder;
	}
}
