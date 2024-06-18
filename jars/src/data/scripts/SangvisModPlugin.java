package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

public class SangvisModPlugin extends BaseModPlugin {
    
    private static void initSangvis() {
        new SFGen().generate(Global.getSector());
        
    }
    
    @Override
    public void onNewGame() {
        initSangvis();
    }
    
}