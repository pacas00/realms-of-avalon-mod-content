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

    public VideoFrameDecoder(File videoFile, int GLTextureID) {
        this.videoFile = videoFile;
        videoFileExists = videoFile.exists();
        currTextureID = GLTextureID;
    }

    private boolean isSetup = false;
    @Override
    public void Setup() {
        if (isSetup) return;
        try {
            grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(videoFile));
            picture = grab.getNativeFrame();
            if (picture == null) {
                videoFileExists = false;
            }
            else {
                image = AWTUtil.toBufferedImage(picture);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JCodecException e) {
            e.printStackTrace();
        }

        LoadNextFrame();
        BindNextFrame();
        isSetup = true;
    }

    //Thread
    private boolean Stop = false;
    Thread videoThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!Stop) {
                if (!Stop)
                {
                    long start = System.currentTimeMillis();
                    LoadNextFrame();
                    long timeElapsed = System.currentTimeMillis() - start;
                    if (timeElapsed < 10) {
                        try {
                            if (Stop) break;
                            Thread.sleep(100 - timeElapsed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (Stop) break;
            }
        }
    });


    @Override
    public void StartThread() {
        Stop = false;
        videoThread.start();
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
                    grab.seekToSecondSloppy(0.0d);
                    grab.seekToFramePrecise(0);
                }
            } catch (IOException | JCodecException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void BindNextFrame() {
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
        }
    }
}
