package com.example.data.repository

import com.example.R
import com.example.data.db.AssessmentDao
import com.example.data.db.CaricatureDao
import com.example.data.db.SubmissionDao
import com.example.data.model.AssessmentEntity
import com.example.data.model.CaricatureArchiveEntity
import com.example.data.model.StudentSubmissionEntity
import kotlinx.coroutines.flow.Flow

class HistocarprobeRepository(
    private val assessmentDao: AssessmentDao,
    private val submissionDao: SubmissionDao,
    private val caricatureDao: CaricatureDao
) {
    val allAssessments: Flow<List<AssessmentEntity>> = assessmentDao.getAllAssessments()
    val allSubmissions: Flow<List<StudentSubmissionEntity>> = submissionDao.getAllSubmissions()
    val allCaricatures: Flow<List<CaricatureArchiveEntity>> = caricatureDao.getAllCaricatures()
    val savedCollection: Flow<List<CaricatureArchiveEntity>> = caricatureDao.getSavedCollection()

    fun getStudentSubmissions(nisn: String): Flow<List<StudentSubmissionEntity>> {
        return submissionDao.getSubmissionsForStudent(nisn)
    }

    suspend fun getAssessmentById(id: String): AssessmentEntity? {
        return assessmentDao.getAssessmentById(id)
    }

    suspend fun saveAssessment(assessment: AssessmentEntity) {
        assessmentDao.insertAssessment(assessment)
    }

    suspend fun updateAssessment(assessment: AssessmentEntity) {
        assessmentDao.updateAssessment(assessment)
    }

    suspend fun submitStudentAssessment(submission: StudentSubmissionEntity) {
        submissionDao.insertSubmission(submission)
    }

    suspend fun updateSubmission(submission: StudentSubmissionEntity) {
        submissionDao.updateSubmission(submission)
    }

    suspend fun toggleSaveCaricature(caricature: CaricatureArchiveEntity) {
        caricatureDao.updateCaricature(caricature.copy(isSavedToCollection = !caricature.isSavedToCollection))
    }

    suspend fun seedInitialDataIfEmpty() {
        // Prepopulate default caricatures and assessments for diagnostic cognitive probe testing
        val initialCaricatures = listOf(
            CaricatureArchiveEntity(
                id = "car_1",
                title = "Satir Perkeretaapian Hindia Belanda & Tanam Paksa",
                sourcePlatform = "KITLV Leiden",
                year = 1928,
                imageDrawableRes = R.drawable.img_caricature_hero_1786349502292,
                description = "Karikatur satir mengkritik pembangunan infrastruktur eksploitasi jalur kereta api colonial Hindia Belanda di Jawa abad ke-20.",
                isSavedToCollection = true
            ),
            CaricatureArchiveEntity(
                id = "car_2",
                title = "Gema Sumpah Pemuda & Pers Nasional Batavia",
                sourcePlatform = "Khastara Perpusnas",
                year = 1928,
                imageDrawableRes = R.drawable.img_caricature_sample_1786349518604,
                description = "Ilustrasi pers pribumi menggambarkan kebangkitan kesadaran nasionalis pemuda terpelajar di Batavia.",
                isSavedToCollection = true
            ),
            CaricatureArchiveEntity(
                id = "car_3",
                title = "Eksploitasi Agraria dan Politik Etis",
                sourcePlatform = "JIKN ANRI",
                year = 1915,
                imageDrawableRes = R.drawable.img_caricature_hero_1786349502292,
                description = "Karikatur sindiran terhadap kontradiksi antara janji Politik Etis dengan realitas kerja paksa perkebunan.",
                isSavedToCollection = false
            ),
            CaricatureArchiveEntity(
                id = "car_4",
                title = "Sensor Koran & Kebebasan Berpendapat",
                sourcePlatform = "Delpher",
                year = 1932,
                imageDrawableRes = R.drawable.img_caricature_sample_1786349518604,
                description = "Menggambarkan tindakan pemerintah kolonial memberlakukan Haatzaai Artikelen terhadap pers pribumi.",
                isSavedToCollection = false
            ),
            CaricatureArchiveEntity(
                id = "car_5",
                title = "Sistem Pendidikan Kolonial & Deskriminasi Class",
                sourcePlatform = "Indonesia OneSearch",
                year = 1922,
                imageDrawableRes = R.drawable.img_caricature_hero_1786349502292,
                description = "Kritik visual atas pembatasan akses sekolah ELS/HIS hanya bagi bangsawan dan elite kolonial.",
                isSavedToCollection = false
            )
        )
        caricatureDao.insertCaricatures(initialCaricatures)

        val defaultAssessment1 = AssessmentEntity(
            id = "asm_1",
            title = "Asesmen Diagnostik 1: Satir Perkeretaapian Colonial 1928",
            classTarget = "Kelas XI-1",
            imageDrawableRes = R.drawable.img_caricature_hero_1786349502292,
            durationMinutes = 20,
            probeCount = 4,
            status = "Sedang Berjalan",
            // Probe 1
            probe1Title = "Lapis 1: Identifikasi Visual",
            probe1Prompt = "Unsur visual manakah pada karikatur yang mewakili simbol kapitalisme perkeretaapian kolonial?",
            probe1OptionA = "Lokomotif uap berlogo Mahkota Kerajaan Belanda",
            probe1OptionB = "Pemandangan pegunungan dan pepohonan kelapa",
            probe1OptionC = "Rombongan pedagang tradisional di tepi jalan",
            probe1OptionD = "Bangunan dermaga pelabuhan pelayaran",
            probe1CorrectIndex = 0,
            probe1EssayPrompt = "Sebutkan 2 elemen objek visual lain dalam karikatur yang memperkuat tema eksploitasi modal asing!",
            
            // Probe 2
            probe2Title = "Lapis 2: Interpretasi Simbolik & Satir",
            probe2Prompt = "Apakah pesan satir tersirat dari ukuran relatif para pejabat kolonial yang tampak membesar dibanding buruh rel?",
            probe2OptionA = "Ketimpangan kekuasaan dan hegemoni otoritas kolonial atas tenaga kerja pribumi",
            probe2OptionB = "Efek perspektif jarak melukis biasa",
            probe2OptionC = "Penanda bahwa pejabat tersebut berbadan gemuk",
            probe2OptionD = "Simbol kemakmuran bersama yang dirasakan seluruh rakyat",
            probe2CorrectIndex = 0,
            probe2EssayPrompt = "Analisislah mengapa seniman karikatur memilih menggambar buruh dengan gaya siluet tanpa wajah!",

            // Probe 3
            probe3Title = "Lapis 3: Kontekstualisasi Historis",
            probe3Prompt = "Kebijakan kolonial manakah yang melatarbelakangi maraknya ekspansi rel kereta api private di Jawa abad 20?",
            probe3OptionA = "Undang-Undang Agraria 1870 (Agrarische Wet) dan privatisasi perkebunan",
            probe3OptionB = "Sistem Tanam Paksa (Cultuurstelsel) Van den Bosch",
            probe3OptionC = "Penyerahan Wajib VOC abad 18",
            probe3OptionD = "Konferensi Meja Bundar 1949",
            probe3CorrectIndex = 0,
            probe3EssayPrompt = "Hubungkan krisis depresi ekonomi global (Malaise 1929) dengan kondisi perkeretaapian dalam karikatur ini!",

            // Probe 4
            probe4Title = "Lapis 4: Evaluasi Kritis & Relevansi",
            probe4Prompt = "Sejauh mana sumber visual karikatur pers ini dapat dianggap obyektif sebagai fakta sejarah?",
            probe4OptionA = "Karikatur adalah konstruksi subjektif yang mencerminkan bias/kritik pembuatnya, namun bernilai tinggi mengungkap opini publik",
            probe4OptionB = "Karikatur 100% fakta mutlak tanpa bias",
            probe4OptionC = "Karikatur hanyalah hiburan fiktif tanpa nilai bukti sejarah",
            probe4OptionD = "Karikatur dibuat oleh pemerintah kolonial sebagai laporan resmi",
            probe4CorrectIndex = 0,
            probe4EssayPrompt = "Bagaimanakah Anda menggunakan sumber karikatur ini untuk menyusun narasi kritis sejarah modern?"
        )

        val defaultAssessment2 = AssessmentEntity(
            id = "asm_2",
            title = "Asesmen Diagnostik 2: Pers Pergerakan & Sumpah Pemuda",
            classTarget = "Kelas XI-1",
            imageDrawableRes = R.drawable.img_caricature_sample_1786349518604,
            durationMinutes = 25,
            probeCount = 4,
            status = "Belum Dikerjakan",
            // Probe 1
            probe1Title = "Lapis 1: Identifikasi Tokoh",
            probe1Prompt = "Siapakah elemen gerakan pemuda yang disimbolkan oleh mesin ketik dan pena tajam dalam karikatur?",
            probe1OptionA = "Jurnalis dan intelektual muda pergerakan nasional (Kwee Thiam Tjing / Suwardi)",
            probe1OptionB = "Tentara KNIL Hindia Belanda",
            probe1OptionC = "Gubernur Jenderal de Graeff",
            probe1OptionD = "Pedagang perantara komoditas rempah",
            probe1CorrectIndex = 0,
            probe1EssayPrompt = "Identifikasi 3 tulisan tajuk berita yang tertera pada lembaran kertas pers dalam gambar!",

            // Probe 2
            probe2Title = "Lapis 2: Interpretasi Simbolik",
            probe2Prompt = "Rantai yang terputus di dekat meja ketik menyimbolkan hal apa?",
            probe2OptionA = "Pelepasan dari kebodohan dan ikatan propaganda kesadaran pers",
            probe2OptionB = "Kerusakan barang bangunan studio cetak",
            probe2OptionC = "Kemacetan lalu lintas kota Batavia",
            probe2OptionD = "Simbol kerja sama diplomatik kolonial",
            probe2CorrectIndex = 0,
            probe2EssayPrompt = "Jelaskan makna metafora pena tajam yang menusuk benteng pertahanan kolonial!",

            // Probe 3
            probe3Title = "Lapis 3: Kontekstualisasi Historis",
            probe3Prompt = "Lembaga/organisasi pergerakan manakah yang menggalang gagasan pers Indonesia Bersatu tahun 1928?",
            probe3OptionA = "PPPI (Perhimpunan Pelajar-Pelajar Indonesia) & Kongres Pemuda II",
            probe3OptionB = "Budi Utomo tahun 1908",
            probe3OptionC = "Sarekat Islam cabang Surabaya",
            probe3OptionD = "Indische Partij tahun 1912",
            probe3CorrectIndex = 0,
            probe3EssayPrompt = "Mengapa pers pribumi menjadi sarana paling ditakuti oleh pemerintah kolonial pasca 1928?",

            // Probe 4
            probe4Title = "Lapis 4: Evaluasi Kritis",
            probe4Prompt = "Bagaimanakah keterbatasan aturan Haatzaai Artikelen memengaruhi strategi bahasa karikaturis?",
            probe4OptionA = "Memaksa kartunis menggunakan alegori, simbol hewan/benda, dan sindiran halus untuk menghindari delik pers",
            probe4OptionB = "Kartunis berhenti menggambar sama sekali",
            probe4OptionC = "Kartunis langsung menyebut nama pejabat tanpa sanksi",
            probe4OptionD = "Pemerintah memberi izin bebas tanpa batasan",
            probe4CorrectIndex = 0,
            probe4EssayPrompt = "Evaluasilah efektivitas bahasa karikatur dibanding artikel opini tulisan biasa pada masa pergerakan!"
        )

        assessmentDao.insertAssessment(defaultAssessment1)
        assessmentDao.insertAssessment(defaultAssessment2)

        // Seed sample student submissions for class XI-1
        val seedSubmissions = listOf(
            StudentSubmissionEntity(
                id = "sub_1",
                assessmentId = "asm_1",
                studentNisn = "0051112223",
                studentName = "Ahmad Dahlan",
                studentClass = "Kelas XI-1",
                scoreIdentifikasi = 95f,
                scoreInterpretasi = 90f,
                scoreKontekstualisasi = 85f,
                scoreEvaluasi = 90f,
                totalScore = 90f,
                groupPlacement = "A",
                groupLabel = "Kelompok A — Sangat Mahir",
                essayIdentifikasi = "Visual keretapi memperlihatkan pengawasan ketat aparat kolonial terhadap jalur logistik.",
                essayInterpretasi = "Satir ini menyentil kontradiksi janji modernisasi kereta api yang malah memiskinkan petani lokal.",
                essayKontekstualisasi = "Terjadi pasca berlakunya Agrarische Wet 1870 di mana modal swasta Eropa masuk secara bebas.",
                essayEvaluasi = "Karikatur pers pribumi memberi penyeimbang terhadap laporan resmi buatan Gubernur Jenderal.",
                correctionStatus = "Selesai",
                teacherFeedback = "Penalaran kritis sangat baik dan rujukan historis tepat."
            ),
            StudentSubmissionEntity(
                id = "sub_2",
                assessmentId = "asm_1",
                studentNisn = "0054321987",
                studentName = "Budi Santoso",
                studentClass = "Kelas XI-1",
                scoreIdentifikasi = 85f,
                scoreInterpretasi = 80f,
                scoreKontekstualisasi = 80f,
                scoreEvaluasi = 60f,
                totalScore = 76.25f,
                groupPlacement = "B",
                groupLabel = "Kelompok B — Berkembang Lanjut",
                essayIdentifikasi = "Saya melihat kereta api, pejabat bertopi pet, dan kuli panggul.",
                essayInterpretasi = "Pejabat besar menggambarkan penguasa, sedangkan kuli kecil mewakili rakyat miskin.",
                essayKontekstualisasi = "Pengaruh pembangunan jalan dan rel kereta oleh pemerintah Hindia Belanda.",
                essayEvaluasi = "Karikatur cukup bagus tapi agak bias mendukung kritik pers.",
                correctionStatus = "Selesai",
                teacherFeedback = "Identifikasi & Interpretasi sangat kuat. Tingkatkan analisis evaluasi sumber sejarah!"
            ),
            StudentSubmissionEntity(
                id = "sub_3",
                assessmentId = "asm_1",
                studentNisn = "0053334445",
                studentName = "Citra Dewi",
                studentClass = "Kelas XI-1",
                scoreIdentifikasi = 75f,
                scoreInterpretasi = 70f,
                scoreKontekstualisasi = 65f,
                scoreEvaluasi = 50f,
                totalScore = 65f,
                groupPlacement = "C",
                groupLabel = "Kelompok C — Berkembang",
                essayIdentifikasi = "Obyek gambar adalah stasiun kereta zaman dulu.",
                essayInterpretasi = "Menceritakan tentang suasana perjalan orang zaman dulu.",
                essayKontekstualisasi = "Zaman penjajahan Belanda di pulau Jawa.",
                essayEvaluasi = "Gambar dibuat agar orang tertarik membaca koran.",
                correctionStatus = "Selesai",
                teacherFeedback = "Perlu pendampingan untuk menghubungkan objek dengan konsep Politik Etis."
            ),
            StudentSubmissionEntity(
                id = "sub_4",
                assessmentId = "asm_1",
                studentNisn = "0054445556",
                studentName = "Doni Pratama",
                studentClass = "Kelas XI-1",
                scoreIdentifikasi = 60f,
                scoreInterpretasi = 50f,
                scoreKontekstualisasi = 45f,
                scoreEvaluasi = 40f,
                totalScore = 48.75f,
                groupPlacement = "D",
                groupLabel = "Kelompok D — Mulai Berkembang",
                essayIdentifikasi = "Ada gambar orang dan kereta uap.",
                essayInterpretasi = "Orang-orang sedang berada di pasar.",
                essayKontekstualisasi = "Tidak tahu tepatnya tahun berapa.",
                essayEvaluasi = "Gambarnya kurang jelas.",
                correctionStatus = "Perlu Koreksi Esai",
                teacherFeedback = "Diberikan scaffolding materi literasi visual karikatur dasar."
            ),
            StudentSubmissionEntity(
                id = "sub_5",
                assessmentId = "asm_1",
                studentNisn = "0055556667",
                studentName = "Eka Rahmawati",
                studentClass = "Kelas XI-1",
                scoreIdentifikasi = 40f,
                scoreInterpretasi = 35f,
                scoreKontekstualisasi = 30f,
                scoreEvaluasi = 25f,
                totalScore = 32.5f,
                groupPlacement = "E",
                groupLabel = "Kelompok E — Perlu Intervensi Khusus",
                essayIdentifikasi = "Kereta api tua.",
                essayInterpretasi = "Tidak paham maksud gambar.",
                essayKontekstualisasi = "Belanda.",
                essayEvaluasi = "Biasa saja.",
                correctionStatus = "Perlu Koreksi Esai",
                teacherFeedback = "Perlu matrikulasi remedi membaca simbol visual dan linimasa pergerakan."
            )
        )

        for (sub in seedSubmissions) {
            submissionDao.insertSubmission(sub)
        }
    }
}
