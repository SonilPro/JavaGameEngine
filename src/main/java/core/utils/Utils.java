package core.utils;

import core.ShaderManager;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {

    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static void loadShader(String filename, ShaderManager shader) throws Exception {
        boolean isFragmentShader = false;
        StringBuilder vertexShaderCode = new StringBuilder();
        StringBuilder fragmentShaderCode = new StringBuilder();
        try (InputStream in = Utils.class.getResourceAsStream(filename);
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            while (scanner.hasNext()) {
                //REMOVE DELIMITER
                String temp = scanner.useDelimiter("//A").nextLine() + "\n";
                if (temp.contains("VERTEX")) {
                    isFragmentShader = false;
                } else if (temp.contains("FRAGMENT")) {
                    isFragmentShader = true;
                } else if (!isFragmentShader) {
                    vertexShaderCode.append(temp);
                } else {
                    fragmentShaderCode.append(temp);
                }
            }
        }
        shader.setVertexShadeCode(vertexShaderCode.toString());
        shader.setFragmentShaderCode(fragmentShaderCode.toString());
    }

    public static List<String> readAllLines(String filename) {
        List<String> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(filename)))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return result;
    }

}
