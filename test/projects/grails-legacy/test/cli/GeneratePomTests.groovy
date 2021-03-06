import grails.test.AbstractCliTestCase

class GeneratePomTests extends AbstractCliTestCase {
    def basePom = new File("pom.xml")
    def pomFile = new File("target/pom.xml")

    void setUp() {
        basePom.delete()
        pomFile.delete()
    }

    void tearDown() {
        basePom.delete()
        pomFile.delete()
    }

    void testDefault() {
        runAndVerify()
    }

    void testWithExistingPom() {
        pomFile.text = """\
<?xml version="1.0" ?>
<project>
</project>
"""

        runAndVerify()
    }

    void testWithBasePom() {
        basePom.text = """\
<?xml version="1.0" ?>
<project>
</project>
"""
        
        execute([ "generate-pom "])
             
        assertEquals 1, waitForProcess()
        verifyHeader()
                              
        // Make sure that the script was found.
        assertFalse "GeneratePom script not found.", output.contains("Script not found:")

        // It should have printed a message about the base POM already
        // existing.
        assertTrue "Command did not say that POM generation was skipped.", output.contains("Skipping POM generation because 'pom.xml' exists in the root of the project")

        // Check that the POM file does *not* exist.
        assertFalse "Generated POM file does exists", pomFile.exists()
    }


    private runAndVerify() {
        execute([ "generate-pom" ])
             
        assertEquals 0, waitForProcess()
        verifyHeader()
                              
        // Make sure that the script was found.
        assertFalse "GeneratePom script not found.", output.contains("Script not found:")

        // First check that the POM file exists.
        assertTrue "POM file does not exist", pomFile.exists()

        // Now check the content using XmlSlurper.
        def pom = new XmlSlurper().parseText(pomFile.text)
        assertEquals "4.0.0", pom.modelVersion.text()
        assertEquals "org.grails.plugins", pom.groupId.text()
        assertEquals "legacy", pom.artifactId.text()
        assertEquals "1.1-SNAPSHOT", pom.version.text()
        assertEquals "zip", pom.packaging.text()

        assertEquals "", pom.dependencies.text()

        assertEquals 0, pom.name.size()
        assertEquals "Legacy plugin for testing.", pom.description.text()
        assertEquals "http://grails.org/plugin/legacy", pom.url.text()
        assertEquals 0, pom.licenses.size()
        assertEquals 0, pom.organization.size()
        assertEquals 1, pom.developers.developer.size()
        assertEquals "Dilbert", pom.developers.developer[0].name.text()
        assertEquals "dilbert@somewhere.net", pom.developers.developer[0].email.text()
        assertEquals 0, pom.issueManagement.size()
        assertEquals 0, pom.scm.size()
    }
}
