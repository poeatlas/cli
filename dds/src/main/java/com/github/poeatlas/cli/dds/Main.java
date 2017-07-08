package com.github.poeatlas.cli.dds;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.brotli.dec.BrotliInputStream;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Created by blei on 7/6/17.
 */
@Slf4j
public class Main {
  private final static String DDS = "dds";
  private final static String PNG = "png";


  public static void main(final String[] args) throws IOException {
    final Main app = new Main();

    app.run(new File(args[0]));
  }

  public void run(final File file) throws IOException {
    @Cleanup final InputStream is = FileUtils.openInputStream(file);

    final byte[] decSizeBytes = new byte[4];
    is.read(decSizeBytes);

    // converts 4 big endian ordered bytes to a little endian int
    // have to & 0xFF to eliminate the conversion from byte to int
    final int size = ((decSizeBytes[3] & 0xFF) << 24)
                     | ((decSizeBytes[2] & 0xFF) << 16)
                     | ((decSizeBytes[1] & 0xFF) << 8)
                     | (decSizeBytes[0] & 0xFF);


    ImageReader imageReader = ImageIO.getImageReadersBySuffix(DDS).next();

    @Cleanup final BrotliInputStream brotliIS = new BrotliInputStream(is);
    final ByteArrayInputStream bais = new ByteArrayInputStream(IOUtils.readFully(brotliIS, size));
    @Cleanup final ImageInputStream iis = ImageIO.createImageInputStream(bais);
    String outputPath = file.getPath().replaceFirst("\\.dds$", ".png");

    if (!outputPath.endsWith(".png")) {
      outputPath += ".png";
    }

    final File output = new File(outputPath);

    imageReader.setInput(iis);

    BufferedImage     image = readMipmap(bais, imageReader, 4);
    ImageIO.write(image, PNG, output);
    image = readMipmap(bais, imageReader, 2);
    ImageIO.write(image, PNG, output);

  }

  /** To read a mipmap, have to reset the original stream so that a mipmap can be read. */
  private BufferedImage readMipmap(final ByteArrayInputStream stream,
                                   final ImageReader reader,
                                   final int mipmap)
      throws IOException {
    stream.reset();

    return reader.read(mipmap);
  }
}
