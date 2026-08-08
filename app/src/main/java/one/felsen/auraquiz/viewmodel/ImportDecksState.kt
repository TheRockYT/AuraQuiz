package one.felsen.auraquiz.viewmodel

import one.felsen.auraquiz.data.ExportFile

sealed interface ImportDecksState {
    data class IMPORTING(val file: ExportFile) : ImportDecksState
    data object COMPLETED : ImportDecksState
}
