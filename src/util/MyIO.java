package util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class MyIO {
    private static DataInputStream in;
    private static DataOutputStream out;
    private static BufferedReader br;
    public static void readSourceFile(String filePath) {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(filePath);
        } catch (Exception e) {
            System.err.println("Error: " + filePath + " is not founded.");
            throw new RuntimeException(e);
        }
        MyIO.in = new DataInputStream(fileInputStream);
        MyIO.br = new BufferedReader(new InputStreamReader(in));
    }
    public static String readLine() {
        try {
            return MyIO.br.readLine();
        } catch (Exception e) {
            System.err.println("Error: read file fails");
            throw new RuntimeException(e);
        }
    }
    public static void openTargetFile(String filePath) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(filePath);
        } catch (Exception e) {
            System.err.println("Error: " + filePath + " is not founded.");
            throw new RuntimeException(e);
        }
        MyIO.out = new DataOutputStream(fileOutputStream);
    }
    public static void writeTargetFile(String content) {
        try {
            MyIO.out.writeBytes(content);
        } catch (Exception e) {
            System.err.println("Error: Write target file failed.");
            throw new RuntimeException(e);
        }
    }
    public static void closeFiles() {
        try {
            MyIO.in.close();
            MyIO.br.close();
            MyIO.out.close();
        } catch (Exception e) {
            System.err.println("Error: Close IOStreams failed.");
            throw new RuntimeException(e);
        }
    }
}
