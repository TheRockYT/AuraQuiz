package one.felsen.auraquiz.viewmodel

enum class ConflictStrategy(val title: String, val description: String) {
    MERGE(
        title = "Merge",
        description = "Merges data based on the update timestamp. The newest edit wins."
    ),
    PREFER_IMPORTED(
        title = "Prefer Imported",
        description = "Keeps all changes from the imported file and overwrites local conflicts."
    ),
    PREFER_LOCAL(
        title = "Prefer Local",
        description = "Keeps all local changes and ignores conflicting data from the imported file."
    )
}