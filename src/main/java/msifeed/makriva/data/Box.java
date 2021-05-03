package msifeed.makriva.data;

public class Box {
    public int[] uv = new int[2];
    public float[] pos = new float[3];
    public int[] size = new int[3];

    public float[] offset = new float[3];
    public float[] rotationPoint = new float[3];

    public float delta = 1;
    public boolean mirrored = false;
}
