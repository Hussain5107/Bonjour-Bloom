package com.example.bonjourbloom.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        ProfileEntity::class,
        FamilySettingsEntity::class,
        MasteryEntity::class,
        LearningAttemptEntity::class,
        ReviewItemEntity::class,
        ObjectiveMasteryEntity::class,
        DailyPlanEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun familySettingsDao(): FamilySettingsDao
    abstract fun masteryDao(): MasteryDao
    abstract fun learningAttemptDao(): LearningAttemptDao
    abstract fun reviewItemDao(): ReviewItemDao
    abstract fun objectiveMasteryDao(): ObjectiveMasteryDao
    abstract fun dailyPlanDao(): DailyPlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bonjour_bloom.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
