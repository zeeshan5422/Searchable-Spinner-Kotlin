package com.zues.myspinner.app

/* ---  Created by akhtarz on 7/23/2019. ---*/
data class myModel constructor(
    val id: Int, val name: String
){
    override fun toString(): String {
        return name
    }
}