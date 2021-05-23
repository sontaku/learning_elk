import requests
from pandas import DataFrame
from bs4 import BeautifulSoup
import re
from datetime import datetime
import os
import pymysql
date = str(datetime.now())
date = date[:date.rfind(':')].replace(' ', '_')
date = date.replace(':', '시') + '분'

query = input('검색 키워드를 입력하세요 : ')
news_num = int(input('총 필요한 뉴스기사 수를 입력해주세요(숫자만 입력) : '))
query = query.replace(' ', '+')

news_url = 'https://search.naver.com/search.naver?where=news&sm=tab_jum&query={}'

req = requests.get(news_url.format(query))
soup = BeautifulSoup(req.text, 'html.parser')

news_dict = {}
idx = 0
cur_page = 1

print()
print('크롤링 중...')

while idx < news_num:
  ### 네이버 뉴스 웹페이지 구성이 바뀌어 태그명, class 속성 값 등을 수정함(20210126) ###

  table = soup.find('ul', {'class': 'list_news'})
  li_list = table.find_all('li', {'id': re.compile('sp_nws.*')})
  area_list = [li.find('div', {'class': 'news_area'}) for li in li_list]
  a_list = [area.find('a', {'class': 'news_tit'}) for area in area_list]
  print('테스트', a_list)
  for n in a_list[:min(len(a_list), news_num - idx)]:
    news_dict[idx] = {'title': n.get('title'),
                      'url': n.get('href'),
                      'category': '금융'
                      }

    print(news_dict[idx])
    idx += 1

  cur_page += 1

  pages = soup.find('div', {'class': 'sc_page_inner'})
  next_page_url = [p for p in pages.find_all('a') if p.text == str(cur_page)][0].get('href')

  req = requests.get('https://search.naver.com/search.naver' + next_page_url)
  soup = BeautifulSoup(req.text, 'html.parser')

print('크롤링 완료')
# conn = pymysql.connect(host='DB 서버 IP', user='계정', password='비번', db='데이터베이스명', charset='utf8', autocommit=True,cursorclass=pymysql.cursors.DictCursor)
conn = pymysql.connect(host='localhost', user='elkt', password='elkt', db='elktest', charset='utf8', autocommit=True,cursorclass=pymysql.cursors.DictCursor)
print('연결성공')
cursor = conn.cursor()

print(news_dict)

for key, value in news_dict.items():
  print(key, value)
  print(value['title'])
  print(value['url'])
  parse = re.sub('[-=#/\:$}""]', " ", value['title'])
  print("테스트입니다", parse)
  sql = 'REPLACE INTO news(news_id ,title ,url, category, currdate) VALUES (news_id,"{0}","{1}","{2}",sysdate())'.format(parse, value['url'], value['category'])
  cursor.execute(sql)

conn.commit()
cursor.close()
conn.close()
