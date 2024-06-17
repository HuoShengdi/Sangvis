package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import data.scripts.world.systems.Lycoris;

import java.util.List;

public class SFGen implements SectorGeneratorPlugin {
    @Override
    public void generate(SectorAPI sector) {
        initFactionRelationships(sector);
        new Lycoris().generate(Global.getSector());
    }
    
    public static void initFactionRelationships(SectorAPI sector) {
        FactionAPI sangvis = sector.getFaction("sangvis_ferri");
        List<FactionAPI> factionList = sector.getAllFactions();
        factionList.remove(sangvis);
        for (FactionAPI faction : factionList) {
            if (faction != sangvis && !faction.isNeutralFaction())
            {
                sangvis.setRelationship(faction.getId(), RepLevel.HOSTILE);
            }
        }
        sangvis.setRelationship("player", RepLevel.HOSTILE);
    }
}