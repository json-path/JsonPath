------------------------------------------
0.5.5
------------------------------------------
- Replaced com.googlecode.json-simple with net.minidev.json-smart
- Introduced different parse modes, JsonPath.SLACK_MODE and JsonPath.STRICT_MODE (the slack mode lets you use single quotes or even no quotes at all)

------------------------------------------
0.5.3
------------------------------------------
- Major refactoring
- JsonPath does not always produce a List as response

------------------------------------------
0.5.2
------------------------------------------

- Fixed issue that path was never considered definite if containing a ':'
- Bracket notation is now first class citizen $.foo.bar == $.['foo'].['bar']
- Added JsonAsserter.assertNotDefined(String path) to allow checks for negative existence of a path
