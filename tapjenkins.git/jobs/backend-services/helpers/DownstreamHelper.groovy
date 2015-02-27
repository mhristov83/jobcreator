package helpers

class DownstreamHelper {
  static Closure parameterized(String projectToBuild, Boolean noParams = false, String trigger = 'SUCCESS') {
    return { project ->
      project / publishers / 'hudson.plugins.parameterizedtrigger.BuildTrigger' / 'configs' << {
        'hudson.plugins.parameterizedtrigger.BuildTriggerConfig' {
          projects(projectToBuild);
          condition(trigger);
          triggerWithNoParameters(noParams);
        }
      }
    }
  }

  static Closure useCurrentBuildParams() {
    return { project ->
      project / publishers / 'hudson.plugins.parameterizedtrigger.BuildTrigger' / 'configs' / 'hudson.plugins.parameterizedtrigger.BuildTriggerConfig' / 'configs' << {
        'hudson.plugins.parameterizedtrigger.CurrentBuildParameters' {
        }
      }
    }
  }

  static Closure useParamsFromFile(String fileName = 'bs.props', String encodingArg = 'unicode') {
    return { project ->
      project / publishers / 'hudson.plugins.parameterizedtrigger.BuildTrigger' / 'configs' / 'hudson.plugins.parameterizedtrigger.BuildTriggerConfig' / 'configs' << {
        'hudson.plugins.parameterizedtrigger.FileBuildParameters' {
          propertiesFile(fileName)
          encoding(encodingArg)
          failTriggerOnMissing(true)
          useMatrixChild(false)
          onlyExactRuns(false)
        }
      }
    }
  }

  static Closure manual(String projectToBuild) {
    return { project ->
      project / publishers << {
        'au.com.centrumsystems.hudson.plugin.buildpipeline.trigger.BuildPipelineTrigger' {
          configs {
            'hudson.plugins.parameterizedtrigger.CurrentBuildParameters' {
            }
          }
          downstreamProjectNames(projectToBuild);
        }
      }
    }
  }
}