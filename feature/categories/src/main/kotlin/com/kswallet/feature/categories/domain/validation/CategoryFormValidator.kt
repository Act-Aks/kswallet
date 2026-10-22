package com.kswallet.feature.categories.domain.validation

/**
 * Pure validation rules. No Android, no coroutines, no repository - which is exactly why
 * they are cheap to unit test and impossible to duplicate in a screen.
 */
object CategoryFormValidator {

    const val MAX_NAME_LENGTH = 40

    /**
     * @param duplicateNameExists a function, not a boolean, so that the caller decides how
     *   to answer the question (in-memory list in a test, SQL count in production).
     */
    fun validate(
        input: CategoryFormInput,
        duplicateNameExists: (String) -> Boolean,
    ): List<CategoryFormError> = buildList {
        val name = input.name.trim()
        when {
            name.isEmpty() -> add(CategoryFormError.EmptyName)
            name.length > MAX_NAME_LENGTH -> add(CategoryFormError.NameTooLong)
            duplicateNameExists(name) -> add(CategoryFormError.DuplicateName)
        }
    }

    /** The persisted shape of a valid form. */
    fun normalizeName(name: String): String = name.trim()
}
