package com.mrx7014.ytdlp.data
import androidx.room.*
@Entity(tableName="downloads") data class DownloadRecordEntity(@PrimaryKey(autoGenerate=true) val id: Long=0, val title:String, val url:String, val format:String, val range:String, val status:String, val createdAt:Long)
@Dao interface DownloadDao { @Query("SELECT * FROM downloads ORDER BY createdAt DESC") fun observe(): kotlinx.coroutines.flow.Flow<List<DownloadRecordEntity>>; @Insert suspend fun insert(item: DownloadRecordEntity) }
@Database(entities=[DownloadRecordEntity::class], version=1, exportSchema=false) abstract class AppDatabase: RoomDatabase() { abstract fun downloads(): DownloadDao; companion object { @Volatile private var instance:AppDatabase?=null; fun get(context:android.content.Context)=instance ?: synchronized(this){ instance ?: Room.databaseBuilder(context,AppDatabase::class.java,"downloads.db").build().also{instance=it} } } }
