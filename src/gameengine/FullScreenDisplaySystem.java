package gameengine;

import sage.display.DisplaySystem;
import sage.display.IDisplaySystem;
import sage.renderer.IRenderer;
import sage.renderer.RendererFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Created by Max on 3/11/2015.
 */
public class FullScreenDisplaySystem implements IDisplaySystem {
    private JFrame myFrame;
    private GraphicsDevice device;
    private IRenderer myRenderer;
    private int width, height, bitDepth, refreshRate;
    private Canvas rendererCanvas;
    private boolean isCreated;
    private boolean isFullScreen;

    public FullScreenDisplaySystem(int w, int h, int depth, int rate, boolean isFS, String rName) {
        width = w;
        height = h;
        bitDepth = depth;
        refreshRate = rate;
        this.isFullScreen = isFS;

        myRenderer = RendererFactory.createRenderer(rName);
        if (myRenderer == null) {
            throw new RuntimeException("Unable to find renderer '" + rName + "'");
        }
        rendererCanvas = myRenderer.getCanvas();
        myFrame = new JFrame("Space Race");
        myFrame.add(rendererCanvas);

        initScreen(isFullScreen);

        DisplaySystem.setCurrentDisplaySystem(this);
        myFrame.setVisible(true);
        isCreated = true;
    }

    private void initScreen(boolean fullScreenRequested) {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = environment.getDefaultScreenDevice();
        DisplayMode dispMode = device.getDisplayMode();
        if (device.isFullScreenSupported() && fullScreenRequested) {
            myFrame.setUndecorated(true);
            myFrame.setResizable(false);
            myFrame.setIgnoreRepaint(true);

            device.setFullScreenWindow(myFrame);

            if (dispMode != null && device.isDisplayChangeSupported()) {
                try {
                    device.setDisplayMode(dispMode);
                    myFrame.setSize(dispMode.getWidth(), dispMode.getHeight());
                } catch (Exception ex) {
                    System.err.println("Exception while setting device DisplayMode: " + ex);
                }
            } else {
                System.err.println("Cannot set display mode");
            }
        } else {
            myFrame.setSize(dispMode.getWidth(), dispMode.getHeight());
            myFrame.setLocationRelativeTo(null);
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getBitDepth() {
        return bitDepth;
    }

    @Override
    public void setBitDepth(int bitDepth) {
        this.bitDepth = bitDepth;
    }

    @Override
    public int getRefreshRate() {
        return refreshRate;
    }

    @Override
    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }

    @Override
    public void setTitle(String s) {

    }

    @Override
    public IRenderer getRenderer() {
        return myRenderer;
    }

    @Override
    public void close() {
        if (device != null) {
            Window window = device.getFullScreenWindow();
            if (window != null) {
                window.dispose();
            }
            device.setFullScreenWindow(null);
        }
    }

    @Override
    public boolean isCreated() {
        return isCreated;
    }

    @Override
    public boolean isFullScreen() {
        return isFullScreen;
    }

    @Override
    public void addKeyListener(KeyListener keyListener) {

    }

    @Override
    public void addMouseListener(MouseListener mouseListener) {

    }

    @Override
    public void addMouseMotionListener(MouseMotionListener mouseMotionListener) {

    }

    @Override
    public boolean isShowing() {
        return false;
    }

    @Override
    public void convertPointToScreen(Point point) {

    }

    @Override
    public void setPredefinedCursor(int i) {

    }

    @Override
    public void setCustomCursor(String s) {

    }
}
