Feature: web
   Scenario: Open home page
	Given the home page url is "www.home.com"
	When browser open the home page
 	Then the title of page is home
