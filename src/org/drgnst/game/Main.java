package org.drgnst.game;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import org.drgnst.game.gfx.Screen;

/**
 * @author Sopiro
 * <p>
 * 2015. 12. 14. ¿ÀÈÄ 5:31:23
 */
public class Main extends Canvas implements Runnable
{
    private static final long serialVersionUID = 1L;

    public static final int WIDTH = 160;
    public static final int HEIGHT = WIDTH * 3 / 4;
    public static final int SCALE = 4;
    public static final String TITLE = "Perspective";
    public static final double FRAME_LIMIT = 60.0;

    private boolean isRunning = false;

    public final BufferedImage image;
    public final int[] pixels;

    private Game game;
    private Screen screen;
    private InputHandler inputHandler;

    public Main()
    {
        Dimension d = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        inputHandler = new InputHandler();

        addKeyListener(inputHandler);
        addMouseListener(inputHandler);
        addFocusListener(inputHandler);
        addMouseMotionListener(inputHandler);
        addMouseWheelListener(inputHandler);
    }

    public void start()
    {
        if (isRunning)
            return;

        isRunning = true;

        init();
        new Thread(this).start();
    }

    public void init()
    {
        game = new Game();
        screen = new Screen(WIDTH, HEIGHT);
    }

    public void run()
    {
        final double nsPerUpdate = 1000000000.0 / FRAME_LIMIT;

        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        int frames = 0;
        int updates = 0;

        long frameCounter = System.currentTimeMillis();

        while (isRunning)
        {
            long currentTime = System.nanoTime();
            long passedTime = currentTime - lastTime;
            lastTime = currentTime;
            unprocessedTime += passedTime;

            if (unprocessedTime >= nsPerUpdate)
            {
                unprocessedTime = 0;
                update();
                updates++;
            }

            render();
            frames++;

            if (System.currentTimeMillis() - frameCounter >= 1000)
            {
                // System.out.println("Frames : " + frames + ", Updates :" +
                // updates);
                frames = 0;
                updates = 0;
                frameCounter += 1000;
            }
        }

        dispose();
    }

    public void render()
    {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null)
        {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        for (int i = 0; i < pixels.length; i++)
        {
            pixels[i] = 0;
        }

        screen.render(game);

        for (int i = 0; i < pixels.length; i++)
        {
            pixels[i] = screen.pixels[i];
        }

        g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);

        g.dispose();
        bs.show();
    }

    public void update()
    {
        game.update(inputHandler.keys);
        screen.update();
    }

    public void stop()
    {
        if (!isRunning)
            return;

        isRunning = false;
    }

    public void dispose()
    {
        System.exit(0);
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        frame.setTitle(TITLE);
        frame.setResizable(false);
        Main game = new Main();
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);

        game.start();
    }
}