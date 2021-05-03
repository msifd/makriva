package msifeed.makriva.data;

import java.util.ArrayList;
import java.util.List;

public class Bone {
    public String parent;
    public List<Box> boxes = new ArrayList<>();

    public float[] position = new float[3];
    public float[] rotation = new float[3];
}
