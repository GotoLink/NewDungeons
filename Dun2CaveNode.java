package newdungeons;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class Dun2CaveNode
{
    int x;
    int y;
    int z;
    int size;
    World world;
    Random random;
    ArrayList conections = new ArrayList();

    Dun2CaveNode()
    {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.size = 1;
    }

    Dun2CaveNode(World var1, Random var2, int var3, int var4, int var5, int var6, ArrayList var7)
    {
        this.world = var1;
        this.random = var2;
        this.x = var3;
        this.y = var4;
        this.z = var5;
        this.size = var6;

        if (var7 != null)
        {
            for (int var8 = 0; var8 < var7.size(); ++var8)
            {
                this.conections.add(var7.get(var8));
            }
        }
    }

    public boolean conect(Dun2CaveNode var1)
    {
        if (this.contains(this.conections, var1))
        {
            return false;
        }
        else
        {
            this.conections.add(var1);
            return true;
        }
    }

    public boolean contains(ArrayList var1, Object var2)
    {
        if (var1 != null)
        {
            for (int var3 = 0; var3 < var1.size(); ++var3)
            {
                if (var1.get(var3) == var2)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public void clear()
    {
        for (int var1 = -this.size; var1 < this.size + 1; ++var1)
        {
            for (int var2 = -this.size; var2 < this.size + 1; ++var2)
            {
                for (int var3 = -this.size; var3 < this.size + 1; ++var3)
                {
                    if (this.distance(this.x, this.y, this.z, var1 + this.x, var2 + this.y, var3 + this.z) < (double)this.size)
                    {
                        if (this.y + var2 < 58)
                        {
                            if (this.distance(this.x, this.y, this.z, var1 + this.x, var2 + this.y, var3 + this.z) < (double)(this.size - 1))
                            {
                                this.world.setBlock(this.x + var1, this.y + var2, this.z + var3, Block.lavaStill.blockID);
                            }
                            else
                            {
                                this.world.setBlock(this.x + var1, this.y + var2, this.z + var3, Block.stone.blockID);
                            }
                        }
                        else if (this.y + var2 > 50)
                        {
                            this.world.setBlock(this.x + var1, this.y + var2, this.z + var3, 0);
                        }
                    }
                }
            }
        }
    }

    public double distance(int var1, int var2, int var3, int var4, int var5, int var6)
    {
        return (double)MathHelper.sqrt_double((double)((var1 - var4) * (var1 - var4) + (var2 - var5) * (var2 - var5) + (var3 - var6) * (var3 - var6)));
    }

    public double distance(Dun2CaveNode var1, Dun2CaveNode var2)
    {
        return this.distance(var1.x, var1.y, var1.z, var2.x, var2.y, var2.z);
    }

    public int abs(double var1)
    {
        return var1 > 0.0D ? (int)var1 : -((int)var1);
    }
}
