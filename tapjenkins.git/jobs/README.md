We are using the Job DSL plugin to configure Jenkins. More info: https://github.com/jenkinsci/job-dsl-plugin 

Unfortunately this plugin does not support out of the box all other plugins that we use, so in order to configure them we take advantage of the "configure" extensibility point. (https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block)
When such unsupported plugin needs to be configured, it is recommended to create a helper class (named *PluginName*Helper.groovy) and create the configuration in a static method, which returns the closure needed for the configure block. The method should accept parameters which are likely to be different for the various jobs that are using it. This way we can reuse the logic and avoid writing complex configurations directly in the *.job.groovy files. Have a look at the existing helper classes for reference.

It is also recommended to use the JobFactory class for creating jobs instead of instantiating a job directly. This way we can easily add base configurations to all jobs of certain type, for example all jobs that run on linux machines are tagged with 'ubuntu-common' label. In this case a job would be instantiated by calling "JobFactory.createLinux(this,'My-Linux-Job')"

Naming conventions:
- For jobs use small case letters and '-' as word delimiter. The extension should be ".job.groovy".
- For helper files use camel case and ".groovy" extension.



 


