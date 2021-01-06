package net.petercashel.RealmsOfAvalonMod.GUI.VideoRendering;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

public interface IVideoFrameDecoder {


    public int currTextureID = 0;

    int FPSAVG();

    void Setup();

    void StopThread();

    void StartThread();

    public void BindNextFrame();
}
