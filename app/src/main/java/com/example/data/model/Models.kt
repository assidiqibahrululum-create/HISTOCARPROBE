package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assessments")
data class AssessmentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val classTarget: String,
    val imageDrawableRes: Int,
    val imageUrl: String? = null,
    val durationMinutes: Int = 20,
    val probeCount: Int = 4,
    val status: String = "Belum Dikerjakan", // "Belum Dikerjakan", "Sedang Berjalan", "Selesai"
    val createdDate: Long = System.currentTimeMillis(),
    
    // Probe 1: Identifikasi
    val probe1Title: String = "Identifikasi Unsur Karikatur",
    val probe1Prompt: String,
    val probe1OptionA: String,
    val probe1OptionB: String,
    val probe1OptionC: String,
    val probe1OptionD: String,
    val probe1CorrectIndex: Int,
    val probe1EssayPrompt: String = "Jelaskan alasan visual Anda memilih jawaban di atas berdasarkan simbol karikatur.",

    // Probe 2: Interpretasi Simbolik
    val probe2Title: String = "Interpretasi Simbolik & Satir",
    val probe2Prompt: String,
    val probe2OptionA: String,
    val probe2OptionB: String,
    val probe2OptionC: String,
    val probe2OptionD: String,
    val probe2CorrectIndex: Int,
    val probe2EssayPrompt: String = "Uraikan makna tersirat dari simbol/tokoh dalam gambar terhadap kritik sosial saat itu.",

    // Probe 3: Kontekstualisasi Historis
    val probe3Title: String = "Kontekstualisasi Historis",
    val probe3Prompt: String,
    val probe3OptionA: String,
    val probe3OptionB: String,
    val probe3OptionC: String,
    val probe3OptionD: String,
    val probe3CorrectIndex: Int,
    val probe3EssayPrompt: String = "Hubungkan peristiwa dalam karikatur dengan latar belakang politik-ekonomi periode tersebut.",

    // Probe 4: Evaluasi Kritis
    val probe4Title: String = "Evaluasi Kritis & Relevansi",
    val probe4Prompt: String,
    val probe4OptionA: String,
    val probe4OptionB: String,
    val probe4OptionC: String,
    val probe4OptionD: String,
    val probe4CorrectIndex: Int,
    val probe4EssayPrompt: String = "Bagaimanakah sudut pandang pembuat karikatur memengaruhi objektivitas narasi sejarah?"
)

@Entity(tableName = "student_submissions")
data class StudentSubmissionEntity(
    @PrimaryKey val id: String,
    val assessmentId: String,
    val studentNisn: String,
    val studentName: String,
    val studentClass: String,
    val submissionTime: Long = System.currentTimeMillis(),
    
    // 4 Dimension Probe Scores (0 - 100)
    val scoreIdentifikasi: Float,
    val scoreInterpretasi: Float,
    val scoreKontekstualisasi: Float,
    val scoreEvaluasi: Float,
    val totalScore: Float,
    
    // Group Placement: A, B, C, D, E
    val groupPlacement: String, // "A", "B", "C", "D", "E"
    val groupLabel: String,     // e.g., "Kelompok B — Berkembang Lanjut"
    
    // Student Essay Answers
    val essayIdentifikasi: String,
    val essayInterpretasi: String,
    val essayKontekstualisasi: String,
    val essayEvaluasi: String,
    
    val correctionStatus: String = "Selesai", // "Selesai", "Perlu Koreksi Esai"
    val teacherFeedback: String = "Pemahaman simbolik dan identifikasi visual sangat tajam."
)

@Entity(tableName = "caricature_archive")
data class CaricatureArchiveEntity(
    @PrimaryKey val id: String,
    val title: String,
    val sourcePlatform: String, // "KITLV Leiden", "Khastara Perpusnas", "JIKN ANRI", "Delpher", "Indonesia OneSearch"
    val year: Int,
    val imageDrawableRes: Int,
    val description: String,
    val isSavedToCollection: Boolean = false
)

enum class UserRole {
    GURU,
    SISWA,
    GUEST
}

data class UserSession(
    val role: UserRole = UserRole.GUEST,
    val nisn: String = "0054321987",
    val nik: String = "3201987654320001",
    val name: String = "Budi Santoso",
    val studentClass: String = "Kelas XI-1",
    val tempatLahir: String = "Bandung",
    val tanggalLahir: String = "15-08-2007",
    val teacherBelajarId: String = "guru.sejarah@belajar.id",
    val teacherName: String = "Dr. Farida Rahmawati, M.Pd.",
    val schoolName: String = "SMA Negeri 1 Bandung"
)
