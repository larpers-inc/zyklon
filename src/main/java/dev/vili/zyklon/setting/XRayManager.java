package dev.vili.zyklon.setting;

import dev.vili.zyklon.util.ZLogger;
import dev.vili.zyklon.Zyklon;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.util.ArrayList;

public class XRayManager {
    public File MainDirectory;

    public XRayManager() {
        MainDirectory = new File(MinecraftClient.getInstance().runDirectory, Zyklon.name);
        initDefaultBlocks();
    }
    private final ArrayList<String> blocks = new ArrayList<>();

    private void writeFile(ArrayList<String> toSave, File file) {
        try {
            PrintWriter printWriter = new PrintWriter(file);
            for (String string : toSave) {
                printWriter.println(string);
            }
            printWriter.close();
        } catch (FileNotFoundException e) {e.printStackTrace();}
    }

    public void save() {
        try {
            File file = new File(MainDirectory, "xray.txt");
            ArrayList<String> toSave = new ArrayList<>();

            if (blocks.isEmpty())
                toSave.add(initDefaultBlocks());
            else toSave.addAll(blocks);

            writeFile(toSave, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            File file = new File(MainDirectory, "xray.txt");
            ArrayList<String> toSave = new ArrayList<>();
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = br.readLine()) != null) {
                toSave.add(line);
                blocks.add(line);
                ZLogger.logger.info("Added xray block: " + line);
            }
            br.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    public boolean isXrayBlock(Block block) {
        return blocks.contains(getBlocks(block));
    }

    public String initDefaultBlocks() {
        blocks.add(Blocks.DEEPSLATE_COAL_ORE.getTranslationKey());
        blocks.add(Blocks.DEEPSLATE_IRON_ORE.getTranslationKey());
        blocks.add(Blocks.DEEPSLATE_GOLD_ORE.getTranslationKey());
        blocks.add(Blocks.DEEPSLATE_REDSTONE_ORE.getTranslationKey());
        blocks.add(Blocks.DEEPSLATE_DIAMOND_ORE.getTranslationKey());
        blocks.add(Blocks.DEEPSLATE_LAPIS_ORE.getTranslationKey());
        blocks.add(Blocks.DEEPSLATE_EMERALD_ORE.getTranslationKey());
        blocks.add(Blocks.EMERALD_ORE.getTranslationKey());
        blocks.add(Blocks.EMERALD_BLOCK.getTranslationKey());
        blocks.add(Blocks.DIAMOND_ORE.getTranslationKey());
        blocks.add(Blocks.DIAMOND_BLOCK.getTranslationKey());
        blocks.add(Blocks.ANCIENT_DEBRIS.getTranslationKey());
        blocks.add(Blocks.GOLD_ORE.getTranslationKey());
        blocks.add(Blocks.GOLD_BLOCK.getTranslationKey());
        blocks.add(Blocks.IRON_ORE.getTranslationKey());
        blocks.add(Blocks.IRON_BLOCK.getTranslationKey());
        blocks.add(Blocks.COAL_ORE.getTranslationKey());
        blocks.add(Blocks.COAL_BLOCK.getTranslationKey());
        blocks.add(Blocks.REDSTONE_BLOCK.getTranslationKey());
        blocks.add(Blocks.REDSTONE_ORE.getTranslationKey());
        blocks.add(Blocks.LAPIS_ORE.getTranslationKey());
        blocks.add(Blocks.LAPIS_BLOCK.getTranslationKey());
        blocks.add(Blocks.NETHER_QUARTZ_ORE.getTranslationKey());
        blocks.add(Blocks.MOSSY_COBBLESTONE.getTranslationKey());
        blocks.add(Blocks.SPAWNER.getTranslationKey());
        blocks.add(Blocks.BOOKSHELF.getTranslationKey());
        blocks.add(Blocks.CHEST.getTranslationKey());
        blocks.add(Blocks.ENDER_CHEST.getTranslationKey());
        blocks.add(Blocks.ANVIL.getTranslationKey());
        blocks.add(Blocks.BARREL.getTranslationKey());
        blocks.add(Blocks.BLACK_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.BLUE_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.BROWN_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.CYAN_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.GRAY_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.GREEN_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.LIGHT_BLUE_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.LIGHT_GRAY_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.LIME_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.MAGENTA_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.ORANGE_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.PINK_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.PURPLE_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.RED_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.WHITE_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.YELLOW_SHULKER_BOX.getTranslationKey());
        blocks.add(Blocks.CRAFTING_TABLE.getTranslationKey());
        blocks.add(Blocks.FURNACE.getTranslationKey());
        blocks.add(Blocks.BEDROCK.getTranslationKey());
        blocks.add(Blocks.OBSIDIAN.getTranslationKey());

        for (String block : blocks) {
            return block;
        }
        return null;
    }

    public String getBlocks(Block block) {
        return block.getTranslationKey();
    }

}
