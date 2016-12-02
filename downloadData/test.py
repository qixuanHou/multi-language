# _*_coding:utf8_*_
from selenium import webdriver
import sqlite3
from time import sleep


def login(driver):
	
	driver.get("http://weibo.com/login.php")
	username = driver.find_element_by_id("loginname")
	username.clear()
	username.send_keys("graitdm@sina.cn")
	x = driver.find_element_by_class_name("info_list.password")
	password = x.find_element_by_xpath("//div/input[@name='password']")
	password.clear()
	password.send_keys("litmus")
	submit = driver.find_element_by_class_name("W_btn_a.btn_32px")
	submit.click()
	sleep(30)




def get(url, filename):
	
	conn = sqlite3.connect('weibo.db')
	c = conn.cursor()
	for i in range(1, 51):
		print "page" + str(i) + "\n"
		getMID(url + str(i), filename + str(i) + ".txt", conn, c)

def getMID(url, filename, conn, c):
	driver.get(url)
	try:
		items = driver.find_elements_by_tag_name("div")
	except:
		return None
	#items = driver.find_elements_by_class_name("content.clearfix")
	for i in items:
		if (i.get_attribute("mid") is not None):
			try:
				username = i.find_element_by_class_name("W_texta.W_fb").text
			except:
				username = "None"
			try:
				text = i.find_element_by_class_name("comment_txt").text
			except:
				text = "None"
			try:
				time = i.find_element_by_class_name("feed_from.W_textb").text
			except:
				time = "None"
			mid = str(i.get_attribute("mid"))
			if (text == "None" or time == "None" or username == "None"):
				pass
			else:
				s = i.get_attribute("mid") + "\n" + username + "\n" + text + "\n" + time + "\n\n"
				sutf8 = s.encode('UTF-8')
				open(filename, 'a').write(sutf8)
				try:
				#print("INSERT INTO weibo VALUES ('%s', '%s', '%s', '%s')"%(mid, username, text, time))
					c.execute("""INSERT INTO weibo VALUES ('%s', '%s', '%s', '%s')"""%(mid, username, text, time))
					conn.commit()
				except sqlite3.IntegrityError:
					print "mid" + i.get_attribute("mid") + "\n"
					pass
				except:
					print("INSERT INTO weibo VALUES ('%s', '%s', '%s', '%s')"%(mid, username, text, time))
					pass





driver = webdriver.Firefox()
login(driver)
from datetime import datetime
print(str(datetime.now()))
get("http://s.weibo.com/weibo/%25E6%25B3%25A5%25E7%259F%25B3%25E6%25B5%2581?&b=1&page=", "N")
print(str(datetime.now()))
get("http://s.weibo.com/weibo/%25E5%259C%25B0%25E9%259C%2587&b=1&page=", "D")
print(str(datetime.now()))
get("http://s.weibo.com/weibo/%25E6%25BB%2591%25E5%259D%25A1&b=1&page=", "H")
print(str(datetime.now()))
get("http://s.weibo.com/weibo/%25E5%25A1%258C%25E6%2596%25B9&b=1&page=", "T")
print(str(datetime.now()))
get("http://s.weibo.com/weibo/%25E5%25B4%25A9%25E5%25A1%258C&b=1&page=", "B")
print(str(datetime.now()))
get("http://s.weibo.com/weibo/%25E5%25B1%25B1%25E5%25B4%25A9&b=1&page=", "S")
print(str(datetime.now()))




#N - 泥石流 - http://s.weibo.com/weibo/%25E6%25B3%25A5%25E7%259F%25B3%25E6%25B5%2581?&b=1&page=
#D - 地震 － http://s.weibo.com/weibo/%25E5%259C%25B0%25E9%259C%2587&b=1&page=
#H - 滑坡 － http://s.weibo.com/weibo/%25E6%25BB%2591%25E5%259D%25A1&b=1&page=
#T - 塌方 － http://s.weibo.com/weibo/%25E5%25A1%258C%25E6%2596%25B9&b=1&page=
#B - 崩塌 － http://s.weibo.com/weibo/%25E5%25B4%25A9%25E5%25A1%258C&b=1&page=
#S - 山崩 － http://s.weibo.com/weibo/%25E5%25B1%25B1%25E5%25B4%25A9&b=1&page=



