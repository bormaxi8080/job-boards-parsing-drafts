package ru.airlabs.ego;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.airlabs.ego.model.Resume;
import ru.airlabs.ego.parsers.*;

import java.io.File;
import java.io.IOException;


/**
 * Парсер резюме из html в json
 */
public class ResumeParser {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * @param string html-файл в виде строки
     * @param type   тип сайта
     * @return json строка
     */
    public static String parse(String string, Type type) {
        Document htmlFile = Jsoup.parse(string, "UTF-8");
        String resume = parse(htmlFile, type);
        return resume;
    }

    /**
     * @param file html-файл
     * @param type тип сайта
     * @return json строка
     */
    public static String parse(File file, Type type) {
        Document htmlFile = null;
        try {
            htmlFile = Jsoup.parse(file, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parse(htmlFile, type);
    }

    private static String parse(Document document, Type type) {
        Resume resume = null;
        if (Type.HH.equals(type)) {
            resume = HHParser.parse(document);
        } else if (Type.FRIEND_WORK.equals(type)) {
            resume = FriendWorkParser.parse(document);
        } else if (Type.JOB_IN_MOSCOW.equals(type)) {
            resume = JIMParser.parse(document);
        } else if (Type.JOB_MO.equals(type)) {
            resume = JobMOParser.parse(document);
        } else if (Type.TRUD_VSEM.equals(type)) {
            resume = TVParser.parse(document);
        } else if (Type.JOB_IN_ABAKAN.equals(type)) {
            resume = JobAbakanParser.parse(document);
        }

        String json = null;
        try {
            json = jsonMapper.writeValueAsString(resume);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }
}
