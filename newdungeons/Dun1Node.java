package newdungeons;

public class Dun1Node {
	public final int wp;
	public final int wn;
	public final int lp;
	public final int ln;
	public final int x;
	public final int y;
	public final int z;

	@SuppressWarnings("UnusedDeclaration")
    Dun1Node() {
		this(0, 0, 0, 0, 0, 0, 0);
	}

	Dun1Node(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
		this.wp = var1;
		this.wn = var2;
		this.lp = var3;
		this.ln = var4;
		this.x = var5;
		this.y = var6;
		this.z = var7;
	}

	@Override
	public String toString() {
		return "\nwp: " + this.wp + "\n" + "wn: " + this.wn + "\n" + "lp: " + this.lp + "\n" + "ln: " + this.ln + "\n" + "x: " + this.x + "\n" + "z: " + this.z;
	}
}
