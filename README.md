This project is an simple implementation for a number -> word translator, following
the project instructions:

   "Write some code that will accept an amount and convert it to the
    appropriate string representation.
    Example:
        Convert 2523.04
        to "Two thousand five hundred twenty-three and 04/100 dollars"


I went ahead and made the solution a Grails application, to allow it to be driven by
a simple web front end.  Please note that Grails 2.0.3 is required - it can be downloaded from:

    http://dist.springframework.org.s3.amazonaws.com/release/GRAILS/grails-2.0.3.zip

The application can be accessed by running the following on the command line:

    grails run-app

and then opening http://localhost:8080/translateNumbers/ in a browser.

The DollarTranslator.groovy class can be found in the src\groovy folder, and does
all the work.  A simple controller is under \grails-app\controllers.

Accompanying Spock tests can be found under test\unit and can be run in an ide, or via
the command line, using:

    grails test-app

It was a fun little project, and most edge cases were covered.  Whole dollars up to 21 characters
are translated (999 quintillion), negatives are rejected, zero is handled, erroneous input is caught 
and messaged appropriately.

David J Hay
