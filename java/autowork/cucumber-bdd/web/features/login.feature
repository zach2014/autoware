Feature: login
    Scenario: login with valid username and password
	Given open login page with url
	When type username and password
	And  click login button
	Then open home page
