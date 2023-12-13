package utils;

import functions.ImageOperation;

import java.awt.image.BufferedImage;

public class RgbMaster {
    private BufferedImage image;
    private int width;
    private int height;
    private boolean hasAlfaChannel;
    private int[] pixels;

    public RgbMaster(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.hasAlfaChannel = image.getAlphaRaster() != null;
        this.pixels = image.getRGB(0, 0, width, height, null, 0, height * width);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void changeImage(ImageOperation operation) throws Exception {
        for (int i = 0; i < this.pixels.length; i++) {
            float[] pixel = ImageUtils.rgbIntToArray(this.pixels[i]);
            float[] newPixel = operation.execute(pixel);
            this.pixels[i] = ImageUtils.arrayToRgbInt(newPixel);
        }
        final int type = hasAlfaChannel ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        this.image = new BufferedImage(this.width, this.height, type);
        image.setRGB(0, 0, this.width, this.height, this.pixels, 0, this.width * this.height);
    }

}
