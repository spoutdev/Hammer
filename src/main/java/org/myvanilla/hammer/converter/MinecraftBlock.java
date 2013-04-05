package org.myvanilla.hammer.converter;

public class MinecraftBlock {
	private int x, y, z, id;
	private byte data;

	public MinecraftBlock(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public MinecraftBlock(int x, int y, int z, byte data) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.data = data;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public byte getData() {
		return data;
	}

	public int getId() {
		return id;
	}
}
