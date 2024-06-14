package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;


public class Lycoris {
    public void generate(SectorAPI sector) {
        
        StarSystemAPI system = sector.createStarSystem("Lycoris");
        
        system.setBackgroundTextureFilename("graphics/backgrounds/background5.jpg");
        
        //create star
        PlanetAPI lycoris_star = system.initStar("lycoris", //unique id for star
                "star_red_dwarf", // id in planets.json
                320f, // star radius
                170, // corona radius
                4f, // solar wind burn level
                0.25f, // flare probability
                1.0f); // cr loss mult
        system.setLightColor(new Color(245,215,200));
        
        //add system tags
        system.addTag(Tags.THEME_HIDDEN);
        
        //add gate
        SectorEntityToken gate = system.addCustomEntity("lycoris_gate", // unique id
                "Lycoris Gate", // display name
                "inactive_gate", // type of object
                null); // faction owner
        
        gate.setCircularOrbit(system.getEntityById("lycoris"), // focus
                360, // angle
                1500f, // orbit radius
                150f); // orbit period (days)
        
        //inner jump point opposite gate
        JumpPointAPI innerJumpPoint = Global.getFactory().createJumpPoint("lycoris_inner_jump_point", "Lycoris Inner Jump Point");
//        OrbitAPI innerJumpPointOrbit = Global.getFactory().createCircularOrbit(lycoris_star, 180, 1500f, 150f);
//        innerJumpPoint.setOrbit(innerJumpPointOrbit);
        innerJumpPoint.setStandardWormholeToHyperspaceVisual();
        innerJumpPoint.setCircularOrbit(system.getEntityById("lycoris"), 180, 1500f, 150f);
        system.addEntity(innerJumpPoint);
        
        system.addAsteroidBelt(lycoris_star, // focus
                61, // numAsteroids
                2800, // orbit radius
                240, // width
                200, // minOrbitDays
                220,
                Terrain.ASTEROID_BELT,
                "Lycoris "); // maxOrbitDays
        
        //add comm relay
        SectorEntityToken relay = system.addCustomEntity("lycoris_relay",
                "Lycoris Relay",
                "comm_relay",
                null);

        relay.setCircularOrbitPointingDown(system.getEntityById("lycoris"), // focus
                300, // angle
                6400f, // orbit radius
                360f); // orbit period (days)
        
        //add sensor array
        SectorEntityToken sensors = system.addCustomEntity("lycoris_sensor_array",
                "Lycoris Sensor Array",
                "sensor_array",
                null);

        sensors.setCircularOrbitPointingDown(system.getEntityById("lycoris"), // focus
                180, // angle
                6400f, // orbit radius
                360f); // orbit period (days)
        
        //add nav buoy
        SectorEntityToken navBuoy = system.addCustomEntity("lycoris_nav_buoy",
                "Lycoris Nav Buoy",
                "nav_buoy",
                null);
        
        navBuoy.setCircularOrbitPointingDown(system.getEntityById("lycoris"),
                50,
                3200f,
                250f);
        
        //add home planet
        PlanetAPI lunasia = system.addPlanet("sf_planet_lunasia", lycoris_star, "Lunasia", "tundra", 240, 120, 6400, 360f);
        lunasia.getSpec().setCloudRotation(25f);
        lunasia.applySpecChanges();
        //lunasia.setCustomDescriptionId("planet_lunasia");
        
        //Add conditions to Lunasia
        Misc.initConditionMarket(lunasia);
        lunasia.getMarket().addCondition(Conditions.HABITABLE);
        lunasia.getMarket().addCondition(Conditions.COLD);
        lunasia.getMarket().addCondition(Conditions.FARMLAND_ADEQUATE);
        lunasia.getMarket().addCondition(Conditions.ORE_MODERATE);
        lunasia.getMarket().addCondition(Conditions.RARE_ORE_MODERATE);
        lunasia.getMarket().addCondition(Conditions.INIMICAL_BIOSPHERE);
        lunasia.getMarket().addCondition(Conditions.ROGUE_AI_CORE);
        lunasia.getMarket().addCondition(Conditions.DECIVILIZED);
        lunasia.getMarket().addCondition(Conditions.RUINS_VAST);
        
        float radiusAfter = StarSystemGenerator.addOrbitingEntities(system, lycoris_star, StarAge.OLD, 0, 1, 7500, 1, true);
        
        system.autogenerateHyperspaceJumpPoints(true, true);

        //Clear local hyperspace clouds
        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;

        float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f, 0.25f);
    }
}