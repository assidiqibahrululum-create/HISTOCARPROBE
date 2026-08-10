package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.HistocarprobeRepository
import com.example.remote.GeminiClient
import com.example.remote.ProbeDraftResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = HistocarprobeRepository(
        assessmentDao = db.assessmentDao(),
        submissionDao = db.submissionDao(),
        caricatureDao = db.caricatureDao()
    )

    val userSession = MutableStateFlow(UserSession())

    val allAssessments: StateFlow<List<AssessmentEntity>> = repository.allAssessments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubmissions: StateFlow<List<StudentSubmissionEntity>> = repository.allSubmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCaricatures: StateFlow<List<CaricatureArchiveEntity>> = repository.allCaricatures
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedCollection: StateFlow<List<CaricatureArchiveEntity>> = repository.savedCollection
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active selection flows
    val activeAssessment = MutableStateFlow<AssessmentEntity?>(null)
    val activeSubmission = MutableStateFlow<StudentSubmissionEntity?>(null)
    val selectedCaricatureForAssessment = MutableStateFlow<CaricatureArchiveEntity?>(null)

    val isAiLoading = MutableStateFlow(false)
    val aiDraftMessage = MutableStateFlow("")

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun loginStudent(nisn: String, nik: String, tempatLahir: String, tanggalLahir: String) {
        userSession.value = UserSession(
            role = UserRole.SISWA,
            nisn = nisn.ifBlank { "0054321987" },
            nik = nik.ifBlank { "3201987654320001" },
            name = "Budi Santoso",
            studentClass = "Kelas XI-1",
            tempatLahir = tempatLahir.ifBlank { "Bandung" },
            tanggalLahir = tanggalLahir.ifBlank { "15-08-2007" }
        )
    }

    fun loginTeacher(belajarId: String) {
        userSession.value = UserSession(
            role = UserRole.GURU,
            teacherBelajarId = belajarId.ifBlank { "guru.sejarah@belajar.id" },
            teacherName = "Dr. Farida Rahmawati, M.Pd.",
            schoolName = "SMA Negeri 1 Bandung"
        )
    }

    fun logout() {
        userSession.value = UserSession(role = UserRole.GUEST)
    }

    fun selectAssessmentToTake(assessment: AssessmentEntity) {
        activeAssessment.value = assessment
    }

    fun submitAssessmentResult(
        assessment: AssessmentEntity,
        ans1: Int, essay1: String,
        ans2: Int, essay2: String,
        ans3: Int, essay3: String,
        ans4: Int, essay4: String
    ) {
        // Calculate scores
        val s1 = if (ans1 == assessment.probe1CorrectIndex) 100f else 40f
        val s2 = if (ans2 == assessment.probe2CorrectIndex) 100f else 40f
        val s3 = if (ans3 == assessment.probe3CorrectIndex) 100f else 40f
        val s4 = if (ans4 == assessment.probe4CorrectIndex) 100f else 40f

        val total = (s1 + s2 + s3 + s4) / 4f

        val (group, label) = when {
            total >= 88f -> "A" to "Kelompok A — Sangat Mahir"
            total >= 72f -> "B" to "Kelompok B — Berkembang Lanjut"
            total >= 60f -> "C" to "Kelompok C — Berkembang"
            total >= 45f -> "D" to "Kelompok D — Mulai Berkembang"
            else -> "E" to "Kelompok E — Perlu Intervensi Khusus"
        }

        val session = userSession.value
        val submission = StudentSubmissionEntity(
            id = "sub_" + UUID.randomUUID().toString().take(8),
            assessmentId = assessment.id,
            studentNisn = session.nisn,
            studentName = session.name,
            studentClass = session.studentClass,
            submissionTime = System.currentTimeMillis(),
            scoreIdentifikasi = s1,
            scoreInterpretasi = s2,
            scoreKontekstualisasi = s3,
            scoreEvaluasi = s4,
            totalScore = total,
            groupPlacement = group,
            groupLabel = label,
            essayIdentifikasi = essay1.ifBlank { "Penjelasan visual singkat." },
            essayInterpretasi = essay2.ifBlank { "Analisis makna tersirat." },
            essayKontekstualisasi = essay3.ifBlank { "Hubungan dengan era pergerakan." },
            essayEvaluasi = essay4.ifBlank { "Tinjauan kritis sumber karikatur." },
            correctionStatus = "Selesai"
        )

        viewModelScope.launch {
            repository.submitStudentAssessment(submission)
            // Update assessment status for student
            repository.updateAssessment(assessment.copy(status = "Selesai"))
            activeSubmission.value = submission
        }
    }

    fun selectSubmissionDetail(submission: StudentSubmissionEntity) {
        activeSubmission.value = submission
    }

    fun toggleSaveCaricature(caricature: CaricatureArchiveEntity) {
        viewModelScope.launch {
            repository.toggleSaveCaricature(caricature)
        }
    }

    fun selectCaricatureForAssessmentCreation(caricature: CaricatureArchiveEntity) {
        selectedCaricatureForAssessment.value = caricature
    }

    fun generateAiDraftForProbe(
        topic: String,
        probeLayer: String,
        onResult: (ProbeDraftResult) -> Unit
    ) {
        viewModelScope.launch {
            isAiLoading.value = true
            val draft = GeminiClient.generateProbeQuestion(topic, probeLayer)
            isAiLoading.value = false
            onResult(draft)
        }
    }

    fun saveTeacherCreatedAssessment(assessment: AssessmentEntity) {
        viewModelScope.launch {
            repository.saveAssessment(assessment)
        }
    }

    fun updateManualPlacement(submission: StudentSubmissionEntity, newGroup: String, notes: String) {
        val label = when (newGroup) {
            "A" -> "Kelompok A — Sangat Mahir"
            "B" -> "Kelompok B — Berkembang Lanjut"
            "C" -> "Kelompok C — Berkembang"
            "D" -> "Kelompok D — Mulai Berkembang"
            else -> "Kelompok E — Perlu Intervensi Khusus"
        }
        val updated = submission.copy(
            groupPlacement = newGroup,
            groupLabel = label,
            teacherFeedback = notes.ifBlank { "Penyesuaian penempatan manual oleh guru." }
        )
        viewModelScope.launch {
            repository.updateSubmission(updated)
            activeSubmission.value = updated
        }
    }
}
