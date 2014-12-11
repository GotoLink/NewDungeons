package newdungeons;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Random;

public final class DunLootPotion extends DunLootItem {
	final int potionID;

	DunLootPotion(Item var1, int var2, int var3, double var4, double var6, int var8, int var9) {
		super(var1, var2, var3, var4, var6, var8);
		this.potionID = var9;
	}

	@Override
	ItemStack getItemStack(Random var1) {
		if (!(this.item instanceof Item)) {
			return null;
		} else {
			int var3 = var1.nextInt(this.maxStack - this.minStack) + this.minStack;
			ItemStack var2;
			int var4;
			do {
				var4 = this.potionID;
				var2 = new ItemStack((Item) this.item, var3, var4);
			} while (this.getEffectNamesFromDamage(var4) == null);
			return var2;
		}
	}
}
