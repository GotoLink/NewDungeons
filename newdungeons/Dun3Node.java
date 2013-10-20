package newdungeons;

import java.util.Random;

import net.minecraft.world.World;

public class Dun3Node {
	private int x;
	private int y;
	private int z;
	private Dun3Node xp;
	private Dun3Node xn;
	private Dun3Node zp;
	private Dun3Node zn;
	private int xpd;
	private int xnd;
	private int zpd;
	private int znd;
	private int nodesLeft;
	private int nodeChance = 2;
	private int minDis = 10;
	private int maxDis = 20;
	private boolean madeNodes = false;

	Dun3Node() {
		this.x = 0;
		this.y = 60;
		this.z = 0;
		this.xp = null;
		this.xn = null;
		this.zp = null;
		this.zn = null;
		this.nodesLeft = 0;
	}

	Dun3Node(int var1, int var2, int var3, int var4) {
		this.x = var1;
		this.y = var2;
		this.z = var3;
		this.nodesLeft = var4;
	}

	public int makeNodes(Random var1) {
		int var2 = 0;
		if (this.nodesLeft > 0 && !this.madeNodes) {
			if (var1.nextInt(this.nodeChance) == 1) {
				this.xpd = var1.nextInt(this.maxDis - this.minDis) + this.minDis;
				this.xp = new Dun3Node(this.x + this.xpd, this.y, this.z, this.nodesLeft - 1);
				++var2;
			}
			if (var1.nextInt(this.nodeChance) == 1) {
				this.xnd = var1.nextInt(this.maxDis - this.minDis) + this.minDis;
				this.xn = new Dun3Node(this.x - this.xnd, this.y, this.z, this.nodesLeft - 1);
				++var2;
			}
			if (var1.nextInt(this.nodeChance) == 1) {
				this.zpd = var1.nextInt(this.maxDis - this.minDis) + this.minDis;
				this.zp = new Dun3Node(this.x, this.y, this.z + this.zpd, this.nodesLeft - 1);
				++var2;
			}
			if (var1.nextInt(this.nodeChance) == 1) {
				this.znd = var1.nextInt(this.maxDis - this.minDis) + this.minDis;
				this.zn = new Dun3Node(this.x, this.y, this.z - this.znd, this.nodesLeft - 1);
				++var2;
			}
			if (this.xp != null) {
				this.xp.makeNodes(var1);
			}
			if (this.xn != null) {
				this.xn.makeNodes(var1);
			}
			if (this.zp != null) {
				this.zp.makeNodes(var1);
			}
			if (this.zn != null) {
				this.zn.makeNodes(var1);
			}
		}
		return var2;
	}

	public void genTunels(World var1) {
		for (int var2 = 0; var2 < 4; ++var2) {
			int var3;
			for (var3 = 0; var3 < this.xpd; ++var3) {
				var1.setBlock(this.x + var3, this.y + var2, this.z + 1, 0);
				var1.setBlock(this.x + var3, this.y + var2, this.z, 0);
				var1.setBlock(this.x + var3, this.y + var2, this.z - 1, 0);
			}
			for (var3 = 0; var3 < this.xnd; ++var3) {
				var1.setBlock(this.x - var3, this.y + var2, this.z + 1, 0);
				var1.setBlock(this.x - var3, this.y + var2, this.z, 0);
				var1.setBlock(this.x - var3, this.y + var2, this.z - 1, 0);
			}
			for (var3 = 0; var3 < this.zpd; ++var3) {
				var1.setBlock(this.x + 1, this.y + var2, this.z + var3, 0);
				var1.setBlock(this.x, this.y + var2, this.z + var3, 0);
				var1.setBlock(this.x - 1, this.y + var2, this.z + var3, 0);
			}
			for (var3 = 0; var3 < this.znd; ++var3) {
				var1.setBlock(this.x + 1, this.y + var2, this.z - var3, 0);
				var1.setBlock(this.x, this.y + var2, this.z - var3, 0);
				var1.setBlock(this.x - 1, this.y + var2, this.z - var3, 0);
			}
		}
		if (this.xp != null) {
			this.xp.genTunels(var1);
		}
		if (this.xn != null) {
			this.xn.genTunels(var1);
		}
		if (this.zp != null) {
			this.zp.genTunels(var1);
		}
		if (this.zn != null) {
			this.zn.genTunels(var1);
		}
	}
}
