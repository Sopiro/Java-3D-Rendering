package org.drgnst.game.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author Sopiro
 * <p>
 * 2015. 12. 14. ¿ÀÈÄ 5:31:54
 */
public class Textures
{
    public static Bitmap textures = loadTexture("/textures/textures.png");

    public static Bitmap loadTexture(String path)
    {
        try
        {
            BufferedImage image = ImageIO.read(Textures.class.getResourceAsStream(path));
            Bitmap res = new Bitmap(image.getWidth(), image.getHeight());
            image.getRGB(0, 0, res.width, res.height, res.pixels, 0, res.width);

            for (int i = 0; i < res.pixels.length; i++)
            {
                int ci = res.pixels[i];
                int col = (ci & 0xf) >> 2;
                if (ci == 0xffff00ff)
                    col = -1;
                res.pixels[i] = col;

            }

            return res;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
