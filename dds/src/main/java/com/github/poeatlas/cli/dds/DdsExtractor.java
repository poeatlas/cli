package com.github.poeatlas.cli.dds;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.brotli.dec.BrotliInputStream;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Created by NothingSoup on 7/9/17.
 */
@AllArgsConstructor
@Slf4j
@Data
public class DdsExtractor {
  private static final int ASTERISK_ASCII_NUMBER = 42;
  private static final String DDS = "dds";
  private static final String PNG = "png";

  private int mipmap;

  private File root;

  /**
   * extracts dds file.
   * @param inputFile file to extract
   * @throws IOException file invalid
   */
  public void extract(final File inputFile) throws IOException {
    // change extension of file from dds to png if found
    String outputPath = inputFile.getPath().replaceFirst("\\.dds$", "");

    outputPath = StringUtils.join(outputPath, ".png");

    extract(inputFile, new File(outputPath));
  }

  /**
   * extracts the dds files using given mipmap.
   *
   * @param inputFile file to extract
   * @throws IOException if file is invalid
   */
  public void extract(final File inputFile, final File outputFile) throws IOException {
    // log.info("{} will be extracted as a PNG image.", inputFile.getPath(), getMipmap());

    @Cleanup final InputStream is = FileUtils.openInputStream(inputFile);

    final byte[] decSizeBytes = IOUtils.readFully(is, 4);

    if (decSizeBytes[0] == ASTERISK_ASCII_NUMBER) {
      final byte[] remainingBytes = IOUtils.readFully(is, (int) (inputFile.length() - 4));
      final String beginning = new String(decSizeBytes, StandardCharsets.UTF_8);
      final String ending = new String(remainingBytes, StandardCharsets.UTF_8);

      final File extractFile = new File(getRoot(),beginning.substring(1) + ending);
      log.info("{} references {}", inputFile.getPath(), extractFile.getPath());
      extract(extractFile, outputFile);
      return;
    }

    log.debug("decSizeBytes: {}", decSizeBytes);
    // converts 4 big endian ordered bytes to a little endian int
    // have to & 0xFF to eliminate the conversion from byte to int
    // final int size = ((decSizeBytes[3] & 0xFF) << 24)
    //                  | ((decSizeBytes[2] & 0xFF) << 16)
    //                  | ((decSizeBytes[1] & 0xFF) << 8)
    //                  | (decSizeBytes[0] & 0xFF);

    final int size = ByteBuffer.wrap(decSizeBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

    if (log.isDebugEnabled()) {
      log.debug("Brotli decoded size: {}", size);
    }

    // converts brotli encoded file into ByteArrayInputStream
    @Cleanup final BrotliInputStream bis = new BrotliInputStream(is);
    final byte[] decodedBytes = IOUtils.readFully(bis, size);
    @Cleanup final ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
    @Cleanup final ImageInputStream iis = ImageIO.createImageInputStream(bais);

    final ImageReader imageReader = ImageIO.getImageReadersBySuffix(DDS).next();

    imageReader.setInput(iis);

    final BufferedImage image = imageReader.read(getMipmap());

    ImageIO.write(image, PNG, outputFile);
    log.info("{} ({}x{}) created", outputFile.getPath(), image.getWidth(), image.getHeight());
  }

}
