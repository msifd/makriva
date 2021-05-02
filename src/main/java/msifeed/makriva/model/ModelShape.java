package msifeed.makriva.model;

public class ModelShape {
    public transient boolean compiled = false;

    public void compile() {
//        for (ModelBone bone : bones.values()) {
//            bone.compile();
//        }

        compiled = true;
    }
}
