package helpers

class MSTestHelper {
  static Closure run(String testContainers, String testResultFile, String args) {
    return { project ->
      project / builders << {
        'org.jenkinsci.plugins.MsTestBuilder' {
          msTestName('MSTest VS2012');
          testFiles(testContainers);
          categories();
          resultFile(testResultFile);
          cmdLineArgs(args);
          continueOnFail(true);
        }
      }
    }
  }

  static Closure importResult(String testResultFile) {
    return { project ->
      project / publishers << {
        'hudson.plugins.mstest.MSTestPublisher' {
          testResultsFile(testResultFile);
        }
      }
    }
  }
}