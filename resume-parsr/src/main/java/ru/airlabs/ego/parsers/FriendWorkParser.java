package ru.airlabs.ego.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.airlabs.ego.model.AdditionalEducation;
import ru.airlabs.ego.model.Education;
import ru.airlabs.ego.model.Experience;
import ru.airlabs.ego.model.Resume;

import java.util.ArrayList;
import java.util.List;

public class FriendWorkParser {

    private static final String NAME_ATTRIBUTE = "name";

    public static Resume parse(Document htmlFile) {
        Resume resume = new Resume();

        //ФИО
        String lastName = getValFromElem(htmlFile, "LastName");
        String firstName = getValFromElem(htmlFile, "FirstName");
        String middleName = getValFromElem(htmlFile, "MiddleName");
        resume.setName(lastName + " " + firstName + " " + middleName);

        //Адрес
        resume.setAddressLocality(getValFromElem(htmlFile, "City"));

        //Метро
        Element subwayElement = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Subway").first();
        String subway = getSelectedField(subwayElement);
        resume.setMetro(subway);

        //Дата рождения
        resume.setBirthDate(getValFromElem(htmlFile, "BirthDate"));

        //Пол
        Elements genderElements = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Sex");
        String gender = genderElements != null ? genderElements.select("[checked]").attr("id") : null;
        resume.setGender(gender);

        //Возраст
        resume.setAge(getValFromElem(htmlFile, "Age"));

        //Телефон
        String phone1 = getValFromElem(htmlFile, "Phone1");
        String phone2 = getValFromElem(htmlFile, "Phone2");
        resume.setTelephone(phone1 + (phone2 == null || phone2.isEmpty() ? "" : ", " + phone2));

        //емэйл
        String email1 = getValFromElem(htmlFile, "Email1");
        String email2 = getValFromElem(htmlFile, "Email2");
        resume.setEmail(email1 + (email2 == null || email2.isEmpty() ? "" : ", " + email2));

        //Вакансия
        resume.setVacancy(getValFromElem(htmlFile, "Position"));

        //Зарплата
        String salary = getValFromElem(htmlFile, "Salary");
        Element currencyElement = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Currency").first();
        String currency = getSelectedField(currencyElement);
        if (salary != null && !salary.isEmpty()) resume.setSalary(salary + " " + currency);

        //Контакты
        List<String> contacts = new ArrayList<>();
        String VK = getValFromElem(htmlFile, "VK");
        if (VK != null && !VK.isEmpty()) contacts.add("VK: " + VK);
        String LI = getValFromElem(htmlFile, "LI");
        if (LI != null && !LI.isEmpty()) contacts.add("linkedin: " + LI);
        String TW = getValFromElem(htmlFile, "TW");
        if (TW != null && !TW.isEmpty()) contacts.add("twitter: " + TW);
        String MK = getValFromElem(htmlFile, "MK");
        if (MK != null && !MK.isEmpty()) contacts.add("my_round: " + MK);
        String FB = getValFromElem(htmlFile, "FB");
        if (FB != null && !FB.isEmpty()) contacts.add("facebook: " + FB);
        String GP = getValFromElem(htmlFile, "GP");
        if (GP != null && !GP.isEmpty()) contacts.add("gplus: " + GP);
        String SK = getValFromElem(htmlFile, "SK");
        if (SK != null && !SK.isEmpty()) contacts.add("skype: " + SK);
        String OK = getValFromElem(htmlFile, "OK");
        if (OK != null && !OK.isEmpty()) contacts.add("ok: " + OK);
        resume.setContacts(contacts);


        //Опыт работы
        List<Experience> experiences = new ArrayList<>();
        for (int i = 0; true; i++) {
            Element companyElement = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Experience[" + i + "].Company").first();
            if (companyElement == null || companyElement.val().isEmpty()) break;
            Experience experience = new Experience();
            experience.setCompanyName(companyElement.val());

            experience.setExperiencePosition(getValFromElem(htmlFile, "Experience[" + i + "].Position"));

            experience.setAddressLocality(getValFromElem(htmlFile, "Experience[" + i + "].City"));

            Element fromMonthExpElement = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Experience[" + i + "].FromMonth").first();
            String fromMonthExp = getSelectedField(fromMonthExpElement);
            Element fromYearExpElement = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Experience[" + i + "].FromYear").first();
            String fromYear = getSelectedField(fromYearExpElement);
            Element toMonthExpElement = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Experience[" + i + "].ToMonth").first();
            String toMonthExp = getSelectedField(toMonthExpElement);
            Element toYearExpElement = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Experience[" + i + "].ToYear").first();
            String toYearExp = getSelectedField(toYearExpElement);
            experience.setDates(fromMonthExp + " " + fromYear + " - " + toMonthExp + " " + toYearExp);

            experience.setExperienceDescription(getValFromElem(htmlFile, "Experience[" + i + "].Description"));
            experiences.add(experience);
        }
        resume.setExperiences(experiences);


        //Повышение квалификации, курсы
        List<AdditionalEducation> additionalEducations = new ArrayList<>();
        for (int i = 0; true; i++) {
            Element organizationElement = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Courses[" + i + "].Organization").first();
            if (organizationElement == null || organizationElement.val().isEmpty()) break;
            AdditionalEducation education = new AdditionalEducation();
            education.setEducationOrganization(organizationElement.val());

            education.setEducationName(getValFromElem(htmlFile, "Courses[" + i + "].Name"));

            Element yearExpElement = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Courses[" + i + "].Year").first();
            String yearExp = getSelectedField(yearExpElement);
            education.setDate(yearExp);
            additionalEducations.add(education);
        }
        resume.setAdditionalEducations(additionalEducations);

        //Образование
        List<Education> educations = new ArrayList<>();
        for (int i = 0; true; i++) {
            Element universityElement = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Education[" + i + "].University").first();
            if (universityElement == null || universityElement.val().isEmpty()) break;
            Education education = new Education();
            education.setEducationOrganization(universityElement.val());

            education.setEducationName(getValFromElem(htmlFile, "Education[" + i + "].Faculty"));

            Element graduateYearElement = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Education[" + i + "].GraduateYear").first();
            String graduateYear = getSelectedField(graduateYearElement);
            education.setDate(graduateYear);

            Element levelElement = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Education[" + i + "].Level").first();
            String level = getSelectedField(levelElement);
            education.setLevel(level);
            educations.add(education);
        }
        resume.setEducationsInfo(educations);

        //Знание языков
        List<String> languages = new ArrayList<>();
        for (int i = 0; true; i++) {
            Element langsElement = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Langs[" + i + "]").first();
            if (langsElement == null || "not_selected".equals(getSelectedField(langsElement))) break;
            String lang = getSelectedField(langsElement);

            Element langLevelsElement = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "LangLevels[" + i + "]").first();
            String langLevel = getSelectedField(langLevelsElement);
            languages.add(lang + " - " + langLevel);
        }
        resume.setLanguages(languages);

        //Ключевые навыки
        Elements skilsElements = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, "Skills");
        List<String> skils = new ArrayList<>();
        skilsElements.forEach(e -> {
            if (e.val() != null && !e.val().isEmpty())
                skils.add(e.val());
        });
        resume.setSkills(skils);

        //Гражданство
        resume.setCitizenship(getValFromElem(htmlFile, "Citizenship"));

        //Обо мне
        resume.setAboutMe(getValFromElem(htmlFile, "AboutMe"));
        return resume;
    }

    private static String getVal(Element element) {
        return element != null ? element.val() : null;
    }

    private static String getValFromElem(Document htmlFile, String attributeName) {
        Element element = htmlFile.getElementsByAttributeValue(NAME_ATTRIBUTE, attributeName).first();
        return getVal(element);
    }

    private static String getSelectedField(Element element) {
        if (element == null) return null;
        else if (!element.select("[selected]").isEmpty()) return element.select("[selected]").first().text();
        else if (!element.select("select > option").isEmpty()) return element.select("select > option").first().text();
        else return null;
    }

}
