package newdungeons;

public class Dun1Node {
	public int wp;
	public int wn;
	public int lp;
	public int ln;
	public int x;
	public int y;
	public int z;

	Dun1Node() {
		this.wp = 0;
		this.wn = 0;
		this.lp = 0;
		this.ln = 0;
		this.x = 0;
		this.z = 0;
		this.y = 0;
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
