import helpers.*

def defaultBranch = ''

def job = JobFactory.createLinux(this,'BS-Commit-Policy-Check')

GitHelper.useGitLabBsRepo(job, '**')

job.with {
  description 'Checks if each commit in the Backend Services repos complies with the policy.'

  triggers{
    scm ''
  }

  steps {
    shell 'bash Automation/CommitPolicyCheck.sh $GIT_PREVIOUS_COMMIT'
  }

  publishers {
    mailer 'alexander.filipov@telerik.com Anton.Dobrev@telerik.com Anton.Sotirov@telerik.com Dimitar.Dimitrov@telerik.com Dimo.Mitev@telerik.com Evgeni.Boevski@telerik.com GeorgiN.Georgiev@telerik.com Lyubomir.Dokov@telerik.com Stoyan.Ivanov@telerik.com Tsvetomir.Nedyalkov@telerik.com Vasil.Dininski@telerik.com Yordan.Dimitrov@telerik.com Yosif.Yosifov@telerik.com'
  }

  wrappers {
    colorizeOutput "xterm"
    timestamps()
  }
}