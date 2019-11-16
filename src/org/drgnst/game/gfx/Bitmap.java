package org.drgnst.game.gfx;

/**
 * @author Sopiro
 *
 * 2015. 12. 14. ¿ÀÈÄ 5:32:08
 */
public class Bitmap
{
	public int width;
	public int height;
	public int[] pixels;

	public Bitmap(int width, int height)
	{
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
	}

	public void render(Bitmap b, int ox, int oy)
	{
		for (int y = 0; y < b.height; y++)
		{
			int yy = y + oy;
			if (yy < 0 || yy >= height)
				continue;

			for (int x = 0; x < b.width; x++)
			{
				int xx = x + ox;
				if (xx < 0 || xx >= width)
					continue;

				int alpha = b.pixels[x + y * b.width];

				if (alpha > 0)
					pixels[xx + yy * width] = alpha;
			}
		}
	}

	public void clear()
	{
		for (int i = 0; i < pixels.length; i++)
		{
			pixels[i] = 0;
		}
	}
}