/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mavis3;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author rimsha@geeks
 */
public class Mavis3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.print("MAVIS Collection Record Transformation\n\n");
        System.out.print("By AVPreserve https://www.avpreserve.com\n\n");
        // create a scanner so we can read the command-line input
        Scanner scanner = new Scanner(System.in);

        //  prompt for the user's name
        System.out.print("File/folder to process: ");

        // get their input as a String
        String inputPath = scanner.nextLine();

        // prompt for their age
        System.out.print("Ouput folder path: ");

        // get the age as an int
        String outputPath = scanner.nextLine();

        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        if (!inputFile.exists()) {
            System.out.print("File/folder to process does not exists");
            return;
        } else if (inputFile.isFile() && !FilenameUtils.getExtension(inputPath).equals("xml")) {
            System.out.print("Invalid file extension. Please select xml only.");
            return;
        }

        if (!outputFile.exists() || !outputFile.isDirectory()) {
            System.out.print("Ouput folder does not exists.");
            return;
        }

        if (inputFile.isDirectory()) {
            File[] directoryListing = inputFile.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    if (FilenameUtils.getExtension(child.getPath()).equals("xml")) {
                        System.out.println("Processing " + child.getName());
                        XMLParser parser = new XMLParser(child);
                        Map xmlMap = parser.parseXML();
                        parser.getDataFields(xmlMap, outputPath);
                    }
                }
            }
        } else {
            System.out.println("Processing " + inputFile.getName());
            XMLParser parser = new XMLParser(inputFile);
            Map xmlMap = parser.parseXML();
            parser.getDataFields(xmlMap, outputPath);
        }
        System.out.println("Process succesfully completed.");
    }

    private static void print(Map map, Integer tab) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            String key = pairs.getKey().toString();
            Object value = pairs.getValue();
            if (value instanceof Map) {
                System.out.println(getTab(tab) + key + " ==> [");
                print((Map) value, tab + 1);
                System.out.println(getTab(tab) + "]");
            } else if (value instanceof List) {
                System.out.println(getTab(tab) + key + " ==> [");
                print((List) value, tab + 1);
                System.out.println(getTab(tab) + "]");
            } else {
                System.out.println(getTab(tab) + key + " ==> " + value);
            }
        }
    }

    private static void print(List list, Integer tab) {
        for (Integer index = 0; index < list.size(); index++) {
            Object value = list.get(index);
            if (value instanceof Map) {
                System.out.println(getTab(tab) + index + ": {");
                print((Map) value, tab + 1);
                System.out.println(getTab(tab) + "}");
            } else if (value instanceof List) {
                print((List) value, tab + 1);
            } else {
                System.out.println(getTab(tab) + index.toString() + ": " + value);
            }
        }
    }

    public static String getTab(Integer tab) {
        String string = "";
        for (Integer index = 0; index < tab; index++) {
            string += "    ";
        }
        return string;
    }

}
