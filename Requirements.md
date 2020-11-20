# Political Speeches

## Objective: 
To process statistics on political speeches.

## Input: 

CSV files (UTF-8 encoding), which correspond to the following scheme:
Speaker, topic, date, words

It should be possible to start an HTTP server with maven or sbt, which accepts 1 or more URLS as query parameters under the route GET / evaluation? Url1 = url1 & url2 = url2.

The CSV files located at these URLs are evaluated and, if the input is valid, the following
Questions answered:
1. Which politician made the most speeches in 2013?
2. Which politician gave the most speeches on the subject of "internal security"?
3. Which politician spoke the fewest words overall?
Output: JSON in the following format.
```
{
	”MostSpeeches”: String | zero,
	“MostSecurity”: String | zero,
	“LeastWordy”: String | zero
}
```
If no or no clear answer is possible for a question, this field should be filled with zero

### Example:
CSV content:

Speaker, topic, date, words  
Alexander Abel, Education Policy, 2012-10-30, 5310  
Bernhard Belling, Coal Subsidies, 2012-11-05, 1210  
Caesare Collins, Coal Subsidies, 2012-11-06, 1119  
Alexander Abel, Internal Security, 2012-12-11, 911
