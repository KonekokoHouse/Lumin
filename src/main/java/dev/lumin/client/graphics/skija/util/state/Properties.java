package dev.lumin.client.graphics.skija.util.state;

public class Properties {
    private int[] lastUnpackSkipImages = new int[1];
    private int[] lastUnpackImageHeight = new int[1];
    private int[] lastPackSkipImages = new int[1];
    private int[] lastPackImageHeight = new int[1];
    private int[] lastUnpackSkipRows = new int[1];
    private int[] lastUnpackSkipPixels = new int[1];
    private int[] lastUnpackRowLength = new int[1];
    private int[] lastUnpackAlignment = new int[1];
    private int[] lastUnpackLsbFirst = new int[1];
    private int[] lastUnpackSwapBytes = new int[1];
    private int[] lastPackAlignment = new int[1];
    private int[] lastPackSkipRows = new int[1];
    private int[] lastPackSkipPixels = new int[1];
    private int[] lastPackRowLength = new int[1];
    private int[] lastPackLsbFirst = new int[1];
    private int[] lastPackSwapBytes = new int[1];
    private int[] lastActiveTexture = new int[1];
    private int[] lastProgram = new int[1];
    private int[] lastTexture = new int[1];
    private int[] lastSampler = new int[1];
    private int[] lastArrayBuffer = new int[1];
    private int[] lastVertexArrayObject = new int[1];
    private int[] lastPolygonMode = new int[2];
    private int[] lastViewport = new int[4];
    private int[] lastScissorBox = new int[4];
    private int[] lastBlendSrcRgb = new int[1];
    private int[] lastBlendDstRgb = new int[1];
    private int[] lastBlendSrcAlpha = new int[1];
    private int[] lastBlendDstAlpha = new int[1];
    private int[] lastBlendEquationRgb = new int[1];
    private int[] lastBlendEquationAlpha = new int[1];
    private int[] lastPixelUnpackBufferBinding = new int[1];
    private boolean lastEnableBlend;
    private boolean lastEnableCullFace;
    private boolean lastEnableDepthTest;
    private boolean lastEnableStencilTest;
    private boolean lastEnableScissorTest;
    private boolean lastEnablePrimitiveRestart;
    private boolean lastDepthMask;

    public int[] getLastUnpackSkipImages() {
        return lastUnpackSkipImages;
    }

    public void setLastUnpackSkipImages(int[] lastUnpackSkipImages) {
        this.lastUnpackSkipImages = lastUnpackSkipImages;
    }

    public int[] getLastUnpackImageHeight() {
        return lastUnpackImageHeight;
    }

    public void setLastUnpackImageHeight(int[] lastUnpackImageHeight) {
        this.lastUnpackImageHeight = lastUnpackImageHeight;
    }

    public int[] getLastPackSkipImages() {
        return lastPackSkipImages;
    }

    public void setLastPackSkipImages(int[] lastPackSkipImages) {
        this.lastPackSkipImages = lastPackSkipImages;
    }

    public int[] getLastPackImageHeight() {
        return lastPackImageHeight;
    }

    public void setLastPackImageHeight(int[] lastPackImageHeight) {
        this.lastPackImageHeight = lastPackImageHeight;
    }

    public int[] getLastUnpackSkipRows() {
        return lastUnpackSkipRows;
    }

    public void setLastUnpackSkipRows(int[] lastUnpackSkipRows) {
        this.lastUnpackSkipRows = lastUnpackSkipRows;
    }

    public int[] getLastUnpackSkipPixels() {
        return lastUnpackSkipPixels;
    }

    public void setLastUnpackSkipPixels(int[] lastUnpackSkipPixels) {
        this.lastUnpackSkipPixels = lastUnpackSkipPixels;
    }

    public int[] getLastUnpackRowLength() {
        return lastUnpackRowLength;
    }

    public void setLastUnpackRowLength(int[] lastUnpackRowLength) {
        this.lastUnpackRowLength = lastUnpackRowLength;
    }

    public int[] getLastUnpackAlignment() {
        return lastUnpackAlignment;
    }

    public void setLastUnpackAlignment(int[] lastUnpackAlignment) {
        this.lastUnpackAlignment = lastUnpackAlignment;
    }

    public int[] getLastUnpackLsbFirst() {
        return lastUnpackLsbFirst;
    }

    public void setLastUnpackLsbFirst(int[] lastUnpackLsbFirst) {
        this.lastUnpackLsbFirst = lastUnpackLsbFirst;
    }

    public int[] getLastUnpackSwapBytes() {
        return lastUnpackSwapBytes;
    }

    public void setLastUnpackSwapBytes(int[] lastUnpackSwapBytes) {
        this.lastUnpackSwapBytes = lastUnpackSwapBytes;
    }

    public int[] getLastPackAlignment() {
        return lastPackAlignment;
    }

    public void setLastPackAlignment(int[] lastPackAlignment) {
        this.lastPackAlignment = lastPackAlignment;
    }

    public int[] getLastPackSkipRows() {
        return lastPackSkipRows;
    }

    public void setLastPackSkipRows(int[] lastPackSkipRows) {
        this.lastPackSkipRows = lastPackSkipRows;
    }

    public int[] getLastPackSkipPixels() {
        return lastPackSkipPixels;
    }

    public void setLastPackSkipPixels(int[] lastPackSkipPixels) {
        this.lastPackSkipPixels = lastPackSkipPixels;
    }

    public int[] getLastPackRowLength() {
        return lastPackRowLength;
    }

    public void setLastPackRowLength(int[] lastPackRowLength) {
        this.lastPackRowLength = lastPackRowLength;
    }

    public int[] getLastPackLsbFirst() {
        return lastPackLsbFirst;
    }

    public void setLastPackLsbFirst(int[] lastPackLsbFirst) {
        this.lastPackLsbFirst = lastPackLsbFirst;
    }

    public int[] getLastPackSwapBytes() {
        return lastPackSwapBytes;
    }

    public void setLastPackSwapBytes(int[] lastPackSwapBytes) {
        this.lastPackSwapBytes = lastPackSwapBytes;
    }

    public int[] getLastActiveTexture() {
        return lastActiveTexture;
    }

    public void setLastActiveTexture(int[] lastActiveTexture) {
        this.lastActiveTexture = lastActiveTexture;
    }

    public int[] getLastProgram() {
        return lastProgram;
    }

    public void setLastProgram(int[] lastProgram) {
        this.lastProgram = lastProgram;
    }

    public int[] getLastTexture() {
        return lastTexture;
    }

    public void setLastTexture(int[] lastTexture) {
        this.lastTexture = lastTexture;
    }

    public int[] getLastSampler() {
        return lastSampler;
    }

    public void setLastSampler(int[] lastSampler) {
        this.lastSampler = lastSampler;
    }

    public int[] getLastArrayBuffer() {
        return lastArrayBuffer;
    }

    public void setLastArrayBuffer(int[] lastArrayBuffer) {
        this.lastArrayBuffer = lastArrayBuffer;
    }

    public int[] getLastVertexArrayObject() {
        return lastVertexArrayObject;
    }

    public void setLastVertexArrayObject(int[] lastVertexArrayObject) {
        this.lastVertexArrayObject = lastVertexArrayObject;
    }

    public int[] getLastPolygonMode() {
        return lastPolygonMode;
    }

    public void setLastPolygonMode(int[] lastPolygonMode) {
        this.lastPolygonMode = lastPolygonMode;
    }

    public int[] getLastViewport() {
        return lastViewport;
    }

    public void setLastViewport(int[] lastViewport) {
        this.lastViewport = lastViewport;
    }

    public int[] getLastScissorBox() {
        return lastScissorBox;
    }

    public void setLastScissorBox(int[] lastScissorBox) {
        this.lastScissorBox = lastScissorBox;
    }

    public int[] getLastBlendSrcRgb() {
        return lastBlendSrcRgb;
    }

    public void setLastBlendSrcRgb(int[] lastBlendSrcRgb) {
        this.lastBlendSrcRgb = lastBlendSrcRgb;
    }

    public int[] getLastBlendDstRgb() {
        return lastBlendDstRgb;
    }

    public void setLastBlendDstRgb(int[] lastBlendDstRgb) {
        this.lastBlendDstRgb = lastBlendDstRgb;
    }

    public int[] getLastBlendSrcAlpha() {
        return lastBlendSrcAlpha;
    }

    public void setLastBlendSrcAlpha(int[] lastBlendSrcAlpha) {
        this.lastBlendSrcAlpha = lastBlendSrcAlpha;
    }

    public int[] getLastBlendDstAlpha() {
        return lastBlendDstAlpha;
    }

    public void setLastBlendDstAlpha(int[] lastBlendDstAlpha) {
        this.lastBlendDstAlpha = lastBlendDstAlpha;
    }

    public int[] getLastBlendEquationRgb() {
        return lastBlendEquationRgb;
    }

    public void setLastBlendEquationRgb(int[] lastBlendEquationRgb) {
        this.lastBlendEquationRgb = lastBlendEquationRgb;
    }

    public int[] getLastBlendEquationAlpha() {
        return lastBlendEquationAlpha;
    }

    public void setLastBlendEquationAlpha(int[] lastBlendEquationAlpha) {
        this.lastBlendEquationAlpha = lastBlendEquationAlpha;
    }

    public int[] getLastPixelUnpackBufferBinding() {
        return lastPixelUnpackBufferBinding;
    }

    public void setLastPixelUnpackBufferBinding(int[] lastPixelUnpackBufferBinding) {
        this.lastPixelUnpackBufferBinding = lastPixelUnpackBufferBinding;
    }

    public boolean isLastEnableBlend() {
        return lastEnableBlend;
    }

    public void setLastEnableBlend(boolean lastEnableBlend) {
        this.lastEnableBlend = lastEnableBlend;
    }

    public boolean isLastEnableCullFace() {
        return lastEnableCullFace;
    }

    public void setLastEnableCullFace(boolean lastEnableCullFace) {
        this.lastEnableCullFace = lastEnableCullFace;
    }

    public boolean isLastEnableDepthTest() {
        return lastEnableDepthTest;
    }

    public void setLastEnableDepthTest(boolean lastEnableDepthTest) {
        this.lastEnableDepthTest = lastEnableDepthTest;
    }

    public boolean isLastEnableStencilTest() {
        return lastEnableStencilTest;
    }

    public void setLastEnableStencilTest(boolean lastEnableStencilTest) {
        this.lastEnableStencilTest = lastEnableStencilTest;
    }

    public boolean isLastEnableScissorTest() {
        return lastEnableScissorTest;
    }

    public void setLastEnableScissorTest(boolean lastEnableScissorTest) {
        this.lastEnableScissorTest = lastEnableScissorTest;
    }

    public boolean isLastEnablePrimitiveRestart() {
        return lastEnablePrimitiveRestart;
    }

    public void setLastEnablePrimitiveRestart(boolean lastEnablePrimitiveRestart) {
        this.lastEnablePrimitiveRestart = lastEnablePrimitiveRestart;
    }

    public boolean isLastDepthMask() {
        return lastDepthMask;
    }

    public void setLastDepthMask(boolean lastDepthMask) {
        this.lastDepthMask = lastDepthMask;
    }
}
