package Mycraft.Rendering.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

import Mycraft.Core.Client;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class FontLoader {
    public static Font loadFont(String fontFile) throws IOException {
        Font font = new Font();
            String filePath = Client.assetsPath + "fonts/" + fontFile;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("common ")) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("lineHeight=")) {
                            font.setLineHeight(Integer.parseInt(part.split("=")[1]));
                        } else if (part.startsWith("base=")) {
                            font.setBase(Integer.parseInt(part.split("=")[1]));
                        }
                    }
                }

                if (line.startsWith("char ")) {
                    Font.CharInfo charInfo = new Font.CharInfo();
                    String[] tokens = line.split(" ");
                    for (String token : tokens) {
                        String[] pair = token.split("=");
                        if (pair.length != 2) continue;
                        String key = pair[0];
                        String value = pair[1];

                        switch (key) {
                            case "id": charInfo.id = Integer.parseInt(value); break;
                            case "x": charInfo.x = Float.parseFloat(value); break;
                            case "y": charInfo.y = Float.parseFloat(value); break;
                            case "width": charInfo.width = Float.parseFloat(value); break;
                            case "height": charInfo.height = Float.parseFloat(value); break;
                            case "xoffset": charInfo.xOffset = Float.parseFloat(value); break;
                            case "yoffset": charInfo.yOffset = Float.parseFloat(value); break;
                            case "xadvance": charInfo.xAdvance = Float.parseFloat(value); break;
                        }
                    }
                    font.addChar(charInfo);
                }
            }
        }

        return font;
    }

    public static int loadTexture(String filePath, Font font) {
        int textureID;
        String path = Client.assetsPath + "fonts/" + filePath;
        try (var stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true);
            ByteBuffer image = STBImage.stbi_load(path, width, height, channels, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load texture: " + path);
            }

            textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);
            
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

            STBImage.stbi_image_free(image);

            font.setTextureID(textureID);
            font.setTextureSize(width.get(0), height.get(0));
        }

        return textureID;
    }
}


