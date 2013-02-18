package translatenumbers

import com.translateNumbers.DollarTranslator
import spock.lang.Specification

import static org.junit.Assert.*

import grails.test.mixin.*

/**
 * Spec for the TranslateController
 *
 * @author David J Hay
 */
@TestFor(TranslateController)
class TranslateControllerSpec extends Specification{

    def "translate valid dollar"() {
        setup:
        request.method = "POST"
        params.input = input

        when:
        controller.index()

        then:
        response.text == translation

        where:
        input           | translation
        '1000'          | 'Translation: One thousand dollars'        //handle zeros to the right
        '1480'          | 'Translation: One thousand four hundred eighty dollars'
        '9999.87'       | 'Translation: Nine thousand nine hundred ninety-nine and 87/100 dollars'
        '12000'         | 'Translation: Twelve thousand dollars'
        '12876'         | 'Translation: Twelve thousand eight hundred seventy-six dollars'
        '1458933'       | 'Translation: One million four hundred fifty-eight thousand nine hundred thirty-three dollars'
        '2300458933'    | 'Translation: Two billion three hundred million four hundred fifty-eight thousand nine hundred thirty-three dollars'
        '12000000000000'| 'Translation: Twelve trillion dollars'
        '12000000000001'| 'Translation: Twelve trillion one dollars'
    }

    def "handle no param"() {
        setup:
        request.method = "POST"

        when:
        controller.index()

        then:
        response.text == 'Error: Please enter a number.'
    }

    def "handle bad param"() {
        setup:
        request.method = "POST"
        params.input = "xyz"

        when:
        controller.index()

        then:
        response.text == 'The amount you entered is not valid:<br>-Input must be a valid number'
    }

}
