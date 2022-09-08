package ru.airlabs.ego.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.airlabs.ego.model.Experience;
import ru.airlabs.ego.model.Resume;

import java.util.ArrayList;
import java.util.List;

import static org.jsoup.helper.StringUtil.isBlank;

/**
 * Класс для парсинга резюме из сервиса jobinmoscow.ru
 *
 * @author Aleksey Gorbachev
 */
public class JIMParser {

    private static final String RESUME_MAIN_CLASS = "rezume-block";
    private static final String RESUME_INFO_CLASS = "info-rezume";

    public static Resume parse(Document htmlFile) {
        Resume resume = new Resume();
        // получаем блок с основной информацией о соискателе
        final Element resumeBlock = htmlFile.getElementsByClass(RESUME_MAIN_CLASS).first();
        // получаем блок с дополнительной информацией
        final Element resumeInfoBlock = htmlFile.getElementsByClass(RESUME_INFO_CLASS).first();
        // получаем блок с контактами
        final Element resumeContactBlock = resumeBlock.getElementById("contacts");

        // заполняем основную информацию
        Elements elements = resumeBlock.getElementsByTag("p");
        for (Element element : elements) {
            String elementText = element.text();
            if (isBlank(elementText)) continue;
            if (elementText.contains("ФИО:")) {
                resume.setName(elementText.substring(5));
            }
            if (elementText.contains("Пол:")) {
                resume.setGender(elementText.substring(5));
            }
            if (elementText.contains("Город проживания:")) {
                resume.setAddressLocality(elementText.substring(18).replace("(показать на карте)", "").trim());
            }
            if (elementText.contains("Дата рождения:")) {
                fillBirthDateAndAgeInResume(resume, elementText.substring(15));
            }
        }
        // заполняем дополнительную информацию
        Elements infoElements = resumeInfoBlock.getElementsByTag("tr");
        for (Element infoElement : infoElements) {
            Element infoElementName = infoElement.getElementsByTag("td").get(0);
            Element infoElementContent = infoElement.getElementsByTag("td").get(1);

            if (infoElementName.text().contains("Образование")) {
                resume.setEducation(infoElementContent.text());
            }
            if (infoElementName.text().contains("Владение иностранными языками")) {
                List<String> languages = new ArrayList<>();
                languages.add(infoElementContent.text());
                resume.setLanguages(languages);
            }
            if (infoElementName.text().contains("Профессиональные навыки и знания")) {
                List<String> skills = new ArrayList<>();
                skills.add(infoElementContent.text());
                resume.setSkills(skills);
            }
            if (infoElementName.text().contains("Опыт работы")) {
                List<Experience> experiences = new ArrayList<>();
                Experience experience = new Experience();
                experience.setExperienceDescription(infoElementContent.text());
                experiences.add(experience);
                resume.setExperiences(experiences);
            }
            if (infoElementName.text().contains("Дополнительные сведения")) {
                resume.setAboutMe(infoElementContent.text());
            }
        }
        // заполняем контакты
        for (Element contactElement : resumeContactBlock.getElementsByTag("p")) {
            String contact = contactElement.text();
            if (isBlank(contact)) continue;
            if (contact.contains("Мобильный телефон:")) {
                resume.setTelephone(contact.substring(19));
            }
            if (contact.contains("E-mail:")) {
                resume.setEmail(contact.substring(8));
            }
        }
        // должность
        resume.setVacancy(resumeBlock.select("p.title").first().text());
        // зарплата
        String salary = resumeBlock.select("p.pay").first().text();
        resume.setSalary("по договоренности".equals(salary) ? null : salary);
        //URL фото
        Element photoURLElement = resumeBlock.getElementsByClass("left-block").first().getElementsByTag("a").first();
        String photoURL = photoURLElement != null ? photoURLElement.select("img").attr("src") : null;
        resume.setPhotoURL(photoURL);

        return resume;
    }

    /**
     * Заполнение даты рождения и возраста соискателя в резюме
     *
     * @param resume резюме соискателя
     * @param text строка
     */
    private static void fillBirthDateAndAgeInResume(Resume resume, String text) {
        if (isBlank(text)) return;
        String[] tokens = text.split("\\s+");
        if (tokens.length < 3) return;
        resume.setBirthDate(formatDateToken(tokens[0]) + "." + convertMonthToDate(tokens[1]) + "." + tokens[2]);
        if (tokens.length == 3) return;
        resume.setAge(tokens[3].replaceAll("[^0-9]+",""));
    }

    public static String formatDateToken(String token) {
        if (token.length() == 1) {
            return "0" + token;
        } else {
            return token;
        }
    }

    /**
     * Конвертация строкового названия месяца в числовой (например май -> 05)
     *
     * @return числовое представление месяца
     */
    public static String convertMonthToDate(String month) {
        switch (month) {
            case "января":
                return "01";
            case "февраля":
                return "02";
            case "марта":
                return "03";
            case "апреля":
                return "04";
            case "мая":
                return "05";
            case "июня":
                return "06";
            case "июля":
                return "07";
            case "августа":
                return "08";
            case "сентября":
                return "09";
            case "октября":
                return "10";
            case "ноября":
                return "11";
            case "декабря":
                return "12";
            default:
                return null;
        }
    }
}
