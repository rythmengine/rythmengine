# Change History

# 1.3.1
* Support customised resource bundle encoding #392

## 1.3.0
* Allow application to configure `DateFormat` factory #375
* Add long/short/medium format for Date type object

## 1.2.2 14/Jan/2018
* TemplateClass not cached when template home defined #373
* Resource not cached when there are root dir #372

## 1.2.1 06/Jan/2018
* When there are ISourceCodeEnhancer provided implicit variables passing user variable by position will fail #371

## 1.2.0 25/Mar/2017
* Fix #348 Update Java to 1.7
* Fix #351 Support windows line endings in nl2br?
* Fix #354 Issues with hardcoded temporary directory
* Fix #359 format null value handling
* Fix #362 Remove `__sep` and `__util` iterable variables
* Fix #361 Replace `Stack` with `Deque` in `TemplateBase`

## 1.1.7-SNAPSHOT
* Revert #314 to fix issue #346 (entered release 1.1.5)

## 1.1.6-SNAPSHOT
* Update dependency versions:
** ecj to 4.5.1
** commons-lang3 to 3.4
** fastjson to 1.2.11
** mvel2 to 2.2.8.Final
** joda-time to 2.9.3 (provided)
** gson to 2.6.2 (provided)
** appengine-api-1.0-sdk to 1.9.37 (provided)
** ehcache to 2.10.2 (provided)
** spymemcached to 2.12.1 (provided)

## 1.1.5
* Implement ReadWrite lock in TemplateClass - Remove public variables from
TemplateClass
* #316 Allow passing key into StringTemplateResource

## 1.1.4-SNAPSHOT
* Add toJSON() to S class (and as built-in extension)
* Fix FindBug reported issues

## 1.1.3-SNAPSHOT
* Use caller class resource loader to load current template
* Fix issue: ClassResourceLoader failed to load tag resources
* Add sp2nbsp() transformer
* Drop support to Java 1.5 and below. Default java source version is 1.6

## 1.1.2-SNAPSHOT
* Fix GH issue #271: Allow template author to add arbitrary code at class
level

## 1.1.1-SNAPSHOT:
* Fix GH issue 251: StringIndexOutOfBoundsException in TemplateBase
* Fix GH issue 249: @def does not support package contained in the return
  type name
* Fix GH issue 244: Declared args in __global.rythm position might not be
  correct
* Fix GH issue #239: Failed invocation after template reload in dev mode
* Fix GH issue 215: Can't run on Java 8
* Fix GH issue 170: cache service now default to SimpleCacheService to avoid
  non-daemon issue of ehcache service with default configuration
* Fix GH issue #164: @nocompact() doesn't work as expected
* Add getTemplate() method to Rythm facade. See GH Issue #70
* Add "resource.refresh.interval" configuration allow the developer to define
  resource refresh check period
* Fix GH issue #264: support define class in a template

## 1.1.0-SNAPSHOT: base version where the release history start
