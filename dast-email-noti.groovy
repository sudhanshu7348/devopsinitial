def call() {
  
   def targetMailId = "Ashutosh.Pattanaik@ge.com, Venkateswararao.Ganpisetty@ge.com, hemalatha.lankalapalli@ge.com;"

   def bodyTemplate = """
       <html>

          <body style="margin: 20px; font-family: Arial, Helvetica, sans-serif;
                    font-size: small">
              <p>
                  Hi Application Team,
              </p>
              <p>
                  Qualys DAST Scan completed for the application - Credit Card Payment Engine - CMDB - 1100601719.
              </p>
              <p>
                  This scan was executed as part of Jenkins' build schedule.
              </p>
              <p>
                  <span style="font-weight: bold;">Jenkins Job URL</span> - ${BUILD_URL}
              </p>
              <p>
                  <span style="font-weight: bold;">Application URL</span> - https://location.ver02.geicenter.com/health-console
              </p>
              <p>
                  Complete vulnerability details can be obtained in BEASSTT Portal - https://beasstt.itrisk.ge.com/welcome
              </p>
              <p>
                  <span style="font-weight: bold;">Support: For any questions or concerns, please contact
                      <span><a href="mailto:healthappsecsupport@ge.com">@HEALTH AppSec Support</a></span> and we will get back to
                      you within 24 hours.</span>
              </p>
              <br>
              <p>
                  Regards,
              <p>
                  AppSec Team
              </p>
            </body>
        </html>
        """

       emailext body: bodyTemplate, subject: 'DAST security scan of CC Payment Engine - CMDB - 1100601719', to: targetMailId, from: 'JenkinsAlert@ge.com'

}
