package org.drgnst.game.gfx;

/**
 * @author Sopiro
 * <p>
 * 2015. 12. 14. ¿ÀÈÄ 5:31:57
 */
public class Sprite
{
    public final double x, y, z;
    public int tex = 0;
    public int col = 0x444444;

    public Sprite(double x, double y, double z, int tex, int col)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.tex = tex;
        this.col = col;
    }
}
