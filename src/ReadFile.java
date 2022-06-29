import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadFile {

    //Метод считывания файла
    public String readFileContentsOrNull(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            return null;
        }
    }
}
