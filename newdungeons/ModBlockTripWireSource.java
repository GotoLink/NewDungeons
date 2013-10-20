package newdungeons;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTripWireSource;

public class ModBlockTripWireSource extends BlockTripWireSource {
	protected ModBlockTripWireSource(int var1) {
		super(var1);
	}

	@Override
	public int idDropped(int var1, Random var2, int var3) {
		return Block.tripWireSource.idDropped(0, (Random) null, 0);
	}
}
