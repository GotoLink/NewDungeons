package newdungeons;

public class ColorMazeDoor
{
    private int color;
    private int x;
    private int z;
    private boolean open;

    ColorMazeDoor(int var1)
    {
        this(var1, 0, 0);
    }

    ColorMazeDoor(int var1, int var2, int var3)
    {
        this.color = 0;
        this.x = 0;
        this.z = 0;
        this.open = false;
        this.color = var1;
        this.x = var2;
        this.z = var3;
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

    public void move(int var1, int var2)
    {
        this.x = var1;
        this.z = var2;
    }

    public void setX(int var1)
    {
        this.x = var1;
    }

    public void setZ(int var1)
    {
        this.z = var1;
    }
}
