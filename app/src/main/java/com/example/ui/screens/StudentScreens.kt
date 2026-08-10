@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.AssessmentEntity
import com.example.data.model.StudentSubmissionEntity
import com.example.data.model.UserSession
import com.example.ui.components.DiagnosticRadarChart
import com.example.ui.components.GroupBadge
import com.example.ui.components.PrimaryLargeButton
import com.example.ui.theme.*

// 5. BERANDA SISWA (with bottom nav: Beranda, Riwayat, Profil)
@Composable
fun StudentHomeScreen(
    userSession: UserSession,
    assessments: List<AssessmentEntity>,
    submissions: List<StudentSubmissionEntity>,
    onSelectAssessment: (AssessmentEntity) -> Unit,
    onViewSubmissionResult: (StudentSubmissionEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Top Student Header Greeting
        Surface(
            color = NavyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Halo, ${userSession.name} 👋",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "${userSession.studentClass} • NISN: ${userSession.nisn}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TealLight
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MustardYellow
                    ) {
                        Text(
                            text = "SISWA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = NavyDark
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Diagnostic Quick Status Banner
                val latestSub = submissions.firstOrNull()
                Surface(
                    color = TealDark,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = null,
                            tint = MustardYellow,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Status Diagnostik Kognitif",
                                style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(0.8f))
                            )
                            if (latestSub != null) {
                                Text(
                                    text = latestSub.groupLabel,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            } else {
                                Text(
                                    text = "Belum Ada Asesmen Selesai",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Daftar Asesmen Karikatur",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = NavyDark
            ),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Vertical scrollable list of assessment cards
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(assessments) { item ->
                AssessmentCard(
                    assessment = item,
                    submissions = submissions,
                    onClick = { onSelectAssessment(item) },
                    onResultClick = {
                        val sub = submissions.find { it.assessmentId == item.id }
                        if (sub != null) onViewSubmissionResult(sub) else onSelectAssessment(item)
                    }
                )
            }
        }
    }
}

@Composable
fun AssessmentCard(
    assessment: AssessmentEntity,
    submissions: List<StudentSubmissionEntity>,
    onClick: () -> Unit,
    onResultClick: () -> Unit
) {
    val sub = submissions.find { it.assessmentId == assessment.id }
    val isCompleted = sub != null || assessment.status == "Selesai"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isCompleted && sub != null) onResultClick() else onClick()
            }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Image(
                    painter = painterResource(
                        id = if (assessment.imageDrawableRes != 0) assessment.imageDrawableRes else R.drawable.img_caricature_hero_1786349502292
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        isCompleted -> GroupAGreen
                        assessment.status == "Sedang Berjalan" -> MustardYellow
                        else -> TextMuted
                    },
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = when {
                            isCompleted -> "Selesai"
                            assessment.status == "Sedang Berjalan" -> "Sedang Berjalan"
                            else -> "Belum Dikerjakan"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (assessment.status == "Sedang Berjalan") NavyDark else Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = assessment.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Layers,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${assessment.probeCount} Lapis Probe",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${assessment.durationMinutes} Menit",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }

                if (sub != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GroupBadge(group = sub.groupPlacement)
                        Text(
                            text = "Lihat Hasil ➔",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

// 6. HALAMAN MULAI ASESMEN & INTERACTIVE TEST TAKING SCREEN
@Composable
fun StudentAssessmentInstructionScreen(
    assessment: AssessmentEntity,
    onStartTest: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Petunjuk Asesmen", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Image(
                            painter = painterResource(
                                id = if (assessment.imageDrawableRes != 0) assessment.imageDrawableRes else R.drawable.img_caricature_hero_1786349502292
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = assessment.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Asesmen diagnostik ini menggunakan metode Form 4 Lapis Probe Karikatur Sejarah dari guru. Anda akan diminta menjawab pilihan ganda dan memberikan esai singkat pendukung probe pada tiap lapis.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = TealContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ringkasan Ketentuan Probe Guru:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Jumlah Probe: ${assessment.probeCount} Lapis (Identifikasi, Interpretasi, Kontekstualisasi, Evaluasi Kritis)", style = MaterialTheme.typography.bodySmall)
                        Text("• Estimasi Durasi: ${assessment.durationMinutes} Menit", style = MaterialTheme.typography.bodySmall)
                        Text("• Cara Menjawab: Pilihan Ganda + Esai Singkat Pendukung Probe", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            PrimaryLargeButton(
                text = "Mulai Asesmen",
                onClick = onStartTest,
                containerColor = TealPrimary,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

// Interactive 4-Layer Probe Test Taking Screen
@Composable
fun StudentTakeAssessmentScreen(
    assessment: AssessmentEntity,
    onSubmit: (
        ans1: Int, essay1: String,
        ans2: Int, essay2: String,
        ans3: Int, essay3: String,
        ans4: Int, essay4: String
    ) -> Unit,
    onBack: () -> Unit
) {
    var currentStep by remember { mutableStateOf(1) } // 1, 2, 3, 4
    var showZoomModal by remember { mutableStateOf(false) }

    // Step answers state
    var ans1 by remember { mutableStateOf(0) }
    var essay1 by remember { mutableStateOf("") }

    var ans2 by remember { mutableStateOf(0) }
    var essay2 by remember { mutableStateOf("") }

    var ans3 by remember { mutableStateOf(0) }
    var essay3 by remember { mutableStateOf("") }

    var ans4 by remember { mutableStateOf(0) }
    var essay4 by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Probe Lapis $currentStep dari 4", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Batal")
                    }
                },
                actions = {
                    IconButton(onClick = { showZoomModal = true }) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "Perbesar Karikatur", tint = MustardYellow)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Caricature Image Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showZoomModal = true }
            ) {
                Image(
                    painter = painterResource(
                        id = if (assessment.imageDrawableRes != 0) assessment.imageDrawableRes else R.drawable.img_caricature_hero_1786349502292
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ZoomIn, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ketuk untuk Perbesar", style = MaterialTheme.typography.labelSmall.copy(color = Color.White))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Linear Step Indicator
            LinearProgressIndicator(
                progress = { currentStep / 4f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = TealPrimary,
                trackColor = TealContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Step Content
            when (currentStep) {
                1 -> ProbeStepCard(
                    layerTitle = assessment.probe1Title,
                    prompt = assessment.probe1Prompt,
                    options = listOf(assessment.probe1OptionA, assessment.probe1OptionB, assessment.probe1OptionC, assessment.probe1OptionD),
                    selectedOption = ans1,
                    onOptionSelect = { ans1 = it },
                    essayPrompt = assessment.probe1EssayPrompt,
                    essayAnswer = essay1,
                    onEssayChange = { essay1 = it }
                )
                2 -> ProbeStepCard(
                    layerTitle = assessment.probe2Title,
                    prompt = assessment.probe2Prompt,
                    options = listOf(assessment.probe2OptionA, assessment.probe2OptionB, assessment.probe2OptionC, assessment.probe2OptionD),
                    selectedOption = ans2,
                    onOptionSelect = { ans2 = it },
                    essayPrompt = assessment.probe2EssayPrompt,
                    essayAnswer = essay2,
                    onEssayChange = { essay2 = it }
                )
                3 -> ProbeStepCard(
                    layerTitle = assessment.probe3Title,
                    prompt = assessment.probe3Prompt,
                    options = listOf(assessment.probe3OptionA, assessment.probe3OptionB, assessment.probe3OptionC, assessment.probe3OptionD),
                    selectedOption = ans3,
                    onOptionSelect = { ans3 = it },
                    essayPrompt = assessment.probe3EssayPrompt,
                    essayAnswer = essay3,
                    onEssayChange = { essay3 = it }
                )
                else -> ProbeStepCard(
                    layerTitle = assessment.probe4Title,
                    prompt = assessment.probe4Prompt,
                    options = listOf(assessment.probe4OptionA, assessment.probe4OptionB, assessment.probe4OptionC, assessment.probe4OptionD),
                    selectedOption = ans4,
                    onOptionSelect = { ans4 = it },
                    essayPrompt = assessment.probe4EssayPrompt,
                    essayAnswer = essay4,
                    onEssayChange = { essay4 = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sticky Navigation Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("← Sebelumnya")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Button(
                    onClick = {
                        if (currentStep < 4) {
                            currentStep++
                        } else {
                            onSubmit(ans1, essay1, ans2, essay2, ans3, essay3, ans4, essay4)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (currentStep < 4) "Lanjut Probe ➔" else "Kirim Jawaban",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showZoomModal) {
        Dialog(onDismissRequest = { showZoomModal = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceLight
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(
                            id = if (assessment.imageDrawableRes != 0) assessment.imageDrawableRes else R.drawable.img_caricature_hero_1786349502292
                        ),
                        contentDescription = "Detail Zoom Karikatur",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { showZoomModal = false }) {
                        Text("Tutup Zoom")
                    }
                }
            }
        }
    }
}

@Composable
fun ProbeStepCard(
    layerTitle: String,
    prompt: String,
    options: List<String>,
    selectedOption: Int,
    onOptionSelect: (Int) -> Unit,
    essayPrompt: String,
    essayAnswer: String,
    onEssayChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                color = TealContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = layerTitle,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = prompt,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            options.forEachIndexed { idx, opt ->
                val isSelected = selectedOption == idx
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) TealContainer else SurfaceVariant,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, TealPrimary) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onOptionSelect(idx) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onOptionSelect(idx) },
                            colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = opt,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pendukung Probe (Esai Singkat):",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )

            Text(
                text = essayPrompt,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = essayAnswer,
                onValueChange = onEssayChange,
                placeholder = { Text("Tuliskan uraian analisis Anda di sini...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

// 7 & 8. HALAMAN HASIL DIAGNOSTIK (SISWA)
@Composable
fun StudentDiagnosticResultScreen(
    submission: StudentSubmissionEntity,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hasil Diagnostik Kognitif", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Placement Group Badge Header
            Surface(
                color = SurfaceLight,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "HASIL PENEMPATAN KELOMPOK",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    GroupBadge(group = submission.groupPlacement)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Skor Rata-Rata Probe: ${submission.totalScore.toInt()} / 100",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Kamu kuat di Identifikasi & Interpretasi Simbolik — fokus belajar berikutnya: Evaluasi Kritis Karikatur.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4-Dimension Radar Chart
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Diagram Radar 4 Dimensi Probe",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )

                    DiagnosticRadarChart(
                        scoreIdentifikasi = submission.scoreIdentifikasi,
                        scoreInterpretasi = submission.scoreInterpretasi,
                        scoreKontekstualisasi = submission.scoreKontekstualisasi,
                        scoreEvaluasi = submission.scoreEvaluasi
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Strongest & Weakest Dimensions Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = TealContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = TealPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dimensi Terkuat", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Identifikasi Visual (${submission.scoreIdentifikasi.toInt()}%)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingDown, contentDescription = null, tint = GroupDOrange)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dimensi Terlemah", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Evaluasi Kritis (${submission.scoreEvaluasi.toInt()}%)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Scrollable Learning Recommendations
            Text(
                text = "Rekomendasi Belajar Terpersonalisasi",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    RecommendationCard(
                        title = "Modul Evaluasi Kritis Pers Kolonial",
                        category = "Materi Penguatan",
                        duration = "15 Min Baca"
                    )
                }
                item {
                    RecommendationCard(
                        title = "Latihan Analisis Haatzaai Artikelen",
                        category = "Studi Kasus",
                        duration = "20 Min Kuis"
                    )
                }
                item {
                    RecommendationCard(
                        title = "E-Library Khastara Perpusnas",
                        category = "Arsip Referensi",
                        duration = "Akses Gratis"
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(
    title: String,
    category: String,
    duration: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.width(220.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(
                color = TealContainer,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = duration, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            }
        }
    }
}

// History & Profile Tabs for Student
@Composable
fun StudentHistoryScreen(
    submissions: List<StudentSubmissionEntity>,
    onSelectSubmission: (StudentSubmissionEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(20.dp)
    ) {
        Text(
            text = "Riwayat Asesmen & Diagnostik",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (submissions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada riwayat asesmen yang diselesaikan.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(submissions) { sub ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectSubmission(sub) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = sub.studentName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Skor: ${sub.totalScore.toInt()} / 100",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                            GroupBadge(group = sub.groupPlacement)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentProfileScreen(
    userSession: UserSession,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            shape = RoundedCornerShape(30.dp),
            color = NavyDark,
            modifier = Modifier.size(90.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = userSession.name.take(1),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MustardYellow
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userSession.name,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
        )

        Text(
            text = "${userSession.studentClass} • SMA Negeri 1 Bandung",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileDetailRow(label = "NISN", value = userSession.nisn)
                Divider(modifier = Modifier.padding(vertical = 10.dp))
                ProfileDetailRow(label = "NIK", value = userSession.nik)
                Divider(modifier = Modifier.padding(vertical = 10.dp))
                ProfileDetailRow(label = "Tempat Lahir", value = userSession.tempatLahir)
                Divider(modifier = Modifier.padding(vertical = 10.dp))
                ProfileDetailRow(label = "Tanggal Lahir", value = userSession.tanggalLahir)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = GroupEPink),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Keluar Akun Siswa", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = NavyDark))
    }
}
