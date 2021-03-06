class TailReader {

  def EOL = "\r\n"
  boolean stop = false
  def metrics = new File("./deployed-bundles/UGEApp-1.0/univa_logs/metrics/stats.log")
  def job_log = new File("./deployed-bundles/UGEApp-1.0/univa_logs/jobs/jobs.log")
  def new_job = new File("./deployed-bundles/UGEApp-1.0/univa_logs/new_jobs/new_job.log")  
  def other = new File("./deployed-bundles/UGEApp-1.0/univa_logs/acct/acct.log")

  public void stop () {
    stop = true
  }

  public void tail (File file) {
    def runnable = {
     def reader

     try {
      reader = file.newReader()
      reader.skip(file.length())

      def line

      while (!stop) {
        line = reader.readLine()
        if (line) {
          if (line.contains("host")){
            metrics.append(line+EOL)
          } else if(line.contains("job_log")){
            job_log.append(line+EOL)
          } else if(line.contains("new_job")){
            new_job.append(line+EOL)
          } else {
            other.append(line+EOL)
          }
           }
           else {
            Thread.currentThread().sleep(100)
          }
        }

      }
      finally {
        reader?.close()
      }
      } as Runnable

      def t = new Thread(runnable)
      t.start()
    }

  }


  if(reportingPath==null){
    println "Usage - tailParser.groovy [path to reporting file] - i.e groovy tailParser.groovy /home/tom/uge/default/common/reporting"
    System.exit(0)
  }

  File reporting = new File(reportingPath)
  def reader = new TailReader()
  if(reporting.exists()){
    reader.tail(reporting)
  } else {
    def errorFile = new File("./error.log")
    errorFile.append(reporting.exists())
    errorFile.append("We couldn't find " + reporting.absolutePath + " are you sure you provided the correct path?\n")
  }
