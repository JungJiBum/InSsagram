package com.example.jibum.inssagram.model

import org.w3c.dom.Comment

data class ContentDTO(
    var explain: String? = null,
    var imageUrl: String? = null,
    var uid: String? = null,
    var userID: String? = null,
    var timestamp: Long? = null,
    var favoriteCOunt: Int = 0,
    var favorites: Map<String, Boolean> = HashMap()
) {
    data class Coment(
        var uid: String? = null,
        var userId: String? = null,
        var comment: String? = null,
        var timestamp: Long? = null
    )

}
