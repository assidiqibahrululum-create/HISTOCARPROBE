package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.ui.screens.*
import com.example.ui.theme.HistocarprobeTheme
import com.example.ui.theme.NavyDark
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HistocarprobeTheme {
                MainApp()
            }
        }
    }
}

enum class AppScreen {
    SPLASH,
    LANDING,
    STUDENT_LOGIN,
    TEACHER_LOGIN,
    STUDENT_MAIN,
    STUDENT_INSTRUCTION,
    STUDENT_TAKE_TEST,
    STUDENT_RESULT,
    TEACHER_MAIN
}

@Composable
fun MainApp(appViewModel: AppViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

    val userSession by appViewModel.userSession.collectAsStateWithLifecycle()
    val assessments by appViewModel.allAssessments.collectAsStateWithLifecycle()
    val submissions by appViewModel.allSubmissions.collectAsStateWithLifecycle()
    val caricatures by appViewModel.allCaricatures.collectAsStateWithLifecycle()
    val savedCollection by appViewModel.savedCollection.collectAsStateWithLifecycle()

    val activeAssessment by appViewModel.activeAssessment.collectAsStateWithLifecycle()
    val activeSubmission by appViewModel.activeSubmission.collectAsStateWithLifecycle()
    val selectedCaricature by appViewModel.selectedCaricatureForAssessment.collectAsStateWithLifecycle()

    // Student Bottom Nav tab index: 0 = Beranda, 1 = Riwayat, 2 = Profil
    var studentTab by remember { mutableStateOf(0) }

    // Teacher Bottom Nav tab index: 0 = Beranda, 1 = Bank Soal, 2 = Buat Asesmen, 3 = Hasil
    var teacherTab by remember { mutableStateOf(0) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                AppScreen.SPLASH -> {
                    SplashScreen(
                        onTimeout = { currentScreen = AppScreen.LANDING }
                    )
                }

                AppScreen.LANDING -> {
                    LandingScreen(
                        onNavigateStudentLogin = { currentScreen = AppScreen.STUDENT_LOGIN },
                        onNavigateTeacherLogin = { currentScreen = AppScreen.TEACHER_LOGIN }
                    )
                }

                AppScreen.STUDENT_LOGIN -> {
                    StudentLoginScreen(
                        onLoginSuccess = { nisn, nik, tempat, tanggal ->
                            appViewModel.loginStudent(nisn, nik, tempat, tanggal)
                            studentTab = 0
                            currentScreen = AppScreen.STUDENT_MAIN
                        },
                        onBack = { currentScreen = AppScreen.LANDING }
                    )
                }

                AppScreen.TEACHER_LOGIN -> {
                    TeacherLoginScreen(
                        onLoginSuccess = { belajarId ->
                            appViewModel.loginTeacher(belajarId)
                            teacherTab = 0
                            currentScreen = AppScreen.TEACHER_MAIN
                        },
                        onBack = { currentScreen = AppScreen.LANDING }
                    )
                }

                AppScreen.STUDENT_MAIN -> {
                    Scaffold(
                        bottomBar = {
                            NavigationBar(
                                containerColor = SurfaceColor,
                                windowInsets = WindowInsets.navigationBars
                            ) {
                                NavigationBarItem(
                                    selected = studentTab == 0,
                                    onClick = { studentTab = 0 },
                                    icon = { Icon(if (studentTab == 0) Icons.Default.Home else Icons.Outlined.Home, contentDescription = "Beranda") },
                                    label = { Text("Beranda") }
                                )
                                NavigationBarItem(
                                    selected = studentTab == 1,
                                    onClick = { studentTab = 1 },
                                    icon = { Icon(if (studentTab == 1) Icons.Default.History else Icons.Outlined.History, contentDescription = "Riwayat") },
                                    label = { Text("Riwayat") }
                                )
                                NavigationBarItem(
                                    selected = studentTab == 2,
                                    onClick = { studentTab = 2 },
                                    icon = { Icon(if (studentTab == 2) Icons.Default.Person else Icons.Outlined.Person, contentDescription = "Profil") },
                                    label = { Text("Profil") }
                                )
                            }
                        }
                    ) { navPadding ->
                        Box(modifier = Modifier.padding(navPadding)) {
                            when (studentTab) {
                                0 -> StudentHomeScreen(
                                    userSession = userSession,
                                    assessments = assessments,
                                    submissions = submissions,
                                    onSelectAssessment = { asm ->
                                        appViewModel.selectAssessmentToTake(asm)
                                        currentScreen = AppScreen.STUDENT_INSTRUCTION
                                    },
                                    onViewSubmissionResult = { sub ->
                                        appViewModel.selectSubmissionDetail(sub)
                                        currentScreen = AppScreen.STUDENT_RESULT
                                    }
                                )
                                1 -> StudentHistoryScreen(
                                    submissions = submissions,
                                    onSelectSubmission = { sub ->
                                        appViewModel.selectSubmissionDetail(sub)
                                        currentScreen = AppScreen.STUDENT_RESULT
                                    }
                                )
                                else -> StudentProfileScreen(
                                    userSession = userSession,
                                    onLogout = {
                                        appViewModel.logout()
                                        currentScreen = AppScreen.LANDING
                                    }
                                )
                            }
                        }
                    }
                }

                AppScreen.STUDENT_INSTRUCTION -> {
                    if (activeAssessment != null) {
                        StudentAssessmentInstructionScreen(
                            assessment = activeAssessment!!,
                            onStartTest = { currentScreen = AppScreen.STUDENT_TAKE_TEST },
                            onBack = { currentScreen = AppScreen.STUDENT_MAIN }
                        )
                    } else {
                        currentScreen = AppScreen.STUDENT_MAIN
                    }
                }

                AppScreen.STUDENT_TAKE_TEST -> {
                    if (activeAssessment != null) {
                        StudentTakeAssessmentScreen(
                            assessment = activeAssessment!!,
                            onSubmit = { a1, e1, a2, e2, a3, e3, a4, e4 ->
                                appViewModel.submitAssessmentResult(
                                    assessment = activeAssessment!!,
                                    ans1 = a1, essay1 = e1,
                                    ans2 = a2, essay2 = e2,
                                    ans3 = a3, essay3 = e3,
                                    ans4 = a4, essay4 = e4
                                )
                                currentScreen = AppScreen.STUDENT_RESULT
                            },
                            onBack = { currentScreen = AppScreen.STUDENT_MAIN }
                        )
                    } else {
                        currentScreen = AppScreen.STUDENT_MAIN
                    }
                }

                AppScreen.STUDENT_RESULT -> {
                    if (activeSubmission != null) {
                        StudentDiagnosticResultScreen(
                            submission = activeSubmission!!,
                            onBack = { currentScreen = AppScreen.STUDENT_MAIN }
                        )
                    } else {
                        currentScreen = AppScreen.STUDENT_MAIN
                    }
                }

                AppScreen.TEACHER_MAIN -> {
                    Scaffold(
                        bottomBar = {
                            NavigationBar(
                                containerColor = SurfaceColor,
                                windowInsets = WindowInsets.navigationBars
                            ) {
                                NavigationBarItem(
                                    selected = teacherTab == 0,
                                    onClick = { teacherTab = 0 },
                                    icon = { Icon(if (teacherTab == 0) Icons.Default.Dashboard else Icons.Outlined.Dashboard, contentDescription = "Beranda") },
                                    label = { Text("Beranda") }
                                )
                                NavigationBarItem(
                                    selected = teacherTab == 1,
                                    onClick = { teacherTab = 1 },
                                    icon = { Icon(if (teacherTab == 1) Icons.Default.Collections else Icons.Outlined.Collections, contentDescription = "Bank Soal") },
                                    label = { Text("Bank Soal") }
                                )
                                NavigationBarItem(
                                    selected = teacherTab == 2,
                                    onClick = { teacherTab = 2 },
                                    icon = { Icon(if (teacherTab == 2) Icons.Default.AddCircle else Icons.Outlined.AddCircleOutline, contentDescription = "Buat Asesmen") },
                                    label = { Text("Buat Asesmen") }
                                )
                                NavigationBarItem(
                                    selected = teacherTab == 3,
                                    onClick = { teacherTab = 3 },
                                    icon = { Icon(if (teacherTab == 3) Icons.Default.Analytics else Icons.Outlined.Analytics, contentDescription = "Hasil") },
                                    label = { Text("Hasil") }
                                )
                            }
                        }
                    ) { navPadding ->
                        Box(modifier = Modifier.padding(navPadding)) {
                            when (teacherTab) {
                                0 -> TeacherDashboardScreen(
                                    userSession = userSession,
                                    assessments = assessments,
                                    submissions = submissions,
                                    onNavigateCreate = { teacherTab = 2 },
                                    onNavigateResults = { teacherTab = 3 }
                                )
                                1 -> TeacherBankSoalScreen(
                                    caricatures = caricatures,
                                    savedCollection = savedCollection,
                                    onToggleSave = { car -> appViewModel.toggleSaveCaricature(car) },
                                    onUseForAssessment = { car ->
                                        appViewModel.selectCaricatureForAssessmentCreation(car)
                                        teacherTab = 2
                                    }
                                )
                                2 -> TeacherCreateAssessmentScreen(
                                    selectedCaricature = selectedCaricature ?: caricatures.firstOrNull(),
                                    onGenerateAiDraft = { topic, layer, onResult ->
                                        appViewModel.generateAiDraftForProbe(topic, layer, onResult)
                                    },
                                    onSaveAssessment = { newAsm ->
                                        appViewModel.saveTeacherCreatedAssessment(newAsm)
                                        teacherTab = 0
                                    }
                                )
                                else -> TeacherClassResultsScreen(
                                    submissions = submissions,
                                    onUpdatePlacement = { sub, newGroup, notes ->
                                        appViewModel.updateManualPlacement(sub, newGroup, notes)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

val SurfaceColor = Color(0xFFFFFFFF)
