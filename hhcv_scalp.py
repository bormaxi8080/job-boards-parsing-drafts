import time
import os
import re
from bs4 import BeautifulSoup


def cv_parse(hh_filename):

    hh_file = open("HHSaves\\" + hh_filename, 'rb')

    soup = BeautifulSoup(hh_file, 'html.parser')  # Просим BeautifulSoup "разобрать" полученную
    # страничку на элементы, создав объект soup

    cvs = soup.find_all(class_="resume-search-item__content-wrapper")
    cvids = soup.find_all(class_="bloko-toggle HH-Employer-VacancyResponse-Topic-ExperienceTrigger")

    # data-hh-last-experience-id

    for line in range(0, len(cvs)):
        cv_file = open("CVStore\\cv_" + str(cvids[line].get('data-hh-last-experience-id') + ".html"), 'w', encoding='utf-8')

        cv_soup = BeautifulSoup(str(cvs[line]), 'html.parser') # Грузим снапшот в отдельный суп

        # Извлекаем и обрабатываем имя и возраст.
        cv_fullname = cv_soup.find(class_="resume-search-item__fullname")
        cv_fullname = str(cv_fullname.text)
        cv_fullname = cv_fullname.split(",")
        cv_fullname[0] = cv_fullname[0].strip()  # Убираем лишние пробелы в начале и конце ФИО
        cv_fullname[0] = re.sub(" +", " ", cv_fullname[0])  # Убираем лишние пробелы между словами
        cv_fullname_list = cv_fullname[0].split(" ")
        cv_fullname_template = ("\n<br>Фамилия", "\n<br>Имя", "\n<br>Отчество", "\n<br>#", "\n<br>#", "\n<br>#")

        cv_file.write("<p>")
        for name_item in range(0, len(cv_fullname_list)):
            cv_file.write(cv_fullname_template[name_item] + ": <b>" + str(cv_fullname_list[name_item]) + "</b>")
        cv_file.write("</p>\n\n\n")

        if len(cv_fullname) > 1:
            cv_file.write("<p>Возраст: " + str(cv_fullname[1]) + "</p>\n\n\n")

        cv_comp_list = cv_soup.find_all(class_="resume-search-item__company-name")
        cv_comp_set =[]

        cv_file.write("<ul>\n")
        for cv_comp in range(0, len(cv_comp_list)):
            cv_file.write("<li>" + str(cv_comp_list[cv_comp].text) + "\n")
            if str(cv_comp_list[cv_comp].text) not in cv_comp_set:
                cv_comp_set.append(str(cv_comp_list[cv_comp].text))
        cv_file.write("</ul>\n\n")

        cv_file.write("<p>\n")
        for cv_comp in range(0, len(cv_comp_set)):
            cv_file.write(cv_comp_set[cv_comp] + " \n")
        cv_file.write("</p>")

        cv_file.write("\n\n\n <br>--- CV#:" + str(cvids[line].get('data-hh-last-experience-id')) +
                      " --- " + file_list[g] + "<br> \n\n\n")
        cv_file.write(str(cv_soup.prettify()))

        cv_file.close()

    hh_file.close()


def hhsave_getlist():
    file_getlist = os.walk("HHSaves\\")
    name_list = []
    for d in file_getlist:
        name_list.append(d[-1])
    return name_list


# Основной цикл

cur_time = time.time()

file_list = hhsave_getlist()
file_list = file_list[0]

for g in range(0, len(file_list)):
    print(file_list[g], "\n")
    cv_parse(file_list[g])
    print(int((cur_time - time.time()) * 1000), "ms")

print(int((cur_time - time.time()) * 1000), "ms")
