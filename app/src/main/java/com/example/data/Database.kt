package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val creatorName: String,
    val creatorAvatar: String = "",
    val views: Int = 120,
    val likes: Int = 18,
    val type: String = "VIDEO", // "VIDEO" or "SHORT" or "LIVE"
    val duration: String = "10:00",
    val category: String = "Trending",
    val auraScore: Int = 120,
    val viralityChance: Int = 45, // "Smart Viral Detector" percentage
    val captions: String = "",
    val isLiked: Boolean = false,
    val isSubscribed: Boolean = false,
    val isGhostUpload: Boolean = false,
    val scheduledTime: Long = 0L,
    val videoResUrl: String = "",
    val viewsCountFormatted: String = "1.2K views",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoId: Int,
    val author: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "battles")
data class BattleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val creatorAName: String,
    val creatorAAvatar: String = "",
    val creatorAVideoTitle: String = "",
    val creatorAVotes: Int = 12,
    val creatorBName: String,
    val creatorBAvatar: String = "",
    val creatorBVideoTitle: String = "",
    val creatorBVotes: Int = 10,
    val totalSeconds: Int = 60,
    val hasVoted: String? = null // "A" or "B" or null
)

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos WHERE isGhostUpload = 0 ORDER BY createdAt DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE type = 'SHORT' AND isGhostUpload = 0 ORDER BY createdAt DESC")
    fun getShorts(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE type = 'VIDEO' AND isGhostUpload = 0 ORDER BY createdAt DESC")
    fun getRegularVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isGhostUpload = 1 ORDER BY createdAt DESC")
    fun getGhostUploads(): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity): Long

    @Update
    suspend fun updateVideo(video: VideoEntity)

    @Query("UPDATE videos SET isLiked = :liked, likes = likes + :offset WHERE id = :id")
    suspend fun updateLike(id: Int, liked: Boolean, offset: Int)

    @Query("UPDATE videos SET isSubscribed = :subscribed WHERE creatorName = :creatorName")
    suspend fun updateSubscription(creatorName: String, subscribed: Boolean)

    @Query("UPDATE videos SET captions = :captions WHERE id = :id")
    suspend fun updateCaptions(id: Int, captions: String)

    @Query("SELECT * FROM videos WHERE id = :id")
    suspend fun getVideoById(id: Int): VideoEntity?

    @Query("DELETE FROM videos WHERE id = :id")
    suspend fun deleteVideoById(id: Int)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY createdAt DESC")
    fun getCommentsForVideo(videoId: Int): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)
}

@Dao
interface BattleDao {
    @Query("SELECT * FROM battles")
    fun getAllBattles(): Flow<List<BattleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBattle(battle: BattleEntity)

    @Update
    suspend fun updateBattle(battle: BattleEntity)
}

@Database(entities = [VideoEntity::class, CommentEntity::class, BattleEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun commentDao(): CommentDao
    abstract fun battleDao(): BattleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "umaira_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
