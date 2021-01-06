package net.petercashel.RealmsOfAvalonMod.GUI.VideoRendering;

public interface IVideoFrameDecoder {


    public int currTextureID = 0;

    int FPSAVG();

    int Setup(int loadingTexID);

    void StopThread();

    void StartThread();

    public boolean BindNextFrame();

    public void ToggleFPSLock();
}
