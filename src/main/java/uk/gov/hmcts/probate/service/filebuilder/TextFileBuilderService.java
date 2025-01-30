package uk.gov.hmcts.probate.service.filebuilder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.TextFileBuilderException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

@Slf4j
@Service
public class TextFileBuilderService {

    private String sanitiseFilePath(String fileName) {
        return fileName.replaceAll("/", "");
    }

    private void writeDataToFile(
            final BufferedWriter writer,
            final String data,
            final String delimiter) throws IOException {
        writer.write(data);
        if (!data.contains("\n")) {
            writer.write(delimiter);
        }
    }

    public File createFile(
            final List<String> data,
            final String delimiter,
            final String fileName,
            final String service) {
        log.info("Creating file={} with {} elements", fileName, data.size());
        final String sanFileName = sanitiseFilePath(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sanFileName))) {
            for (String item : data) {
                writeDataToFile(writer, item, delimiter);
            }
        } catch (IOException e) {
            final String exMsg = MessageFormat.format(
                    "Failed to write data to file {0} for service {1}", sanFileName, service);
            log.error(exMsg, e);
            throw new TextFileBuilderException(exMsg, e);
        }

        log.info("Created file={}", sanFileName);
        return new File(sanitiseFilePath(fileName));
    }
}