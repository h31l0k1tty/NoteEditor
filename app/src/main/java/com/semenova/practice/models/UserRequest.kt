package com.semenova.practice.models


class UserRequest(
    firstName: String, lastName: String, middleName: String,
    login: String, password: String, email: String,
    dateOfBirth: String?, role: RoleEnum, gender: GenderEnum,
    photo: String? = null, confirmPassword: String = ""
) {
    var firstName: String = ""
    var lastName: String = ""
    var middleName: String = ""
    var login: String = ""
    var password: String = ""
    var email: String = ""
    var dateOfBirth: String? = null
    var role: String = RoleEnum.User.ordinal.toString()
    var gender: String = GenderEnum.Male.ordinal.toString()
    var photo: String? = null
    var confirmPassword: String = ""

    init {
        this.firstName = firstName
        this.lastName = lastName
        this.middleName = middleName
        this.login = login
        this.password = password
        this.email = email
        this.dateOfBirth = dateOfBirth
        this.role = role.ordinal.toString()
        this.gender = gender.ordinal.toString()
        this.photo = photo
        this.confirmPassword = confirmPassword.ifEmpty { password }
    }
}