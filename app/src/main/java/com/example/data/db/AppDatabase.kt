package com.example.data.db

import android.content.Context
import androidx.room.*
import com.example.data.model.AssessmentEntity
import com.example.data.model.CaricatureArchiveEntity
import com.example.data.model.StudentSubmissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssessmentDao {
    @Query("SELECT * FROM assessments ORDER BY createdDate DESC")
    fun getAllAssessments(): Flow<List<AssessmentEntity>>

    @Query("SELECT * FROM assessments WHERE id = :id")
    suspend fun getAssessmentById(id: String): AssessmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentEntity)

    @Update
    suspend fun updateAssessment(assessment: AssessmentEntity)
}

@Dao
interface SubmissionDao {
    @Query("SELECT * FROM student_submissions ORDER BY submissionTime DESC")
    fun getAllSubmissions(): Flow<List<StudentSubmissionEntity>>

    @Query("SELECT * FROM student_submissions WHERE assessmentId = :assessmentId")
    fun getSubmissionsForAssessment(assessmentId: String): Flow<List<StudentSubmissionEntity>>

    @Query("SELECT * FROM student_submissions WHERE studentNisn = :nisn ORDER BY submissionTime DESC")
    fun getSubmissionsForStudent(nisn: String): Flow<List<StudentSubmissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: StudentSubmissionEntity)

    @Update
    suspend fun updateSubmission(submission: StudentSubmissionEntity)
}

@Dao
interface CaricatureDao {
    @Query("SELECT * FROM caricature_archive")
    fun getAllCaricatures(): Flow<List<CaricatureArchiveEntity>>

    @Query("SELECT * FROM caricature_archive WHERE isSavedToCollection = 1")
    fun getSavedCollection(): Flow<List<CaricatureArchiveEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaricatures(caricatures: List<CaricatureArchiveEntity>)

    @Update
    suspend fun updateCaricature(caricature: CaricatureArchiveEntity)
}

@Database(
    entities = [
        AssessmentEntity::class,
        StudentSubmissionEntity::class,
        CaricatureArchiveEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assessmentDao(): AssessmentDao
    abstract fun submissionDao(): SubmissionDao
    abstract fun caricatureDao(): CaricatureDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "histocarprobe_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
