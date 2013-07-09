package newdungeons;

public class ColorMazeLine
{
    public ColorMazePoint first;
    public ColorMazePoint second;
    public int x1;
    public int y1;
    public int x2;
    public int y2;

    public ColorMazeLine(ColorMazePoint var1, ColorMazePoint var2)
    {
        this.first = var1;
        this.second = var2;
        this.x1 = this.first.x;
        this.y1 = this.first.y;
        this.x2 = this.second.x;
        this.y2 = this.second.y;
    }

    public boolean horazontal()
    {
        return this.x1 == this.x2;
    }

    public boolean vertical()
    {
        return this.y1 == this.y2;
    }

    public int getStart()
    {
        return this.horazontal() ? (this.x1 < this.x2 ? this.x1 : this.x2) : (this.y1 < this.y2 ? this.y1 : this.y2);
    }

    public int getEnd()
    {
        return this.horazontal() ? (this.x2 > this.x1 ? this.x2 : this.x1) : (this.y2 > this.y1 ? this.y2 : this.y1);
    }
}
