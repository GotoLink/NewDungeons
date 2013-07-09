package newdungeons;

public class ColorMazeRoom
{
    private int color;
    private int x;
    private int y;
    ColorMazeHelper parent;
    ColorMazeDoor rightDoor;
    ColorMazeDoor leftDoor;
    ColorMazeDoor upDoor;
    ColorMazeDoor downDoor;

    ColorMazeRoom(ColorMazeHelper var1, int var2, int var3)
    {
        this(var1, 0, var2, var3);
    }

    ColorMazeRoom(ColorMazeHelper var1, int var2, int var3, int var4)
    {
        this(var1, var2, (ColorMazeDoor)null, (ColorMazeDoor)null, (ColorMazeDoor)null, (ColorMazeDoor)null, var3, var4);
    }

    ColorMazeRoom(ColorMazeHelper var1, int var2, ColorMazeDoor var3, ColorMazeDoor var4, ColorMazeDoor var5, ColorMazeDoor var6, int var7, int var8)
    {
        this.color = 0;
        this.parent = var1;
        this.color = var2;
        this.x = var7;
        this.y = var8;
        this.rightDoor = var3;
        this.leftDoor = var4;
        this.upDoor = var5;
        this.downDoor = var6;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getColor()
    {
        return this.color;
    }

    public int setColor(int var1)
    {
        this.color = var1;
        return this.color;
    }

    public ColorMazeRoom updateAllDoors()
    {
        this.rightDoor.setColor(this.min(this.parent.getRoomColor(this.x + 1, this.y), this.color));
        this.leftDoor.setColor(this.min(this.parent.getRoomColor(this.x - 1, this.y), this.color));
        this.upDoor.setColor(this.min(this.parent.getRoomColor(this.x, this.y + 1), this.color));
        this.downDoor.setColor(this.min(this.parent.getRoomColor(this.x, this.y - 1), this.color));
        return this;
    }

    public ColorMazeRoom updateDoors()
    {
        this.leftDoor.setColor(this.min(this.getLeft().getColor(), this.color));
        this.rightDoor.setColor(this.min(this.getRight().getColor(), this.color));
        this.downDoor.setColor(this.min(this.getDown().getColor(), this.color));
        this.upDoor.setColor(this.min(this.getUp().getColor(), this.color));
        return this;
    }

    public ColorMazeDoor getRight()
    {
        return this.rightDoor;
    }

    public ColorMazeDoor getLeft()
    {
        return this.leftDoor;
    }

    public ColorMazeDoor getUp()
    {
        return this.upDoor;
    }

    public ColorMazeDoor getDown()
    {
        return this.downDoor;
    }

    private int min(int var1, int var2)
    {
        return var1 < var2 ? var1 : var2;
    }

    public void setLeft(ColorMazeDoor var1)
    {
        this.leftDoor = var1;
    }

    public void setRight(ColorMazeDoor var1)
    {
        this.rightDoor = var1;
    }

    public void setUp(ColorMazeDoor var1)
    {
        this.upDoor = var1;
    }

    public void setDown(ColorMazeDoor var1)
    {
        this.downDoor = var1;
    }

    public void makeDoors()
    {
        if (this.parent.getRoom(this.x - 1, this.y) != null && this.parent.getRoom(this.x - 1, this.y).getRight() != null)
        {
            this.setLeft(this.parent.getRoom(this.x - 1, this.y).getRight());
        }
        else
        {
            this.setLeft(new ColorMazeDoor(9));
        }

        if (this.parent.getRoom(this.x + 1, this.y) != null && this.parent.getRoom(this.x + 1, this.y).getLeft() != null)
        {
            this.setRight(this.parent.getRoom(this.x + 1, this.y).getLeft());
        }
        else
        {
            this.setRight(new ColorMazeDoor(9));
        }

        if (this.parent.getRoom(this.x, this.y + 1) != null && this.parent.getRoom(this.x, this.y + 1).getDown() != null)
        {
            this.setUp(this.parent.getRoom(this.x, this.y + 1).getDown());
        }
        else
        {
            this.setUp(new ColorMazeDoor(9));
        }

        if (this.parent.getRoom(this.x, this.y - 1) != null && this.parent.getRoom(this.x, this.y - 1).getUp() != null)
        {
            this.setDown(this.parent.getRoom(this.x, this.y - 1).getUp());
        }
        else
        {
            this.setDown(new ColorMazeDoor(9));
        }
    }
}
