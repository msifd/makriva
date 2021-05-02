package msifeed.makriva.model;

import net.minecraft.client.model.ModelRenderer;

import java.util.ArrayList;
import java.util.List;

public class ModelBone {
    public transient String id;
    public transient List<ModelRenderer> cubes = new ArrayList<>();

    public void compile() {
        cubes.clear();

//        for (Box box : boxes) {
//            final ModelRenderer model = new ModelRenderer();
//        }
    }
}
