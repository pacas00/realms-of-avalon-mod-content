package net.petercashel.RealmsOfAvalonMod.GUI.VideoRendering;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.lwjgl.BufferUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;

public class VideoFrameDecoder implements IVideoFrameDecoder {

    private File videoFile = null;
    private boolean videoFileExists = false;
    private FrameGrab grab = null;
    private ByteBuffer buffer = null;
    private ByteBuffer activeBuffer = null;
    private Picture picture = null;;
    private BufferedImage image = null;
    public int currTextureID = 0;

    public int videoFPSAVG = 0;
    public boolean fpsLock = true;

    public int FPSAVG() {
        return videoFPSAVG;
    }

    public VideoFrameDecoder(File videoFile, int GLTextureID) {
        this.videoFile = videoFile;
        videoFileExists = videoFile.exists();
        currTextureID = GLTextureID;
    }

    private boolean isSetup = false;
    @Override
    public int Setup(int loadingTexID) {
        if (isSetup) return 0;
        try {
            grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(videoFile));
            picture = grab.getNativeFrame();
            if (picture == null) {
                videoFileExists = false;
            }
            else {
                image = AWTUtil.toBufferedImage(picture);
            }

            //Load and Bind onto new passed in ID
            int[] pixels = new int[image.getWidth() * image.getHeight()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

            int size = image.getWidth() * image.getHeight() * 3;
            if (buffer == null || buffer.capacity() < size) {
                buffer = BufferUtils.createByteBuffer(size); //4 for RGBA, 3 for RGB
            }
            buffer.rewind();

            for(int y = 0; y < image.getHeight(); y++){
                for(int x = 0; x < image.getWidth(); x++){
                    int pixel = pixels[y * image.getWidth() + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                    buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                    buffer.put((byte) (pixel & 0xFF));	            // Blue component
                    //buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
                }
            }

            buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

            glBindTexture(GL_TEXTURE_2D, loadingTexID); //Bind texture ID

            //Setup texture scaling filtering
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, image.getWidth(), image.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, buffer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JCodecException e) {
            e.printStackTrace();
        }

        isSetup = true;
        return loadingTexID;
    }

    //Thread
    private boolean Stop = false;
    Thread videoThread = CreateThread();

    private Thread CreateThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                long average = 1;
                while (!Stop) {
                    if (!Stop)
                    {
                        if (grab == null) {
                            //WHAT
                            try {
                                grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(videoFile));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JCodecException e) {
                                e.printStackTrace();
                            }
                        }

                        long start = System.currentTimeMillis();
                        LoadNextFrame();
                        long timeElapsed = System.currentTimeMillis() - start;
                        average =  ( timeElapsed + average ) / 2;
                        videoFPSAVG = (int) (1000 / average);
                        if (videoFPSAVG > 25 && fpsLock) videoFPSAVG = 25;
                        if (timeElapsed < 40 && fpsLock) {
                            try {
                                if (Stop) break;
                                Thread.sleep(40 - timeElapsed);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (Stop) break;
                }
                System.out.println(average + " was average");
            }
        });
    }


    @Override
    public void StartThread() {
        Stop = false;
        if (videoThread.getState() != Thread.State.NEW && videoThread.getState() != Thread.State.RUNNABLE) {
            if (videoThread != null) {
                Stop = true;
                while (videoThread.getState() != Thread.State.TERMINATED) {
                    //Block and stop the thread
                }
                Stop = false;
            }

            videoThread = CreateThread();
        }

        if (videoThread.getState() == Thread.State.NEW) {
            videoThread.start();
        }

    }

    @Override
    public void StopThread() {
        Stop = true;
    }


    private void LoadNextFrame() {
        if (!Stop && videoFileExists) {
            long start = System.currentTimeMillis();
            long timeElapsed = System.currentTimeMillis() - start;
            try {
                if (null != (picture = grab.getNativeFrame())) {
                    timeElapsed = System.currentTimeMillis() - start;
                    try {
                        AWTUtil.toBufferedImage(picture, image);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        image = AWTUtil.toBufferedImage(picture);
                    }
                    timeElapsed = System.currentTimeMillis() - start;

                    int[] pixels = new int[image.getWidth() * image.getHeight()];
                    image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
                    timeElapsed = System.currentTimeMillis() - start;

                    int size = image.getWidth() * image.getHeight() * 4;
                    if (buffer == null || buffer.capacity() < size) {
                        buffer = BufferUtils.createByteBuffer(size); //4 for RGBA, 3 for RGB
                    }
                    buffer.rewind();
                    timeElapsed = System.currentTimeMillis() - start;

                    for(int y = 0; y < image.getHeight(); y++){
                        for(int x = 0; x < image.getWidth(); x++){
                            int pixel = pixels[y * image.getWidth() + x];
                            buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                            buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                            buffer.put((byte) (pixel & 0xFF));	            // Blue component
                            buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
                        }
                    }
                    timeElapsed = System.currentTimeMillis() - start;

                    buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS
                    timeElapsed = System.currentTimeMillis() - start;

                    synchronized(this) {
                        if (activeBuffer == null || activeBuffer.capacity() < size) {
                            activeBuffer = BufferUtils.createByteBuffer(size); //4 for RGBA, 3 for RGB
                        }
                        //activeBuffer.clear();
                        activeBuffer.rewind();
                        activeBuffer.put(buffer);
                        activeBuffer.flip();
                    }
                    timeElapsed = System.currentTimeMillis() - start;

                } else {
                    grab.seekToFramePrecise(0);
                }
            } catch (IOException | JCodecException e) {
                e.printStackTrace();
                try {
                    grab.seekToFramePrecise(0);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (JCodecException jCodecException) {
                    jCodecException.printStackTrace();
                }
            } catch (NullPointerException npe) {
                try {
                    grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(videoFile));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JCodecException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized boolean BindNextFrame() {
        synchronized(this) {
            // You now have a ByteBuffer filled with the color data of each pixel.
            // Now just create a texture ID and bind it. Then you can load it using
            // whatever OpenGL method you want, for example:

            //int textureID = glGenTextures(); //Generate texture ID
            glBindTexture(GL_TEXTURE_2D, currTextureID); //Bind texture ID

            //Setup texture scaling filtering
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, activeBuffer);

            if (activeBuffer != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void ToggleFPSLock() {
        this.fpsLock = !this.fpsLock;
    }
}
