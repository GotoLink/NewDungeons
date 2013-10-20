package newdungeons;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneBrick;

public class ModBlockStoneBrick extends BlockStoneBrick {
	public ModBlockStoneBrick(int var1) {
		super(var1);
	}

	@Override
	public int idDropped(int var1, Random var2, int var3) {
		return Block.stoneBrick.idDropped(0, (Random) null, 0);
	}
}
