package com.github.poeatlas.cli.dds;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
 * Created by NothingSoup on 7/9/17.
 */
@AllArgsConstructor
public class DdsExtractor {
  private static final String DDS = "dds";
  private static final String PNG = "png";

  @Setter
  @Getter
  private int mipmap;

  /**
   * extracts the dds files using given mipmap.
   * @param file file to extract
   * @throws IOException if file is invalid
   */
  public void extract(final File file) throws IOException {
    @Cleanup final InputStream is = FileUtils.openInputStream(file);

    final byte[] decSizeBytes = IOUtils.readFully(is, 4);
    // is.read(decSizeBytes);

    // converts 4 big endian ordered bytes to a little endian int
    // have to & 0xFF to eliminate the conversion from byte to int
    final int size = ((decSizeBytes[3] & 0xFF) << 24)
                     | ((decSizeBytes[2] & 0xFF) << 16)
                     | ((decSizeBytes[1] & 0xFF) << 8)
                     | (decSizeBytes[0] & 0xFF);

    final ImageReader imageReader = ImageIO.getImageReadersBySuffix(DDS).next();

    // converts brotli encoded file into ByteArrayInputStream
    @Cleanup final BrotliInputStream bis = new BrotliInputStream(is);
    final ByteArrayInputStream bais = new ByteArrayInputStream(IOUtils.readFully(bis, size));
    @Cleanup final ImageInputStream iis = ImageIO.createImageInputStream(bais);

    // change extension of file from dds to png if found
    String outputPath = file.getPath().replaceFirst("\\.dds$", ".png");

    // in case the file does not have matched extension, add png
    if (!outputPath.endsWith(".png")) {
      outputPath = StringUtils.join(outputPath, ".png");
    }

    final File output = new File(outputPath);
    imageReader.setInput(iis);

    final BufferedImage image = imageReader.read(getMipmap());
    ImageIO.write(image, PNG, output);
  }

}
