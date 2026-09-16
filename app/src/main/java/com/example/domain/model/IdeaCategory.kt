package com.example.domain.model

enum class IdeaCategory(
    val key: String,
    val labelAr: String,
    val iconName: String
) {
    ALL("الكل", "الكل", "All"),
    SHORTS("Shorts", "شورتس (Shorts)", "Bolt"),
    TUTORIAL("Tutorial", "شرح تعليمي (Tutorial)", "School"),
    VLOG("Vlog", "فلوج ويوميات (Vlog)", "Videocam"),
    STORY("Story", "قصص وروايات (Story)", "MenuBook"),
    PROMO("Promo", "إعلاني وترويج (Promo)", "Campaign");

    companion object {
        val availableTags = listOf(SHORTS, TUTORIAL, VLOG, STORY, PROMO)

        fun fromKey(key: String): IdeaCategory {
            return entries.find { it.key.equals(key, ignoreCase = true) } ?: SHORTS
        }

        fun getDisplayName(key: String): String {
            return fromKey(key).labelAr
        }
    }
}
