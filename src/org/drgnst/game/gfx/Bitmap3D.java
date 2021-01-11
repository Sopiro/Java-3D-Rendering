package org.drgnst.game.gfx;

import org.drgnst.game.Game;
import org.drgnst.game.Level.Block;
import org.drgnst.game.Level.Level;

/**
 * @author Sopiro
 * <p>
 * 2015. 12. 14. ¿ÀÈÄ 5:32:03
 */
public class Bitmap3D extends Bitmap
{
    private double[] depthBuffer;
    private double[] depthBufferWall;
    private double xCam, yCam, zCam, rot, rSin, rCos, fov, xCenter, yCenter;

    public Bitmap3D(int width, int height)
    {
        super(width, height);

        depthBuffer = new double[width * height];
        depthBufferWall = new double[width];
    }

    public void render(Game game)
    {
        for (int x = 0; x < width; x++)
            depthBufferWall[x] = 0;
        for (int x = 0; x < width * height; x++)
            depthBuffer[x] = 10000;

        fov = height;
        xCenter = width / 2.0;
        yCenter = height / 3.0;

        xCam = game.player.x;
        yCam = game.player.y;
        zCam = 1;
        rot = Math.sin(game.time / 40.0) * 0.5;
        rot = game.player.rot;

        rSin = Math.sin(rot);
        rCos = Math.cos(rot);

        renderFloor(game.level);

        Level level = game.level;
        for (int y = -1; y <= level.height; y++)
        {
            for (int x = -1; x <= level.width; x++)
            {
                Block c = level.getBlock(x, y);
                Block e = level.getBlock(x + 1, y);
                Block w = level.getBlock(x - 1, y);
                Block s = level.getBlock(x, y - 1);
                Block n = level.getBlock(x, y + 1);

                if (!c.SOLID_RENDER)
                    continue;

                if (!e.SOLID_RENDER)
                    renderWall(x + 1, y, x + 1, y + 1, c.tex, (c.col & 0xfefefe) >> 1);
                if (!w.SOLID_RENDER)
                    renderWall(x, y + 1, x, y, c.tex, (c.col & 0xfefefe) >> 1);
                if (!s.SOLID_RENDER)
                    renderWall(x, y, x + 1, y, c.tex, c.col);
                if (!n.SOLID_RENDER)
                    renderWall(x + 1, y + 1, x, y + 1, c.tex, c.col);
            }
        }

        for (int y = -1; y <= level.height; y++)
        {
            for (int x = -1; x <= level.width; x++)
            {
                Block c = level.getBlock(x, y);

                for (int i = 0; i < c.sprites.size(); i++)
                {
                    Sprite sprite = c.sprites.get(i);

                    renderSprite(x + sprite.x, sprite.y, y + sprite.z, sprite.tex, sprite.col);
                }
            }
        }
    }

    public void renderSprite(double x, double y, double z, int tex, int col)
    {
        double xo = x - xCam;
        double yo = y + zCam / 8;
        double zo = z - yCam;

        double xx = xo * rCos + zo * rSin;
        double yy = yo;
        double zz = -xo * rSin + zo * rCos;

        if (zz < 0.1)
            return;

        double xPixel0 = xx / zz * fov + xCenter - (fov / 2) / zz;
        double xPixel1 = xx / zz * fov + xCenter + (fov / 2) / zz;
        double yPixel0 = yy / zz * fov + yCenter - (fov / 2) / zz;
        double yPixel1 = yy / zz * fov + yCenter + (fov / 2) / zz;

        int xp0 = (int) (xPixel0);
        int xp1 = (int) (xPixel1);
        int yp0 = (int) (yPixel0);
        int yp1 = (int) (yPixel1);

        if (xp0 < 0)
            xp0 = 0;
        if (xp1 > width)
            xp1 = width;
        if (yp0 < 0)
            yp0 = 0;
        if (yp1 > height)
            yp1 = height;

        zz *= 8;
        for (int yp = yp0; yp < yp1; yp++)
        {
            double pry = (yp - Math.floor(yPixel0)) / (Math.floor(yPixel1) - Math.floor(yPixel0));
            int yt = (int) (pry * 16);

            for (int xp = xp0; xp < xp1; xp++)
            {
                double prx = (xp - Math.floor(xPixel0)) / (Math.floor(xPixel1) - Math.floor(xPixel0));
                int xt = (int) (prx * 16);

                if (depthBuffer[xp + yp * width] > zz)
                {
                    int color = Textures.textures.pixels[xt + tex * 16 + (yt + 16 * 2) * 128];
                    if (color > 0)
                    {
                        pixels[xp + yp * width] = color * col;
                        depthBuffer[xp + yp * width] = zz;
                    }
                }
            }
        }
    }

    public void renderWall(double x0, double y0, double x1, double y1, int tex, int col)
    {
        double xo0 = x0 - 0.5 - xCam;
        double u0 = -0.5 + zCam / 8;
        double d0 = +0.5 + zCam / 8;
        double zo0 = y0 - 0.5 - yCam;

        double xx0 = xo0 * rCos + zo0 * rSin;
        double zz0 = -xo0 * rSin + zo0 * rCos;

        double xo1 = x1 - 0.5 - xCam;
        double u1 = -0.5 + zCam / 8;
        double d1 = +0.5 + zCam / 8;
        double zo1 = y1 - 0.5 - yCam;

        double xx1 = xo1 * rCos + zo1 * rSin;
        double zz1 = -xo1 * rSin + zo1 * rCos;

        double t0 = 0;
        double t1 = 16;

        double clip = 0.1;

        if (zz0 < clip && zz1 < clip)
        {
            return;
        }

        if (zz0 < clip)
        {
            double p = (clip - zz0) / (zz1 - zz0);
            zz0 = zz0 + (zz1 - zz0) * p;
            xx0 = xx0 + (xx1 - xx0) * p;
            t0 = t0 + (t1 - t0) * p;
        }

        if (zz1 < clip)
        {
            double p = (clip - zz1) / (zz1 - zz0);
            zz1 = zz1 + (zz1 - zz0) * p;
            xx1 = xx1 + (xx1 - xx0) * p;
            t1 = t1 + (t1 - t0) * p;
        }

        double xPixel0 = xx0 / zz0 * fov + xCenter;
        double xPixel1 = xx1 / zz1 * fov + xCenter;

        if (xPixel0 > xPixel1)
            return;
        int xp0 = (int) Math.ceil(xPixel0);
        int xp1 = (int) Math.ceil(xPixel1);
        if (xp0 < 0)
            xp0 = 0;
        if (xp1 > width)
            xp1 = width;

        double yPixel00 = (u0 / zz0 * fov + yCenter) - 0.5;
        double yPixel10 = (u1 / zz1 * fov + yCenter) - 0.5;
        double yPixel01 = (d0 / zz0 * fov + yCenter);
        double yPixel11 = (d1 / zz1 * fov + yCenter);

        double iz0 = 1 / zz0;
        double iz1 = 1 / zz1;

        double xt0 = t0 * iz0;
        double xt1 = t1 * iz1;

        for (int x = xp0; x < xp1; x++)
        {
            double p = (x - xPixel0) / (xPixel1 - xPixel0);
            double yPixel0 = yPixel00 + (yPixel10 - yPixel00) * p;
            double yPixel1 = yPixel01 + (yPixel11 - yPixel01) * p;
            double iz = iz0 + (iz1 - iz0) * p;

            if (depthBufferWall[x] > iz)
                continue;
            depthBufferWall[x] = iz;

            int xTex = (int) ((xt0 + (xt1 - xt0) * p) / iz);

            if (yPixel0 > yPixel1)
                return;
            int yp0 = (int) Math.ceil(yPixel0);
            int yp1 = (int) Math.ceil(yPixel1);
            if (yp0 < 0)
                yp0 = 0;
            if (yp1 > height)
                yp1 = height;

            for (int y = yp0; y < yp1; y++)
            {
                double py = (y - yPixel0) / (yPixel1 - yPixel0);
                int yTex = (int) (py * 16);

                depthBuffer[x + y * width] = 8 / iz;
                pixels[x + y * width] = Textures.textures.pixels[(xTex + tex * 16) + (yTex + 0) * Textures.textures.width] * col;
            }
        }
    }

    public void renderFloor(Level level)
    {
        for (int y = 0; y < height; y++)
        {
            double yd = ((y + 0.5) - (yCenter)) / fov;
            double zd = (4 + zCam) / yd;

            if (yd < 0)
            {
                zd = (4 - zCam) / -yd;
            }

            for (int x = 0; x < width; x++)
            {
                double xd = (x - (xCenter)) / fov;
                xd *= zd;

                double xx = xd * rCos - zd * rSin + (xCam + 0.5) * 8;
                double yy = xd * rSin + zd * rCos + (yCam + 0.5) * 8;

                int xPix = (int) xx * 2;
                int yPix = (int) yy * 2;

                if (xx < 0)
                    xPix--;
                if (yy < 0)
                    yPix--;

                int xTile = xPix >> 4;
                int yTile = yPix >> 4;

                Block block = level.getBlock(xTile, yTile);

                int tex = yd > 0 ? block.floorTex : block.ceilTex;
                int col = yd > 0 ? block.floorCol : block.ceilCol;

                depthBuffer[x + y * width] = zd;
                pixels[x + y * width] = Textures.textures.pixels[((xPix & 15) + tex * 16) | (15 - (yPix & 15) + 16) * Textures.textures.width] * col;
            }
        }
    }

    public void renderFog(int shader)
    {
        for (int i = 0; i < depthBuffer.length; i++)
        {
            double t = (i % width - width / 2.0) / (width / 1.4);

            int brightness = (int) (255 - (depthBuffer[i] * 3 * (t * t * 3 + 1.2)));

            int xo = i % width;
            int yo = i / width;

            brightness = brightness + (xo * shader + yo * 2 & 3) * 16 >> 5 << 5;

            if (brightness < 0)
                brightness = 0;
            if (brightness > 255)
                brightness = 255;

            int color = pixels[i];
            int r = (color >> 16) & 0xff;
            int g = (color >> 8) & 0xff;
            int b = (color) & 0xff;
            r = (int) (r / 255.0 * brightness);
            g = (int) (g / 255.0 * brightness);
            b = (int) (b / 255.0 * brightness);

            pixels[i] = r << 16 | g << 8 | b;
        }
    }
}