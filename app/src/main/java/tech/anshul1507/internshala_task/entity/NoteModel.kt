package tech.anshul1507.internshala_task.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
class NoteModel(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "mail") val mail: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}