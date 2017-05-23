## Releasing this project

Prep the release
`mvn release:prepare -DdryRun=true`

If all ok:
`mvn release:clean release:prepare release:perform`

If it fails:
`mvn release:rollback` and check for any extra files or files that couldn't be rolled back.

If all is well, check out the tag and deploy it:
`mvn clean deploy`

Then go to https://oss.sonatype.org/ and release.

Note this is the old way using the oss-parent pom which will eventually be updated.

References:
https://confluence.sakaiproject.org/display/~steve.swinsburg/Release+info
http://central.sonatype.org/pages/apache-maven.html
http://central.sonatype.org/pages/ossrh-guide.html


