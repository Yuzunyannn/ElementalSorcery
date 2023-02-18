package yuzunyannn.elementalsorcery.dungeon;

public class DungeonPos {

	public final int x;
	public final int z;

	public DungeonPos(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public int hashCode() {
		int i = 1664525 * this.x + 1013904223;
		int j = 1664525 * (this.z ^ -559038737) + 1013904223;
		return i ^ j;
	}

	public boolean equals(Object other) {
		if (this == other) return true;
		else if (!(other instanceof DungeonPos)) return false;
		else {
			DungeonPos chunkpos = (DungeonPos) other;
			return this.x == chunkpos.x && this.z == chunkpos.z;
		}
	}

	public String toString() {
		return "[" + this.x + ", " + this.z + "]";
	}
}
