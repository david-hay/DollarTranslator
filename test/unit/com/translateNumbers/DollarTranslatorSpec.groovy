package com.translateNumbers

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Spec for DollarTranslator
 *
 * @author: David J Hay
 * Date: 2/16/13
 * Time: 12:48 PM
 */
@Unroll
class DollarTranslatorSpec extends Specification {

    def "translate cents portion"() {
        when:
        def centInput = '08'
        String cents = DollarTranslator.translateCents(centInput)

        then:
        cents == "$centInput/100"
    }

    def "private method to pad string to make it's length a multiple of 3"() {
        when:
        String padded = DollarTranslator.pad(s)

        then:
        padded == result

        where:
        s           | result
        '1'         | '001'
        '12'        | '012'
        '123'       | '123'
        '1234'      | '001234'
        '12345'     | '012345'
        '123456'    | '123456'

    }

    def "private method translates triplet correctly"() {
        when:
        String dollarPortion = DollarTranslator.translateTriplet(dollars, '')

        then:
        dollarPortion == translation

        where:
        dollars     | translation
        '000'         | ''              //zero handled elsewhere
        '001'         | 'one'
        '009'         | 'nine'
        '010'         | 'ten'
        '012'         | 'twelve'
        '019'         | 'nineteen'
        '020'         | 'twenty'
        '021'         | 'twenty-one'
        '029'         | 'twenty-nine'
        '030'         | 'thirty'
        '045'         | 'forty-five'
        '099'         | 'ninety-nine'
        '100'         | 'one hundred'
        '101'         | 'one hundred one'
        '199'         | 'one hundred ninety-nine'
        '450'         | 'four hundred fifty'
        '999'         | 'nine hundred ninety-nine'
    }

    def "translate dollar portion under 1000"() {
        when:
        String dollarPortion = DollarTranslator.translateDollarPortion(dollars)

        then:
        dollarPortion == translation

        where:
        dollars     | translation
        '0'         | 'zero'
        '1'         | 'one'
        '9'         | 'nine'
        '10'        | 'ten'
        '12'        | 'twelve'
        '20'        | 'twenty'
        '030'       | 'thirty'
        '045'       | 'forty-five'
        '099'       | 'ninety-nine'
        '100'       | 'one hundred'
        '101'       | 'one hundred one'
        '999'       | 'nine hundred ninety-nine'
    }

    def "translate dollar portion over 1000"() {
        when:
        String dollarPortion = DollarTranslator.translateDollarPortion(input)

        then:
        dollarPortion == translation

        where:
        input           | translation
        '1000'          | 'one thousand'        //handle zeros to the right
        '1480'          | 'one thousand four hundred eighty'
        '9999'          | 'nine thousand nine hundred ninety-nine'
        '12000'         | 'twelve thousand'
        '12876'         | 'twelve thousand eight hundred seventy-six'
        '1458933'       | 'one million four hundred fifty-eight thousand nine hundred thirty-three'
        '2300458933'    | 'two billion three hundred million four hundred fifty-eight thousand nine hundred thirty-three'
        '12000000000000'| 'twelve trillion'
        '12000000000001'| 'twelve trillion one'
    }

    def "appends dollar(s) correctly"() {
        when:
        String translation = DollarTranslator.translate(input)

        then:
        translation == expected

        where:
        input           | expected
        '0'             | 'Zero dollars'
        '1'             | 'One dollar'
        '2300458933'    | 'Two billion three hundred million four hundred fifty-eight thousand nine hundred thirty-three dollars'
    }

    def "translates dollars and cen"() {
        when:
        String translation = DollarTranslator.translate(a)

        then:
        translation == b

        where:
        a               | b
        '0'             | 'Zero dollars'
        '0.01'          | 'Zero and 01/100 dollars'
        '1.45'          | 'One and 45/100 dollars'
        '10.00'         | 'Ten dollars'
        '102.99'        | 'One hundred two and 99/100 dollars'
    }

    def "validate input"() {
        when:
        def errors = DollarTranslator.validate(str.replaceAll(',', ''))

        then:
        errors.size() == count

        where:
        str             |   count
        '1.23'          |  0
        '-1'            |  1
        '1.2x'          |  1
        '1.2.3'         |  2
        '1,222.34'      |  0
        '1,222,332.34'  |  0
        '1.2'           |  1
        '1.234'         |  1
        '9999999999999999999999' | 1         //length > 21
        '999999999999999999999.99' | 0         //length > 21
    }

    def "catch input errors"() {
        when:
        DollarTranslator.translate('')

        then:
        thrown(IllegalArgumentException)
    }

    def "throw exception on bad input error"() {
        when:
        DollarTranslator.translate(badStr)

        then:
        thrown(TranslatorException)

        where:
        badStr << ['1.2x', '1.2.3', '1.2', '1.234', '99999999999999999999999']
    }

}
