package com.docutesis.users.exceptions

class UserNotFoundException(message: String) : RuntimeException(message)
class DuplicateCognitoIdException(message: String) : RuntimeException(message)