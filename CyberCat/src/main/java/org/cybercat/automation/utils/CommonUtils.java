/**Copyright 2013 The Cybercat project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cybercat.automation.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


public class CommonUtils {

    private static final Logger log = Logger.getLogger(CommonUtils.class);
   
    private static Random rand = new Random();   

    public static boolean isX64() {
        String os = System.getProperty("os.arch").toLowerCase();
        return (os.indexOf("64") >= 0);
    }

    public static boolean isWindows() {

        String os = System.getProperty("os.name").toLowerCase();
        // windows
        return (os.indexOf("win") >= 0);

    }

    public static boolean isMac() {

        String os = System.getProperty("os.name").toLowerCase();
        // Mac
        return (os.indexOf("mac") >= 0);

    }

    public static boolean isUnix() {

        String os = System.getProperty("os.name").toLowerCase();
        // linux or unix
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

    }

    public static String getCurrentDate() {
        return dateToString(new Date());
    }

    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return dateFormat.format(date);
    }

    public static void appendToFile(String text, String filePath, String fileName) {
        // TODO file path should address to projects folder: TestResults

        FileWriter fWriter = null;
        BufferedWriter writer = null;

        try {
            new File(filePath).mkdirs();
            fWriter = new FileWriter(filePath + "/" + fileName, true);
            writer = new BufferedWriter(fWriter);
            writer.append(text);
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(String text, String filePath, String fileName) {
        FileWriter fWriter = null;
        BufferedWriter writer = null;
        try {
            new File(filePath).mkdirs();
            fWriter = new FileWriter(filePath + "/" + fileName);
            writer = new BufferedWriter(fWriter);
            writer.write(text);
            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 0-10000
    private static long counter = 0;

    public static long genGuid() {
        if (counter >= 9999) {
            counter = 0;
        }
        return System.currentTimeMillis() * 10000 + (counter++);
    }

    public static int generateNumber(int ind) {
        int index = rand.nextInt(ind) + 1;
        log.info("Random value is " + index);
        return index;
    }
    
    public static int[] generateRandNumbers(int range,int count){
         List<Integer> integerList = new ArrayList<Integer>(range);  
         int[] mas= new int[count];
            for (int i = 0; i < range; i++) {
                integerList.add(i );
            }
            Collections.shuffle(integerList, rand);            
            for(int i=0;i<count; i++){
                mas[i]=integerList.get(i);
                log.info("Random value is " +  mas[i]);
            }
            return mas;            
    }

    public static int extractDigitFromText(String text) throws CommonTestException {
        Pattern pat = Pattern.compile("\\d{1}");
        Matcher m = pat.matcher(text);
        StringBuilder numbers = new StringBuilder();
        while (m.find()) {              
            numbers.append(Integer.parseInt(m.group()));
        }
        if (numbers.capacity()!=0)
            return Integer.parseInt(numbers.toString());
        else
            throw new CommonTestException("Text does not contain any numbers");
    }

    public static List<Integer> extractDigitsFromText(String text) throws CommonTestException {
        Pattern pat = Pattern.compile("\\d+");
        Matcher m = pat.matcher(text);
        List<Integer> numbers = new ArrayList<>();
        while (m.find()) {
            numbers.add(Integer.parseInt(m.group()));
        }
        if (numbers.size() > 0)

            return numbers;
        else
            throw new CommonTestException("Text does not contain any numbers");

    }

    public static String extractImageUrlFromText(String text) throws CommonTestException {
        Pattern pat = Pattern.compile("(http).*(\\.png)");
        Matcher m = pat.matcher(text);
        if (m.find()) {
            String url = m.group();
            log.info(url);
            return url;
        }
        throw new CommonTestException("Text does not contain any url");
    }
    
    public static boolean matrixEquals(int[][] firstMatrix, int[][] secondMatrix) {
        for (int i = 0; i < firstMatrix.length; i++)
            for (int j = 0; j < firstMatrix[0].length; j++)
                if (firstMatrix[i][j] != secondMatrix[i][j])
                    return false;
        return true;
    }
    
    public static ArrayList<File> findFile(File dir, String[] extension, String name, boolean recursive) throws CommonTestException {
        ArrayList<File> files = new ArrayList<File>();
        try {

            Collection<File> allFiles = FileUtils.listFiles(dir, extension, recursive);
            for (Iterator<File> iterator = allFiles.iterator(); iterator.hasNext();) {
                File file = (File) iterator.next();
                if (file.getName().contains(name))
                    files.add(file);               
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (files.isEmpty())
            throw new CommonTestException("The files contain " + name + " doesn't exist in current directory!!");
        return files;
    }
    
    public static String validateFileName(String name){
        return name.replaceAll("[^\\w]", "_");
    }

    public static String generateRandomString(String chars, int length) {
        Random rand = new Random();
        StringBuilder buf = new StringBuilder();
        for (int i=0; i<length; i++) {
            buf.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return buf.toString();
    }
}
