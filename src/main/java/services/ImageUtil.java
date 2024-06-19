package services;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageUtil {

    public static BufferedImage decodeBase64ToImage(String base64String) throws IllegalArgumentException, IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64String);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        return ImageIO.read(bis);
    }


    public static String extractImageNameFromBase64(String base64Data) throws IllegalArgumentException {
        if (base64Data != null && !base64Data.isEmpty()) {
            // Vérifier si les données base64 commencent par un préfixe indiquant qu'elles contiennent des données d'image
            if (base64Data.startsWith("data:image")) {
                // Extraire le type MIME de l'image
                int startIndex = base64Data.indexOf("/");
                int endIndex = base64Data.indexOf(";");
                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    String imageType = base64Data.substring(startIndex + 1, endIndex);
                    // Générer un nom de fichier basé sur le type MIME de l'image
                    String imageName = "image." + imageType;
                    return imageName;
                } else {
                    throw new IllegalArgumentException("Les données base64 ne contiennent pas de type MIME valide : " + base64Data);
                }
            } else {
                // Si les données ne contiennent pas de préfixe d'image, supposons que le nom de fichier est directement passé
                return base64Data;
            }
        } else {
            throw new IllegalArgumentException("Les données base64 sont vides ou nulles");
        }
    }
    public static String encodeBufferedImageToBase64(BufferedImage bufferedImage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String extractImageNameWithoutExtension(String imageName) {
        int index = imageName.lastIndexOf('.');
        return index == -1 ? imageName : imageName.substring(0, index);
    }

    public static BufferedImage convertBase64ToBufferedImage(String base64String) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64String);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            return ImageIO.read(bis);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Image convertBufferedImageToImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
}