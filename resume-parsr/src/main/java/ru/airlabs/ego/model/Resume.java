package ru.airlabs.ego.model;

import java.util.ArrayList;
import java.util.List;

public class Resume {

    private String vacancy;
    private String salary;
    private String name;
    private String photoURL;
    private String gender;
    private String age;
    private String birthDate;
    private String addressLocality;
    private String metro;
    private String telephone;
    private String email;
    private String contactPreferred;
    private List<String> contacts;
    private String specializationCategory;
    private List<String> positionSpecializations;
    private List<Experience> experiences;
    private List<String> skills;
    //private String driverExperience;
    private String aboutMe;
    private String education;
    private List<Education> educationsInfo;
    private List<String> languages;
    private List<AdditionalEducation> additionalEducations;
    private List<Examination> examinations;
    private String citizenship;

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    //    public String getDriverExperience() {
//        return driverExperience;
//    }
//
//    public void setDriverExperience(String driverExperience) {
//        this.driverExperience = driverExperience;
//    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public List<String> getPositionSpecializations() {
        return positionSpecializations;
    }

    public void setPositionSpecializations(List<String> positionSpecializations) {
        this.positionSpecializations = positionSpecializations;
    }

    public String getSpecializationCategory() {
        return specializationCategory;
    }

    public void setSpecializationCategory(String specializationCategory) {
        this.specializationCategory = specializationCategory;
    }

    public String getMetro() {
        return metro;
    }

    public void setMetro(String metro) {
        this.metro = metro;
    }

    public String getAddressLocality() {
        return addressLocality;
    }

    public void setAddressLocality(String addressLocality) {
        this.addressLocality = addressLocality;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public List<Examination> getExaminations() {
        return examinations;
    }

    public void setExaminations(List<Examination> examinations) {
        this.examinations = examinations;
    }

    public List<AdditionalEducation> getAdditionalEducations() {
        if (additionalEducations == null) {
            additionalEducations = new ArrayList<>();
        }
        return additionalEducations;
    }

    public void setAdditionalEducations(List<AdditionalEducation> additionalEducations) {
        this.additionalEducations = additionalEducations;
    }

    public List<String> getLanguages() {
        if (languages == null) {
            languages = new ArrayList<>();
        }
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<Education> getEducationsInfo() {
        if (educationsInfo == null) {
            educationsInfo = new ArrayList<>();
        }
        return educationsInfo;
    }

    public void setEducationsInfo(List<Education> educationsInfo) {
        this.educationsInfo = educationsInfo;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public List<String> getSkills() {
        if (skills == null) {
            skills = new ArrayList<>();
        }
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getVacancy() {
        return vacancy;
    }

    public void setVacancy(String vacancy) {
        this.vacancy = vacancy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getContactPreferred() {
        return contactPreferred;
    }

    public void setContactPreferred(String contactPreferred) {
        this.contactPreferred = contactPreferred;
    }

    public List<Experience> getExperiences() {
        if (experiences == null) {
            experiences = new ArrayList<>();
        }
        return experiences;
    }

    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }
}
