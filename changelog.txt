------------------------------------------
0.5.2
------------------------------------------

- Fixed issue that path was never considered definite if containing a ':'
- Bracket notation is now first class citizen $.foo.bar == $.['foo'].['bar']
- Added JsonAsserter.assertNotDefined(String path) to allow checks for negative existence of a path

