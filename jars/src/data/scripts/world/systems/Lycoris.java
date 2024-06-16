package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
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

        system.addRingBand(lycoris_star, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 2800, 210f);
        system.addAsteroidBelt(lycoris_star, // focus
                161, // numAsteroids
                2800, // orbit radius
                240, // width
                200, // minOrbitDays
                220,
                Terrain.ASTEROID_BELT,
                "Lycoris Inner Band"); // maxOrbitDays
        
        //add comm relay
        SectorEntityToken relay = system.addCustomEntity("lycoris_relay",
                "Lycoris Relay",
                "comm_relay",
                null);

        relay.setCircularOrbitPointingDown(system.getEntityById("lycoris"), // focus
                300, // angle
                4200f, // orbit radius
                280f); // orbit period (days)
        
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
//        PlanetAPI beilan = system.addPlanet("sf_planet_beilan", lycoris_star, "Beilan", "tundra", 240, 120, 6400, 360f);
//        beilan.getSpec().setCloudRotation(25f);
//        beilan.applySpecChanges();
        //beilan.setCustomDescriptionId("planet_beilan");
        
        //Add conditions to Beilan
//        Misc.initConditionMarket(beilan);
//        beilan.getMarket().addCondition(Conditions.HABITABLE);
//        beilan.getMarket().addCondition(Conditions.COLD);
//        beilan.getMarket().addCondition(Conditions.FARMLAND_ADEQUATE);
//        beilan.getMarket().addCondition(Conditions.ORE_MODERATE);
//        beilan.getMarket().addCondition(Conditions.INIMICAL_BIOSPHERE);
//        beilan.getMarket().addCondition(Conditions.ROGUE_AI_CORE);
//        beilan.getMarket().addCondition(Conditions.DECIVILIZED);
//        beilan.getMarket().addCondition(Conditions.RUINS_VAST);
        
        //outer dust belt & asteroid field
        system.addRingBand(lycoris_star, "misc", "rings_dust0", 256f, 3, Color.white, 256f, 7200, 400f);
        system.addAsteroidBelt(lycoris_star, 120, 7200,300,380,420, Terrain.ASTEROID_BELT, "Lycoris Outer Band");
        
        //station hidden in belt
        SectorEntityToken butterflyStation = system.addCustomEntity("lycoris_butterfly_station","Sangvis Base 0","station_side07","neutral");
        butterflyStation.setCircularOrbitWithSpin(system.getEntityById("lycoris"), 360*(float)Math.random(), 7200, 400f, 9, 21);
        butterflyStation.setDiscoverable(true);
        butterflyStation.setDiscoveryXP(3000f);
        butterflyStation.setSensorProfile(0.5f);
        
        //abandoned station marketplace
        Misc.setAbandonedStationMarket("abandoned_factory_market", butterflyStation);
        butterflyStation.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addSpecial(new SpecialItemData("sf_core_bp",null), 1f);
        butterflyStation.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addHullmods("sf_neural_cloud", 1);
        
        butterflyStation.setCustomDescriptionId("station_butterfly");
        
        float radiusAfter = StarSystemGenerator.addOrbitingEntities(system, lycoris_star, StarAge.OLD, 1, 3, 8000, 1, true);
        
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