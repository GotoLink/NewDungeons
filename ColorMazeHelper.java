package newdungeons;

import java.util.ArrayList;

public class ColorMazeHelper
{
    ArrayList x = new ArrayList();

    ColorMazeHelper(int var1, int var2)
    {
        for (int var3 = 0; var3 < var1; ++var3)
        {
            this.x.add(new ArrayList());

            for (int var4 = 0; var4 < var2; ++var4)
            {
                ((ArrayList)this.x.get(this.x.size() - 1)).add(new ColorMazeRoom(this, var3, var4));
            }
        }
    }

    public boolean canPlace(int var1, int var2, int var3)
    {
        boolean var4 = false;

        if (this.getRoomColor(var2 - 1, var3) >= var1)
        {
            var4 = true;
        }

        if (this.getRoomColor(var2 + 1, var3) >= var1)
        {
            var4 = true;
        }

        if (this.getRoomColor(var2, var3 + 1) >= var1)
        {
            var4 = true;
        }

        if (this.getRoomColor(var2, var3 - 1) >= var1)
        {
            var4 = true;
        }

        if (this.getRoomColor(var2, var3) != 0)
        {
            var4 = false;
        }

        for (int var5 = 0; var5 < 6; ++var5)
        {
            for (int var6 = 0; var6 < 6; ++var6)
            {
                System.out.print(this.getRoomColor(var5, var6));
            }
        }
        return var4;
    }

    public ColorMazeRoom getRoom(int var1, int var2)
    {
        try
        {
            return (ColorMazeRoom)((ArrayList)this.x.get(var1)).get(var2);
        }
        catch (Exception var4)
        {
            return null;
        }
    }

    public int getRoomColor(int var1, int var2)
    {
        try
        {
            return ((ColorMazeRoom)((ArrayList)this.x.get(var1)).get(var2)).getColor();
        }
        catch (Exception var4)
        {
            return 0;
        }
    }

    public void setRoomColor(int var1, int var2, int var3)
    {
        ((ColorMazeRoom)((ArrayList)this.x.get(var2)).get(var3)).setColor(var1);
    }

    public boolean setDoorColor(int var1, int var2, int var3, String var4)
    {
        if (var4 == "Right")
        {
            ((ColorMazeRoom)((ArrayList)this.x.get(var2)).get(var3)).getRight().setColor(var1);
        }
        else if (var4 == "Left")
        {
            ((ColorMazeRoom)((ArrayList)this.x.get(var2)).get(var3)).getLeft().setColor(var1);
        }
        else if (var4 == "Up")
        {
            ((ColorMazeRoom)((ArrayList)this.x.get(var2)).get(var3)).getUp().setColor(var1);
        }
        else
        {
            if (var4 != "Down")
            {
                System.err.println("unable to set door color. unknown position " + var4);
                return false;
            }

            ((ColorMazeRoom)((ArrayList)this.x.get(var2)).get(var3)).getDown().setColor(var1);
        }

        return true;
    }

    public int getDoorColor(int var1, int var2, String var3)
    {
        if (var3 == "Right")
        {
            return ((ColorMazeRoom)((ArrayList)this.x.get(var1)).get(var2)).getRight().getColor();
        }
        else if (var3 == "Left")
        {
            return ((ColorMazeRoom)((ArrayList)this.x.get(var1)).get(var2)).getLeft().getColor();
        }
        else if (var3 == "Up")
        {
            return ((ColorMazeRoom)((ArrayList)this.x.get(var1)).get(var2)).getUp().getColor();
        }
        else if (var3 == "Down")
        {
            return ((ColorMazeRoom)((ArrayList)this.x.get(var1)).get(var2)).getDown().getColor();
        }
        else
        {
            System.err.println("unable to set door color. unknown position " + var3);
            return 0;
        }
    }
}
