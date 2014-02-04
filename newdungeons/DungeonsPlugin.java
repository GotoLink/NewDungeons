package newdungeons;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.io.IOException;
import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions(value = {"newdungeons"})
@IFMLLoadingPlugin.Name(value = "NewDungeonsPlugin")
public class DungeonsPlugin extends AccessTransformer implements IFMLLoadingPlugin {
    public DungeonsPlugin() throws IOException {
        super("newdungeons_at.cfg");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return "newdungeons.DungeonsPlugin";
    }
}
