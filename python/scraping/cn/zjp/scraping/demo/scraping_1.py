from urllib.request import urlopen
from bs4 import BeautifulSoup

page = urlopen("http://www.bing.com")
prettyPage = BeautifulSoup(page.read())
print(prettyPage.title)