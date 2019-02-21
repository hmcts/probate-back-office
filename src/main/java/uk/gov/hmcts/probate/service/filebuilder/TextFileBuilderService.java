package uk.gov.hmcts.probate.service.filebuilder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class TextFileBuilderService {

    private BufferedWriter writer;

    private void openWriter(String fileName) {
        try {
            writer = new BufferedWriter(new FileWriter(sanitiseFilePath(fileName)));
        } catch (IOException e) {
            log.error("Failed creating buffered writer", e.getMessage());
        }
    }

    private String sanitiseFilePath(String fileName) {
        return fileName.replaceAll("/", "");
    }

    private void writeDataToFile(String data, String delimiter, boolean trim) {
        try {
            writer.write(data);
            if (!trim) {
                writer.write(delimiter);
            }
        } catch (Exception e) {
            log.error("Failed writing {} to file", data, e.getMessage());
        }
    }

    public File createFile(List<String> data, String delimiter, String fileName, boolean trimDelimiter) throws
            IOException {
        openWriter(fileName);
        for (String item : data) {
            boolean trim = (data.indexOf(item) == data.size() - 1 && trimDelimiter);
            writeDataToFile(item, delimiter, trim);
        }
        writer.close();

        return new File(sanitiseFilePath(fileName));
    }
}
