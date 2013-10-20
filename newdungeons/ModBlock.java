package newdungeons;

public class ModBlock {
	static int blockID;
	static int metadata;
	static String spawnerData;

	ModBlock() {
		blockID = 0;
		metadata = 0;
		spawnerData = "";
	}

	public int setBlockID(int var1) {
		blockID = var1;
		return blockID;
	}
}
