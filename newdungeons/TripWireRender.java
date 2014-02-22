package newdungeons;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class TripWireRender implements ISimpleBlockRenderingHandler{
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        IIcon iicon = renderer.getBlockIconFromSide(block, 0);
        int l = world.getBlockMetadata(x, y, z);
        boolean flag = (l & 4) == 4;
        boolean flag1 = (l & 2) == 2;

        if (renderer.hasOverrideBlockTexture())
        {
            iicon = renderer.overrideBlockTexture;
        }

        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        double d0 = (double)iicon.getMinU();
        double d1 = (double)iicon.getInterpolatedV(flag ? 2.0D : 0.0D);
        double d2 = (double)iicon.getMaxU();
        double d3 = (double)iicon.getInterpolatedV(flag ? 4.0D : 2.0D);
        double d4 = (double)(flag1 ? 3.5F : 1.5F) / 16.0D;
        boolean flag2 = ModBlockTripWire.getRenderDir(world, x, y, z, l, 1);
        boolean flag3 = ModBlockTripWire.getRenderDir(world, x, y, z, l, 3);
        boolean flag4 = ModBlockTripWire.getRenderDir(world, x, y, z, l, 2);
        boolean flag5 = ModBlockTripWire.getRenderDir(world, x, y, z, l, 0);
        float f = 0.03125F;
        float f1 = 0.5F - f / 2.0F;
        float f2 = f1 + f;

        if (!flag4 && !flag3 && !flag5 && !flag2)
        {
            flag4 = true;
            flag5 = true;
        }

        if (flag4)
        {
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)z + 0.25D, d0, d1);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)z + 0.25D, d0, d3);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)z, d2, d3);
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)z, d2, d1);
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)z, d2, d1);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)z, d2, d3);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)z + 0.25D, d0, d3);
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)z + 0.25D, d0, d1);
        }

        if (flag4 || flag5 && !flag3 && !flag2)
        {
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)z + 0.5D, d0, d1);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)z + 0.5D, d0, d3);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)z + 0.25D, d2, d3);
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)z + 0.25D, d2, d1);
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)z + 0.25D, d2, d1);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)z + 0.25D, d2, d3);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)z + 0.5D, d0, d3);
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)z + 0.5D, d0, d1);
        }

        if (flag5 || flag4 && !flag3 && !flag2)
        {
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)z + 0.75D, d0, d1);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)z + 0.75D, d0, d3);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)z + 0.5D, d2, d3);
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)z + 0.5D, d2, d1);
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)z + 0.5D, d2, d1);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)z + 0.5D, d2, d3);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)z + 0.75D, d0, d3);
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)z + 0.75D, d0, d1);
        }

        if (flag5)
        {
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)(z + 1), d0, d1);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)(z + 1), d0, d3);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)z + 0.75D, d2, d3);
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)z + 0.75D, d2, d1);
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)z + 0.75D, d2, d1);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)z + 0.75D, d2, d3);
            tessellator.addVertexWithUV((double)((float)x + f2), (double)y + d4, (double)(z + 1), d0, d3);
            tessellator.addVertexWithUV((double)((float)x + f1), (double)y + d4, (double)(z + 1), d0, d1);
        }

        if (flag2)
        {
            tessellator.addVertexWithUV((double)x, (double)y + d4, (double)((float)z + f2), d0, d3);
            tessellator.addVertexWithUV((double)x + 0.25D, (double)y + d4, (double)((float)z + f2), d2, d3);
            tessellator.addVertexWithUV((double)x + 0.25D, (double)y + d4, (double)((float)z + f1), d2, d1);
            tessellator.addVertexWithUV((double)x, (double)y + d4, (double)((float)z + f1), d0, d1);
            tessellator.addVertexWithUV((double)x, (double)y + d4, (double)((float)z + f1), d0, d1);
            tessellator.addVertexWithUV((double)x + 0.25D, (double)y + d4, (double)((float)z + f1), d2, d1);
            tessellator.addVertexWithUV((double)x + 0.25D, (double)y + d4, (double)((float)z + f2), d2, d3);
            tessellator.addVertexWithUV((double)x, (double)y + d4, (double)((float)z + f2), d0, d3);
        }

        if (flag2 || flag3 && !flag4 && !flag5)
        {
            tessellator.addVertexWithUV((double)x + 0.25D, (double)y + d4, (double)((float)z + f2), d0, d3);
            tessellator.addVertexWithUV((double)x + 0.5D, (double)y + d4, (double)((float)z + f2), d2, d3);
            tessellator.addVertexWithUV((double)x + 0.5D, (double)y + d4, (double)((float)z + f1), d2, d1);
            tessellator.addVertexWithUV((double)x + 0.25D, (double)y + d4, (double)((float)z + f1), d0, d1);
            tessellator.addVertexWithUV((double)x + 0.25D, (double)y + d4, (double)((float)z + f1), d0, d1);
            tessellator.addVertexWithUV((double)x + 0.5D, (double)y + d4, (double)((float)z + f1), d2, d1);
            tessellator.addVertexWithUV((double)x + 0.5D, (double)y + d4, (double)((float)z + f2), d2, d3);
            tessellator.addVertexWithUV((double)x + 0.25D, (double)y + d4, (double)((float)z + f2), d0, d3);
        }

        if (flag3 || flag2 && !flag4 && !flag5)
        {
            tessellator.addVertexWithUV((double)x + 0.5D, (double)y + d4, (double)((float)z + f2), d0, d3);
            tessellator.addVertexWithUV((double)x + 0.75D, (double)y + d4, (double)((float)z + f2), d2, d3);
            tessellator.addVertexWithUV((double)x + 0.75D, (double)y + d4, (double)((float)z + f1), d2, d1);
            tessellator.addVertexWithUV((double)x + 0.5D, (double)y + d4, (double)((float)z + f1), d0, d1);
            tessellator.addVertexWithUV((double)x + 0.5D, (double)y + d4, (double)((float)z + f1), d0, d1);
            tessellator.addVertexWithUV((double)x + 0.75D, (double)y + d4, (double)((float)z + f1), d2, d1);
            tessellator.addVertexWithUV((double)x + 0.75D, (double)y + d4, (double)((float)z + f2), d2, d3);
            tessellator.addVertexWithUV((double)x + 0.5D, (double)y + d4, (double)((float)z + f2), d0, d3);
        }

        if (flag3)
        {
            tessellator.addVertexWithUV((double)x + 0.75D, (double)y + d4, (double)((float)z + f2), d0, d3);
            tessellator.addVertexWithUV((double)(x + 1), (double)y + d4, (double)((float)z + f2), d2, d3);
            tessellator.addVertexWithUV((double)(x + 1), (double)y + d4, (double)((float)z + f1), d2, d1);
            tessellator.addVertexWithUV((double)x + 0.75D, (double)y + d4, (double)((float)z + f1), d0, d1);
            tessellator.addVertexWithUV((double)x + 0.75D, (double)y + d4, (double)((float)z + f1), d0, d1);
            tessellator.addVertexWithUV((double)(x + 1), (double)y + d4, (double)((float)z + f1), d2, d1);
            tessellator.addVertexWithUV((double)(x + 1), (double)y + d4, (double)((float)z + f2), d2, d3);
            tessellator.addVertexWithUV((double)x + 0.75D, (double)y + d4, (double)((float)z + f2), d0, d3);
        }
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return 0;
    }
}
