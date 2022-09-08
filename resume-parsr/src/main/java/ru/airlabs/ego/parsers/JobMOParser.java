package ru.airlabs.ego.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.airlabs.ego.model.Education;
import ru.airlabs.ego.model.Experience;
import ru.airlabs.ego.model.Resume;

import java.util.ArrayList;
import java.util.List;

import static org.jsoup.helper.StringUtil.isBlank;
import static ru.airlabs.ego.parsers.JIMParser.convertMonthToDate;
import static ru.airlabs.ego.parsers.JIMParser.formatDateToken;

/**
 * Класс для парсинга резюме из сервиса job-mo.ru
 *
 * @author Aleksey Gorbachev
 */
public class JobMOParser {

    private static final String RESUME_MAIN_CLASS = "main-box";

    public static Resume parse(Document htmlFile) {
        Resume resume = new Resume();
        // получаем основной блок с информацией о соискателе
        final Element resumeBlock = htmlFile.getElementsByClass(RESUME_MAIN_CLASS).first();

        // заполняем основную информацию из таблицы
        Elements elements = resumeBlock.select("table.table-to-div").first().getElementsByTag("tr");
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            Elements rows =  element.getElementsByTag("td");
            if (rows.size() < 2) continue;
            Element elementName = rows.get(0);
            Element elementContent = rows.get(1);

            if (elementName.text().contains("Имя")) {
                resume.setName(elementContent.text());
            }
            if (elementName.text().contains("Пол")) {
                resume.setGender(elementContent.text());
            }
            if (elementName.text().contains("Возраст")) {
                fillBirthDateAndAgeInResume(resume, elementContent.text());
            }
            if (elementName.text().contains("Телефон")) {
                resume.setTelephone(elementContent.text());
            }
            if (elementName.text().contains("E-mail")) {
                resume.setEmail(elementContent.text());
            }
            if (elementName.text().contains("Проживание")) {
                resume.setAddressLocality(elementContent.text());
            }
            if (elementName.text().contains("Заработная плата")) {
                resume.setSalary(elementContent.text());
            }
            if (elementName.text().contains("Иностранные языки")) {
                List<String> languages = new ArrayList<>();
                languages.add(elementContent.text());
                resume.setLanguages(languages);
            }
            if (elementName.text().contains("Навыки и умения")) {
                List<String> skills = new ArrayList<>();
                skills.add(elementContent.text());
                resume.setSkills(skills);
            }
            if (elementName.text().contains("Образование")) {
                String educationName = elementContent.text();
                Element educationDateRow = elements.get(i + 1);
                Element educationOrganizationRow = elements.get(i + 2);
                if (educationDateRow.getElementsByTag("td").get(0).text().contains("Окончание")) { // проверяем, что попали в блок образования
                    Education education = new Education();
                    education.setEducationName(educationName);
                    education.setDate(educationDateRow.getElementsByTag("td").get(1).text());
                    education.setEducationOrganization(educationOrganizationRow.getElementsByTag("td").get(1).text());

                    resume.getEducationsInfo().add(education);
                } else {
                    resume.setEducation(educationName);
                }
            }
            if (elementName.text().contains("Период работы")) {
                String experiencePeriod = elementContent.text();
                Element experiencePositionRow = elements.get(i + 1);
                Element experienceOrganizationRow = elements.get(i + 2);
                Element experienceDescriptionRow = elements.get(i + 3);
                if (experiencePositionRow.getElementsByTag("td").get(0).text().contains("Должность")) { // проверяем, что попали в блок опыта
                    Experience experience = new Experience();
                    experience.setDates(experiencePeriod);
                    experience.setExperiencePosition(experiencePositionRow.getElementsByTag("td").get(1).text());
                    experience.setCompanyName(experienceOrganizationRow.getElementsByTag("td").get(1).text());
                    experience.setExperienceDescription(experienceDescriptionRow.getElementsByTag("td").get(1).text());
                    resume.getExperiences().add(experience);
                }
            }
        }

        // должность
        resume.setVacancy(resumeBlock.getElementsByTag("h1").first().text());
        //URL фото
        Element photoURLElement = resumeBlock.getElementsByTag("img").first();
        String photoURL = photoURLElement != null ? photoURLElement.attr("src") : null;
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
        resume.setAge(tokens[0]);
        if (tokens.length < 5) return;
        resume.setBirthDate(formatDateToken(tokens[3].replaceAll("[^0-9]+","")) +
                "." + convertMonthToDate(tokens[4]) +
                "." + tokens[5].replaceAll("[^0-9]+",""));
    }
}
