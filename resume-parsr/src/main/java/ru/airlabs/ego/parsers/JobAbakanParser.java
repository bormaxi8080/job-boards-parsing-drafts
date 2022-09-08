package ru.airlabs.ego.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.airlabs.ego.model.Education;
import ru.airlabs.ego.model.Experience;
import ru.airlabs.ego.model.Resume;

import static org.jsoup.helper.StringUtil.isBlank;

/**
 * Класс для парсинга резюме из сервиса rabota19.ru (Работа в Абакане)
 *
 * @author Aleksey Gorbachev
 */
public class JobAbakanParser {

    private static final String RESUME_MAIN_BLOCK = "main-content";

    public static Resume parse(Document htmlFile) {
        Resume resume = new Resume();
        // получаем основной блок с информацией о соискателе
        final Element resumeBlock = htmlFile.select("div." + RESUME_MAIN_BLOCK).first();
        // получаем и заполняем блок с названием вакансии
        resume.setVacancy(getText(resumeBlock.select("h1.red").first()));
        // получаем блоки заголовков и информационные блоки
        Elements titleElements = resumeBlock.getElementsByTag("h5");
        Elements infoElements = resumeBlock.select("div.margin10, div[style]");
        // заполняем имя, пол, возраст, адрес для соискателя
        fillNameAndSexAndAgeAndAddress(resume, titleElements.get(0));
        for (int i = 1; i < titleElements.size(); i++) {
            String title = getText(titleElements.get(i));
            if (title.contains("Заработная плата")) {
                fillSalary(resume, infoElements.get(i));
            }
            if (title.contains("Образование")) {
                fillEducationInfo(resume, infoElements.get(i));
            }
            if (title.contains("Опыт работы")) {
                fillExperienceInfo(resume, infoElements.get(i));
            }
            if (title.contains("Личные качества")) {
                resume.setAboutMe(getText(infoElements.get(i)));
            }
            if (title.contains("Дополнительно")) {
                fillAdditionalInfo(resume, infoElements.get(i));
            }
            if (title.contains("Контактная информация")) {
                int index = i + 1 == titleElements.size() ? i : i + 1;
                fillContactInfo(resume, infoElements.get(index));
            }
        }
        //URL фото
        Elements photoURLElement = resumeBlock.getElementsByClass("resume-avatar");
        if (!photoURLElement.isEmpty()) {
            resume.setPhotoURL(photoURLElement.first().getElementsByTag("img").first().attr("src"));
        }

        return resume;
    }

    /**
     * Заполнить пол, возраст, адрес для соискателя
     *
     * @param resume  резюме
     * @param element DOM-элемент
     */
    private static void fillNameAndSexAndAgeAndAddress(Resume resume, Element element) {
        String text = getText(element);
        String[] parts = text.split(",");
        if (parts.length == 4) {
            resume.setName(parts[0].trim());
            resume.setGender(parts[1].trim());
            resume.setAge(parts[2].trim());
            resume.setAddressLocality(parts[3].trim());
        } else if (parts.length == 3) {
            resume.setGender(parts[0].trim());
            resume.setAge(parts[1].trim());
            resume.setAddressLocality(parts[2].trim());
        } else if (parts.length == 2) {
            resume.setGender(parts[0].trim());
            resume.setAge(parts[1].trim());
        } else {
            resume.setGender(parts[0].trim());
        }
    }

    /**
     * Заполнить зарплату для соискателя
     *
     * @param resume  резюме
     * @param element DOM-элемент
     */
    private static void fillSalary(Resume resume, Element element) {
        String text = getText(element);
        if (!isBlank(text)) {
            resume.setSalary(text.replace("\u20BD", "руб.").trim());
        }
    }

    /**
     * Заполнить информацию об образовании для соискателя
     *
     * @param resume  резюме
     * @param element DOM-элемент
     */
    private static void fillEducationInfo(Resume resume, Element element) {
        Elements educationElements = element.getElementsByTag("p");
        for (int i = 0; i < educationElements.size(); i++) {
            Element el = educationElements.get(i);
            String text = getText(el);
            if (isBlank(text)) continue;
            if (text.contains("Образование")) {
                resume.setEducation(text);
            } else if (text.contains("Образовательные учреждения")) {
                Education education = new Education();
                education.setEducationName(resume.getEducation());
                education.setEducationOrganization(text);
                resume.getEducationsInfo().add(education);
            }
        }
    }

    /**
     * Заполнить информацию об опыте для соискателя
     *
     * @param resume  резюме
     * @param element DOM-элемент
     */
    private static void fillExperienceInfo(Resume resume, Element element) {
        Elements experienceElements = element.getElementsByTag("p");
        for (int i = 0; i < experienceElements.size(); i++) {
            Element el = experienceElements.get(i);
            String text = getText(el);
            if (isBlank(text)) continue;
            if (text.contains("Общий стаж работы")) {
                Experience experience = new Experience();
                experience.setExperienceTimeinterval(text);
                resume.getExperiences().add(experience);
                resume.getSkills().add(text);
            } else {
                if (!resume.getExperiences().isEmpty()) {
                    resume.getExperiences().get(0).setExperienceDescription(text);
                } else {
                    Experience exp = new Experience();
                    exp.setExperienceDescription(text);
                    resume.getExperiences().add(exp);
                }

            }
        }
    }

    /**
     * Заполнить доп. информацию для соискателя
     *
     * @param resume  резюме
     * @param element DOM-элемент
     */
    private static void fillAdditionalInfo(Resume resume, Element element) {
        Elements additionalElements = element.getElementsByTag("p");
        for (int i = 0; i < additionalElements.size(); i++) {
            Element el = additionalElements.get(i);
            String text = getText(el);
            if (isBlank(text)) continue;
            if (text.contains("Иностранные языки")) {
                resume.getLanguages().add(text);
            } else {
                resume.getSkills().add(text);
            }
        }
    }

    /**
     * Заполнить контакты для соискателя (телефон)
     *
     * @param resume  резюме
     * @param element DOM-элемент
     */
    private static void fillContactInfo(Resume resume, Element element) {
        Elements elements = element.getElementsByTag("div");
        for (int i = 0; i < elements.size(); i++) {
            Element contactElement = elements.get(i);
            String text = getText(contactElement);
            if (!isBlank(text) && text.contains("Телефон")) {
                resume.setTelephone(getText(contactElement.getElementsByTag("strong").first()));
            }
        }
    }

    private static String getText(Element element) {
        return element != null ? element.text() : null;
    }
}
