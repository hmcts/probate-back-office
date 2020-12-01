package uk.hmcts.reform.probate.backoffice.util;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceLoader {
  public static String loadJson(final String filePath) throws Exception {
    return new String(loadResource(filePath), Charset.forName("utf-8"));
  }

  private static byte[] loadResource(final String filePath) throws Exception {
    URL url = ResourceLoader.class.getClassLoader().getResource(filePath);

    if (url == null) {
      throw new IllegalArgumentException(String.format("Could not find resource in path %s", filePath));
    }

    return Files.readAllBytes(Paths.get(url.toURI()));
  }
}
