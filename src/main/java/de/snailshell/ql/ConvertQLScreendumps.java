package de.snailshell.ql;

import de.snailshell.ql.mode8.Screendump;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConvertQLScreendumps {

    private static List<String> SCREENDUMPS =
            List.of("MissionX/MX_pic", "PaintShop/PaintShop_pic", "Parallax/Parallax_pic");

    private static String OUTPUT_FOLDER = "images";

    public static void main(String... args) throws IOException {

        Files.createDirectories(Path.of(OUTPUT_FOLDER));

        for (String filename : SCREENDUMPS) {

            RenderedImage image = Screendump.loadFromFile(filename);

            ImageIO.write(image, "png", new File(OUTPUT_FOLDER + "/" + outputFilename(filename)));
        }
    }

    private static String outputFilename(String filename) {
        return filename.substring(0, filename.indexOf("/")) + ".png";
    }
}
