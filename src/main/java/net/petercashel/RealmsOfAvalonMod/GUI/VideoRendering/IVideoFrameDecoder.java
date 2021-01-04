package net.petercashel.RealmsOfAvalonMod.GUI.VideoRendering;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

public interface IVideoFrameDecoder {

    File videoFile = null;
    FrameGrab grab = null;
    int videoFrameCurrent = 0;
    ByteBuffer buffer = null;
    Picture picture = null;
    BufferedImage image = null;
    public int currTextureID = 0;

    void Setup();

    void StopThread();

    void StartThread();

    public void BindNextFrame();
}
