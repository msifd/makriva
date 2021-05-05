package msifeed.makriva.data;

import msifeed.makriva.expr.IExpr;

import java.util.ArrayList;
import java.util.List;

public class Bone {
    public String id;
    public BipedPart parent;
    public String texture;

//    public int[] textureSize = new int[]{64, 64};
//    public boolean mirrored = false;

    public float[] offset = new float[3];
//    public float[] position = new float[3];

    public float[] rotationPoint = new float[3];
    public IExpr[] rotation = new IExpr[3];

    public List<Box> boxes = new ArrayList<>();
    public List<Bone> bones = new ArrayList<>();
}
