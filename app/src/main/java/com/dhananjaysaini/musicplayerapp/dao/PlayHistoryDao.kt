package com.dhananjaysaini.musicplayerapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dhananjaysaini.musicplayerapp.model.PlayHistoryEntity

@Dao
interface PlayHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: PlayHistoryEntity)

    @Query("SELECT * FROM play_history ORDER BY lastPlayed DESC LIMIT 20")
    fun getRecentlyPlayed(): LiveData<List<PlayHistoryEntity>>

    @Query("SELECT * FROM play_history ORDER BY playCount DESC LIMIT 20")
    fun getTopPlayed(): LiveData<List<PlayHistoryEntity>>

    @Query("SELECT * FROM play_history WHERE songId = :id LIMIT 20")
    suspend fun getSong(id: String): PlayHistoryEntity?
}
