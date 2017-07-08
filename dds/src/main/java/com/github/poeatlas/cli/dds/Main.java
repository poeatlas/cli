package com.github.poeatlas.cli.dds;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import lombok.extern.slf4j.Slf4j;
import org.brotli.dec.BrotliInputStream;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
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

    app.run(new File("/home/NothingSoup/POE/output/Art/2DItems/Maps/AtlasMaps/Academy3.dds"));
  }

  public void run(final File file) throws IOException {
    InputStream is = new FileInputStream(file);

    final byte[] sizeBuf = new byte[4];

    is.read(sizeBuf,0,4);
    Conversion conv = new Conversion();
    final int size = conv.byteArrayToInt(0);
    // buffer for header to get size of decoded file
    // ByteBuffer buf = ByteBuffer.allocate(4);

    // buf.order(LITTLE_ENDIAN);
    // fileChannel.read(buf);

    // buf.flip();

    // final int size = buf.getInt();

    final BrotliInputStream brotliIS = new BrotliInputStream(is);
    // final ReadableByteChannel brotliChannel = Channels.newChannel(brotliIS);

    byte[] destBuffer = new byte[size];
    // ByteBuffer brotliBuf = ByteBuffer.wrap(destBuffer);

    InputStream decIS = new ByteArrayInputStream(destBuffer);

    ImageReader imageReader = ImageIO.getImageReadersBySuffix(DDS).next();
    ImageInputStream iis = ImageIO.createImageInputStream(decIS);

    BufferedImage image;
    File output;

    imageReader.setInput(iis);

    final int maxImages = imageReader.getNumImages(true);
    System.out.println("max images: " + maxImages);
    for (int i = 0; i < maxImages; i++) {
      String prefix = maxImages > 1 ? i + "." : "";

      output = changeExt(file, prefix + PNG);
      image = imageReader.read(i);
      System.out.println("current image: " + image);
      ImageIO.write(image, PNG, output);
    }
  }

  private File changeExt(final File file, final String newName) throws IOException {
    String path = file.getPath(); // ex: 2ditems/atlasmaps/channel.dds
    String name = file.getName();
    int periodIndex = name.lastIndexOf('.');
    int slashIndex = path.lastIndexOf('/');

    if (periodIndex != -1) {
      name = name.substring(0, periodIndex);
    }
    // new folder for multi images
    String newPath = path.substring(0, slashIndex) + '/' + name + '/';
    path = newPath + newName; // ex: 2ditems/atlasmaps/channel/1.png

    final File output = new File(path);
    File parentPath = output.getParentFile();
    // create new folder for images
    if (!parentPath.exists() && !parentPath.mkdir()) {
      throw new IOException("Failed to create directory: " + output.getPath());
    } else {
      return output;
    }
  }

}
