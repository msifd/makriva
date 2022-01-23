package msifeed.makriva.model;

import java.util.ArrayList;
import java.util.List;

public class Bone {
    public String id;
    public BipedPart parent;
    public String texture;

    public float[] offset = new float[3];
    public float[] rotationPoint = new float[3];
    public float[] rotation = new float[3];

    public List<Cube> cubes = new ArrayList<>();
    public List<Quad> quads = new ArrayList<>();
    public List<Bone> bones = new ArrayList<>();
}
