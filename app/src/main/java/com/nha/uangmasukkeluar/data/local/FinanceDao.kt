package com.nha.uangmasukkeluar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nha.uangmasukkeluar.data.local.entity.FinanceInEntity
import com.nha.uangmasukkeluar.data.local.entity.FinanceOutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {

    @Query("SELECT * FROM finance_in ORDER BY dateTime ASC")
    fun getFinanceIn(): Flow<List<FinanceInEntity>>

    @Query("SELECT * FROM finance_out ORDER BY dateTime ASC")
    fun getFinanceOut(): Flow<List<FinanceOutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinanceIn(data: FinanceInEntity)

    @Update
    suspend fun updateFinanceIn(data: FinanceInEntity)

    @Query("DELETE FROM finance_in WHERE id = :id")
    suspend fun deleteFinanceIn(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinanceOut(data: FinanceOutEntity)

    @Query("DELETE FROM finance_out WHERE id = :id")
    suspend fun deleteFinanceOut(id: Int)
}
