@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.AssessmentEntity
import com.example.data.model.CaricatureArchiveEntity
import com.example.data.model.StudentSubmissionEntity
import com.example.data.model.UserSession
import com.example.ui.components.GroupBadge
import com.example.ui.components.Mini4BarChart
import com.example.ui.components.PrimaryLargeButton
import com.example.ui.theme.*

// 9. DASHBOARD GURU
@Composable
fun TeacherDashboardScreen(
    userSession: UserSession,
    assessments: List<AssessmentEntity>,
    submissions: List<StudentSubmissionEntity>,
    onNavigateCreate: () -> Unit,
    onNavigateResults: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Teacher Header
        Surface(
            color = NavyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = userSession.teacherName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "${userSession.schoolName} • ${userSession.teacherBelajarId}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TealLight)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MustardYellow
                    ) {
                        Text(
                            text = "GURU",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = NavyDark
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal Scrollable Summary Cards
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SummaryMetricCard(
                    title = "Jumlah Kelas",
                    value = "3 Kelas",
                    subtitle = "XI-1, XI-2, XI-3",
                    icon = Icons.Default.Class,
                    color = TealContainer
                )
            }
            item {
                SummaryMetricCard(
                    title = "Jumlah Asesmen",
                    value = "${assessments.size} Asesmen",
                    subtitle = "4 Lapis Probe Aktif",
                    icon = Icons.Default.Assignment,
                    color = SurfaceVariant
                )
            }
            item {
                SummaryMetricCard(
                    title = "Status Pengerjaan",
                    value = "85% Selesai",
                    subtitle = "${submissions.size} Siswa Mengumpulkan",
                    icon = Icons.Default.FactCheck,
                    color = MustardLight
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Daftar Kelas Binaan",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )

            TextButton(onClick = onNavigateResults) {
                Text("Lihat Hasil Kelompok ➔", color = TealPrimary, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Class List
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listOf("Kelas XI-1", "Kelas XI-2", "Kelas XI-3")) { className ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateResults() }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TealContainer,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Groups, contentDescription = null, tint = TealPrimary)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = className,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NavyDark
                                    )
                                )
                                Text(
                                    text = "32 Siswa • Asesmen Karikatur 1928",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                        }

                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier.width(160.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = NavyDark)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary))
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyDark))
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
        }
    }
}

// 10. HALAMAN BANK KARIKATUR TERINTEGRASI AI (GURU)
@Composable
fun TeacherBankSoalScreen(
    caricatures: List<CaricatureArchiveEntity>,
    savedCollection: List<CaricatureArchiveEntity>,
    onToggleSave: (CaricatureArchiveEntity) -> Unit,
    onUseForAssessment: (CaricatureArchiveEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterChip by remember { mutableStateOf("Semua") } // "Khastara Perpusnas", "Indonesia OneSearch", "JIKN ANRI", "Delpher", "KITLV Leiden"
    var selectedTab by remember { mutableStateOf(0) } // 0: "E-Library Karikatur", 1: "Koleksi Saya"
    var selectedDetailItem by remember { mutableStateOf<CaricatureArchiveEntity?>(null) }

    val filterOptions = listOf("Semua", "Khastara Perpusnas", "Indonesia OneSearch", "JIKN ANRI", "Delpher", "KITLV Leiden")

    val displayedList = if (selectedTab == 1) {
        savedCollection
    } else {
        caricatures.filter { item ->
            (selectedFilterChip == "Semua" || item.sourcePlatform.equals(selectedFilterChip, ignoreCase = true)) &&
            (searchQuery.isBlank() || item.title.contains(searchQuery, ignoreCase = true) || item.description.contains(searchQuery, ignoreCase = true))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Search & Header
        Surface(color = NavyDark, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Bank Karikatur Terintegrasi AI",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Full-width search input with camera/upload icon
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari tema karikatur sejarah...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                    trailingIcon = {
                        IconButton(onClick = { /* Camera/Upload reference image */ }) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Unggah Gambar Referensi", tint = MustardYellow)
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceLight,
                        unfocusedContainerColor = SurfaceLight,
                        focusedBorderColor = MustardYellow,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Horizontal filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterOptions) { chip ->
                FilterChip(
                    selected = selectedFilterChip == chip,
                    onClick = { selectedFilterChip = chip },
                    label = { Text(chip) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Tab switcher
        TabRow(selectedTabIndex = selectedTab, containerColor = SurfaceLight) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Arsip Nasional (${caricatures.size})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Koleksi Saya (${savedCollection.size})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2-Column Grid
        if (displayedList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tidak ada karikatur sejarah ditemukan.")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayedList) { item ->
                    CaricatureGridCard(
                        item = item,
                        onClick = { selectedDetailItem = item },
                        onToggleSave = { onToggleSave(item) }
                    )
                }
            }
        }
    }

    if (selectedDetailItem != null) {
        val item = selectedDetailItem!!
        Dialog(onDismissRequest = { selectedDetailItem = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceLight
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Image(
                        painter = painterResource(id = if (item.imageDrawableRes != 0) item.imageDrawableRes else R.drawable.img_caricature_hero_1786349502292),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyDark)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = TealContainer, shape = RoundedCornerShape(6.dp)) {
                            Text(text = item.sourcePlatform, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = NavyDark), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Tahun ${item.year}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = item.description, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = {
                                onToggleSave(item)
                                selectedDetailItem = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                if (item.isSavedToCollection) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (item.isSavedToCollection) "Tersimpan" else "Simpan")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                onUseForAssessment(item)
                                selectedDetailItem = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Buat Asesmen")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CaricatureGridCard(
    item: CaricatureArchiveEntity,
    onClick: () -> Unit,
    onToggleSave: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Image(
                    painter = painterResource(id = if (item.imageDrawableRes != 0) item.imageDrawableRes else R.drawable.img_caricature_hero_1786349502292),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onToggleSave,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        if (item.isSavedToCollection) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Simpan ke Koleksi",
                        tint = MustardYellow
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Surface(color = TealContainer, shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = item.sourcePlatform,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NavyDark),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.title,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = NavyDark)
                )
                Text(
                    text = "Tahun ${item.year}",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )
            }
        }
    }
}

// 11. HALAMAN PENYUSUNAN ASESMEN (GURU)
@Composable
fun TeacherCreateAssessmentScreen(
    selectedCaricature: CaricatureArchiveEntity?,
    onGenerateAiDraft: (topic: String, layer: String, onResult: (com.example.remote.ProbeDraftResult) -> Unit) -> Unit,
    onSaveAssessment: (AssessmentEntity) -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("Asesmen Diagnostik 4 Lapis Karikatur Sejarah") }
    var classTarget by remember { mutableStateOf("Kelas XI-1") }

    // Accordion expand states
    var exp1 by remember { mutableStateOf(true) }
    var exp2 by remember { mutableStateOf(false) }
    var exp3 by remember { mutableStateOf(false) }
    var exp4 by remember { mutableStateOf(false) }

    // Probe 1
    var p1Prompt by remember { mutableStateOf("Unsur visual manakah pada karikatur yang mewakili simbol kapitalisme perkeretaapian kolonial?") }
    var p1OptA by remember { mutableStateOf("Lokomotif uap berlogo Mahkota Kerajaan Belanda") }
    var p1OptB by remember { mutableStateOf("Pemandangan pegunungan dan pepohonan kelapa") }
    var p1OptC by remember { mutableStateOf("Rombongan pedagang tradisional di tepi jalan") }
    var p1OptD by remember { mutableStateOf("Bangunan dermaga pelabuhan pelayaran") }
    var p1Essay by remember { mutableStateOf("Sebutkan 2 elemen objek visual lain dalam karikatur yang memperkuat tema eksploitasi modal asing!") }

    // Probe 2
    var p2Prompt by remember { mutableStateOf("Apakah pesan satir tersirat dari ukuran relatif para pejabat kolonial yang tampak membesar dibanding buruh rel?") }
    var p2OptA by remember { mutableStateOf("Ketimpangan kekuasaan dan hegemoni otoritas kolonial atas tenaga kerja pribumi") }
    var p2OptB by remember { mutableStateOf("Efek perspektif jarak melukis biasa") }
    var p2OptC by remember { mutableStateOf("Penanda bahwa pejabat tersebut berbadan gemuk") }
    var p2OptD by remember { mutableStateOf("Simbol kemakmuran bersama yang dirasakan seluruh rakyat") }
    var p2Essay by remember { mutableStateOf("Analisislah mengapa seniman karikatur memilih menggambar buruh dengan gaya siluet tanpa wajah!") }

    // Probe 3
    var p3Prompt by remember { mutableStateOf("Kebijakan kolonial manakah yang melatarbelakangi maraknya ekspansi rel kereta api private di Jawa abad 20?") }
    var p3OptA by remember { mutableStateOf("Undang-Undang Agraria 1870 (Agrarische Wet) dan privatisasi perkebunan") }
    var p3OptB by remember { mutableStateOf("Sistem Tanam Paksa (Cultuurstelsel) Van den Bosch") }
    var p3OptC by remember { mutableStateOf("Penyerahan Wajib VOC abad 18") }
    var p3OptD by remember { mutableStateOf("Konferensi Meja Bundar 1949") }
    var p3Essay by remember { mutableStateOf("Hubungkan krisis depresi ekonomi global (Malaise 1929) dengan kondisi perkeretaapian dalam karikatur ini!") }

    // Probe 4
    var p4Prompt by remember { mutableStateOf("Sejauh mana sumber visual karikatur pers ini dapat dianggap obyektif sebagai fakta sejarah?") }
    var p4OptA by remember { mutableStateOf("Karikatur adalah konstruksi subjektif yang mencerminkan bias/kritik pembuatnya, namun bernilai tinggi mengungkap opini publik") }
    var p4OptB by remember { mutableStateOf("Karikatur 100% fakta mutlak tanpa bias") }
    var p4OptC by remember { mutableStateOf("Karikatur hanyalah hiburan fiktif tanpa nilai bukti sejarah") }
    var p4OptD by remember { mutableStateOf("Karikatur dibuat oleh pemerintah kolonial sebagai laporan resmi") }
    var p4Essay by remember { mutableStateOf("Bagaimanakah Anda menggunakan sumber karikatur ini untuk menyusun narasi kritis sejarah modern?") }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Penyusunan Asesmen Diagnostik",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = NavyDark)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Selected Caricature Image on Top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(
                        id = selectedCaricature?.imageDrawableRes ?: R.drawable.img_caricature_hero_1786349502292
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Asesmen Diagnostik") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Form 4 Lapis Probe (Expandable Accordion)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyDark)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Accordion 1: Identifikasi
            AccordionCard(
                title = "Lapis 1: Identifikasi",
                isExpanded = exp1,
                onToggle = { exp1 = !exp1 },
                prompt = p1Prompt, onPromptChange = { p1Prompt = it },
                optA = p1OptA, onOptAChange = { p1OptA = it },
                optB = p1OptB, onOptBChange = { p1OptB = it },
                optC = p1OptC, onOptCChange = { p1OptC = it },
                optD = p1OptD, onOptDChange = { p1OptD = it },
                essay = p1Essay, onEssayChange = { p1Essay = it },
                onGenerateAi = {
                    onGenerateAiDraft(title, "Identifikasi") { res ->
                        p1Prompt = res.prompt
                        p1OptA = res.optionA
                        p1OptB = res.optionB
                        p1OptC = res.optionC
                        p1OptD = res.optionD
                        p1Essay = res.essayPrompt
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Accordion 2: Interpretasi Simbolik
            AccordionCard(
                title = "Lapis 2: Interpretasi Simbolik",
                isExpanded = exp2,
                onToggle = { exp2 = !exp2 },
                prompt = p2Prompt, onPromptChange = { p2Prompt = it },
                optA = p2OptA, onOptAChange = { p2OptA = it },
                optB = p2OptB, onOptBChange = { p2OptB = it },
                optC = p2OptC, onOptCChange = { p2OptC = it },
                optD = p2OptD, onOptDChange = { p2OptD = it },
                essay = p2Essay, onEssayChange = { p2Essay = it },
                onGenerateAi = {
                    onGenerateAiDraft(title, "Interpretasi Simbolik") { res ->
                        p2Prompt = res.prompt
                        p2OptA = res.optionA
                        p2OptB = res.optionB
                        p2OptC = res.optionC
                        p2OptD = res.optionD
                        p2Essay = res.essayPrompt
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Accordion 3: Kontekstualisasi Historis
            AccordionCard(
                title = "Lapis 3: Kontekstualisasi Historis",
                isExpanded = exp3,
                onToggle = { exp3 = !exp3 },
                prompt = p3Prompt, onPromptChange = { p3Prompt = it },
                optA = p3OptA, onOptAChange = { p3OptA = it },
                optB = p3OptB, onOptBChange = { p3OptB = it },
                optC = p3OptC, onOptCChange = { p3OptC = it },
                optD = p3OptD, onOptDChange = { p3OptD = it },
                essay = p3Essay, onEssayChange = { p3Essay = it },
                onGenerateAi = {
                    onGenerateAiDraft(title, "Kontekstualisasi Historis") { res ->
                        p3Prompt = res.prompt
                        p3OptA = res.optionA
                        p3OptB = res.optionB
                        p3OptC = res.optionC
                        p3OptD = res.optionD
                        p3Essay = res.essayPrompt
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Accordion 4: Evaluasi Kritis
            AccordionCard(
                title = "Lapis 4: Evaluasi Kritis",
                isExpanded = exp4,
                onToggle = { exp4 = !exp4 },
                prompt = p4Prompt, onPromptChange = { p4Prompt = it },
                optA = p4OptA, onOptAChange = { p4OptA = it },
                optB = p4OptB, onOptBChange = { p4OptB = it },
                optC = p4OptC, onOptCChange = { p4OptC = it },
                optD = p4OptD, onOptDChange = { p4OptD = it },
                essay = p4Essay, onEssayChange = { p4Essay = it },
                onGenerateAi = {
                    onGenerateAiDraft(title, "Evaluasi Kritis") { res ->
                        p4Prompt = res.prompt
                        p4OptA = res.optionA
                        p4OptB = res.optionB
                        p4OptC = res.optionC
                        p4OptD = res.optionD
                        p4Essay = res.essayPrompt
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Full-width sticky button "Simpan & Assign ke Kelas"
            PrimaryLargeButton(
                text = "Simpan & Assign ke Kelas",
                onClick = {
                    val newAssessment = AssessmentEntity(
                        id = "asm_" + System.currentTimeMillis(),
                        title = title,
                        classTarget = classTarget,
                        imageDrawableRes = selectedCaricature?.imageDrawableRes ?: R.drawable.img_caricature_hero_1786349502292,
                        durationMinutes = 20,
                        probeCount = 4,
                        status = "Sedang Berjalan",
                        probe1Prompt = p1Prompt, probe1OptionA = p1OptA, probe1OptionB = p1OptB, probe1OptionC = p1OptC, probe1OptionD = p1OptD, probe1CorrectIndex = 0, probe1EssayPrompt = p1Essay,
                        probe2Prompt = p2Prompt, probe2OptionA = p2OptA, probe2OptionB = p2OptB, probe2OptionC = p2OptC, probe2OptionD = p2OptD, probe2CorrectIndex = 0, probe2EssayPrompt = p2Essay,
                        probe3Prompt = p3Prompt, probe3OptionA = p3OptA, probe3OptionB = p3OptB, probe3OptionC = p3OptC, probe3OptionD = p3OptD, probe3CorrectIndex = 0, probe3EssayPrompt = p3Essay,
                        probe4Prompt = p4Prompt, probe4OptionA = p4OptA, probe4OptionB = p4OptB, probe4OptionC = p4OptC, probe4OptionD = p4OptD, probe4CorrectIndex = 0, probe4EssayPrompt = p4Essay
                    )
                    onSaveAssessment(newAssessment)
                    Toast.makeText(context, "Asesmen berhasil disimpan & di-assign ke siswa!", Toast.LENGTH_SHORT).show()
                },
                containerColor = TealPrimary
            )
        }
    }
}

@Composable
fun AccordionCard(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    prompt: String, onPromptChange: (String) -> Unit,
    optA: String, onOptAChange: (String) -> Unit,
    optB: String, onOptBChange: (String) -> Unit,
    optC: String, onOptCChange: (String) -> Unit,
    optD: String, onOptDChange: (String) -> Unit,
    essay: String, onEssayChange: (String) -> Unit,
    onGenerateAi: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyDark))
                Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onGenerateAi) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MustardDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Draf dari AI", color = MustardDark, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = onPromptChange,
                        label = { Text("Pertanyaan Probe PG") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = optA, onValueChange = onOptAChange, label = { Text("Pilihan A (Kunci Jawaban)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(value = optB, onValueChange = onOptBChange, label = { Text("Pilihan B") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(value = optC, onValueChange = onOptCChange, label = { Text("Pilihan C") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(value = optD, onValueChange = onOptDChange, label = { Text("Pilihan D") }, modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = essay,
                        onValueChange = onEssayChange,
                        label = { Text("Petunjuk Esai Pendukung Probe") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// 12. DASHBOARD HASIL KELAS (GURU)
@Composable
fun TeacherClassResultsScreen(
    submissions: List<StudentSubmissionEntity>,
    onUpdatePlacement: (submission: StudentSubmissionEntity, newGroup: String, notes: String) -> Unit
) {
    var selectedFilterGroup by remember { mutableStateOf("Semua") } // "Semua", "A", "B", "C", "D", "E"
    var selectedStudentForManual by remember { mutableStateOf<StudentSubmissionEntity?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }

    val filteredList = submissions.filter {
        selectedFilterGroup == "Semua" || it.groupPlacement.equals(selectedFilterGroup, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Surface(color = NavyDark, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Dashboard Hasil Kelas Berdiferensiasi",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = "Rekapitulasi Skor 4 Probe & Penempatan Kelompok",
                            style = MaterialTheme.typography.bodySmall.copy(color = TealLight)
                        )
                    }

                    IconButton(onClick = { showExportDialog = true }) {
                        Icon(Icons.Default.Download, contentDescription = "Unduh Statistik", tint = MustardYellow)
                    }
                }
            }
        }

        // Filter / Sort by Kelompok Penempatan Chips (A, B, C, D, E)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val chips = listOf("Semua", "A", "B", "C", "D", "E")
            items(chips) { group ->
                FilterChip(
                    selected = selectedFilterGroup == group,
                    onClick = { selectedFilterGroup = group },
                    label = { Text(if (group == "Semua") "Semua Kelompok" else "Kelompok $group") }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Student Table/List
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList) { student ->
                StudentResultRowCard(
                    submission = student,
                    onManualAdjustClick = { selectedStudentForManual = student }
                )
            }
        }
    }

    // Manual Adjustment Dialog
    if (selectedStudentForManual != null) {
        val student = selectedStudentForManual!!
        var newGroupChoice by remember { mutableStateOf(student.groupPlacement) }
        var feedbackNotes by remember { mutableStateOf(student.teacherFeedback) }

        Dialog(onDismissRequest = { selectedStudentForManual = null }) {
            Surface(shape = RoundedCornerShape(16.dp), color = SurfaceLight) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Sesuaikan Penempatan Manual", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyDark))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Siswa: ${student.studentName} (${student.studentClass})", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Pilih Kelompok Penempatan Baru:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("A", "B", "C", "D", "E").forEach { g ->
                            FilterChip(
                                selected = newGroupChoice == g,
                                onClick = { newGroupChoice = g },
                                label = { Text(g) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = feedbackNotes,
                        onValueChange = { feedbackNotes = it },
                        label = { Text("Catatan Pendidik / Alasan Penyesuaian") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { selectedStudentForManual = null }) { Text("Batal") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onUpdatePlacement(student, newGroupChoice, feedbackNotes)
                                selectedStudentForManual = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Text("Simpan Penyesuaian")
                        }
                    }
                }
            }
        }
    }

    // Download Statistics Summary Modal
    if (showExportDialog) {
        Dialog(onDismissRequest = { showExportDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp), color = SurfaceLight) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Unduh Statistik Keseluruhan Kelas", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyDark))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Laporan diagnostik siap diunduh dalam format PDF/CSV untuk laporan Kurikulum Merdeka Pembelajaran Berdiferensiasi.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Kelompok A: 1 Siswa (20%)", style = MaterialTheme.typography.bodySmall)
                    Text("• Kelompok B: 1 Siswa (20%)", style = MaterialTheme.typography.bodySmall)
                    Text("• Kelompok C: 1 Siswa (20%)", style = MaterialTheme.typography.bodySmall)
                    Text("• Kelompok D: 1 Siswa (20%)", style = MaterialTheme.typography.bodySmall)
                    Text("• Kelompok E: 1 Siswa (20%)", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { showExportDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Unduh File Laporan PDF")
                    }
                }
            }
        }
    }
}

@Composable
fun StudentResultRowCard(
    submission: StudentSubmissionEntity,
    onManualAdjustClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = submission.studentName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NavyDark))
                    Text(text = "Rata-Rata: ${submission.totalScore.toInt()} / 100", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                }

                GroupBadge(group = submission.groupPlacement)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Skor 4 Probe:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                    Mini4BarChart(
                        s1 = submission.scoreIdentifikasi,
                        s2 = submission.scoreInterpretasi,
                        s3 = submission.scoreKontekstualisasi,
                        s4 = submission.scoreEvaluasi
                    )
                }

                OutlinedButton(
                    onClick = onManualAdjustClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Sesuaikan Penempatan", style = MaterialTheme.typography.labelSmall.copy(color = TealPrimary, fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
