package ru.airlabs.ego.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.airlabs.ego.model.*;

import java.util.ArrayList;
import java.util.List;

public class HHParser {

    private static final String DATA_QA_ATTRIBUTE = "data-qa";

    private static final String ITEMPROP_ATTRIBUTE = "itemprop";

    public static Resume parse(Document htmlFile) {
        Resume resume = new Resume();

        //Вакансия
        resume.setVacancy(getTextFromElementWithDataQaAttribute(htmlFile, "resume-block-title-position"));

        //Зарплата
        resume.setSalary(getTextFromElementWithDataQaAttribute(htmlFile, "resume-block-salary"));

        //ФИО;
        resume.setName(getTextFromElementWithDataQaAttribute(htmlFile, "resume-personal-name"));

        //URL фото
        Element photoURLElement = htmlFile.getElementsByAttributeValue(DATA_QA_ATTRIBUTE, "resume-photo-image").first();
        String photoURL = photoURLElement != null ? photoURLElement.attr("src") : null;
        resume.setPhotoURL(photoURL);

        //Пол
        resume.setGender(getTextFromElementWithDataQaAttribute(htmlFile, "resume-personal-gender"));

        //Возраст
        resume.setAge(getTextFromElementWithDataQaAttribute(htmlFile, "resume-personal-age"));

        //Дата рождения
        Element birthDateElement = htmlFile.getElementsByAttributeValue(ITEMPROP_ATTRIBUTE, "birthDate").first();
        String birthDate = birthDateElement != null ? birthDateElement.attr("content") : null;
        resume.setBirthDate(birthDate);

        //Адрес
        resume.setAddressLocality(getTextFromElementWithItempropAttribute(htmlFile, "addressLocality"));

        //Метро
        resume.setMetro(getTextFromElementWithDataQaAttribute(htmlFile, "resume-personal-metro"));

        //Телефон
        Element telephonePrintElement = htmlFile.getElementsByClass("resume__contacts-phone-print-version").first();
        if (telephonePrintElement != null) {
            Element telephoneElement = telephonePrintElement.getElementsByAttributeValue(ITEMPROP_ATTRIBUTE, "telephone").first();
            resume.setTelephone(getText(telephoneElement));
        } else {
            resume.setTelephone(getTextFromElementWithItempropAttribute(htmlFile, "telephone"));
        }

        //емэйл
        resume.setEmail(getTextFromElementWithItempropAttribute(htmlFile, "email"));


        //Предпочитаемый способ связи
        resume.setContactPreferred(getTextFromElementWithDataQaAttribute(htmlFile, "resume-contact-preferred"));

        //Контакты
        Elements contactsElements = htmlFile.getElementsByClass("resume-header-contact");
        List<String> contacts = new ArrayList<>();
        contactsElements.forEach(e -> {
            if (!e.getElementsByClass("siteicon_skype").isEmpty())
                contacts.add("skype: " + e.text());
            else contacts.add(e.text());
        });
        resume.setContacts(contacts);

        //Категория
        resume.setSpecializationCategory(getTextFromElementWithDataQaAttribute(htmlFile, "resume-block-specialization-category"));

        //Специализация
        Elements positionSpecializationsElements = htmlFile.getElementsByAttributeValue(DATA_QA_ATTRIBUTE, "resume-block-position-specialization");
        List<String> positionSpecializations = new ArrayList<>();
        positionSpecializationsElements.forEach(e -> {
            positionSpecializations.add(e.text());
        });
        resume.setPositionSpecializations(positionSpecializations);

        //Опыт работы
        Elements works = htmlFile.getElementsByAttributeValue(ITEMPROP_ATTRIBUTE, "worksFor");
        List<Experience> experiences = new ArrayList<>();
        works.forEach(e -> {
            Experience experience = new Experience();

            String companyName = e.getElementsByAttributeValue(ITEMPROP_ATTRIBUTE, "name").text();
            experience.setCompanyName(companyName);

            String experiencePosition = e.getElementsByAttributeValue(DATA_QA_ATTRIBUTE, "resume-block-experience-position").text();
            experience.setExperiencePosition(experiencePosition);

            String experienceDescription = e.getElementsByAttributeValue(DATA_QA_ATTRIBUTE, "resume-block-experience-description").text();
            experience.setExperienceDescription(experienceDescription);

            String experienceAddressLocality = e.getElementsByAttributeValue(ITEMPROP_ATTRIBUTE, "addressLocality").text();
            experience.setAddressLocality(experienceAddressLocality);

            String experienceTimeinterval = e.getElementsByClass("resume-block__experience-timeinterval").text();
            experience.setExperienceTimeinterval(experienceTimeinterval);

            String beginEndDate = e.getElementsByClass("bloko-column_l-2").text().replaceFirst(" " + experienceTimeinterval, "");
            experience.setDates(beginEndDate);
            experiences.add(experience);
        });
        resume.setExperiences(experiences);

        //Ключевые навыки
        Elements skilsElements = htmlFile.getElementsByAttributeValue(DATA_QA_ATTRIBUTE, "bloko-tag__text");
        List<String> skils = new ArrayList<>();
        skilsElements.forEach(e -> {
            skils.add(e.text());
        });
        resume.setSkills(skils);

        //Обо мне
        resume.setAboutMe(getTextFromElementWithDataQaAttribute(htmlFile, "resume-block-skills"));

        //Образование
        Element educationsElement = htmlFile.getElementsByAttributeValue(DATA_QA_ATTRIBUTE, "resume-block-education").first();
        resume.setEducation(educationsElement.getElementsByClass("resume-block__title-text").text());

        Elements educationsElements = educationsElement.getElementsByClass("resume-block-item-gap");
        List<Education> educations = new ArrayList<>();
        educationsElements.forEach(e -> {
            Education education = new Education();
            String date = e.getElementsByClass("bloko-column_l-2").text();
            education.setDate(date);
            String eduName = e.getElementsByAttributeValue(ITEMPROP_ATTRIBUTE, "name").text();
            education.setEducationName(eduName);
            String educationOrganization = e.getElementsByAttributeValue(DATA_QA_ATTRIBUTE, "resume-block-education-organization").text();
            education.setEducationOrganization(educationOrganization);
            educations.add(education);
        });
        resume.setEducationsInfo(educations);


        //Знание языков
        Elements languageElements = htmlFile.getElementsByAttributeValue(DATA_QA_ATTRIBUTE, "resume-block-language-item");
        List<String> languages = new ArrayList<>();
        languageElements.forEach(e -> {
            languages.add(e.text());
        });
        resume.setLanguages(languages);

        //Повышение квалификации, курсы
        Element additionalEducationsElement = htmlFile.getElementsByAttributeValue(DATA_QA_ATTRIBUTE, "resume-block-additional-education").first();
        if (additionalEducationsElement != null) {
            Elements additionalEducationsElements = additionalEducationsElement.getElementsByClass("resume-block-item-gap");
            List<AdditionalEducation> additionalEducations = new ArrayList<>();
            additionalEducationsElements.forEach(e -> {
                AdditionalEducation education = new AdditionalEducation();
                String date = e.getElementsByClass("bloko-column_l-2").text();
                education.setDate(date);
                String eduName = e.getElementsByAttributeValue(ITEMPROP_ATTRIBUTE, "name").text();
                education.setEducationName(eduName);
                String educationOrganization = e.getElementsByAttributeValue(DATA_QA_ATTRIBUTE, "resume-block-education-organization").text();
                education.setEducationOrganization(educationOrganization);
                additionalEducations.add(education);
            });
            resume.setAdditionalEducations(additionalEducations);
        }

        //Аттестаты
        Element examinationsElement = htmlFile.getElementsByAttributeValue(DATA_QA_ATTRIBUTE, "resume-block-attestation-education").first();
        if (examinationsElement != null) {
            Elements examinationsElements = examinationsElement.getElementsByClass("resume-block-item-gap");
            List<Examination> examinations = new ArrayList<>();
            examinationsElements.forEach(e -> {
                Examination examination = new Examination();
                String date = e.getElementsByClass("bloko-column_l-2").text();
                examination.setDate(date);
                String eduName = e.getElementsByAttributeValue(ITEMPROP_ATTRIBUTE, "name").text();
                examination.setEducationName(eduName);
                String educationOrganization = e.getElementsByAttributeValue(DATA_QA_ATTRIBUTE, "resume-block-education-organization").text();
                examination.setEducationOrganization(educationOrganization);
                examinations.add(examination);
            });
            resume.setExaminations(examinations);
        }

        //Гражданство
        resume.setCitizenship(getTextFromElementWithItempropAttribute(htmlFile, "nationality"));

        return resume;
    }

    private static String getText(Element element) {
        return element != null ? element.text() : null;
    }

    private static String getTextFromElementWithItempropAttribute(Document htmlFile, String attributeName) {
        Element element = htmlFile.getElementsByAttributeValue(ITEMPROP_ATTRIBUTE, attributeName).first();
        return getText(element);
    }

    private static String getTextFromElementWithDataQaAttribute(Document htmlFile, String attributeName) {
        Element element = htmlFile.getElementsByAttributeValue(DATA_QA_ATTRIBUTE, attributeName).first();
        return getText(element);
    }
}
