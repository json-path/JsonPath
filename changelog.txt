------------------------------------------
0.5.2
------------------------------------------

- Fixed issue that path was never considered definite if containing a ':'
- Bracket notation is now first class citizen $.foo.bar == $.['foo'].['bar']
- Added JsonAsserter.assertNotDefined(String path) to allow checks for negative existence of a path

------------------------------------------
0.5.3
------------------------------------------
- Major refactoring
- JsonPath does not always produce a List as response

