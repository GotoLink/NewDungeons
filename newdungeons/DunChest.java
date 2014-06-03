package newdungeons;

public class DunChest {
	final int x;
	final int y;
	final int z;
	boolean looted = false;

	DunChest(int var1, int var2, int var3) {
		this.x = var1;
		this.y = var2;
		this.z = var3;
	}

	@Override
	public String toString() {
		return "\n\nx: " + this.x + "\ny: " + this.y + "\nz: " + this.z + "looted: " + this.looted + "\n";
	}
}
