package com.example.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateProbeQuestion(
        topic: String,
        probeLayer: String
    ): ProbeDraftResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "DEFAULT_KEY") {
            return@withContext getFallbackProbeDraft(topic, probeLayer)
        }

        val prompt = """
            Buatkan 1 soal pilihan ganda (PG) dan 1 petunjuk esai singkat pendukung probe untuk asesmen diagnostik kognitif sejarah berfokus pada karikatur tentang "$topic".
            Lapis Probe: "$probeLayer" (Pilih salah satu dari: Identifikasi, Interpretasi Simbolik, Kontekstualisasi Historis, Evaluasi Kritis).
            
            Format balasan persis seperti ini:
            SOAL_PG: [Tuliskan pertanyaan PG]
            OPSI_A: [Pilihan A]
            OPSI_B: [Pilihan B]
            OPSI_C: [Pilihan C]
            OPSI_D: [Pilihan D]
            KUNCI: [A/B/C/D]
            PETUNJUK_ESAI: [Tuliskan instruksi esai pendek alasan visual/historis]
        """.trimIndent()

        try {
            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonPayload.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val bodyString = response.body?.string()

            if (response.isSuccessful && !bodyString.isNullOrBlank()) {
                val jsonResp = JSONObject(bodyString)
                val candidates = jsonResp.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val cand = candidates.getJSONObject(0)
                    val content = cand.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text", "")
                        if (text.isNotBlank()) {
                            return@withContext parseAiResponse(text, topic, probeLayer)
                        }
                    }
                }
            }
            getFallbackProbeDraft(topic, probeLayer)
        } catch (e: Exception) {
            getFallbackProbeDraft(topic, probeLayer)
        }
    }

    private fun parseAiResponse(text: String, topic: String, probeLayer: String): ProbeDraftResult {
        var pgPrompt = ""
        var optA = ""
        var optB = ""
        var optC = ""
        var optD = ""
        var correctIdx = 0
        var essayPrompt = ""

        val lines = text.lines()
        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("SOAL_PG:") -> pgPrompt = trimmed.removePrefix("SOAL_PG:").trim()
                trimmed.startsWith("OPSI_A:") -> optA = trimmed.removePrefix("OPSI_A:").trim()
                trimmed.startsWith("OPSI_B:") -> optB = trimmed.removePrefix("OPSI_B:").trim()
                trimmed.startsWith("OPSI_C:") -> optC = trimmed.removePrefix("OPSI_C:").trim()
                trimmed.startsWith("OPSI_D:") -> optD = trimmed.removePrefix("OPSI_D:").trim()
                trimmed.startsWith("KUNCI:") -> {
                    val key = trimmed.removePrefix("KUNCI:").trim().uppercase()
                    correctIdx = when {
                        key.contains("B") -> 1
                        key.contains("C") -> 2
                        key.contains("D") -> 3
                        else -> 0
                    }
                }
                trimmed.startsWith("PETUNJUK_ESAI:") -> essayPrompt = trimmed.removePrefix("PETUNJUK_ESAI:").trim()
            }
        }

        if (pgPrompt.isBlank() || optA.isBlank()) {
            return getFallbackProbeDraft(topic, probeLayer)
        }

        return ProbeDraftResult(pgPrompt, optA, optB, optC, optD, correctIdx, essayPrompt)
    }

    private fun getFallbackProbeDraft(topic: String, probeLayer: String): ProbeDraftResult {
        return when (probeLayer.lowercase()) {
            "identifikasi" -> ProbeDraftResult(
                prompt = "Berdasarkan rincian visual pada karikatur $topic, simbol atau objek manakah yang paling mendominasi latar belakang?",
                optionA = "Objek utama berukuran menonjol dengan atribut jabatan/kekuasaan",
                optionB = "Bangunan gedung bertingkat gaya arsitektur modern",
                optionC = "Kerumunan massa tanpa identitas yang jelas",
                optionD = "Latar belakang lanskap alam kosong tanpa simbol",
                correctIndex = 0,
                essayPrompt = "Sebutkan 3 elemen visual spesifik dari karikatur ini dan jelaskan karakter fisik masing-masing!"
            )
            "interpretasi simbolik" -> ProbeDraftResult(
                prompt = "Makna metafora tersirat apakah yang ingin disampaikan kartunis melalui ekspresi dan pose tokoh pada karikatur $topic?",
                optionA = "Kritik satir terhadap dominasi wewenang dan penderitaan kelompok lemah",
                optionB = "Pujian tulus atas pencapaian ketaatan hukum",
                optionC = "Dokumentasi hiburan kartun tanpa pesan sosial",
                optionD = "Undangan kerja sama antar golongan",
                correctIndex = 0,
                essayPrompt = "Uraikan gagasan utama dan sindiran halus yang terkandung di balik metafora gambar tersebut!"
            )
            "kontekstualisasi historis" -> ProbeDraftResult(
                prompt = "Peristiwa atau kebijakan sejarah abad 20 manakah yang menjadi latar waktu lahirnya karikatur $topic?",
                optionA = "Dinamika pergerakan nasional, perimbangan pers, dan kebijakan pemerintah kolonial",
                optionB = "Perang Dunia II di kawasan Pasifik",
                optionC = "Masa pendudukan Jepang tahun 1942",
                optionD = "Revolusi industri di Inggris abad 18",
                correctIndex = 0,
                essayPrompt = "Hubungkan kondisi sosial-politik pada masa tersebut dengan narasi kritis yang disuarakan dalam karikatur!"
            )
            else -> ProbeDraftResult(
                prompt = "Bagaimanakah Anda mengevaluasi tingkat objektivitas karikatur $topic sebagai sumber belajar sejarah?",
                optionA = "Karikatur bersifat subjektif bernilai opini publik, sehingga harus dikroscek dengan dokumen sejarah lain",
                optionB = "Karikatur adalah catatan fakta 100% tanpa sudut pandang pembuat",
                optionC = "Karikatur tidak layak dijadikan sumber sejarah karena hanya gambar kartun",
                optionD = "Karikatur pasti dibuat atas perintah pemerintah resmi",
                correctIndex = 0,
                essayPrompt = "Jelaskan kelebihan dan keterbatasan karikatur pers sebagai bukti sejarah dibanding arsip tertulis resmi!"
            )
        }
    }
}

data class ProbeDraftResult(
    val prompt: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctIndex: Int,
    val essayPrompt: String
)
