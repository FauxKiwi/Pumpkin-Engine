package com.pumpkin.core

var application: Application? = null
    set(value) {
        field = value
        main()
    }

private fun main() {
    application!!.initI()
}