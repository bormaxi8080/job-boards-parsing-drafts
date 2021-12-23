import requests
import time
from bs4 import BeautifulSoup

'''
Данный модуль реализует загрузку названий всех компаний, разместивших вакансии в сфере РАЗРАБОТКА ПО на hh.ru в Москве
Проход осуществляется раздельно по каждой ветке метро с формированием отдельных отчетов.
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
metro - номер ветки метро для формирования запроса. 
'''
metro = 2  # Указываем метро где искать: 2 - Замоскворецкая ветка, 2.29 - Водный стадион, 2.512 - Технопарк и т.п.

# Формируем шаблон поискового запроса - подставляем значение metro, но без номера страницы поиска (вставим потом).
hh_url = "https://hh.ru/search/vacancy?area=1&clusters=true&enable_snippets=true&items_on_page=100&metro=" \
         + str(metro) + "&no_magic=true&specialization=1.221&page="

hh_headers = {"user-agent": "my-app/0.0.1"}  # Обращение с сайтам требует передачи служебной информации
# (заголовков запросов), иначе сайт HH.ru не даст нам страничку из вредности, чтобы не парсили.
# Настраиваем все заголовки в отдельной переменной.

hh_curl = hh_url + '0'  # Теперь нам надо обратиться к самой первой страничке выдачи, чтобы узнать сколько
# всего у нас страничек с вакансиями нашлось - добавляем к шаблону ноль в конце. Получится "....&page=0"

hh_page = requests.get(hh_curl, headers=hh_headers, timeout=None)  # библиотекой requests дергаем страничку,
# все что нужно для запрос мы уже внесли в переменные заранее.

soup = BeautifulSoup(hh_page.text, 'html.parser')  # Просим BeautifulSoup "разобрать" полученную
# страничку на элементы, создав объект soup

last_page = soup.find_all(class_='bloko-button HH-Pager-Control')  # Ищем в супе сколько у нас всего страниц, выводим
print(last_page[-1].text)

hh_filename = str(int(time.time())) + "compparsed.html"  # Придумываем имя файла, тупо берем текущее время и ставим вначале
companies_unique = {}  # инициализируем словарик для данных о компаниях (без повторов).

# Тут основной цикл - бежим по страницам с первой (0) до последней в выдаче
for page in range(0, int(last_page[-1].text)):

    cur_time = time.time()  # Засекли текущее время
    cur_quant_of_companies = len(companies_unique)  # Засекли, сколько у нас уже компаний в списке

    hh_curl = hh_url + str(page)  # добавляем к шаблону номер страницы в конце, чтобы было &page=0, &page=1, &page=2
    print(hh_curl)

    # Берем страничку и кладем ее в суп
    hh_page = requests.get(hh_curl, headers=hh_headers, timeout=None)
    soup = BeautifulSoup(hh_page.text, 'html.parser')

    line = 1  # Начинаем с "1", так как самый первый ("0") из нужных нам тэгов указывает на hh.ru, а не на работодателя
    companies = soup.find_all(class_='bloko-link bloko-link_secondary')  # берем все тэги с компаниями из супа

    # В цикле разберем тэги с названиями компаний-работодателей (из вакансий) - один за другим
    # берем очередное название компании и смотрим, есть ли такой ключ в companies_unique (он глобальный)
    # если компании там нет, то кладем значние в виде списка [URL, 1] (URL цепляем из href)
    # если компания уже есть, то добавляем +1 ко второму элементу списка по этому ключу-компаниии
    while line < len(companies):
        cur_company = str(companies[line].text)
        if cur_company not in companies_unique:
            companies_unique[cur_company] = [str(companies[line].get('href')), 1]
        else:
            companies_unique[cur_company][1] += 1

        line += 1

    # Выводим справочную инфу: время парсинга, сколько компаний уже найдено всего,
    # а также сколько новых компаний найдено на этой страничке
    # Например: Page parsed for 604 ms. Total: 346 companies (+37)
    print("Page parsed for " + str(int((time.time() - cur_time) * 1000)) + " ms. Total: " + str(len(companies_unique))
          + " companies (+" + str(len(companies_unique) - cur_quant_of_companies) + ")")

    time.sleep(2)  # Берем паузу 2 секунды

# Теперь, когда все страницы обработаны, нам все это надо записать в файл. Имя мы уже сгенерировали выше.
hh_file = open(hh_filename, 'a', encoding='utf-8')

# Сначала записываем особо важные компании, "лидеры" (больше 10 вакансий)
print("Writing leaders to file ...")
line = 1  # Счетчик строк здесь в принципе не нужен, я оставил его тут, если захочется выводить нумерованный список.
for key in companies_unique:
    if int(companies_unique[key][1]) > 10:
        hh_file.write(str(int(companies_unique[key][1])) + '\t <a href=\"https://hh.ru' + companies_unique[key][0] +
                      '\" target="_blank"> <b>' + key + '</b> </a> <br>' + '\n')
    line += 1  # Счетчик строк здесь в принципе не нужен, я оставил его если захочется выводить нумерованный список.

hh_file.write('\n <br> -------------------- <br><br>\n')

#  Потом выводим полный список компаний, выделяя жирным те, у кого больше 5 вакансий
print("Writing companies list to file ...")
line = 1
for key in companies_unique:
    hh_file.write(str(int(companies_unique[key][1])) + '\t <a href=\"https://hh.ru' + companies_unique[key][0] +
                  '\" target="_blank">')
    if int(companies_unique[key][1]) > 5:
        hh_file.write("<b>" + key + "</b>")
    else:
        hh_file.write(key)
    hh_file.write('</a> <br>' + '\n')
    line += 1  # Счетчик строк здесь в принципе не нужен, я оставил его если захочется выводить нумерованный список.
hh_file.close()
