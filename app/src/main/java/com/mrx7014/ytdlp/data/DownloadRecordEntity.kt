package com.mrx7014.ytdlp.data

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "downloads")
data class DownloadRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val format: String,
    val range: String,
    val status: String,
    val outputPath: String? = null,
    val progressPercent: Int = 0,
    val speedText: String? = null,
    val etaSeconds: Long? = null,
    val downloadedText: String? = null,
    val totalText: String? = null,
    val currentLine: String? = null,
    val createdAt: Long
)

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY createdAt DESC")
    fun observe(): Flow<List<DownloadRecordEntity>>

    @Insert
    suspend fun insert(item: DownloadRecordEntity): Long

    @Query("UPDATE downloads SET status = :status, outputPath = :path, progressPercent = :progress WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, path: String? = null, progress: Int = 0)

    @Query("UPDATE downloads SET progressPercent = :progress, speedText = :speed, etaSeconds = :eta, downloadedText = :downloaded, totalText = :total, currentLine = :line WHERE id = :id")
    suspend fun updateProgress(id: Long, progress: Int, speed: String?, eta: Long?, downloaded: String?, total: String?, line: String?)
}

@Database(entities = [DownloadRecordEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloads(): DownloadDao

    companion object {
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE downloads ADD COLUMN progressPercent INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE downloads ADD COLUMN speedText TEXT")
                db.execSQL("ALTER TABLE downloads ADD COLUMN etaSeconds INTEGER")
                db.execSQL("ALTER TABLE downloads ADD COLUMN downloadedText TEXT")
                db.execSQL("ALTER TABLE downloads ADD COLUMN totalText TEXT")
                db.execSQL("ALTER TABLE downloads ADD COLUMN currentLine TEXT")
            }
        }

        @Volatile private var instance: AppDatabase? = null
        fun get(context: android.content.Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context, AppDatabase::class.java, "downloads.db")
                .addMigrations(MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
        }
    }
}
