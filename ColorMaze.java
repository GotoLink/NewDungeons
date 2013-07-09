package newdungeons;

import java.util.Random;

public class ColorMaze
{
    ColorMazeHelper helper;
    Random random;
    int xp;
    int yp;
    static int xs;
    static int ys;
    int numColors = 8;

    ColorMaze(Random var1, int var2, int var3, int var4, int var5)
    {
        this.random = var1;
        this.xp = var2;
        this.yp = var3;
        xs = var4;
        ys = var5;
        this.helper = new ColorMazeHelper(xs, ys);
    }

    public void generate()
    {
        int var1 = this.random.nextInt(6);
        int var2 = this.random.nextInt(6);
        this.helper.setRoomColor(8, var1, var2);
        int var3;
        int var4;

        for (var3 = 7; var3 > 0; --var3)
        {
            for (var4 = 0; var4 < 9 - var3; ++var4)
            {
                var1 = this.random.nextInt(6);
                var2 = this.random.nextInt(6);

                if (this.helper.canPlace(var3, var1, var2))
                {
                    this.helper.setRoomColor(var3, var1, var2);
                }
                else
                {
                    --var4;
                }
            }
        }

        for (var3 = 0; var3 < 6; ++var3)
        {
            for (var4 = 0; var4 < 6; ++var4)
            {
                this.helper.getRoom(var3, var4).makeDoors();
            }
        }

        for (var3 = 0; var3 < 6; ++var3)
        {
            for (var4 = 0; var4 < 6; ++var4)
            {
                this.helper.getRoom(var3, var4).updateDoors();
            }
        }

        for (var3 = 7; var3 > 0; --var3)
        {
            var1 = this.random.nextInt(6);
            var2 = this.random.nextInt(6);
            ++var3;

            if (this.helper.getRoomColor(var1, var2) >= var3)
            {
                var4 = this.random.nextInt(4);
                String var5 = "none";

                if (var4 == 0)
                {
                    var5 = "Left";
                }
                else if (var4 == 1)
                {
                    var5 = "Right";
                }
                else if (var4 == 2)
                {
                    var5 = "Up";
                }
                else if (var4 == 3)
                {
                    var5 = "Down";
                }


                if (this.helper.getDoorColor(var1, var2, var5) == var3 - 1)
                {
                    this.helper.setDoorColor(var3, var1, var2, var5);
                    --var3;
                }
            }
        }

        for (var3 = 0; var3 < 6; ++var3)
        {
            for (var4 = 0; var4 < 6; ++var4)
            {
                System.out.print(this.helper.getRoomColor(var3, var4));
            }
        }
    }

    public static void main(String[] var0)
    {
        place();
    }

    public static void place()
    {
        ColorMazePoint[][] var0 = new ColorMazePoint[xs * 6 + 1][ys * 6 + 1];
        Integer[][] var1 = new Integer[xs * 6 + 1][ys * 6 + 1];
        int var2;
        int var3;

        for (var2 = 1; var2 < 7; ++var2)
        {
            for (var3 = 1; var3 < 7; ++var3)
            {
                var0[var2 * 6][var3 * 6] = new ColorMazePoint();
                var0[var2 * 6][var3 * 6].x = var2 * 6;
                var0[var2 * 6][var3 * 6].y = var3 * 6;
            }
        }

        for (var2 = 0; var2 < xs * 6; ++var2)
        {
            for (var3 = 0; var3 < xs * 6; ++var3)
            {
                if (var0[var2][var3] instanceof ColorMazePoint)
                {
                    var1[var2][var3] = Integer.valueOf(1);
                }

                /*if (var1[var2][var3] != null)
                {
                    System.out.print(var1[var2][var3]);
                }*/
            }
        }
    }
}
