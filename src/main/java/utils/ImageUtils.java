package utils;

import javafx.scene.image.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ImageUtils {
    private static final String IMAGE_DIR = "src/main/resources/images/";
    private static final String DEFAULT_IMAGE = "/images/default_profile.png";

    public static String saveProfileImage(File imageFile) throws IOException {
        // Create images directory if it doesn't exist
        Path destDir = Paths.get(IMAGE_DIR);
        if (!Files.exists(destDir)) {
            Files.createDirectories(destDir);
        }

        // Generate unique filename
        String fileName = "profile_" + System.currentTimeMillis() + "_" + imageFile.getName();
        Path destPath = destDir.resolve(fileName);

        // Copy the file
        Files.copy(imageFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

        // Return the relative path
        return "/images/" + fileName;
    }

    public static Image loadProfileImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                return loadDefaultImage();
            }

            // First try to load from resources
            InputStream resourceStream = ImageUtils.class.getResourceAsStream(imagePath);
            if (resourceStream != null) {
                return new Image(resourceStream);
            }

            // If not found in resources, try to load from file system
            Path filePath = Paths.get("src/main/resources" + imagePath);
            if (Files.exists(filePath)) {
                return new Image(filePath.toUri().toString());
            }

            return loadDefaultImage();
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
            return loadDefaultImage();
        }
    }

    private static Image loadDefaultImage() {
        return new Image(ImageUtils.class.getResourceAsStream(DEFAULT_IMAGE));
    }
}