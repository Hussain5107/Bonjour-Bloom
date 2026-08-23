package com.example.bonjourbloom.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY name ASC")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: String): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    fun getProfileFlow(id: String): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: ProfileEntity)

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun deleteProfileById(id: String)
}

@Dao
interface FamilySettingsDao {
    @Query("SELECT * FROM family_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<FamilySettingsEntity?>

    @Query("SELECT * FROM family_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): FamilySettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: FamilySettingsEntity)
}

@Dao
interface MasteryDao {
    @Query("SELECT * FROM mastery_items WHERE profileId = :profileId")
    fun getMasteryForProfile(profileId: String): Flow<List<MasteryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMastery(item: MasteryEntity)

    @Query("DELETE FROM mastery_items WHERE profileId = :profileId")
    suspend fun clearMasteryForProfile(profileId: String)
}

@Dao
interface LearningAttemptDao {
    @Query("SELECT * FROM learning_attempts WHERE profileId = :profileId ORDER BY timestampEpochMs DESC")
    fun getAttemptsForProfile(profileId: String): Flow<List<LearningAttemptEntity>>

    @Query("SELECT * FROM learning_attempts WHERE profileId = :profileId")
    suspend fun getAttemptsForProfileDirect(profileId: String): List<LearningAttemptEntity>

    @Query("SELECT * FROM learning_attempts WHERE idempotencyKey = :key LIMIT 1")
    suspend fun getAttemptByIdempotencyKey(key: String): LearningAttemptEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAttempt(attempt: LearningAttemptEntity): Long

    @Query("DELETE FROM learning_attempts WHERE profileId = :profileId")
    suspend fun clearAttemptsForProfile(profileId: String)
}

@Dao
interface ReviewItemDao {
    @Query("SELECT * FROM review_items WHERE profileId = :profileId ORDER BY frenchText ASC")
    fun getReviewItemsForProfile(profileId: String): Flow<List<ReviewItemEntity>>

    @Query("SELECT * FROM review_items WHERE profileId = :profileId")
    suspend fun getReviewItemsForProfileDirect(profileId: String): List<ReviewItemEntity>

    @Query("SELECT * FROM review_items WHERE profileId = :profileId AND unitId = :unitId LIMIT 1")
    suspend fun getReviewItem(profileId: String, unitId: String): ReviewItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReviewItem(item: ReviewItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReviewItems(items: List<ReviewItemEntity>)

    @Query("UPDATE review_items SET isSuspended = :isSuspended WHERE profileId = :profileId AND unitId = :unitId")
    suspend fun setSuspended(profileId: String, unitId: String, isSuspended: Boolean)

    @Query("DELETE FROM review_items WHERE profileId = :profileId")
    suspend fun clearReviewItemsForProfile(profileId: String)
}

@Dao
interface ObjectiveMasteryDao {
    @Query("SELECT * FROM objective_mastery WHERE profileId = :profileId ORDER BY objectiveId ASC")
    fun getMasteryForProfile(profileId: String): Flow<List<ObjectiveMasteryEntity>>

    @Query("SELECT * FROM objective_mastery WHERE profileId = :profileId")
    suspend fun getMasteryForProfileDirect(profileId: String): List<ObjectiveMasteryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateObjectiveMastery(item: ObjectiveMasteryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateObjectiveMasteries(items: List<ObjectiveMasteryEntity>)

    @Query("DELETE FROM objective_mastery WHERE profileId = :profileId")
    suspend fun clearObjectiveMasteryForProfile(profileId: String)
}

@Dao
interface DailyPlanDao {
    @Query("SELECT * FROM daily_plans WHERE profileId = :profileId AND dateString = :dateString LIMIT 1")
    suspend fun getPlanForDate(profileId: String, dateString: String): DailyPlanEntity?

    @Query("SELECT * FROM daily_plans WHERE profileId = :profileId AND dateString = :dateString LIMIT 1")
    fun getPlanFlow(profileId: String, dateString: String): Flow<DailyPlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePlan(plan: DailyPlanEntity)

    @Query("DELETE FROM daily_plans WHERE profileId = :profileId")
    suspend fun clearPlansForProfile(profileId: String)
}
