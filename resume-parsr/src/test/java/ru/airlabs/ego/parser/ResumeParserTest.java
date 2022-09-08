package ru.airlabs.ego.parser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import ru.airlabs.ego.ResumeParser;
import ru.airlabs.ego.Type;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ResumeParserTest {

    @Ignore
    @Test
    public void resumeParserTestString() {
        File folder = new File("C:\\JAVA\\resume");
        for (File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());
            String json = null;
            String fileEntryString;
            try {
                fileEntryString = FileUtils.readFileToString(fileEntry, "UTF-8");
                json = ResumeParser.parse(fileEntryString, Type.HH);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(json);
        }
    }

    @Ignore
    @Test
    public void resumeParserTestFile() {
        File folder = new File("C:\\JAVA\\resume");
        for (File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());
            String json = ResumeParser.parse(fileEntry, Type.HH);
            System.out.println(json);
        }
    }

    @Ignore
    @Test
    public void resumeFriendWorkParserTestFile() {
        File folder = new File("C:\\JAVA\\resumeFW");
        for (File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());
            String json = ResumeParser.parse(fileEntry, Type.FRIEND_WORK);
            System.out.println(json);
        }
    }

    /**
     * Тестируем парсинг резюме из jobinmoscow.ru
     */
    @Ignore
    @Test
    public void resumeJobInMoscowParserTestFile() {
        File folder = new File("D:\\RESUME\\resumeJIM");
        for (File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());
            String json = ResumeParser.parse(fileEntry, Type.JOB_IN_MOSCOW);
            System.out.println(json);
        }
    }

    /**
     * Тестируем парсинг резюме из job-mo.ru
     */
    @Ignore
    @Test
    public void resumeJobMoParserTestFile() throws IOException {
        File folder = new File("D:\\RESUME\\resumeJobMO");
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) continue;
            System.out.println(fileEntry.getName());
            String json = ResumeParser.parse(IOUtils.toString(new FileInputStream(fileEntry), "Cp1251"), Type.JOB_MO);
            System.out.println(json);
        }
    }

    /**
     * Тестируем парсинг резюме из trudvsem.ru
     */
    @Ignore
    @Test
    public void resumeTVParserTestFile() throws IOException {
        File folder = new File("D:\\RESUME\\resumeTV");
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) continue;
            System.out.println(fileEntry.getName());
            String json = ResumeParser.parse(fileEntry, Type.TRUD_VSEM);
            System.out.println(json);
        }
    }

    /**
     * Тестируем парсинг резюме из rabota19.ru (Работа в Абакане)
     */
    @Ignore
    @Test
    public void resumeJobAbakanParserTestFile() throws IOException {
        File folder = new File("D:\\RESUME\\resumeJA");
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) continue;
            System.out.println(fileEntry.getName());
            String json = ResumeParser.parse(fileEntry, Type.JOB_IN_ABAKAN);
            System.out.println(json);
        }
    }

}
