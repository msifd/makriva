package msifeed.makriva.mixins;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityMixin {
    @Invoker("setSize")
    void callSetSize(float width, float height);
}
