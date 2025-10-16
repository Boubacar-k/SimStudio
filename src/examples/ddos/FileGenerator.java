package examples.ddos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


class FileGenerator {
	private static File file = new File("data.csv");

    public FileGenerator() {}

    public void appendLine(String line) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l’écriture dans le fichier : " + e.getMessage());
        }
    }
}
