package translatenumbers

import com.translateNumbers.DollarTranslator

/**
 * Controller for Translations
 */
class TranslateController {

    /**
     * Translate a String passed in a request parameter called 'input'
     *
     * Renders a String in the response with either the translation, or
     * an error message.
     */
    def index() {

        String input = params.input

        if (!input) {
            render 'Error: Please enter a number.'
            return
        }

        String translation

        try {
            translation = DollarTranslator.translate(input)
        }
        catch (any) {
            render any.message.replaceAll('\\n', '<br>')
            return
        }

        render "Translation: $translation"
    }

}
