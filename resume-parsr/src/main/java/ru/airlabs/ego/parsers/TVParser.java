package ru.airlabs.ego.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.airlabs.ego.model.AdditionalEducation;
import ru.airlabs.ego.model.Education;
import ru.airlabs.ego.model.Experience;
import ru.airlabs.ego.model.Resume;

import static org.jsoup.helper.StringUtil.isBlank;

/**
 * Класс для парсинга резюме из сервиса trudvsem.ru
 *
 * @author Aleksey Gorbachev
 */
public class TVParser {

    private static final String RESUME_MAIN_TAG = "body";

    public static Resume parse(Document htmlFile) {
        Resume resume = new Resume();
        // получаем основной блок с информацией о соискателе
        final Element resumeBlock = htmlFile.getElementsByTag(RESUME_MAIN_TAG).first();
        // заполняем блоки с информацией из таблицы
        Elements elements = resumeBlock.select("td.row");
        for (Element element : elements) {
            // получаем название блока с информацией по резюме
            Element titleElement = element.getElementsByClass("title").first();
            if (titleElement == null) continue;
            String title = titleElement.text();
            if (isBlank(title)) continue;

            if (title.contains("Основная информация")) {
                fillGeneralInfoInResume(resume, element);
            }
            if (title.contains("Контактная информация")) {
                fillContactInfoInResume(resume, element);
            }
            if (title.contains("История трудовой деятельности")) {
                fillExperienceInfoInResume(resume, element);
            }
            if (title.contains("Образование")) {
                fillEducationInfoInResume(resume, element);
            }
            if (title.contains("Повышение квалификации/курсы (дополнительное образование)")) {
                fillAdditionalEducationInfoInResume(resume, element);
            }
            if (title.contains("Личные и профессиональные качества")) {
                fillSkillsInfoInResume(resume, element);
            }
            if (title.contains("Владение языками")) {
                fillLanguagesInfoInResume(resume, element);
            }
        }
        return resume;
    }

    /**
     * Заполнение основной информации в резюме
     *
     * @param resume      резюме
     * @param infoElement блок с основной информацией
     */
    private static void fillGeneralInfoInResume(Resume resume, Element infoElement) {
        resume.setName(infoElement.getElementsByClass("person-name").first().text());
        resume.setVacancy(infoElement.getElementsByClass("cv-name").first().text());
        resume.setSalary(infoElement.getElementsByClass("cv-salary").first().text());
        //URL фото
        Element photoURLElement = infoElement.getElementsByClass("cv-common-main").first().getElementsByTag("img").first();
        String photoURL = photoURLElement != null ? photoURLElement.attr("src") : null;
        resume.setPhotoURL(photoURL);

        Elements keyElements = infoElement.select("td.key");
        for (Element element : keyElements) {
            if (element.text().contains("Пол:")) {
                resume.setGender(element.parent().select("td.value").first().text());
            }
            if (element.text().contains("Дата рождения:")) {
                resume.setBirthDate(element.parent().select("td.value").first().text());
            }
        }
    }

    /**
     * Заполнение контактной информации в резюме
     *
     * @param resume      резюме
     * @param infoElement блок с контактной информацией
     */
    private static void fillContactInfoInResume(Resume resume, Element infoElement) {
        Elements keyElements = infoElement.select("td.key");
        for (Element element : keyElements) {
            if (element.text().contains("Эл. почта")) {
                resume.setEmail(element.parent().select("td.value").first().text());
            }
            if (element.text().contains("Телефон")) {
                resume.setTelephone(formatPhoneNumber(element.parent().select("td.value").first().text()));
            }
            if (element.text().contains("Населенный пункт")) {
                resume.setAddressLocality(element.parent().select("td.value").first().text());
            }
            if (element.text().contains("Регион")) {
                resume.setAddressLocality(element.parent().select("td.value").first().text());
            }
        }
    }

    /**
     * Заполнение информации по опыту в резюме
     *
     * @param resume      резюме
     * @param infoElement блок с информацией по опыту
     */
    private static void fillExperienceInfoInResume(Resume resume, Element infoElement) {
        Elements expElements = infoElement.select("td.subrow");
        for (Element element : expElements) {
            final String experiencePosition = element.getElementsByClass("select-text").first().text();
            Elements experienceRows = element.getElementsByTag("tr");
            if (experienceRows.size() < 4) continue;   // пропускаем не полностью заполненный опыт
            Element experiencePeriodStartRow = experienceRows.get(0);
            Element experiencePeriodEndRow = experienceRows.get(1);
            Element experienceOrganizationRow = experienceRows.get(2);
            Element experienceDescriptionRow = experienceRows.get(3);
            if (experiencePeriodStartRow.select("td.key").first().text().contains("Месяц начала")) {

                Experience experience = new Experience();
                experience.setDates(experiencePeriodStartRow.select("td.value").first().text() + " - " +
                        experiencePeriodEndRow.select("td.value").first().text());
                experience.setExperiencePosition(experiencePosition);
                experience.setCompanyName(experienceOrganizationRow.select("td.value").first().text());
                experience.setExperienceDescription(experienceDescriptionRow.select("td.value").first().text());
                resume.getExperiences().add(experience);

            }
        }
    }

    /**
     * Заполнение информации по образованию в резюме
     *
     * @param resume      резюме
     * @param infoElement блок с информацией по образованию
     */
    private static void fillEducationInfoInResume(Resume resume, Element infoElement) {
        Elements eduElements = infoElement.select("td.subrow");
        final String educationLevel = infoElement.getElementsByClass("test").first().text();
        resume.setEducation(educationLevel);
        for (Element element : eduElements) {
            Education education  = new Education();

            Elements educationRows = element.getElementsByTag("tr");
            for (Element row : educationRows) {
                if (row.select("td.key").first().text().contains("Наименование учебного заведения")) {
                    education.setEducationOrganization(row.select("td.value").first().text());
                    continue;
                }
                if (row.select("td.key").first().text().contains("Год окончания")) {
                    education.setDate(row.select("td.value").first().text());
                    continue;
                }
                if (row.select("td.key").first().text().contains("Специальность по диплому")) {
                    education.setEducationName(row.select("td.value").first().text());
                    continue;
                }
            }

            resume.getEducationsInfo().add(education);
        }
    }

    /**
     * Заполнение информации по доп. образованию в резюме
     *
     * @param resume      резюме
     * @param infoElement блок с информацией по доп. образованию
     */
    private static void fillAdditionalEducationInfoInResume(Resume resume, Element infoElement) {
        Elements eduElements = infoElement.select("td.subrow");
        for (Element element : eduElements) {
            Elements educationRows = element.getElementsByTag("tr");
            if (educationRows.size() < 3) continue;   // пропускаем не полностью заполненное образование
            Element educationOrganizationRow = educationRows.get(0);
            Element educationDateRow = educationRows.get(1);
            Element educationNameRow = educationRows.get(2);
            if (educationOrganizationRow.select("td.key").first().text().contains("Наименование учебного заведения")) {

                AdditionalEducation education = new AdditionalEducation();
                education.setDate(educationDateRow.select("td.value").first().text());
                education.setEducationOrganization(educationOrganizationRow.select("td.value").first().text());
                education.setEducationName(educationNameRow.select("td.value").first().text());
                resume.getAdditionalEducations().add(education);
            }
        }
    }

    /**
     * Заполнение личной информации в резюме
     *
     * @param resume      резюме
     * @param infoElement блок с личной информацией
     */
    private static void fillSkillsInfoInResume(Resume resume, Element infoElement) {
        Elements keyElements = infoElement.select("td.key");
        for (Element element : keyElements) {
            if (element.text().contains("Профессиональные качества")) {
                resume.getSkills().add(element.parent().select("td.value").first().text());
            }
            if (element.text().contains("Личные качества")) {
                resume.getSkills().add(element.parent().select("td.value").first().text());
            }
            if (element.text().contains("Иная информация")) {
                resume.setAboutMe(element.parent().select("td.value").first().text());
            }
        }
    }

    /**
     * Заполнение информации о языках в резюме
     *
     * @param resume      резюме
     * @param infoElement блок с информацией о языках
     */
    private static void fillLanguagesInfoInResume(Resume resume, Element infoElement) {
        Elements elements = infoElement.select("td.subrow");
        for (Element element : elements) {
            if (element.select("td.key").first().text().contains("Язык")) {

                resume.getLanguages().add(element.parent().select("td.value").get(0).text() + " - " +
                        element.parent().select("td.value").get(1).text());
            }
        }
    }

    private static String formatPhoneNumber(String phoneValue) {
        if (isBlank(phoneValue)) return null;
        return phoneValue.replaceAll("[^0-9]+","");
    }
}
