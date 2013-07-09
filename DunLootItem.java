package newdungeons;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;

public class DunLootItem
{
    Object item;
    int maxStack;
    int minStack;
    double rareity;
    double value;
    int minDanger;
    private HashMap idEffectNameMap;
    int maxEnchatLev;
    int enchatProb;
    int damigeValueMin;
    int damigeValueMax;

    DunLootItem(Item var1, int var2, int var3, double var4, double var6, int var8, int var9, int var10, int var11, int var12)
    {
        this.item = var1;
        this.maxStack = var2;
        this.minStack = var3;
        this.rareity = var4;
        this.value = var6;
        this.minDanger = var8;
        this.idEffectNameMap = new HashMap();
        this.maxEnchatLev = var9;
        this.enchatProb = var10;
        this.damigeValueMin = var11;
        this.damigeValueMax = var12;
    }

    DunLootItem(Block var1, int var2, int var3, double var4, double var6, int var8, int var9, int var10, int var11, int var12)
    {
        this.item = var1;
        this.maxStack = var2;
        this.minStack = var3;
        this.rareity = var4;
        this.value = var6;
        this.minDanger = var8;
        this.maxEnchatLev = var9;
        this.enchatProb = var10;
        this.damigeValueMin = var11;
        this.damigeValueMax = var12;
    }

    DunLootItem(Item var1, int var2, int var3, double var4, double var6, int var8, int var9, int var10)
    {
        this(var1, var2, var3, var4, var6, var8, var9, var10, 0, 1);
    }

    DunLootItem(Block var1, int var2, int var3, double var4, double var6, int var8, int var9, int var10)
    {
        this(var1, var2, var3, var4, var6, var8, var9, var10, 0, 1);
    }

    DunLootItem(Item var1, int var2, int var3, double var4, double var6, int var8)
    {
        this(var1, var2, var3, var4, var6, var8, 0, 0);
    }

    DunLootItem(Block var1, int var2, int var3, double var4, double var6, int var8)
    {
        this(var1, var2, var3, var4, var6, var8, 0, 0);
    }

    ItemStack getItemStack(Random var1)
    {
        int var3;

        if (this.item instanceof Block)
        {
            int var5 = 1;
            var3 = 0;

            if (this.maxStack - this.minStack > 1)
            {
                var5 = var1.nextInt(this.maxStack - this.minStack) + this.minStack;
            }

            if (this.damigeValueMax == this.damigeValueMin)
            {
                var3 = this.damigeValueMax;
            }

            if (this.damigeValueMax - this.damigeValueMin > 1)
            {
                var3 = var1.nextInt(this.damigeValueMax - this.damigeValueMin) + this.damigeValueMin;
            }

            return new ItemStack((Block)this.item, var5, var3);
        }
        else if (this.item instanceof Item)
        {
            var3 = 1;
            int var4 = 0;

            if (this.maxStack - this.minStack > 1)
            {
                var3 = var1.nextInt(this.maxStack - this.minStack) + this.minStack;
            }

            if (this.damigeValueMax == this.damigeValueMin)
            {
                var4 = this.damigeValueMax;
            }

            if (this.damigeValueMax - this.damigeValueMin > 1)
            {
                var4 = var1.nextInt(this.damigeValueMax - this.damigeValueMin) + this.damigeValueMin;
            }

            ItemStack var2;

            if (this.item instanceof ItemPotion)
            {
                do
                {
                    var4 = var1.nextInt(1999999999);
                    var2 = new ItemStack((Item)this.item, var3, var4);
                }
                while (this.getEffectNamesFromDamage(var4) == null);
            }
            else
            {
                var2 = new ItemStack((Item)this.item, var3, var4);
            }

            return var2;
        }
        else
        {
            return null;
        }
    }

    public String toString()
    {
        return this.item instanceof Block ? ((Block)this.item).getUnlocalizedName() : (this.item instanceof Item ? ((Item)this.item).getUnlocalizedName() : null);
    }

    public List getEffectNamesFromDamage(int var1)
    {
        List var2 = (List)this.idEffectNameMap.get(Integer.valueOf(var1));

        if (var2 == null)
        {
            var2 = PotionHelper.getPotionEffects(var1, false);
            this.idEffectNameMap.put(Integer.valueOf(var1), var2);
        }

        return var2;
    }
}
