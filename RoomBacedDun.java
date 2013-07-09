package newdungeons;

import java.util.ArrayList;
import java.util.Random;

public class RoomBacedDun
{
    static int maxSize;
    static int size;
    static Random random;
    static ArrayList main;

    public static void main(String[] var0)
    {
        random = new Random();
        maxSize = 300;
        size = random.nextInt(maxSize);
        main = new ArrayList();

        for (int var1 = -size; var1 < size; ++var1)
        {
            ArrayList var2 = new ArrayList();
            main.add(var2);

            for (int var3 = -size; var3 < size; ++var3)
            {
                ArrayList var4 = new ArrayList();
                var2.add(var4);
            }
        }
    }
}
