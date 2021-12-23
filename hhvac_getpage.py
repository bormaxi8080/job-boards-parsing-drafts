import requests
import time
from bs4 import BeautifulSoup

'''
Данный модуль реализует загрузку названий всех компаний, разместивших вакансии на hh.ru по определенному запросу.
Например, все вакансии около определенных станций метро. Или вакансии в той или иной проф.области.
Используемые модули: requests (загрузка веб-страниц), time (контроль затрачиваемого времени и паузы между запросами),  
BeautifulSoup (версия 4, разбор содержимого страниц)
Значения переменных:
hh_url - шаблон поискового GET-запроса, передает параметры и ограничения поиска.
hh_headers - заголовки HTTP-запроса, которые модуль передает серверу hh.ru
hh_curl - поисковый GET-запрос с указанием конкретной страницы поиска для разбора
hh_page - объект, содержащий полученную поисковую страницу
soup - объект BeautifulSoup, содержащий обработанную структуру поисковой страницы
last_page - переменная, которая содержит тэг с номером последней из поисковых страниц по запросу для прохода всех.
hh_filename - имя файла для сохранения результатов
companies_unique - накопительный список уникальных неповторяющихся названий компаний, чтобы исключить повторы.
hh_file - объект для обращения к файлу записи результатов.
page - номер полученной и обрабатываемой поисковой страницы по запросу.
cur_time - время начала работы цикла обработки страницы, нужно для вычисления длительности обработки
cur_company - название компании при поочередном переборе
cur_quant_of_companies - количество компаний в списке перед начало обработки новой страницы
hh_text - исходный текст поисковой страницы, полученной по запросу. 
'''

hh_url = "https://hh.ru/search/vacancy?area=1&clusters=true&enable_snippets=true&no_magic=true&text=Сколково&page="

hh_headers = {"user-agent": "my-app/0.0.1"}

hh_curl = hh_url + '0'
hh_page = requests.get(hh_curl, headers=hh_headers, timeout=None)
soup = BeautifulSoup(hh_page.text, 'html.parser')
last_page = soup.find_all(class_='bloko-button HH-Pager-Control')
print(last_page[-1].text)

hh_filename = str(int(time.time())) + "compparsed.html"
companies_unique = {}


for page in range(0, int(last_page[-1].text)):

    cur_time = time.time()
    cur_quant_of_companies = len(companies_unique)

    hh_curl = hh_url + str(page)
    print(hh_curl)

    hh_page = requests.get(hh_curl, headers=hh_headers, timeout=None)
    soup = BeautifulSoup(hh_page.text, 'html.parser')

    line = 1
    companies = soup.find_all(class_='bloko-link bloko-link_secondary')

    while line < len(companies):
        cur_company = str(companies[line].text)
        if cur_company not in companies_unique:
            companies_unique[cur_company] = [str(companies[line].get('href')), 1]
        else:
            companies_unique[cur_company][1] += 1

        line += 1

    print("Page parsed for " + str(int((time.time() - cur_time) * 1000)) + " ms. Total: " + str(len(companies_unique))
          + " companies (+" + str(len(companies_unique) - cur_quant_of_companies) + ")")

    time.sleep(2)

print("Writing to file ...")
hh_file = open(hh_filename, 'a', encoding='utf-8')

line = 1
for key in companies_unique:
    hh_file.write(str(line) + '. <a href=\"https://hh.ru' + companies_unique[key][0] + '\" target="_blank">'
                  + key + '</a> ' + str(int(companies_unique[key][1])) + '<br>' + '\n')
    line += 1
hh_file.close()
