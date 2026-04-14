import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.sound.sampled.*;
import java.io.File;
import javax.imageio.ImageIO;

public class PokemonGame extends JFrame implements ActionListener {

    enum Mode { AI, PVP }
    Mode mode = Mode.AI;
    int teamSize = 3;
    boolean battleStarted = false, showPlayerSprite = false, showEnemySprite = false;
    Random r = new Random();
    String currentCard = "menu";

    // Classic Game Boy Colors
    Color GB_DARK = new Color(15, 56, 15);
    Color GB_WHITE = new Color(224, 248, 208);
    Image menuBg;
    BufferedImage iconSheet;
    Clip bgm;

    // Animation variables
    float pScale = 0f, eScale = 0f;
    boolean pFlash = false, eFlash = false;

    enum Type { FIRE, WATER, GRASS, ELECTRIC, NORMAL, POISON, FLYING, PSYCHIC, BUG, GHOST, GROUND, ROCK, DRAGON }

    class Move {
        String name; int dmg; Type type;
        Move(String n, int d, Type t) { name = n; dmg = d; type = t; }
    }

    class Pokemon {
        String name; int maxHP, hp; Type type; Image front, back, icon; Move[] moves;
        Pokemon(String n, int hp, Type t, String b, String f, String i, Move[] m) {
            name = n; maxHP = hp; this.hp = hp; type = t;
            back = load(b); front = load(f); moves = m; icon = load(i); 
        }
        Pokemon(Pokemon p) {
            name = p.name; maxHP = p.maxHP; hp = maxHP; type = p.type;
            front = p.front; back = p.back; moves = p.moves;
        }
        
    }

    Pokemon[] base = {
    new Pokemon("Bulbasaur", 100, Type.GRASS, "images/sprites/back_001.png", "images/sprites/front_001.png", "images/icons/001.png", new Move[]{new Move("Vine Whip", 20, Type.GRASS), new Move("Tackle", 18, Type.NORMAL), new Move("Growl", 0, Type.NORMAL), new Move("Leech Seed", 15, Type.GRASS)}),
    new Pokemon("Ivysaur", 120, Type.GRASS, "images/sprites/back_002.png", "images/sprites/front_002.png", "images/icons/002.png", new Move[]{new Move("Razor Leaf", 25, Type.GRASS), new Move("Tackle", 18, Type.NORMAL), new Move("Growl", 0, Type.NORMAL), new Move("Leech Seed", 15, Type.GRASS)}),
    new Pokemon("Venusaur", 160, Type.GRASS, "images/sprites/back_003.png", "images/sprites/front_003.png", "images/icons/003.png", new Move[]{new Move("Solar Beam", 45, Type.GRASS), new Move("Earthquake", 35, Type.GROUND), new Move("Razor Leaf", 30, Type.GRASS), new Move("Body Slam", 25, Type.NORMAL)}),
    new Pokemon("Charmander", 95, Type.FIRE, "images/sprites/back_004.png", "images/sprites/front_004.png", "images/icons/004.png", new Move[]{new Move("Ember", 20, Type.FIRE), new Move("Scratch", 15, Type.NORMAL), new Move("Growl", 0, Type.NORMAL), new Move("Smokescreen", 0, Type.NORMAL)}),
    new Pokemon("Charmeleon", 115, Type.FIRE, "images/sprites/back_005.png", "images/sprites/front_005.png", "images/icons/005.png", new Move[]{new Move("Flamethrower", 30, Type.FIRE), new Move("Slash", 25, Type.NORMAL), new Move("Dragon Rage", 25, Type.FIRE), new Move("Fire Spin", 20, Type.FIRE)}),
    new Pokemon("Charizard", 155, Type.FIRE, "images/sprites/back_006.png", "images/sprites/front_006.png", "images/icons/006.png", new Move[]{new Move("Fire Blast", 45, Type.FIRE), new Move("Fly", 35, Type.FLYING), new Move("Flamethrower", 35, Type.FIRE), new Move("Dragon Claw", 30, Type.FIRE)}),
    new Pokemon("Squirtle", 105, Type.WATER, "images/sprites/back_007.png", "images/sprites/front_007.png", "images/icons/007.png", new Move[]{new Move("Water Gun", 20, Type.WATER), new Move("Bubble", 15, Type.WATER), new Move("Withdraw", 0, Type.NORMAL), new Move("Bite", 20, Type.NORMAL)}),
    new Pokemon("Wartortle", 125, Type.WATER, "images/sprites/back_008.png", "images/sprites/front_008.png", "images/icons/008.png", new Move[]{new Move("Water Pulse", 25, Type.WATER), new Move("Bite", 25, Type.NORMAL), new Move("Protect", 0, Type.NORMAL), new Move("Rapid Spin", 20, Type.NORMAL)}),
    new Pokemon("Blastoise", 165, Type.WATER, "images/sprites/back_009.png", "images/sprites/front_009.png", "images/icons/009.png", new Move[]{new Move("Hydro Pump", 45, Type.WATER), new Move("Skull Bash", 40, Type.NORMAL), new Move("Surf", 35, Type.WATER), new Move("Ice Beam", 35, Type.WATER)}),
    new Pokemon("Caterpie", 85, Type.BUG, "images/sprites/back_010.png", "images/sprites/front_010.png", "images/icons/010.png", new Move[]{new Move("Tackle", 10, Type.NORMAL), new Move("String Shot", 0, Type.NORMAL), new Move("Bug Bite", 15, Type.BUG), new Move("Struggle", 10, Type.NORMAL)}),
    new Pokemon("Metapod", 95, Type.BUG, "images/sprites/back_011.png", "images/sprites/front_011.png", "images/icons/011.png", new Move[]{new Move("Harden", 0, Type.NORMAL), new Move("Tackle", 10, Type.NORMAL), new Move("Bug Bite", 15, Type.BUG), new Move("Struggle", 10, Type.NORMAL)}),
    new Pokemon("Butterfree", 130, Type.BUG, "images/sprites/back_012.png", "images/sprites/front_012.png", "images/icons/012.png", new Move[]{new Move("Confusion", 25, Type.PSYCHIC), new Move("Psybeam", 30, Type.PSYCHIC), new Move("Gust", 25, Type.FLYING), new Move("Sleep Powder", 0, Type.NORMAL)}),
    new Pokemon("Weedle", 85, Type.BUG, "images/sprites/back_013.png", "images/sprites/front_013.png", "images/icons/013.png", new Move[]{new Move("Poison Sting", 15, Type.POISON), new Move("String Shot", 0, Type.NORMAL), new Move("Bug Bite", 15, Type.BUG), new Move("Struggle", 10, Type.NORMAL)}),
    new Pokemon("Kakuna", 95, Type.BUG, "images/sprites/back_014.png", "images/sprites/front_014.png", "images/icons/014.png", new Move[]{new Move("Harden", 0, Type.NORMAL), new Move("Poison Sting", 15, Type.POISON), new Move("Bug Bite", 15, Type.BUG), new Move("Struggle", 10, Type.NORMAL)}),
    new Pokemon("Beedrill", 130, Type.BUG, "images/sprites/back_015.png", "images/sprites/front_015.png", "images/icons/015.png", new Move[]{new Move("Twineedle", 30, Type.BUG), new Move("Poison Jab", 35, Type.POISON), new Move("Fury Attack", 20, Type.NORMAL), new Move("Pin Missile", 25, Type.BUG)}),
    new Pokemon("Pidgey", 90, Type.FLYING, "images/sprites/back_016.png", "images/sprites/front_016.png", "images/icons/016.png", new Move[]{new Move("Gust", 15, Type.FLYING), new Move("Quick Attack", 15, Type.NORMAL), new Move("Sand Attack", 0, Type.NORMAL), new Move("Tackle", 10, Type.NORMAL)}),
    new Pokemon("Pidgeotto", 120, Type.FLYING, "images/sprites/back_017.png", "images/sprites/front_017.png", "images/icons/017.png", new Move[]{new Move("Wing Attack", 25, Type.FLYING), new Move("Gust", 20, Type.FLYING), new Move("Quick Attack", 15, Type.NORMAL), new Move("Sand Attack", 0, Type.NORMAL)}),
    new Pokemon("Pidgeot", 150, Type.FLYING, "images/sprites/back_018.png", "images/sprites/front_018.png", "images/icons/018.png", new Move[]{new Move("Sky Attack", 45, Type.FLYING), new Move("Wing Attack", 35, Type.FLYING), new Move("Steel Wing", 30, Type.NORMAL), new Move("Mirror Move", 0, Type.FLYING)}),
    new Pokemon("Rattata", 80, Type.NORMAL, "images/sprites/back_019.png", "images/sprites/front_019.png", "images/icons/019.png", new Move[]{new Move("Quick Attack", 15, Type.NORMAL), new Move("Hyper Fang", 25, Type.NORMAL), new Move("Bite", 15, Type.NORMAL), new Move("Tail Whip", 0, Type.NORMAL)}),
    new Pokemon("Raticate", 120, Type.NORMAL, "images/sprites/back_020.png", "images/sprites/front_020.png", "images/icons/020.png", new Move[]{new Move("Super Fang", 40, Type.NORMAL), new Move("Hyper Fang", 35, Type.NORMAL), new Move("Quick Attack", 20, Type.NORMAL), new Move("Crunch", 30, Type.NORMAL)}),
    new Pokemon("Spearow", 85, Type.FLYING, "images/sprites/back_021.png", "images/sprites/front_021.png", "images/icons/021.png", new Move[]{new Move("Peck", 15, Type.FLYING), new Move("Fury Attack", 20, Type.NORMAL), new Move("Growl", 0, Type.NORMAL), new Move("Leer", 0, Type.NORMAL)}),
    new Pokemon("Fearow", 140, Type.FLYING, "images/sprites/back_022.png", "images/sprites/front_022.png", "images/icons/022.png", new Move[]{new Move("Drill Peck", 35, Type.FLYING), new Move("Sky Attack", 45, Type.FLYING), new Move("Mirror Move", 0, Type.FLYING), new Move("Agility", 0, Type.NORMAL)}),
    new Pokemon("Ekans", 95, Type.POISON, "images/sprites/back_023.png", "images/sprites/front_023.png", "images/icons/023.png", new Move[]{new Move("Poison Sting", 15, Type.POISON), new Move("Wrap", 10, Type.NORMAL), new Move("Bite", 15, Type.NORMAL), new Move("Glare", 0, Type.NORMAL)}),
    new Pokemon("Arbok", 145, Type.POISON, "images/sprites/back_024.png", "images/sprites/front_024.png", "images/icons/024.png", new Move[]{new Move("Sludge Bomb", 35, Type.POISON), new Move("Crunch", 30, Type.NORMAL), new Move("Acid", 25, Type.POISON), new Move("Glare", 0, Type.NORMAL)}),
    new Pokemon("Pikachu", 100, Type.ELECTRIC, "images/sprites/back_025.png", "images/sprites/front_025.png", "images/icons/025.png", new Move[]{new Move("Thunderbolt", 40, Type.ELECTRIC), new Move("Quick Attack", 20, Type.NORMAL), new Move("Thunder Wave", 0, Type.ELECTRIC), new Move("Iron Tail", 35, Type.NORMAL)}),
    new Pokemon("Raichu", 140, Type.ELECTRIC, "images/sprites/back_026.png", "images/sprites/front_026.png", "images/icons/026.png", new Move[]{new Move("Thunder", 45, Type.ELECTRIC), new Move("Thunderbolt", 35, Type.ELECTRIC), new Move("Quick Attack", 20, Type.NORMAL), new Move("Iron Tail", 30, Type.NORMAL)}),
    new Pokemon("Sandshrew", 100, Type.GROUND, "images/sprites/back_027.png", "images/sprites/front_027.png", "images/icons/027.png", new Move[]{new Move("Slash", 25, Type.NORMAL), new Move("Dig", 30, Type.GROUND), new Move("Poison Sting", 15, Type.POISON), new Move("Defense Curl", 0, Type.NORMAL)}),
    new Pokemon("Sandslash", 145, Type.GROUND, "images/sprites/back_028.png", "images/sprites/front_028.png", "images/icons/028.png", new Move[]{new Move("Earthquake", 40, Type.GROUND), new Move("Slash", 35, Type.NORMAL), new Move("Poison Sting", 20, Type.POISON), new Move("Swift", 25, Type.NORMAL)}),
    new Pokemon("Nidoran F", 105, Type.POISON, "images/sprites/back_029.png", "images/sprites/front_029.png", "images/icons/029.png", new Move[]{new Move("Scratch", 15, Type.NORMAL), new Move("Poison Sting", 20, Type.POISON), new Move("Bite", 20, Type.NORMAL), new Move("Double Kick", 25, Type.NORMAL)}),
    new Pokemon("Nidorina", 130, Type.POISON, "images/sprites/back_030.png", "images/sprites/front_030.png", "images/icons/030.png", new Move[]{new Move("Poison Fang", 30, Type.POISON), new Move("Bite", 25, Type.NORMAL), new Move("Slash", 30, Type.NORMAL), new Move("Tail Whip", 0, Type.NORMAL)}),
    new Pokemon("Nidoqueen", 175, Type.POISON, "images/sprites/back_031.png", "images/sprites/front_031.png", "images/icons/031.png", new Move[]{new Move("Body Slam", 35, Type.NORMAL), new Move("Earthquake", 45, Type.GROUND), new Move("Sludge Bomb", 40, Type.POISON), new Move("Superpower", 45, Type.NORMAL)}),
    new Pokemon("Nidoran M", 100, Type.POISON, "images/sprites/back_032.png", "images/sprites/front_032.png", "images/icons/032.png", new Move[]{new Move("Peck", 15, Type.FLYING), new Move("Poison Sting", 20, Type.POISON), new Move("Horn Attack", 25, Type.NORMAL), new Move("Double Kick", 25, Type.NORMAL)}),
    new Pokemon("Nidorino", 125, Type.POISON, "images/sprites/back_033.png", "images/sprites/front_033.png", "images/icons/033.png", new Move[]{new Move("Horn Attack", 30, Type.NORMAL), new Move("Poison Jab", 35, Type.POISON), new Move("Fury Attack", 25, Type.NORMAL), new Move("Leer", 0, Type.NORMAL)}),
    new Pokemon("Nidoking", 170, Type.POISON, "images/sprites/back_034.png", "images/sprites/front_034.png", "images/icons/034.png", new Move[]{new Move("Thrash", 45, Type.NORMAL), new Move("Earthquake", 45, Type.GROUND), new Move("Megahorn", 45, Type.BUG), new Move("Sludge Wave", 40, Type.POISON)}),
    new Pokemon("Clefairy", 110, Type.NORMAL, "images/sprites/back_035.png", "images/sprites/front_035.png", "images/icons/035.png", new Move[]{new Move("Pound", 15, Type.NORMAL), new Move("Sing", 0, Type.NORMAL), new Move("Double Slap", 20, Type.NORMAL), new Move("Metronome", 25, Type.NORMAL)}),
    new Pokemon("Clefable", 150, Type.NORMAL, "images/sprites/back_036.png", "images/sprites/front_036.png", "images/icons/036.png", new Move[]{new Move("Moonblast", 40, Type.NORMAL), new Move("Body Slam", 35, Type.NORMAL), new Move("Psychic", 35, Type.PSYCHIC), new Move("Meteor Mash", 45, Type.NORMAL)}),
    new Pokemon("Vulpix", 90, Type.FIRE, "images/sprites/back_037.png", "images/sprites/front_037.png", "images/icons/037.png", new Move[]{new Move("Ember", 20, Type.FIRE), new Move("Quick Attack", 20, Type.NORMAL), new Move("Confuse Ray", 0, Type.NORMAL), new Move("Flamethrower", 35, Type.FIRE)}),
    new Pokemon("Ninetales", 145, Type.FIRE, "images/sprites/back_038.png", "images/sprites/front_038.png", "images/icons/038.png", new Move[]{new Move("Fire Blast", 45, Type.FIRE), new Move("Flamethrower", 35, Type.FIRE), new Move("Will-O-Wisp", 0, Type.FIRE), new Move("Extrasensory", 30, Type.PSYCHIC)}),
    new Pokemon("Jigglypuff", 140, Type.NORMAL, "images/sprites/back_039.png", "images/sprites/front_039.png", "images/icons/039.png", new Move[]{new Move("Sing", 0, Type.NORMAL), new Move("Pound", 15, Type.NORMAL), new Move("Double Slap", 20, Type.NORMAL), new Move("Body Slam", 30, Type.NORMAL)}),
    new Pokemon("Wigglytuff", 180, Type.NORMAL, "images/sprites/back_040.png", "images/sprites/front_040.png", "images/icons/040.png", new Move[]{new Move("Hyper Voice", 40, Type.NORMAL), new Move("Double-Edge", 45, Type.NORMAL), new Move("Play Rough", 35, Type.NORMAL), new Move("Body Slam", 35, Type.NORMAL)}),
    new Pokemon("Zubat", 85, Type.POISON, "images/sprites/back_041.png", "images/sprites/front_041.png", "images/icons/041.png", new Move[]{new Move("Leech Life", 15, Type.BUG), new Move("Bite", 20, Type.NORMAL), new Move("Wing Attack", 25, Type.FLYING), new Move("Confuse Ray", 0, Type.NORMAL)}),
    new Pokemon("Golbat", 135, Type.POISON, "images/sprites/back_042.png", "images/sprites/front_042.png", "images/icons/042.png", new Move[]{new Move("Air Cutter", 30, Type.FLYING), new Move("Bite", 25, Type.NORMAL), new Move("Poison Fang", 30, Type.POISON), new Move("Confuse Ray", 0, Type.NORMAL)}),
    new Pokemon("Oddish", 95, Type.GRASS, "images/sprites/back_043.png", "images/sprites/front_043.png", "images/icons/043.png", new Move[]{new Move("Absorb", 15, Type.GRASS), new Move("Acid", 20, Type.POISON), new Move("Stun Spore", 0, Type.GRASS), new Move("Razor Leaf", 25, Type.GRASS)}),
    new Pokemon("Gloom", 120, Type.GRASS, "images/sprites/back_044.png", "images/sprites/front_044.png", "images/icons/044.png", new Move[]{new Move("Acid", 25, Type.POISON), new Move("Razor Leaf", 30, Type.GRASS), new Move("Petal Dance", 35, Type.GRASS), new Move("Sleep Powder", 0, Type.GRASS)}),
    new Pokemon("Vileplume", 155, Type.GRASS, "images/sprites/back_045.png", "images/sprites/front_045.png", "images/icons/045.png", new Move[]{new Move("Solar Beam", 45, Type.GRASS), new Move("Sludge Bomb", 40, Type.POISON), new Move("Petal Blizzard", 40, Type.GRASS), new Move("Stun Spore", 0, Type.GRASS)}),
    new Pokemon("Meowth", 90, Type.NORMAL, "images/sprites/back_052.png", "images/sprites/front_052.png", "images/icons/052.png", new Move[]{new Move("Pay Day", 20, Type.NORMAL), new Move("Bite", 20, Type.NORMAL), new Move("Fury Swipes", 25, Type.NORMAL), new Move("Scratch", 15, Type.NORMAL)}),
    new Pokemon("Persian", 130, Type.NORMAL, "images/sprites/back_053.png", "images/sprites/front_053.png", "images/icons/053.png", new Move[]{new Move("Slash", 35, Type.NORMAL), new Move("Crunch", 35, Type.NORMAL), new Move("Play Rough", 35, Type.NORMAL), new Move("Power Gem", 30, Type.ROCK)}),
    new Pokemon("Growlithe", 105, Type.FIRE, "images/sprites/back_058.png", "images/sprites/front_058.png", "images/icons/058.png", new Move[]{new Move("Ember", 20, Type.FIRE), new Move("Bite", 20, Type.NORMAL), new Move("Flame Wheel", 30, Type.FIRE), new Move("Take Down", 35, Type.NORMAL)}),
    new Pokemon("Arcanine", 170, Type.FIRE, "images/sprites/back_059.png", "images/sprites/front_059.png", "images/icons/059.png", new Move[]{new Move("Fire Blast", 45, Type.FIRE), new Move("Extreme Speed", 40, Type.NORMAL), new Move("Flare Blitz", 45, Type.FIRE), new Move("Thunder Fang", 30, Type.ELECTRIC)}),
    new Pokemon("Alakazam", 145, Type.PSYCHIC, "images/sprites/back_065.png", "images/sprites/front_065.png", "images/icons/065.png", new Move[]{new Move("Psychic", 45, Type.PSYCHIC), new Move("Psybeam", 35, Type.PSYCHIC), new Move("Recover", 0, Type.NORMAL), new Move("Reflect", 0, Type.NORMAL)}),
    new Pokemon("Machamp", 165, Type.NORMAL, "images/sprites/back_068.png", "images/sprites/front_068.png", "images/icons/068.png", new Move[]{new Move("Submission", 40, Type.NORMAL), new Move("Dynamic Punch", 45, Type.NORMAL), new Move("Strength", 35, Type.NORMAL), new Move("Karate Chop", 30, Type.NORMAL)}),
    new Pokemon("Gengar", 145, Type.GHOST, "images/sprites/back_094.png", "images/sprites/front_094.png", "images/icons/094.png", new Move[]{new Move("Shadow Ball", 45, Type.GHOST), new Move("Dream Eater", 40, Type.PSYCHIC), new Move("Confuse Ray", 0, Type.NORMAL), new Move("Night Shade", 35, Type.GHOST)}),
    new Pokemon("Onix", 110, Type.ROCK, "images/sprites/back_095.png", "images/sprites/front_095.png", "images/icons/095.png", new Move[]{new Move("Rock Throw", 25, Type.ROCK), new Move("Slam", 35, Type.NORMAL), new Move("Dig", 35, Type.GROUND), new Move("Bind", 15, Type.NORMAL)}),
    new Pokemon("Voltorb", 90, Type.ELECTRIC, "images/sprites/back_100.png", "images/sprites/front_100.png", "images/icons/100.png", new Move[]{new Move("Spark", 25, Type.ELECTRIC), new Move("Tackle", 15, Type.NORMAL), new Move("Screech", 0, Type.NORMAL), new Move("Self-Destruct", 45, Type.NORMAL)}),
    new Pokemon("Electrode", 130, Type.ELECTRIC, "images/sprites/back_101.png", "images/sprites/front_101.png", "images/icons/101.png", new Move[]{new Move("Thunderbolt", 35, Type.ELECTRIC), new Move("Thunder", 45, Type.ELECTRIC), new Move("Swift", 30, Type.NORMAL), new Move("Explosion", 50, Type.NORMAL)}),
    new Pokemon("Snorlax", 250, Type.NORMAL, "images/sprites/back_143.png", "images/sprites/front_143.png", "images/icons/143.png", new Move[]{new Move("Body Slam", 40, Type.NORMAL), new Move("Rest", 0, Type.NORMAL), new Move("Hyper Beam", 50, Type.NORMAL), new Move("Earthquake", 40, Type.GROUND)}),
    new Pokemon("Articuno", 175, Type.ROCK, "images/sprites/back_144.png", "images/sprites/front_144.png", "images/icons/144.png", new Move[]{new Move("Ice Beam", 40, Type.ROCK), new Move("Blizzard", 50, Type.ROCK), new Move("Fly", 35, Type.FLYING), new Move("Peck", 20, Type.FLYING)}),
    new Pokemon("Zapdos", 175, Type.ELECTRIC, "images/sprites/back_145.png", "images/sprites/front_145.png", "images/icons/145.png", new Move[]{new Move("Thunderbolt", 40, Type.ELECTRIC), new Move("Thunder", 50, Type.ELECTRIC), new Move("Drill Peck", 35, Type.FLYING), new Move("Agility", 0, Type.NORMAL)}),
    new Pokemon("Moltres", 175, Type.FIRE, "images/sprites/back_146.png", "images/sprites/front_146.png", "images/icons/146.png", new Move[]{new Move("Fire Blast", 50, Type.FIRE), new Move("Flamethrower", 40, Type.FIRE), new Move("Sky Attack", 45, Type.FLYING), new Move("Wing Attack", 30, Type.FLYING)}),
    new Pokemon("Dratini", 95, Type.DRAGON, "images/sprites/back_147.png", "images/sprites/front_147.png", "images/icons/147.png", new Move[]{new Move("Dragon Rage", 30, Type.DRAGON), new Move("Wrap", 15, Type.NORMAL), new Move("Thunder Wave", 0, Type.ELECTRIC), new Move("Slam", 35, Type.NORMAL)}),
    new Pokemon("Dragonair", 140, Type.DRAGON, "images/sprites/back_148.png", "images/sprites/front_148.png", "images/icons/148.png", new Move[]{new Move("Dragon Tail", 35, Type.DRAGON), new Move("Aqua Tail", 35, Type.WATER), new Move("Slam", 35, Type.NORMAL), new Move("Hyper Beam", 45, Type.NORMAL)}),
    new Pokemon("Dragonite", 195, Type.DRAGON, "images/sprites/back_149.png", "images/sprites/front_149.png", "images/icons/149.png", new Move[]{new Move("Outrage", 50, Type.DRAGON), new Move("Hyper Beam", 50, Type.NORMAL), new Move("Fire Punch", 35, Type.FIRE), new Move("Hurricane", 45, Type.FLYING)}),
    new Pokemon("Mewtwo", 220, Type.PSYCHIC, "images/sprites/back_150.png", "images/sprites/front_150.png", "images/icons/150.png", new Move[]{new Move("Psystrike", 55, Type.PSYCHIC), new Move("Shadow Ball", 40, Type.GHOST), new Move("Recover", 0, Type.NORMAL), new Move("Psychic", 45, Type.PSYCHIC)}),
    new Pokemon("Mew", 200, Type.PSYCHIC, "images/sprites/back_151.png", "images/sprites/front_151.png", "images/icons/151.png", new Move[]{new Move("Psychic", 45, Type.PSYCHIC), new Move("Mega Punch", 35, Type.NORMAL), new Move("Metronome", 25, Type.NORMAL), new Move("Transform", 0, Type.NORMAL)})
};

Pokemon[] p1, p2;
    int i1 = 0, i2 = 0, pickIdx = 0;
    boolean pickingP1 = true, turnProcessing = false;
    int p1SelectedMove = -1;

    CardLayout cl = new CardLayout();
    JPanel main = new JPanel(cl), menuPanel, selectPanel;
    DefaultListModel<String> p1Model = new DefaultListModel<>(), p2Model = new DefaultListModel<>();
    BattlePanel battleField = new BattlePanel();
    JButton[] moveBtns = new JButton[4];
    JPanel movePanel = new JPanel(new GridLayout(2, 2, 5, 5));
    JTextArea dialogue = new JTextArea();

    Image load(String p) { return Toolkit.getDefaultToolkit().getImage(p); }

    public PokemonGame() {
        setTitle("Pokemon Battle Simulator");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        try {
        iconSheet = ImageIO.read(new File("POKEMON GAME/images/icons"));
    } catch (Exception e) {
        System.out.println("Error: Could not find icons.png in images/ folder.");
    }

        setupMenu(); setupBattle();
        main.add(menuPanel, "menu"); main.add(battleField, "battle");
        add(main); cl.show(main, "menu");
        
        playTheme("menu");
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) { handleEscape(); return true; } return false;
        });
        setVisible(true);
    }
    
    double typeEffectiveness(Type moveType, Type targetType) {
    if (moveType == Type.FIRE) {
        if (targetType == Type.GRASS || targetType == Type.BUG) return 2.0;
        if (targetType == Type.WATER || targetType == Type.FIRE) return 0.5;
    }
    if (moveType == Type.WATER) {
        if (targetType == Type.FIRE) return 2.0;
        if (targetType == Type.GRASS || targetType == Type.WATER) return 0.5;
    }
    if (moveType == Type.GRASS) {
        if (targetType == Type.WATER) return 2.0;
        if (targetType == Type.FIRE || targetType == Type.GRASS || targetType == Type.POISON || targetType == Type.BUG || targetType == Type.FLYING) return 0.5;
    }
    if (moveType == Type.ELECTRIC) {
        if (targetType == Type.WATER || targetType == Type.FLYING) return 2.0;
        if (targetType == Type.GRASS || targetType == Type.ELECTRIC) return 0.5;
    }
    if (moveType == Type.NORMAL) {
        if (targetType == Type.GHOST) return 0.0; //Type advantage part. (Sir PJ's sister is hot.)
    }
    if (moveType == Type.PSYCHIC) {
        if (targetType == Type.POISON) return 2.0;
        if (targetType == Type.PSYCHIC) return 0.5;
    }
    if (moveType == Type.POISON) {
        if (targetType == Type.GRASS) return 2.0;
        if (targetType == Type.POISON || targetType == Type.GHOST) return 0.5;
    }
    if (moveType == Type.FLYING) {
        if (targetType == Type.GRASS || targetType == Type.BUG) return 2.0;
        if (targetType == Type.ELECTRIC) return 0.5;
    }
    if (moveType == Type.BUG) {
        if (targetType == Type.GRASS || targetType == Type.PSYCHIC) return 2.0;
        if (targetType == Type.FIRE || targetType == Type.POISON || targetType == Type.FLYING) return 0.5;
    }
    if (moveType == Type.GHOST) {
        if (targetType == Type.NORMAL || targetType == Type.PSYCHIC) return 0.0; // Gen 1 Ghost logic
        if (targetType == Type.GHOST) return 2.0;
    }
    return 1.0; // Default: Neutral damage
}

    // ICON CROPPING LOGIC
    Icon getPokemonIcon(int id) {
    // ADJUST THESE TWO VALUES TO MATCH YOUR icons.png EXACTLY
    int iconSize = 32; // The width/height of ONE icon in pixels
    int columns = 15;  // How many icons are in one horizontal row of your sheet

    // Calculate X (column) and Y (row) coordinates
    int x = (id % columns) * iconSize;
    int y = (id / columns) * iconSize;

    try {
        // Crop the 32x32 area from the big iconSheet
        BufferedImage cropped = iconSheet.getSubimage(x, y, iconSize, iconSize);
        
        // Scale it up so it's not too small on high-res screens
        Image scaled = cropped.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    } catch (Exception e) {
        // Fallback: If cropping fails, return a blank icon or null
        System.err.println("Could not crop icon for ID: " + id);
        return null;
    }
}

    void playTheme(String name) {
        try {
            if (bgm != null) bgm.stop();
            File f = new File("sounds/" + name + ".wav");
            if (!f.exists()) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            bgm = AudioSystem.getClip(); bgm.open(ais); bgm.loop(Clip.LOOP_CONTINUOUSLY); bgm.start();
        } catch (Exception e) { }
    }

    void handleEscape() {
        if (currentCard.equals("menu")) System.exit(0);
        else { if (JOptionPane.showConfirmDialog(this, "QUIT?") == 0) resetToMenu(); }
    }

    void setupMenu() {
        menuPanel = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); if (menuBg != null) g.drawImage(menuBg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        JPanel box = new JPanel(new GridLayout(4, 1, 10, 10)); box.setOpaque(false);
        JLabel title = new JLabel("POKEMON BATTLE SIMULATOR", 0);
        title.setFont(new Font("Monospaced", 1, 40)); title.setForeground(GB_DARK);
        JButton vsAi = styleBtn("VS TRAINER"); JButton vsPvp = styleBtn("2 PLAYERS"); JButton exit = styleBtn("EXIT");
        vsAi.addActionListener(e -> chooseTeamSize(Mode.AI));
        vsPvp.addActionListener(e -> chooseTeamSize(Mode.PVP));
        exit.addActionListener(e -> System.exit(0));
        box.add(title); box.add(vsAi); box.add(vsPvp); box.add(exit);
        menuPanel.add(box);
    }

    void chooseTeamSize(Mode m) {
        mode = m;
        String[] opts = {"3 vs 3", "6 vs 6"};
        int res = JOptionPane.showOptionDialog(this, "TEAM SIZE", "BATTLE", 0, 1, null, opts, opts);
        teamSize = (res == 1) ? 6 : 3;
        p1 = new Pokemon[teamSize]; p2 = new Pokemon[teamSize];
        startSelect();
    }

    void startSelect() {
    currentCard = "select"; 
    pickIdx = 0; 
    pickingP1 = true;
    p1Model.clear(); 
    p2Model.clear(); 
    playTheme("select");

    selectPanel = new JPanel(new BorderLayout());
    selectPanel.setBackground(GB_WHITE);

    // 6 columns looks best for icons on most screens
    JPanel grid = new JPanel(new GridLayout(0, 6, 10, 10)); 
    grid.setOpaque(false);

    // Loop through every Pokemon in your 'base' array
    for (int i = 0; i < base.length; i++) {
        int idx = i; // Final variable for the listener
        
        // Get the specific icon for THIS Pokemon
        Icon icon = getPokemonIcon(i); 
        
        JButton b = new JButton(icon);
        b.setBackground(GB_WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(GB_DARK, 2));
        
        // Tooltip helps the player know who the icon belongs to
        b.setToolTipText(base[i].name.toUpperCase());
        
        // Action listener passes the current index to the pick function
        b.addActionListener(e -> pickPokemon(idx)); 
        
        grid.add(b);
    }

    // Wrap the grid in a scroll pane so you can see all 151
    JScrollPane scroll = new JScrollPane(grid);
    scroll.setBorder(null);
    scroll.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling

    // Add draft lists for P1 and P2
    JList<String> p1L = new JList<>(p1Model); 
    p1L.setBorder(BorderFactory.createTitledBorder("P1 SQUAD"));
    JList<String> p2L = new JList<>(p2Model); 
    p2L.setBorder(BorderFactory.createTitledBorder("P2 SQUAD"));
    p1L.setPreferredSize(new Dimension(200, 0)); 
    p2L.setPreferredSize(new Dimension(200, 0));

    selectPanel.add(scroll, BorderLayout.CENTER);
    selectPanel.add(p1L, BorderLayout.EAST);
    selectPanel.add(p2L, BorderLayout.WEST);

    main.add(selectPanel, "select");
    cl.show(main, "select");
}

    void pickPokemon(int idx) {
        if (pickingP1) { p1[pickIdx] = new Pokemon(base[idx]); p1Model.addElement(base[idx].name); }
        else { p2[pickIdx] = new Pokemon(base[idx]); p2Model.addElement(base[idx].name); }
        pickIdx++;
        if (pickIdx == teamSize) {
            if (mode == Mode.AI && pickingP1) {
                for (int i = 0; i < teamSize; i++) p2[i] = new Pokemon(base[r.nextInt(base.length)]);
                startBattle();
            } else if (pickingP1) { pickingP1 = false; pickIdx = 0; JOptionPane.showMessageDialog(this, "P2 TURN"); }
            else startBattle();
        }
    }

    void setupBattle() {
        battleField.setLayout(new BorderLayout());
        dialogue.setEditable(false); dialogue.setFont(new Font("Monospaced", 1, 28));
        dialogue.setBackground(GB_WHITE); dialogue.setBorder(BorderFactory.createLineBorder(GB_DARK, 5));
        JPanel bot = new JPanel(new BorderLayout()); bot.setBackground(GB_WHITE);
        movePanel.setPreferredSize(new Dimension(500, 150));
        for (int i = 0; i < 4; i++) { moveBtns[i] = styleBtn(""); moveBtns[i].addActionListener(this); movePanel.add(moveBtns[i]); }
        movePanel.setVisible(false);
        bot.add(dialogue, "Center"); bot.add(movePanel, "East");
        battleField.add(bot, "South");
    }

    void startBattle() {
        currentCard = "battle"; i1 = 0; i2 = 0; battleStarted = true;
        playTheme("battle"); cl.show(main, "battle");
        dialogue.setText("A battle begins!");
        delay(1500, () -> {
            dialogue.setText("Enemy sends out " + p2[i2].name.toUpperCase() + "!");
            animateSendOut(false);
            delay(1500, () -> {
                dialogue.setText("Go! " + p1[i1].name.toUpperCase() + "!");
                animateSendOut(true);
                delay(1500, this::showChooseMove);
            });
        });
    }

    void animateSendOut(boolean isP) {
        if (isP) { pScale = 0f; pFlash = true; showPlayerSprite = true; }
        else { eScale = 0f; eFlash = true; showEnemySprite = true; }
        javax.swing.Timer t = new javax.swing.Timer(30, null);
        t.addActionListener(e -> {
            if (isP) { pScale += 0.1f; if (pScale >= 1f) { pScale = 1f; pFlash = false; t.stop(); } }
            else { eScale += 0.1f; if (eScale >= 1f) { eScale = 1f; eFlash = false; t.stop(); } }
            battleField.repaint();
        });
        t.start();
    }

    void showChooseMove() {
        turnProcessing = false; p1SelectedMove = -1;
        dialogue.setText((mode == Mode.AI ? "" : "P1: ") + "What will " + p1[i1].name + " do?");
        for (int i = 0; i < 4; i++) moveBtns[i].setText(p1[i1].moves[i].name.toUpperCase());
        movePanel.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 4; i++) if (e.getSource() == moveBtns[i]) {
            if (mode == Mode.PVP && p1SelectedMove == -1) {
                p1SelectedMove = i; movePanel.setVisible(false); dialogue.setText("P2: Choose move!");
                delay(800, () -> { for (int j = 0; j < 4; j++) moveBtns[j].setText(p2[i2].moves[j].name.toUpperCase()); movePanel.setVisible(true); });
            } else executeTurn(i);
        }
    }

    void executeTurn(int p2MoveIdx) {
        turnProcessing = true; movePanel.setVisible(false);
        int aiMove = r.nextInt(4);
        performMove(p1[i1], p2[i2], p1[i1].moves[p1SelectedMove == -1 ? aiMove : p1SelectedMove], () -> {
            if (p2[i2].hp <= 0) handleFaint(false);
            else performMove(p2[i2], p1[i1], p2[i2].moves[p2MoveIdx], () -> {
                if (p1[i1].hp <= 0) handleFaint(true); else showChooseMove();
            });
        });
    }

    void performMove(Pokemon u, Pokemon t, Move m, Runnable next) {
    dialogue.setText(u.name.toUpperCase() + " used " + m.name.toUpperCase() + "!");
    
    delay(1200, () -> {
        double mult = typeEffectiveness(m.type, t.type);
        t.hp = Math.max(0, t.hp - (int)(m.dmg * mult));
        battleField.repaint();

        // Check the effectiveness and display the message
        if (mult > 1.0) {
            dialogue.setText("It's super effective!");
            delay(1200, next);
        } else if (mult > 0.0 && mult < 1.0) {
            dialogue.setText("It's not very effective...");
            delay(1200, next);
        } else if (mult == 0.0) {
            dialogue.setText("It doesn't affect " + t.name.toUpperCase() + "!");
            delay(1200, next);
        } else {
            next.run(); // Normal damage, no extra message
        }
    });
}

    void handleFaint(boolean isP1) {
        if (isP1) { dialogue.setText(p1[i1].name.toUpperCase() + " fainted!"); showPlayerSprite = false; i1++; }
        else { dialogue.setText("Enemy " + p2[i2].name.toUpperCase() + " fainted!"); showEnemySprite = false; i2++; }
        battleField.repaint();
        delay(2000, () -> {
            if (i1 >= teamSize) endGame("P2 WINS!");
            else if (i2 >= teamSize) endGame("P1 WINS!");
            else {
                if (isP1) { dialogue.setText("Go! " + p1[i1].name.toUpperCase() + "!"); animateSendOut(true); }
                else { dialogue.setText("Enemy out " + p2[i2].name.toUpperCase() + "!"); animateSendOut(false); }
                delay(1500, this::showChooseMove);
            }
        });
    }

    void endGame(String m) { JOptionPane.showMessageDialog(this, m); resetToMenu(); }
    void resetToMenu() { currentCard = "menu"; cl.show(main, "menu"); playTheme("menu"); }
    void delay(int ms, Runnable r) { javax.swing.Timer t = new javax.swing.Timer(ms, e -> { ((javax.swing.Timer) e.getSource()).stop(); r.run(); }); t.setRepeats(false); t.start(); }
    
    JButton styleBtn(String t) { 
        JButton b = new JButton(t); b.setFont(new Font("Monospaced", 1, 16)); 
        b.setBackground(GB_WHITE); b.setBorder(BorderFactory.createLineBorder(GB_DARK, 2)); 
        b.setFocusPainted(false); return b; 
    }

    class BattlePanel extends JPanel {
    // Gen 3 GBA Palette
    Color SKY_BLUE = new Color(160, 216, 248);
    Color GRASS_GREEN = new Color(120, 200, 80);
    Color PLATFORM_SHADOW = new Color(80, 160, 60);
    Color HUD_BG = new Color(248, 248, 216); // Creamy white HUD

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Draw Background (Sky and Floor)
        g2.setColor(SKY_BLUE);
        g2.fillRect(0, 0, getWidth(), getHeight() / 2);
        g2.setColor(GRASS_GREEN);
        g2.fillRect(0, getHeight() / 2, getWidth(), getHeight() / 2);

        if (!battleStarted) return;

        // 2. Draw Circles (Platforms) below Pokemon
        if (showEnemySprite) {
            // Enemy Platform (Top Right)
            g2.setColor(PLATFORM_SHADOW);
            g2.fillOval(getWidth() - 450, 280, 350, 80); // Ellipse for perspective
            
            // Draw Enemy HUD and Sprite
            drawGBAHUD(g2, p2[i2], 50, 60, true);
            int w = (int) (300 * eScale), h = (int) (300 * eScale);
            g2.drawImage(p2[i2].front, getWidth() - 200 - (w / 2), 250 - (h / 2), w, h, this);
        }

        if (showPlayerSprite) {
            // Player Platform (Bottom Left)
            g2.setColor(PLATFORM_SHADOW);
            g2.fillOval(80, getHeight() - 250, 450, 100); 

            // Draw Player HUD and Sprite
            drawGBAHUD(g2, p1[i1], getWidth() - 420, getHeight() - 450, false);
            int w = (int) (400 * pScale), h = (int) (400 * pScale);
            g2.drawImage(p1[i1].back, 300 - (w / 2), getHeight() - 320 - (h / 2), w, h, this);
        }
    }
    
     private void playBGM(String filename, boolean loop) {
    if (bgm != null && bgm.isRunning()) bgm.stop(); // Stop current track
    try {
        File musicFile = new File("music/" + filename);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
        bgm = AudioSystem.getClip();
        bgm.open(audioStream);
        if (loop) bgm.loop(Clip.LOOP_CONTINUOUSLY); // Continuous loop for BGM
        bgm.start();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    void drawGBAHUD(Graphics2D g, Pokemon p, int x, int y, boolean isEnemy) {
        // HUD Container
        g.setColor(new Color(64, 64, 64)); // Dark Border
        g.fillRoundRect(x, y, 350, 75, 15, 15);
        g.setColor(HUD_BG);
        g.fillRoundRect(x + 3, y + 3, 344, 69, 12, 12);

        // Name and Level
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 22));
        g.drawString(p.name.toUpperCase(), x + 15, y + 28);
        
        // HP Bar Container
        g.setColor(new Color(100, 100, 100));
        g.drawRect(x + 100, y + 35, 220, 15);
        
        // HP Bar Logic (Green/Yellow/Red)
        double ratio = p.hp / (double) p.maxHP;
        if (ratio > 0.5) g.setColor(new Color(48, 208, 32)); // Green
        else if (ratio > 0.2) g.setColor(new Color(248, 224, 56)); // Yellow
        else g.setColor(new Color(248, 88, 56)); // Red
        
        g.fillRect(x + 101, y + 36, (int) (218 * ratio), 13);

        // Numbers (Player only)
        if (!isEnemy) {
            g.setFont(new Font("Monospaced", Font.BOLD, 18));
            g.setColor(Color.BLACK);
            g.drawString(p.hp + "/" + p.maxHP, x + 230, y + 65);
        }
    }
}

    public static void main(String[] args) { SwingUtilities.invokeLater(PokemonGame::new); }
}