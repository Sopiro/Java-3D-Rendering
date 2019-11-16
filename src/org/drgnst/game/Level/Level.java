package org.drgnst.game.Level;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.drgnst.game.gfx.Sprite;

/**
 * @author Sopiro
 *
 * 2015. 12. 14. ¿ÀÈÄ 5:31:44
 */
public class Level
{
	private static Map<String, Level> levels = new HashMap<String, Level>();

	public String name;
	public int width;
	public int height;
	public int[] pixels;
	public Block[] tile;

	public int xSpawn;
	public int ySpawn;
	
	public Level(String name, int width, int height)
	{
		this.name = name;
		this.width = width;
		this.height = height;
		tile = new Block[width * height];
		this.pixels = new int[width * height];
	}
	
	public void load()
	{
		for(int i = 0; i < width * height; i++)
			pixels[i] = pixels[i] & 0xffffff;
		
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				Block block = new Block();
				
				int type = pixels[x + y * width];
				
				if(type == 0xFFFFFF)
				{
					block = new SolidBlock();
					block.col = 0x667CDB & 0x555555;
				}
				else if(type == 0xFFFF00)
				{
					xSpawn = x;
					ySpawn = y;
				}
				else if(type == 0x00FF00)
				{
					block.addSprite(new Sprite(0, 0, 0, 0, 0x003300));
				}
				else if(type == 0xff00ff)
				{
					block.ceilCol = 0x550055;
					block.floorCol = 0x550000;
				}
				
				tile[x + y * width] = block;
			}
		}
	}

	public Block getBlock(int x, int y)
	{
		if (x < 0 || y < 0 || x >= width || y >= height)
			return new SolidBlock();

		return tile[x + y * width];
	}
	
	public static Level loadLevel(String name)
	{
		if(levels.containsKey(name))
			return levels.get(name);
		
		try
		{
			BufferedImage image = ImageIO.read(Level.class.getResourceAsStream("/levels/" + name + ".png"));
			int w = image.getWidth();
			int h = image.getHeight();
			
			Level res = new Level(name, w, h);
			image.getRGB(0, 0, res.width, res.height, res.pixels, 0, res.width);
			res.load();
			
			return res;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
