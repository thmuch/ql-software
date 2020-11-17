package de.snailshell.ql.mode8;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class Screendump {

    public static final int FILE_LENGTH_BYTES = 32768;

    public static final int WIDTH = 256;
    public static final int HEIGHT = 256;

    public static RenderedImage loadFromFile(String filename) throws IOException {

        try (var in = new BufferedInputStream(new FileInputStream(filename))) {

            int[] pixels = new int[WIDTH * HEIGHT];

            int offset = 0;

            for (int i = 0; i < FILE_LENGTH_BYTES / 2; i++) {

                int hiByte = in.read();
                int loByte = in.read();

                // see section 10.2 in http://www.dilwyn.me.uk/docs/manuals/qltm.pdf for pixel encoding:

                int qlPixel3 = (hiByte & 0x80) >> 5 | (loByte & 0xc0) >> 6;
                int qlPixel2 = (hiByte & 0x20) >> 3 | (loByte & 0x30) >> 4;
                int qlPixel1 = (hiByte & 0x08) >> 1 | (loByte & 0x0c) >> 2;
                int qlPixel0 = (hiByte & 0x02) << 1 | (loByte & 0x03) >> 0;

                pixels[offset++] = javaPixelValue(qlPixel3);
                pixels[offset++] = javaPixelValue(qlPixel2);
                pixels[offset++] = javaPixelValue(qlPixel1);
                pixels[offset++] = javaPixelValue(qlPixel0);
            }

            return bufferedImageFrom(WIDTH, HEIGHT, 2 * WIDTH, HEIGHT, pixels);
        }
    }

    private static int javaPixelValue(int qlPixelValue) {
        return switch (qlPixelValue) {
            case 0 -> PixelValue.BLACK;
            case 1 -> PixelValue.BLUE;
            case 2 -> PixelValue.RED;
            case 3 -> PixelValue.MAGENTA;
            case 4 -> PixelValue.GREEN;
            case 5 -> PixelValue.CYAN;
            case 6 -> PixelValue.YELLOW;
            case 7 -> PixelValue.WHITE;
            default -> throw new IllegalStateException("Unknown QL pixel value " + qlPixelValue);
        };
    }

    private static BufferedImage bufferedImageFrom(int srcWidth, int srcHeight, int outputWidth, int outputHeight, int[] pix) {

        var memoryImageSource = new MemoryImageSource(srcWidth, srcHeight, pix, 0, srcWidth);

        var image = Toolkit.getDefaultToolkit().createImage(memoryImageSource);

        var bufferedImage = new BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_INT_RGB);

        bufferedImage.getGraphics().drawImage(image, 0, 0, outputWidth, outputHeight, null);

        return bufferedImage;
    }
}
