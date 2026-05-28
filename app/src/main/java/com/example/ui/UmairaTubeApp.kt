package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BattleEntity
import com.example.data.CommentEntity
import com.example.data.VideoEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UmairaTubeApp(viewModel: UmairaTubeViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val activeVideo by viewModel.activeVideo.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = UmairaRichBlack
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    "onboarding" -> OnboardingScreen(viewModel)
                    "dashboard" -> DashboardScreen(viewModel)
                    else -> OnboardingScreen(viewModel)
                }
            }

            // Global Full-Screen Video Player Overlay
            activeVideo?.let { video ->
                VideoPlayerOverlay(
                    video = video,
                    viewModel = viewModel,
                    onClose = { viewModel.closeActiveVideo() }
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen(viewModel: UmairaTubeViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUpMode by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(UmairaBlack, Color.Black)
                )
            )
            .padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        // Aesthetic ambient red glowing background circle
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x3FFF003F), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Stylized Logo Element
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(UmairaRed, UmairaDarkRed)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "UMAIRA Logo",
                        tint = UmairaWhite,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "UMAIRA TUBE",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = UmairaWhite,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.SansSerif,
                    style = LocalTextStyle.current.copy(
                        shadow = Shadow(
                            color = UmairaRed,
                            blurRadius = 15f
                        )
                    )
                )

                Text(
                    text = "ULTRA HIGH VIDEO PLATFORM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = UmairaMutedText,
                    letterSpacing = 4.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "“Watch The Future”",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light,
                    color = UmairaRed,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Glassmorphic Login Panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, UmairaBorderRed, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = UmairaGlassGray),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isSignUpMode) "CREATOR SIGN UP" else "SECURE LOGIN",
                        color = UmairaWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email / Channel handle", color = UmairaMutedText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UmairaRed,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = UmairaWhite,
                            unfocusedTextColor = UmairaWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = UmairaMutedText) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UmairaRed,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = UmairaWhite,
                            unfocusedTextColor = UmairaWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3D Styled Login Button
                    Button(
                        onClick = {
                            if (email.isNotBlank()) {
                                viewModel.loginUser(email)
                            } else {
                                viewModel.loginUser("king2240ff@gmail.com")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .border(1.5.dp, Color.White, RoundedCornerShape(16.dp))
                            .testTag("login_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(UmairaRed, Color(0xFFE50914), Color(0xFF990022))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Lock, contentDescription = "Lock", tint = UmairaWhite)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isSignUpMode) "CREATE & ENTER" else "LAUNCH PLATFORM",
                                    color = UmairaWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Google Login simulated
                    OutlinedButton(
                        onClick = { viewModel.loginUser("king2240ff@gmail.com") },
                        border = BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = UmairaWhite),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("google_login_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Google Quick Sign-In",
                                fontWeight = FontWeight.SemiBold,
                                color = UmairaWhite,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isSignUpMode) "Already have a channel? Log in" else "New Creator? Tap here to set up UMAIRA channel",
                        color = UmairaRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { isSignUpMode = !isSignUpMode }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: UmairaTubeViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val currentUserEmail by viewModel.currentUser.collectAsState()
    val isUserAdmin = currentUserEmail != null && viewModel.ownerEmails.contains(currentUserEmail!!.lowercase().trim())

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = UmairaBlack,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = activeTab == "home",
                    onClick = { viewModel.setActiveTab("home") },
                    icon = { Icon(Icons.Filled.Home, "Home") },
                    label = { Text("Home", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = UmairaRed,
                        selectedTextColor = UmairaRed,
                        unselectedIconColor = UmairaMutedText,
                        unselectedTextColor = UmairaMutedText,
                        indicatorColor = Color(0x22FF003F)
                    ),
                    modifier = Modifier.testTag("nav_home")
                )
                NavigationBarItem(
                    selected = activeTab == "shorts",
                    onClick = { viewModel.setActiveTab("shorts") },
                    icon = { Icon(Icons.Filled.VideoLibrary, "Reels") },
                    label = { Text("Reels", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = UmairaRed,
                        selectedTextColor = UmairaRed,
                        unselectedIconColor = UmairaMutedText,
                        unselectedTextColor = UmairaMutedText,
                        indicatorColor = Color(0x22FF003F)
                    ),
                    modifier = Modifier.testTag("nav_shorts")
                )
                NavigationBarItem(
                    selected = activeTab == "studio",
                    onClick = { viewModel.setActiveTab("studio") },
                    icon = { Icon(Icons.Filled.MovieCreation, "Tube Studio") },
                    label = { Text("Tube Studio", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = UmairaRed,
                        selectedTextColor = UmairaRed,
                        unselectedIconColor = UmairaMutedText,
                        unselectedTextColor = UmairaMutedText,
                        indicatorColor = Color(0x22FF003F)
                    ),
                    modifier = Modifier.testTag("nav_studio")
                )
                NavigationBarItem(
                    selected = activeTab == "channel",
                    onClick = { viewModel.setActiveTab("channel") },
                    icon = { Icon(Icons.Filled.AccountCircle, "Your Channel") },
                    label = { Text("Your Channel", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = UmairaRed,
                        selectedTextColor = UmairaRed,
                        unselectedIconColor = UmairaMutedText,
                        unselectedTextColor = UmairaMutedText,
                        indicatorColor = Color(0x22FF003F)
                    ),
                    modifier = Modifier.testTag("nav_channel")
                )
                if (isUserAdmin) {
                    NavigationBarItem(
                        selected = activeTab == "admin",
                        onClick = { viewModel.setActiveTab("admin") },
                        icon = { Icon(Icons.Filled.Shield, "Owner Panel") },
                        label = { Text("Owner Panel", fontSize = 11.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFFD700),
                            selectedTextColor = Color(0xFFFFD700),
                            unselectedIconColor = UmairaMutedText,
                            unselectedTextColor = UmairaMutedText,
                            indicatorColor = Color(0x33FFD700)
                        ),
                        modifier = Modifier.testTag("nav_admin")
                    )
                }
            }
        },
        containerColor = UmairaRichBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                "home" -> HomeScreenTab(viewModel)
                "shorts" -> ShortsScreenTab(viewModel)
                "studio" -> TubeStudioTab(viewModel)
                "channel" -> YourChannelTab(viewModel)
                "admin" -> OwnerCockpitTab(viewModel)
            }
        }
    }
}

@Composable
fun HomeScreenTab(viewModel: UmairaTubeViewModel) {
    val searchVal by viewModel.searchQuery.collectAsState()
    val selectCategory by viewModel.selectedCategory.collectAsState()
    val allVideos by viewModel.videosList.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var showVoiceDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Trending", "AI", "Gaming", "Music", "VFX")

    // Filter list according to selection
    val filteredVideos = allVideos.filter {
        val matchesSearch = it.title.contains(searchVal, ignoreCase = true) ||
                it.creatorName.contains(searchVal, ignoreCase = true)
        val matchesCategory = selectCategory == "All" || it.category.equals(selectCategory, ignoreCase = true)
        matchesSearch && matchesCategory && it.type != "SHORT"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UmairaRichBlack)
    ) {
        // App top level Header branding & Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFFF003F), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "", tint = Color.White, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "UMAIRA",
                color = UmairaWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(UmairaRed, blurRadius = 10f)
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.setActiveTab("studio") },
                colors = ButtonDefaults.buttonColors(containerColor = UmairaRed),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .height(36.dp)
                    .testTag("header_upload_button")
            ) {
                Icon(Icons.Filled.CloudUpload, contentDescription = "Upload", tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Upload", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            IconButton(onClick = { viewModel.logout() }) {
                Icon(Icons.Filled.ExitToApp, contentDescription = "Sign Out", tint = UmairaRed)
            }
        }

        // Beautiful glassmorphism search row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchVal,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search videos or creators...", color = UmairaMutedText, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = UmairaMutedText) },
                trailingIcon = {
                    if (searchVal.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "", tint = UmairaWhite)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = UmairaRed,
                    unfocusedBorderColor = UmairaCardGray,
                    focusedContainerColor = UmairaCardGray,
                    unfocusedContainerColor = UmairaCardGray,
                    focusedTextColor = UmairaWhite,
                    unfocusedTextColor = UmairaWhite
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("search_field"),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            // AI Voice Search Trigger
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(UmairaRed, RoundedCornerShape(16.dp))
                    .clickable { showVoiceDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Mic, contentDescription = "AI Voice Search", tint = UmairaWhite)
            }
        }

        // Show AI Search suggestion completions box if query is typed and match count is there
        if (searchVal.isNotEmpty()) {
            Text(
                text = "AI Smart Suggestions: Showing results matching '$searchVal'",
                color = UmairaRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lateral Categories chips bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectCategory == category
                Box(
                    modifier = Modifier
                        .clickable { viewModel.setCategory(category) }
                        .background(
                            color = if (isSelected) UmairaRed else UmairaCardGray,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) UmairaWhite else UmairaMutedText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Videos Scrollable Grid List
        if (filteredVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.VideoFile,
                        contentDescription = "",
                        tint = UmairaMutedText,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No videos found", color = UmairaMutedText, fontWeight = FontWeight.SemiBold)
                    Text("Try standard tags or another category chip!", color = UmairaMutedText, fontSize = 12.sp)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredVideos) { video ->
                    VideoGridCard(video = video, onClick = { viewModel.selectVideo(video) })
                }
            }
        }
    }

    // Voice Dialog Simulation
    if (showVoiceDialog) {
        VoiceSearchDialog(
            viewModel = viewModel,
            onClose = { showVoiceDialog = false }
        )
    }
}

@Composable
fun VideoGridCard(video: VideoEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, UmairaBorderRed.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = UmairaCardGray)
    ) {
        Column {
            // Simulated Rich Video Thumbnail Block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(UmairaBlack, Color.DarkGray)
                        )
                    )
            ) {
                // Subtle overlay pattern representing simulated screen
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0x11FF003F))
                }

                // Cyber logo centered or overlay labels
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Category pill
                        Box(
                            modifier = Modifier
                                .background(Color(0x99000000), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(video.category, color = UmairaRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        // Aura reward count tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xBBFFD700), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = "", tint = Color.Black, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Aura +${video.auraScore}", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    // Bottom info overlays: Length and Virality
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Virality predictor meter
                        Column(
                            modifier = Modifier
                                .background(Color(0xCC000000), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Viral Potential", color = UmairaMutedText, fontSize = 9.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .height(4.dp)
                                        .width(40.dp)
                                        .background(Color.DarkGray, RoundedCornerShape(2.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(video.viralityChance / 100f)
                                            .background(UmairaRed, RoundedCornerShape(2.dp))
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${video.viralityChance}%", color = UmairaWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Duration Tag
                        Box(
                            modifier = Modifier
                                .background(Color.Black, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(video.duration, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Big glowing play icon centered on hover/touch layer
                    Icon(
                        imageVector = Icons.Filled.PlayCircleFilled,
                        contentDescription = "Play",
                        tint = UmairaRed.copy(alpha = 0.85f),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(54.dp)
                    )
                }
            }

            // Video Description/Meta row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular creator avatar channel
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(UmairaRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        video.creatorAvatar,
                        color = UmairaWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        video.title,
                        color = UmairaWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            video.creatorName,
                            color = UmairaMutedText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Verified Creator",
                            tint = UmairaRed,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "•  ${video.viewsCountFormatted}",
                            color = UmairaMutedText,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// Full Immersive Reels Screen tab
@Composable
fun ShortsScreenTab(viewModel: UmairaTubeViewModel) {
    val shorts by viewModel.shortsList.collectAsState()
    var activeIndex by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    if (shorts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No reels available at the moment.", color = UmairaMutedText)
        }
    } else {
        val activeShort = shorts[activeIndex % shorts.size]

        // Vertical Gestures visual swipe container simulation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Immersive Video player representation
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF2E000A), Color.Black)
                        )
                    )
            ) {
                // Particle feedback to indicate active audio track
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0x1A000000))
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.statusBars.asPaddingValues())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "UMAIRA REELS",
                            fontWeight = FontWeight.ExtraBold,
                            color = UmairaRed,
                            letterSpacing = 2.sp,
                            fontSize = 18.sp,
                            style = LocalTextStyle.current.copy(
                                shadow = Shadow(UmairaRed, blurRadius = 15f)
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xAAFFD700), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = "", tint = Color.Black, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AURA +${activeShort.auraScore}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    // Swipe Up Tip & Next indicator row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (activeIndex > 0) activeIndex--
                                }
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Filled.ArrowUpward, "Previous Reel", tint = UmairaWhite.copy(alpha = 0.6f))
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Box(
                            modifier = Modifier
                                .border(1.5.dp, UmairaRed, CircleShape)
                                .size(72.dp)
                                .clickable { viewModel.selectVideo(activeShort) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Play Reel", tint = UmairaWhite, modifier = Modifier.size(40.dp))
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        IconButton(
                            onClick = {
                                scope.launch {
                                    activeIndex++
                                }
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Filled.ArrowDownward, "Next Reel", tint = UmairaWhite.copy(alpha = 0.6f))
                        }
                    }

                    // Bottom info overlays
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(UmairaRed, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(activeShort.creatorAvatar, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(activeShort.creatorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Filled.CheckCircle, "", tint = UmairaRed, modifier = Modifier.size(12.dp))
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                activeShort.title,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                "Viral Predictive Chance: ${activeShort.viralityChance}% 🔥",
                                color = UmairaRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Right interaction sidebar
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .background(Color(0x77000000), RoundedCornerShape(16.dp))
                                .padding(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(onClick = { viewModel.toggleLike(activeShort) }) {
                                    Icon(
                                        Icons.Filled.ThumbUp,
                                        contentDescription = "Like",
                                        tint = if (activeShort.isLiked) UmairaRed else UmairaWhite
                                    )
                                }
                                Text("${activeShort.likes}", color = UmairaWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(onClick = { viewModel.selectVideo(activeShort) }) {
                                    Icon(Icons.Filled.Comment, contentDescription = "Comments", tint = UmairaWhite)
                                }
                                Text("89", color = UmairaWhite, fontSize = 11.sp)
                            }

                            IconButton(onClick = { /* Download mockup */ }) {
                                Icon(Icons.Filled.Download, contentDescription = "Download Video", tint = UmairaWhite)
                            }
                        }
                    }
                }
            }
        }
    }
}
// SPECTACULAR YOUR CHANNEL PORTAL WITH CUSTOM DYNAMIC LOGO & INTEGRATED AI STUDIO
@Composable
fun YourChannelTab(viewModel: UmairaTubeViewModel) {
    val email by viewModel.currentUser.collectAsState()
    val activeEmail = email ?: "king2240ff@gmail.com"
    val handle = activeEmail.substringBefore("@")

    val channelName by viewModel.channelName.collectAsState()
    val channelSuspended by viewModel.channelSuspended.collectAsState()

    val allVideos by viewModel.videosList.collectAsState()
    val allShorts by viewModel.shortsList.collectAsState()

    val cName = channelName ?: handle
    val userVideos = allVideos.filter { it.creatorName.equals(handle, ignoreCase = true) || it.creatorName.equals(cName, ignoreCase = true) }
    val userShorts = allShorts.filter { it.creatorName.equals(handle, ignoreCase = true) || it.creatorName.equals(cName, ignoreCase = true) }
    val totalUserVideos = userVideos + userShorts
    val totalViews = totalUserVideos.sumOf { it.views }

    var showAiStudio by remember { mutableStateOf(false) }

    val aiSeoResults by viewModel.aiSeoResult.collectAsState()
    val aiThumbnailResults by viewModel.aiThumbnailResult.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UmairaRichBlack)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "YOUR CREATOR CHANNEL",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            color = UmairaWhite,
            letterSpacing = 2.sp,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(UmairaRed, blurRadius = 10f)
            )
        )

        Text(
            text = "UMAIRA TUBE OFFICIAL PARTNER",
            fontSize = 11.sp,
            color = UmairaRed,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        if (channelSuspended) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .border(2.dp, UmairaRed, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Violation Alert",
                        tint = UmairaRed,
                        modifier = Modifier.size(72.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "CHANNEL TERMINATED!",
                        color = UmairaRed,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Your creator channel has been deleted and suspended due to severe violations of Umaira Tube's Explicit Content Safety Policies.\n\nUploading mature, explicit, or sexually suggestive media containing forbidden keywords (e.g., 'xxx', 'sexi', 'sexy', 'porn') results in immediate, automated channel termination. Community guidelines are strictly enforced to preserve copyright-fair and family-friendly environments.",
                        color = UmairaWhite,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.resetChannelAfterViolation(activeEmail) },
                        colors = ButtonDefaults.buttonColors(containerColor = UmairaRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Acknowledge Policies & Appeal (Reset Channel)", fontWeight = FontWeight.Bold, color = UmairaWhite)
                    }
                }
            }
        } else if (channelName == null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .border(1.5.dp, AuraGold.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = UmairaCardGray),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddReaction,
                        contentDescription = "Create Channel Logo",
                        tint = AuraGold,
                        modifier = Modifier.size(56.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "CREATE YOUR OFFICIAL CHANNEL",
                        color = UmairaWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Set up your unique channel identifier first to begin publishing high-fidelity videos/reels, building virtual audiences, tracking analytics, and earning cash!",
                        color = UmairaMutedText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                    )

                    var inputName by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        label = { Text("Your Unique Channel Handle Title", color = UmairaMutedText) },
                        placeholder = { Text("e.g. VashMax Official HQ", color = UmairaMutedText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = UmairaWhite,
                            unfocusedTextColor = UmairaWhite,
                            focusedBorderColor = AuraGold,
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = Color.Black,
                            unfocusedContainerColor = Color.Black
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("create_channel_name_input")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "UMAIRA TUBE COMMUNITY POLICIES AGREEMENT",
                        color = AuraGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 10.dp)
                    )

                    var policyAgree1 by remember { mutableStateOf(false) }
                    var policyAgree2 by remember { mutableStateOf(false) }
                    var policyAgree3 by remember { mutableStateOf(false) }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = policyAgree1,
                                onCheckedChange = { policyAgree1 = it },
                                colors = CheckboxDefaults.colors(checkedColor = UmairaRed, uncheckedColor = Color.LightGray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Strict Content Regulation Check: I promise not to publish explicit adult materials containing mature phrases/tags (XXX, Sexy, Porn). Attempts lead to instant termination.",
                                color = UmairaWhite,
                                fontSize = 11.sp,
                                lineHeight = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = policyAgree2,
                                onCheckedChange = { policyAgree2 = it },
                                colors = CheckboxDefaults.colors(checkedColor = UmairaRed, uncheckedColor = Color.LightGray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Originality & Copyright Integrity: I will follow YouTube-style fairuse constraints and upload original content or customized creative AI artwork.",
                                color = UmairaWhite,
                                fontSize = 11.sp,
                                lineHeight = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = policyAgree3,
                                onCheckedChange = { policyAgree3 = it },
                                colors = CheckboxDefaults.colors(checkedColor = UmairaRed, uncheckedColor = Color.LightGray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Channel Monetization Contract: Agree to participate in the $0.50 CPM views payout rate, with safe payout withdrawal directly to JazzCash or Credit Card.",
                                color = UmairaWhite,
                                fontSize = 11.sp,
                                lineHeight = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (inputName.isNotBlank() && policyAgree1 && policyAgree2 && policyAgree3) {
                                viewModel.createChannel(activeEmail, inputName)
                            }
                        },
                        enabled = inputName.isNotBlank() && policyAgree1 && policyAgree2 && policyAgree3,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuraGold,
                            disabledContainerColor = Color.DarkGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_create_channel_btn")
                    ) {
                        Text(
                            "Create My Channel Now",
                            color = if (inputName.isNotBlank() && policyAgree1 && policyAgree2 && policyAgree3) Color.Black else Color.LightGray,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        } else {
            // Spectacular customized glowing Channel Logo/Avatar
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(UmairaRed.copy(alpha = 0.35f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .border(
                            BorderStroke(
                                2.5.dp,
                                Brush.linearGradient(listOf(UmairaRed, AuraGold))
                            ),
                            CircleShape
                        )
                        .background(Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initialLetter = if (cName.isNotEmpty()) cName.first().toString().uppercase() else "K"
                    Text(
                        text = initialLetter,
                        color = AuraGold,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        style = LocalTextStyle.current.copy(
                            shadow = Shadow(AuraGold, blurRadius = 12f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Channel Info Details
            Text(
                text = cName,
                color = UmairaWhite,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )

            Text(
                text = "@${cName.replace(" ", "").lowercase()} • Creators HQ",
                color = UmairaRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 2.dp)
            )

            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF2E7D32), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("VERIFIED PARTNER CREATOR", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("PRO PLATINUM LEVEL", color = AuraGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = UmairaCardGray),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("SUBSCRIBERS", color = UmairaMutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("2,450", color = UmairaWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = UmairaCardGray),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("TOTAL IMPRESSIONS", color = UmairaMutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("$totalViews", color = UmairaWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = UmairaCardGray),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("TOTAL UPLOADS", color = UmairaMutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("${totalUserVideos.size}", color = UmairaWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // AI Studio Toggle Button configured beautifully inside Your Channel!
            Button(
                onClick = { showAiStudio = !showAiStudio },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .border(
                        BorderStroke(2.dp, Brush.horizontalGradient(listOf(UmairaRed, AuraGold))),
                        RoundedCornerShape(16.dp)
                    )
                    .testTag("launch_ai_studio_btn"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(UmairaRed, Color(0xFF990022))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.MovieCreation,
                            contentDescription = "",
                            tint = UmairaWhite,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (showAiStudio) "CLOSE AI CREATIVE STUDIO" else "⚡ OPEN AI CREATIVE STUDIO",
                            color = UmairaWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showAiStudio) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = UmairaCardGray.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, AuraGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.OfflineBolt, contentDescription = "", tint = AuraGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GEMINI-AI ASSISTANT WORKSPACE",
                                color = UmairaWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Build viral headlines and thumbnail designs in seconds using live Google Gemini AI.",
                            color = UmairaMutedText,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        AiCreativeWorkspace(viewModel, isAiLoading, aiSeoResults, aiThumbnailResults)
                    }
                }
            }
        }
    }
}
      // DYNAMIC COCKPIT FOR UPLOAD MANAGEMENT, REVENUE & LIVE $0.50/1K VIEWS EARNINGS
@Composable
fun TubeStudioTab(viewModel: UmairaTubeViewModel) {
    val email by viewModel.currentUser.collectAsState()
    val activeEmail = email ?: "king2240ff@gmail.com"
    val handle = activeEmail.substringBefore("@")
    
    val channelName by viewModel.channelName.collectAsState()
    val channelSuspended by viewModel.channelSuspended.collectAsState()
    val withdrawalMessage by viewModel.withdrawalMessage.collectAsState()
    val pendingWithdrawalAmount by viewModel.pendingWithdrawalAmount.collectAsState()

    val allVideos by viewModel.videosList.collectAsState()
    val allShorts by viewModel.shortsList.collectAsState()
    
    val adminCpmRate by viewModel.adminCpmRate.collectAsState()
    val cName = channelName ?: handle
    val userVideos = allVideos.filter { it.creatorName.equals(handle, ignoreCase = true) || it.creatorName.equals(cName, ignoreCase = true) }
    val userShorts = allShorts.filter { it.creatorName.equals(handle, ignoreCase = true) || it.creatorName.equals(cName, ignoreCase = true) }
    val totalUserVideos = userVideos + userShorts
    
    val totalViews = totalUserVideos.sumOf { it.views }
    // Earnings rule: 1000 views = dynamic CPM
    val adsenseStatus by viewModel.adsenseStatus.collectAsState()
    val isMonetized = adsenseStatus == "FULLY_MONETIZED"
    val totalEarnings = if (isMonetized) (totalViews / 1000.0) * adminCpmRate else 0.0

    var videoTitle by remember { mutableStateOf("") }
    var selectCategory by remember { mutableStateOf("AI") }
    var selectType by remember { mutableStateOf("VIDEO") } // "VIDEO" or "SHORT"
    var showWithdrawDialog by remember { mutableStateOf(false) }

    var creatorVideoToEdit by remember { mutableStateOf<VideoEntity?>(null) }
    var creatorEditTitleQuery by remember { mutableStateOf("") }
    var creatorVideoToDelete by remember { mutableStateOf<VideoEntity?>(null) }

    val categories = listOf("AI", "Music", "Gaming", "Trending", "VFX")
    val types = listOf("VIDEO", "SHORT")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UmairaRichBlack)
            .padding(16.dp)
    ) {
        Text(
            text = "TUBE CREATOR STUDIO",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            color = UmairaWhite,
            letterSpacing = 2.sp,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(UmairaRed, blurRadius = 10f)
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "MANAGE REVENUE, EARNINGS & PUBLISHING",
            fontSize = 11.sp,
            color = UmairaMutedText,
            letterSpacing = 2.sp,
            modifier = Modifier
                .padding(top = 4.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        if (channelSuspended) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, UmairaRed, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.Report, contentDescription = "", tint = UmairaRed, modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("STUDIO ACCESS SUSPENDED", color = UmairaRed, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Adherence violation flag: You attempted to construct or publish contents violating family safety rules. Go to the 'Your Channel' tab to acknowledge community regulations and appeal.",
                            color = UmairaWhite,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else if (channelName == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, AuraGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = UmairaCardGray)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = "", tint = AuraGold, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("CREATOR STUDIO LOCKED", color = UmairaWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "To publish original videos, accumulate real views, and unlock cash revenue withdrawals, click on the 'Your Channel' tab at the bottom to set up your unique channel name and agree with policies!",
                            color = UmairaMutedText,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        } else {
            if (withdrawalMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x334CAF50)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = "payout", tint = Color(0xFF4CAF50))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(withdrawalMessage!!, color = UmairaWhite, fontSize = 12.sp)
                            Text("Fast Approval Pipeline active. Total requested: $${String.format("%.4f", pendingWithdrawalAmount)} USD", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { viewModel.clearWithdrawalMessage() }) {
                            Icon(Icons.Filled.Close, contentDescription = "dismiss", tint = UmairaWhite)
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Spectacular Creator Overview earnings panel
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, Color(0xFF4CAF50).copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ESTIMATED GOOGLE CHANNELS REVENUE",
                                color = Color(0xFF81C784),
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 1.5.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Live-updating giant cash earnings count
                            Text(
                                text = "$${String.format("%.4f", totalEarnings)} USD",
                                color = Color(0xFF4CAF50),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                style = LocalTextStyle.current.copy(
                                    shadow = Shadow(Color(0xFF4CAF50), blurRadius = 20f)
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(UmairaCardGray, RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Earnings Rate", color = UmairaMutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("$0.50 / 1K views", color = AuraGold, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                }
                                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.DarkGray))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Total Live Views", color = UmairaMutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("$totalViews", color = UmairaWhite, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }

                            Button(
                                onClick = { showWithdrawDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x1F4CAF50)),
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .height(38.dp)
                                    .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(10.dp)),
                                shape = RoundedCornerShape(10.dp),
                                enabled = totalEarnings > 0
                            ) {
                                Text("Request Earnings Payout", color = if (totalEarnings > 0) Color(0xFF4CAF50) else Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Spectacular Creator Monetization Goal Progress board
                item {
                    val subscribers by viewModel.channelSubscribers.collectAsState()
                    val watchHours by viewModel.channelWatchHours.collectAsState()

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, AuraGold.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.MonetizationOn, contentDescription = "", tint = AuraGold, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CREATOR MONETIZATION CENTER",
                                    color = AuraGold,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Subscriber Progress
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Subscribers Goal", color = UmairaWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("$subscribers / 1,000", color = if (subscribers >= 1000) Color(0xFF4CAF50) else UmairaMutedText, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (subscribers.toFloat() / 1000f).coerceAtMost(1f) },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = if (subscribers >= 1000) Color(0xFF4CAF50) else AuraGold,
                                trackColor = Color.DarkGray
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Watch Hours Progress
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Watch Hours Goal", color = UmairaWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("$watchHours / 500 hrs", color = if (watchHours >= 500) Color(0xFF4CAF50) else UmairaMutedText, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (watchHours.toFloat() / 500f).coerceAtMost(1f) },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = if (watchHours >= 500) Color(0xFF4CAF50) else AuraGold,
                                trackColor = Color.DarkGray
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // AdSense Status Badge Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = when (adsenseStatus) {
                                        "FULLY_MONETIZED" -> Color(0x224CAF50)
                                        "ADSENSE_PENDING" -> Color(0x22FFC107)
                                        "ELIGIBLE_TO_APPLY" -> Color(0x3300BCD4)
                                        else -> Color(0x22F44336)
                                    }
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    1.dp,
                                    when (adsenseStatus) {
                                        "FULLY_MONETIZED" -> Color(0xFF4CAF50)
                                        "ADSENSE_PENDING" -> Color(0xFFFFC107)
                                        "ELIGIBLE_TO_APPLY" -> Color(0xFF00BCD4)
                                        else -> Color(0xFFF44336)
                                    }
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = when (adsenseStatus) {
                                            "FULLY_MONETIZED" -> "ADSENSE STATUS: APPROVED & ACTIVE ✅"
                                            "ADSENSE_PENDING" -> "ADSENSE STATUS: UNDER OWNER AUDIT ⏳"
                                            "ELIGIBLE_TO_APPLY" -> "ADSENSE STATUS: APPLICATION READY 🎉"
                                            else -> "ADSENSE STATUS: INELIGIBLE FOR EARNINGS ❌"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = when (adsenseStatus) {
                                            "FULLY_MONETIZED" -> Color(0xFF4CAF50)
                                            "ADSENSE_PENDING" -> Color(0xFFFFC107)
                                            "ELIGIBLE_TO_APPLY" -> Color(0xFF00BCD4)
                                            else -> Color(0xFFF44336)
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = when (adsenseStatus) {
                                            "FULLY_MONETIZED" -> "Congratulations! Your channel is fully monetized. You are earning real-dollar rewards based on active CPM configuration."
                                            "ADSENSE_PENDING" -> "Your AdSense approval is currently pending with the platform owner (Umaira Tube Admin). Once reviewed, real payouts will start generating."
                                            "ELIGIBLE_TO_APPLY" -> "Your channel qualifies for official monetization! Tap the action button below to apply for AdSense account endorsement."
                                            else -> "To start earning real-dollar cash on Umaira Tube, your channel must cross the active thresholds of 1,000 subscribers and 500 watch hours."
                                        },
                                        fontSize = 10.sp,
                                        color = UmairaWhite,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (adsenseStatus == "ELIGIBLE_TO_APPLY") {
                                Button(
                                    onClick = { viewModel.applyForAdSense() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4)),
                                    modifier = Modifier.fillMaxWidth().height(40.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Submit Channel for AdSense Approval", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                }
                            }

                            // Simulation interactive controls to help the user test the monetization walk-through
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Channel Simulation Sandbox:", color = UmairaMutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.simulateAddSubscribers(50) },
                                    colors = ButtonDefaults.buttonColors(containerColor = UmairaCardGray),
                                    border = BorderStroke(1.dp, Color.DarkGray),
                                    modifier = Modifier.weight(1f).height(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("+50 Subs", color = UmairaWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { viewModel.simulateAddWatchHours(30) },
                                    colors = ButtonDefaults.buttonColors(containerColor = UmairaCardGray),
                                    border = BorderStroke(1.dp, Color.DarkGray),
                                    modifier = Modifier.weight(1f).height(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("+30 Hours", color = UmairaWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // High-fidelity instant publish/upload component
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, UmairaBorderRed.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = UmairaCardGray)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CloudUpload, contentDescription = "", tint = UmairaRed)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "UPLOAD NEW VIDEO / REEL",
                                    color = UmairaWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Video title OutlinedTextField
                            OutlinedTextField(
                                value = videoTitle,
                                onValueChange = { videoTitle = it },
                                placeholder = { Text("What is your video description title?", color = UmairaMutedText) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = UmairaWhite,
                                    unfocusedTextColor = UmairaWhite,
                                    focusedBorderColor = UmairaRed,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedContainerColor = Color.Black,
                                    unfocusedContainerColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("upload_title_field"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Type selectors row (VIDEO vs SHORT)
                            Text("Select Type Upload Format:", color = UmairaMutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                types.forEach { t ->
                                    val isSelected = selectType == t
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectType = t }
                                            .background(
                                                color = if (isSelected) UmairaRed else Color.Black,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) UmairaRed else Color.DarkGray,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (t == "SHORT") "Reel Short" else "Full Video",
                                            color = if (isSelected) UmairaWhite else UmairaMutedText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Category Chip Row selector
                            Text("Category tagging:", color = UmairaMutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                items(categories) { cat ->
                                    val isSelected = selectCategory == cat
                                    Box(
                                        modifier = Modifier
                                            .clickable { selectCategory = cat }
                                            .background(
                                                color = if (isSelected) UmairaRed else Color.Black,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) UmairaRed else Color.DarkGray,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = cat,
                                            color = if (isSelected) UmairaWhite else UmairaMutedText,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Bright Crimson publish button!
                            Button(
                                onClick = {
                                    if (videoTitle.isNotBlank()) {
                                        viewModel.uploadUserVideo(
                                            title = videoTitle,
                                            type = selectType,
                                            category = selectCategory
                                        )
                                        videoTitle = ""
                                    }
                                },
                                enabled = videoTitle.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = UmairaRed, disabledContainerColor = Color.DarkGray),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("publish_video_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Publish, contentDescription = "", tint = UmairaWhite)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Publish instantly on Umaira Tube", fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }

                // Beautiful uploaded videos table and specific analytics
                item {
                    Text(
                        text = "YOUR UPLOADED VIDEOS & EARNING STATS",
                        color = UmairaWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                if (totalUserVideos.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = UmairaCardGray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Filled.VideoFile, contentDescription = "", tint = UmairaMutedText, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No video uploads published yet.", color = UmairaMutedText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Use the form above to post your first video!", color = UmairaMutedText, fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    items(totalUserVideos) { v ->
                        val videoEarnings = (v.views / 1000.0) * adminCpmRate
                        Card(
                            colors = CardDefaults.cardColors(containerColor = UmairaCardGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(Color.Black, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (v.type == "SHORT") Icons.Filled.VideoLibrary else Icons.Filled.MovieCreation,
                                        contentDescription = "",
                                        tint = UmairaRed
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(v.title, color = UmairaWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Row(
                                        modifier = Modifier.padding(top = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(UmairaRed.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(v.type, color = UmairaRed, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("${v.views} views", color = UmairaMutedText, fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Earning tracker for this individual uploaded movie/short
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Earnings", color = UmairaMutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = if (isMonetized) "$${String.format("%.4f", videoEarnings)}" else "$0.0000",
                                        color = if (isMonetized) Color(0xFF4CAF50) else Color.Gray,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        style = LocalTextStyle.current.copy(
                                            shadow = Shadow(if (isMonetized) Color(0xFF4CAF50).copy(alpha = 0.5f) else Color.Transparent, blurRadius = 10f)
                                        )
                                    )
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        IconButton(
                                            onClick = {
                                                creatorVideoToEdit = v
                                                creatorEditTitleQuery = v.title
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Filled.Edit, contentDescription = "Edit Video Title", tint = UmairaWhite, modifier = Modifier.size(16.dp))
                                        }
                                        
                                        IconButton(
                                            onClick = {
                                                creatorVideoToDelete = v
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Delete Video", tint = UmairaRed, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (creatorVideoToEdit != null) {
        AlertDialog(
            onDismissRequest = { creatorVideoToEdit = null },
            title = { Text("Edit Video Description Title", color = UmairaWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    Text("Change your published video title below:", color = UmairaMutedText, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = creatorEditTitleQuery,
                        onValueChange = { creatorEditTitleQuery = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = UmairaWhite,
                            unfocusedTextColor = UmairaWhite,
                            focusedBorderColor = UmairaRed,
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = Color.Black,
                            unfocusedContainerColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetId = creatorVideoToEdit?.id
                        if (targetId != null && creatorEditTitleQuery.isNotBlank()) {
                            viewModel.updateVideoTitle(targetId, creatorEditTitleQuery)
                        }
                        creatorVideoToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UmairaRed)
                ) {
                    Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { creatorVideoToEdit = null }) {
                    Text("Cancel", color = UmairaWhite)
                }
            },
            containerColor = UmairaCardGray,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (creatorVideoToDelete != null) {
        AlertDialog(
            onDismissRequest = { creatorVideoToDelete = null },
            title = { Text("Delete Published Video?", color = UmairaRed, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp) },
            text = {
                Text("Are you absolutely sure you want to permanently delete this video? This action is irreversible, and any earned views will be subtracted from your global channel analytics count.", color = UmairaWhite, fontSize = 12.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetId = creatorVideoToDelete?.id
                        if (targetId != null) {
                            viewModel.deleteVideoById(targetId)
                        }
                        creatorVideoToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UmairaRed)
                ) {
                    Text("Delete Permanently", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { creatorVideoToDelete = null }) {
                    Text("Keep Video", color = UmairaWhite)
                }
            },
            containerColor = UmairaCardGray,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showWithdrawDialog) {
        var withdrawMethod by remember { mutableStateOf("JazzCash") } // "JazzCash" or "CreditCard"
        var jazzMobileNumber by remember { mutableStateOf("") }
        var jazzAccountTitle by remember { mutableStateOf("") }

        var ccNumber by remember { mutableStateOf("") }
        var ccHolderName by remember { mutableStateOf("") }
        var ccCvv by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = {
                Text(
                    "Withdraw Creator Earnings",
                    fontWeight = FontWeight.Black,
                    color = UmairaWhite,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Available Account Balance: $${String.format("%.4f", totalEarnings)} USD",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(
                        "Select Payout Network Method:",
                        color = UmairaMutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { withdrawMethod = "JazzCash" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (withdrawMethod == "JazzCash") UmairaRed else Color.Black
                            ),
                            border = BorderStroke(1.dp, if (withdrawMethod == "JazzCash") UmairaRed else Color.LightGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("JazzCash Mobile", color = UmairaWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { withdrawMethod = "CreditCard" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (withdrawMethod == "CreditCard") UmairaRed else Color.Black
                            ),
                            border = BorderStroke(1.dp, if (withdrawMethod == "CreditCard") UmairaRed else Color.LightGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Credit Card", color = UmairaWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (withdrawMethod == "JazzCash") {
                        OutlinedTextField(
                            value = jazzMobileNumber,
                            onValueChange = { jazzMobileNumber = it },
                            label = { Text("JazzCash Mobile Number (e.g. 03xx)", color = UmairaMutedText) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = UmairaWhite,
                                unfocusedTextColor = UmairaWhite,
                                focusedBorderColor = UmairaRed,
                                unfocusedBorderColor = Color.LightGray
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = jazzAccountTitle,
                            onValueChange = { jazzAccountTitle = it },
                            label = { Text("Account Owner Full Title Name", color = UmairaMutedText) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = UmairaWhite,
                                unfocusedTextColor = UmairaWhite,
                                focusedBorderColor = UmairaRed,
                                unfocusedBorderColor = Color.LightGray
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        OutlinedTextField(
                            value = ccNumber,
                            onValueChange = { ccNumber = it },
                            label = { Text("Visa/Mastercard Credit Card Number", color = UmairaMutedText) },
                            placeholder = { Text("XXXX-XXXX-XXXX-XXXX", color = UmairaMutedText) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = UmairaWhite,
                                unfocusedTextColor = UmairaWhite,
                                focusedBorderColor = UmairaRed,
                                unfocusedBorderColor = Color.LightGray
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = ccHolderName,
                                onValueChange = { ccHolderName = it },
                                label = { Text("Cardholder Name", color = UmairaMutedText) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = UmairaWhite,
                                    unfocusedTextColor = UmairaWhite,
                                    focusedBorderColor = UmairaRed,
                                    unfocusedBorderColor = Color.LightGray
                                ),
                                singleLine = true,
                                modifier = Modifier.weight(1.5f)
                            )

                            OutlinedTextField(
                                value = ccCvv,
                                onValueChange = { ccCvv = it },
                                label = { Text("CVV", color = UmairaMutedText) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = UmairaWhite,
                                    unfocusedTextColor = UmairaWhite,
                                    focusedBorderColor = UmairaRed,
                                    unfocusedBorderColor = Color.LightGray
                                ),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val details = if (withdrawMethod == "JazzCash") "$jazzMobileNumber [$jazzAccountTitle]" else "Card: $ccNumber [$ccHolderName]"
                        viewModel.requestWithdrawal(totalEarnings, withdrawMethod, details)
                        showWithdrawDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    enabled = if (totalEarnings <= 0) false else if (withdrawMethod == "JazzCash") jazzMobileNumber.isNotBlank() && jazzAccountTitle.isNotBlank() else ccNumber.isNotBlank() && ccHolderName.isNotBlank()
                ) {
                    Text("Request Withdrawal", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showWithdrawDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text("Cancel", color = UmairaWhite)
                }
            },
            containerColor = UmairaCardGray,
            titleContentColor = UmairaWhite
        )
    }
}

@Composable
fun AiCreativeWorkspace(
    viewModel: UmairaTubeViewModel,
    isAiLoading: Boolean,
    aiSeoResult: String,
    aiThumbnailResult: String?
) {
    var titleQuery by remember { mutableStateOf("") }
    var selectStyle by remember { mutableStateOf("Neo-SciFi Neon") }
    val styles = listOf("Neo-SciFi Neon", "Glassmorphic 3D Cartoon", "Sleek Dark Matte", "Epic Action Collage")

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = UmairaCardGray),
            modifier = Modifier.border(1.dp, UmairaBorderRed, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "AI SEO & Viral Predictor Optimizer",
                    color = UmairaWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "Enforces direct communication with Gemini model to build viral Title, Description, and tags suggestions.",
                    color = UmairaMutedText,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = titleQuery,
                    onValueChange = { titleQuery = it },
                    placeholder = { Text("Enter your focus video title...", color = UmairaMutedText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = UmairaWhite,
                        unfocusedTextColor = UmairaWhite,
                        focusedBorderColor = UmairaRed,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.runAiSeoOptimizer(titleQuery) },
                        colors = ButtonDefaults.buttonColors(containerColor = UmairaRed),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Generate SEO", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.runAiThumbnailGenerator(titleQuery, selectStyle) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Create Thumbnail Concept", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        if (isAiLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = UmairaRed)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Querying Gemini Engine...", color = UmairaRed, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (aiSeoResult.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = UmairaCardGray),
                modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.OfflineBolt, contentDescription = "", tint = UmairaRed)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Optimized Result Output:", color = UmairaWhite, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SelectionContainer {
                        Text(aiSeoResult, color = UmairaWhite, fontSize = 13.sp)
                    }
                }
            }
        }

        aiThumbnailResult?.let { thumb ->
            Card(
                colors = CardDefaults.cardColors(containerColor = UmairaCardGray),
                modifier = Modifier.border(1.dp, AuraGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = "", tint = AuraGold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Customized Thumbnail Concept Design:", color = AuraGold, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SelectionContainer {
                        Text(thumb, color = UmairaWhite, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun GhostUploadScheduler(viewModel: UmairaTubeViewModel, list: List<VideoEntity>) {
    var title by remember { mutableStateOf("") }
    var creator by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("AI") }
    var hoursDelay by remember { mutableStateOf("2") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = UmairaCardGray),
            modifier = Modifier.border(1.dp, UmairaBorderRed, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Ghost scheduled Uploads Tool",
                    color = UmairaWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Text(
                    "Schedule secret uploads that populate dynamically into the index once timers elapse.",
                    color = UmairaMutedText,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Video Title...", color = UmairaMutedText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = UmairaWhite,
                        unfocusedTextColor = UmairaWhite,
                        focusedBorderColor = UmairaRed,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = creator,
                        onValueChange = { creator = it },
                        placeholder = { Text("Your Creator handle...", color = UmairaMutedText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = UmairaWhite,
                            unfocusedTextColor = UmairaWhite,
                            focusedBorderColor = UmairaRed
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = hoursDelay,
                        onValueChange = { hoursDelay = it },
                        placeholder = { Text("Timer Hours release delay...", color = UmairaMutedText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = UmairaWhite,
                            unfocusedTextColor = UmairaWhite,
                            focusedBorderColor = UmairaRed
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val numDelay = hoursDelay.toIntOrNull() ?: 2
                        viewModel.createGhostUpload(
                            title = title,
                            creatorName = creator.ifEmpty { "Apex Channel" },
                            type = "VIDEO",
                            category = category,
                            scheduledDelayHours = numDelay,
                            viralityScorePercent = (55..99).random()
                        )
                        title = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UmairaRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Schedule Ghost Release Upload Task", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Active Pending Ghost Tasks:", color = UmairaWhite, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

        if (list.isEmpty()) {
            Text("No scheduled hidden uploads at present.", color = UmairaMutedText, fontSize = 13.sp)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(list) { v ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = UmairaCardGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(v.title, color = UmairaWhite, fontWeight = FontWeight.Bold)
                                Text("Locked Scheduled Release: +${hoursDelay} hours", color = UmairaRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.releaseGhostUpload(v) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                            ) {
                                Text("RELEASE NOW", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuraLeaderboardPanel() {
    val simulatedEarningSubscribers = 840
    val simWatchHours = 3140

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Aesthetic Aura Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            modifier = Modifier.border(1.2.dp, AuraGold, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Filled.OfflineBolt, contentDescription = "", tint = AuraGold, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("YOUR CREATOR AURA SCORE", color = UmairaWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("982", color = AuraGold, fontWeight = FontWeight.Black, fontSize = 52.sp, style = LocalTextStyle.current.copy(
                    shadow = Shadow(AuraGold, blurRadius = 25f)
                ))
                Text("Rank Status: PRO LEVEL CHIEF", color = UmairaMutedText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Creator monetization parameters simulation tracker
        Card(colors = CardDefaults.cardColors(containerColor = UmairaCardGray)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Ultra monetization milestone tracker Tracker",
                    color = UmairaWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Channel Subscribers: 840 / 1000", color = UmairaWhite, fontSize = 12.sp)
                LinearProgressIndicator(
                    progress = simulatedEarningSubscribers / 1000f,
                    color = UmairaRed,
                    trackColor = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Watch Hours: 3140 / 4000 total hours", color = UmairaWhite, fontSize = 12.sp)
                LinearProgressIndicator(
                    progress = simWatchHours / 4000f,
                    color = AuraGold,
                    trackColor = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Ad rates simulator mockup
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("ESTIMATED MONTHLY SUPERCHAT REVENUE", color = UmairaMutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("$1,240.24 USD", color = Color(0xFF4CAF50), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

// Global Custom Voice Input Simulator Dialogue
@Composable
fun VoiceSearchDialog(viewModel: UmairaTubeViewModel, onClose: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    val scope = rememberCoroutineScope()
    val prompts = listOf("Cinematic trailer 8k", "VashMax VFX transition", "AI growth mobile Android SDK")

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onClose) {
                Text("CLOSE", color = UmairaRed)
            }
        },
        containerColor = UmairaCardGray,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Mic, contentDescription = "", tint = UmairaRed)
                Spacer(modifier = Modifier.width(10.dp))
                Text("AI Voice Dictator Processing", color = UmairaWhite, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (step == 1) {
                    Text("Listening for spoken voice keywords...", color = UmairaWhite, modifier = Modifier.padding(bottom = 12.dp))
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(UmairaRed.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Hearing, contentDescription = "Active Mic Feedback", tint = UmairaRed, modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Try speaking terms directly: Tap to simulate", color = UmairaMutedText, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    prompts.forEach { p ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(Color.Black, RoundedCornerShape(10.dp))
                                .clickable {
                                    scope.launch {
                                        step = 2
                                        delay(1500)
                                        viewModel.runVoiceSearch(p)
                                        onClose()
                                    }
                                }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Speak \"$p\"", color = UmairaWhite, fontWeight = FontWeight.Medium)
                        }
                    }
                } else {
                    Text("AI Translating spoken dialect...", color = UmairaWhite)
                    Spacer(modifier = Modifier.height(14.dp))
                    CircularProgressIndicator(color = UmairaRed)
                }
            }
        }
    )
}

// IMMERSIVE FULL-SCREEN VIDEO FEEDBACK PLAYER OVERLAY
@Composable
fun VideoPlayerOverlay(video: VideoEntity, viewModel: UmairaTubeViewModel, onClose: () -> Unit) {
    val aiCaptions by viewModel.aiCaptionsResult.collectAsState()
    val comments by viewModel.activeComments.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var commentInput by remember { mutableStateOf("") }
    var isPlaying by remember { mutableStateOf(true) }
    var currentProgress by remember { mutableStateOf(0.24f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UmairaRichBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            // Immersive Custom 16:9 Video Stream simulation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color.Black)
            ) {
                // Interactive video frames background or animated loading
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0x22111111))
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.background(Color(0x77000000), CircleShape)
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = UmairaWhite)
                        }

                        // Airplay / Settings indicator
                        IconButton(
                            onClick = {},
                            modifier = Modifier.background(Color(0x77000000), CircleShape)
                        ) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = UmairaWhite)
                        }
                    }

                    // Floating play central control
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(54.dp)
                            .background(Color(0x99000000), CircleShape)
                            .clickable { isPlaying = !isPlaying },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Progress slider layout
                    Column {
                        // AI captions overlay text if compiled
                        if (aiCaptions.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .background(Color(0xAA000000), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = aiCaptions.take(180),
                                    color = Color.Yellow,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("02:30", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Slider(
                                value = currentProgress,
                                onValueChange = { currentProgress = it },
                                colors = SliderDefaults.colors(
                                    thumbColor = UmairaRed,
                                    activeTrackColor = UmairaRed,
                                    inactiveTrackColor = Color.DarkGray
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            )
                            Text(video.duration, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Scrollable Interaction area below player
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Video Details Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFD700), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("AURA REWARD +${video.auraScore}", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .background(UmairaRed, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Viral: ${video.viralityChance}% chance", color = UmairaWhite, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(video.title, color = UmairaWhite, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(UmairaRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(video.creatorAvatar, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(video.creatorName, color = UmairaWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("4.5M Subscribers", color = UmairaMutedText, fontSize = 10.sp)
                    }

                    Button(
                        onClick = { viewModel.toggleSubscription(video.creatorName, video.isSubscribed) },
                        colors = ButtonDefaults.buttonColors(containerColor = if (video.isSubscribed) Color.DarkGray else UmairaRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(if (video.isSubscribed) "SUBSCRIBED" else "SUBSCRIBE", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons center row (Like / Download / AI Captions)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.toggleLike(video) },
                        colors = ButtonDefaults.buttonColors(containerColor = if (video.isLiked) UmairaRed else UmairaCardGray),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ThumbUp, contentDescription = "", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (video.isLiked) "LIKED" else "LIKE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = { viewModel.generateVideoCaptions(video) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isAiLoading) {
                                CircularProgressIndicator(color = UmairaWhite, modifier = Modifier.size(14.dp))
                            } else {
                                Icon(Icons.Filled.Hearing, contentDescription = "", modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AI CAPTION GEN", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = { /* Simulated download */ },
                        colors = ButtonDefaults.buttonColors(containerColor = UmairaCardGray),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Download, contentDescription = "", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SAVE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Persistant Interactive Comments List Section
                Text(
                    "Comment Box (Persistent Room Database):",
                    color = UmairaRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Input Box for real-time Room comments injection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentInput,
                        onValueChange = { commentInput = it },
                        placeholder = { Text("Publish a dynamic comment...", color = UmairaMutedText, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = UmairaWhite,
                            unfocusedTextColor = UmairaWhite,
                            focusedBorderColor = UmairaRed,
                            unfocusedBorderColor = Color.DarkGray
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (commentInput.isNotBlank()) {
                                viewModel.addComment(video.id, commentInput)
                                commentInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(UmairaRed, CircleShape)
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Send", tint = UmairaWhite)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (comments.isEmpty()) {
                        item {
                            Text("Be the first to share your opinion here!", color = UmairaMutedText, fontSize = 12.sp)
                        }
                    } else {
                        items(comments) { comment ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(UmairaCardGray, RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color.Gray, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(comment.author.take(1), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column {
                                    Text(comment.author, color = UmairaRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(comment.content, color = UmairaWhite, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OwnerCockpitTab(viewModel: UmairaTubeViewModel) {
    val email by viewModel.currentUser.collectAsState()
    val activeEmail = email ?: "umairkhanseo22400@gmail.com"
    
    val isOwnerVerified by viewModel.isOwnerVerified.collectAsState()
    val adminVerificationSent by viewModel.adminVerificationSent.collectAsState()
    val verificationProgressMessage by viewModel.verificationProgressMessage.collectAsState()
    
    val adminCreators by viewModel.adminCreators.collectAsState()
    val adminWithdrawals by viewModel.adminWithdrawalRequests.collectAsState()
    
    val allVideos by viewModel.videosList.collectAsState()
    val allShorts by viewModel.shortsList.collectAsState()
    val combinedMediaList = allVideos + allShorts
    
    val adminCpmRate by viewModel.adminCpmRate.collectAsState()
    
    var localOtpInput by remember { mutableStateOf("") }
    var activeSubTab by remember { mutableStateOf("creators") } // "creators", "media", "payouts", "simulation"
    var isSimulationRunning by remember { mutableStateOf(false) }
    var simulationMessage by remember { mutableStateOf("") }

    var adminVideoToEdit by remember { mutableStateOf<VideoEntity?>(null) }
    var adminEditTitleQuery by remember { mutableStateOf("") }
    
    val goldColor = Color(0xFFFFD700)
    val adminCardBg = Color(0xFF1E1E1E)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UmairaRichBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Cockpit Title Banner
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0x22FFD700), RoundedCornerShape(12.dp))
                    .border(1.dp, goldColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = "Admin Security",
                        tint = goldColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PLATFORM SECURED SYSTEM",
                        color = goldColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "OWNER CONTROL COCKPIT",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = UmairaWhite,
                letterSpacing = 2.sp,
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(goldColor, blurRadius = 10f)
                )
            )
            
            Text(
                text = "Authorized Admin: $activeEmail",
                fontSize = 12.sp,
                color = UmairaMutedText,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (!isOwnerVerified) {
            // SECURE VERIFICATION PORTAL
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, goldColor.copy(alpha = 0.25f), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = adminCardBg),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = "Shield Lock Verified",
                        tint = goldColor,
                        modifier = Modifier.size(60.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "OWNER ACCOUNT CONFIRMATION REQUIRED",
                        color = UmairaWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.sp
                    )
                    
                    Text(
                        text = "For security, you must verify ownership of the linked registered developer email with a simulated SMS/Email OTP code link to initiate fully operational control.",
                        color = UmairaMutedText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )
                    
                    if (!adminVerificationSent) {
                        Button(
                            onClick = { viewModel.requestAdminOtp(activeEmail) },
                            colors = ButtonDefaults.buttonColors(containerColor = goldColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("Sent Verification to Gmail", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .border(1.dp, goldColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = verificationProgressMessage,
                                    color = goldColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            OutlinedTextField(
                                value = localOtpInput,
                                onValueChange = { localOtpInput = it },
                                label = { Text("Enter 4-Digit Gmail OTP", color = UmairaMutedText) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = goldColor,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = UmairaWhite,
                                    unfocusedTextColor = UmairaWhite
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.resetOwnerVerification() },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = UmairaWhite),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Reset")
                                }
                                
                                Button(
                                    onClick = {
                                        viewModel.verifyAdminOtp(localOtpInput)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = goldColor),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Verify & Access", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // VERIFIED OPERATION PANEL
            
            // Platform Stats Row
            Card(
                colors = CardDefaults.cardColors(containerColor = adminCardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SYSTEM DASHBOARD OVERVIEW", color = goldColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total views card
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text("Total Uploads", color = UmairaMutedText, fontSize = 10.sp)
                            Text("${combinedMediaList.size}", color = UmairaWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        // Total Views card
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text("Platform Traffic", color = UmairaMutedText, fontSize = 10.sp)
                            Text("${combinedMediaList.sumOf { it.views }} v", color = UmairaWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        // Active Creators
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text("Total Creators", color = UmairaMutedText, fontSize = 10.sp)
                            Text("${adminCreators.size}", color = goldColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // CPM Rate Tuning Cockpit
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Global Platform CPM Rate", color = UmairaWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = String.format("$%.2f / 1K views", adminCpmRate),
                                    color = goldColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Slider(
                                value = adminCpmRate,
                                onValueChange = { viewModel.adminSetCpmRate(it.coerceIn(0.10f, 5.00f)) },
                                valueRange = 0.10f..5.00f,
                                colors = SliderDefaults.colors(
                                    thumbColor = goldColor,
                                    activeTrackColor = goldColor,
                                    inactiveTrackColor = Color.DarkGray
                                )
                            )
                            
                            Text(
                                "Drag slider to dynamically adjust the platform monetization earnings payout rate instantly for all creators.",
                                color = UmairaMutedText,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
            
            // Local Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val tabBtn = @Composable { tabId: String, label: String, active: Boolean ->
                    Button(
                        onClick = { activeSubTab = tabId },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (active) goldColor else Color.DarkGray.copy(alpha = 0.4f)
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (active) Color.Black else UmairaWhite
                        )
                    }
                }
                
                tabBtn("creators", "Creators", activeSubTab == "creators")
                tabBtn("adsense", "AdSense", activeSubTab == "adsense")
                tabBtn("media", "Auditor", activeSubTab == "media")
                tabBtn("payouts", "Payouts", activeSubTab == "payouts")
                tabBtn("simulation", "Simulator", activeSubTab == "simulation")
            }
            
            // Sub tab views container
            when (activeSubTab) {
                "adsense" -> {
                    val adminAdSenseRequests by viewModel.adminAdSenseRequests.collectAsState()
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("PENDING ADSENSE MONETIZATION APPLICATIONS", color = UmairaWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                        if (adminAdSenseRequests.isEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = adminCardBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = "", tint = Color.Gray, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No pending AdSense creator requests found.", color = UmairaMutedText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            adminAdSenseRequests.forEach { req ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = adminCardBg),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color(0xFF00BCD4).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(req.channelName, color = UmairaWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text(req.email, color = UmairaMutedText, fontSize = 11.sp)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF00BCD4).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("PENDING REVIEW", color = Color(0xFF00BCD4), fontSize = 9.sp, fontWeight = FontWeight.Black)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Column {
                                                Text("Subscribers Count", color = UmairaMutedText, fontSize = 9.sp)
                                                Text("${req.subscribers} / 1,000", color = UmairaWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Column {
                                                Text("Watch Time Metric", color = UmairaMutedText, fontSize = 9.sp)
                                                Text("${req.watchHours} / 500 watch hrs", color = UmairaWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { viewModel.adminRejectAdSense(req.email) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                                modifier = Modifier.weight(1f).height(36.dp),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Reject Link", color = UmairaWhite, fontSize = 11.sp)
                                            }
                                            Button(
                                                onClick = { viewModel.adminApproveAdSense(req.email) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                                modifier = Modifier.weight(1.5f).height(36.dp),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Approve Account monetization", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "creators" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("ACTIVE CREATORS MANAGEMENT", color = UmairaWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        
                        adminCreators.forEach { cr ->
                            val defaultHandle = cr.email.substringBefore("@")
                            Card(
                                colors = CardDefaults.cardColors(containerColor = adminCardBg),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, if (cr.isSuspended) UmairaRed.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(if (cr.isSuspended) UmairaRed else Color.DarkGray, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(cr.name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    
                                    Spacer(modifier = Modifier.width(10.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(cr.name, color = UmairaWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            if (cr.isSuspended) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .background(UmairaRed, RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                                ) {
                                                    Text("SUSPENDED", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                                                }
                                            }
                                        }
                                        Text(cr.email, color = UmairaMutedText, fontSize = 10.sp)
                                        Text("${cr.totalVideos} videos • ${cr.totalViews} views • Pending Payout: $${String.format("%.2f", cr.pendingBalance)}", color = goldColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    if (cr.isSuspended) {
                                        IconButton(
                                            onClick = { viewModel.adminToggleCreatorSuspension(cr.email, false) },
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(Color(0xFF2E7D32), RoundedCornerShape(8.dp))
                                        ) {
                                            Icon(Icons.Filled.Check, contentDescription = "Reinstate", tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    } else {
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            IconButton(
                                                onClick = { viewModel.adminToggleCreatorSuspension(cr.email, true) },
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .background(Color(0xFFC62828), RoundedCornerShape(8.dp))
                                            ) {
                                                Icon(Icons.Filled.Block, contentDescription = "Strike", tint = Color.White, modifier = Modifier.size(16.dp))
                                            }
                                            
                                            IconButton(
                                                onClick = { viewModel.adminDeleteChannel(cr.email) },
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .background(Color.Black, RoundedCornerShape(8.dp))
                                            ) {
                                                Icon(Icons.Filled.Delete, contentDescription = "Erase", tint = UmairaRed, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                "media" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("PLATFORM MEDIA AUDITOR FEED", color = UmairaWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        
                        if (combinedMediaList.isEmpty()) {
                            Text("No videos currently uploaded on UMAIRA TUBE.", color = UmairaMutedText, fontSize = 11.sp)
                        } else {
                            combinedMediaList.forEach { vd ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = adminCardBg),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp, 36.dp)
                                                .background(Color.Black, RoundedCornerShape(6.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(if (vd.type == "SHORT") "Reel" else "Video", color = UmairaRed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                        
                                        Spacer(modifier = Modifier.width(10.dp))
                                        
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(vd.title, color = UmairaWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                            Text("By ${vd.creatorName} • ${vd.views} views", color = UmairaMutedText, fontSize = 10.sp)
                                        }
                                        
                                        Spacer(modifier = Modifier.width(8.dp))
                                        
                                        IconButton(
                                            onClick = { 
                                                adminVideoToEdit = vd
                                                adminEditTitleQuery = vd.title 
                                            },
                                            modifier = Modifier
                                                .padding(end = 4.dp)
                                                .size(34.dp)
                                                .background(Color.Gray, RoundedCornerShape(8.dp))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Edit,
                                                contentDescription = "Edit Video Title",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        
                                        IconButton(
                                            onClick = { viewModel.adminTakedownVideo(vd.id) },
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(Color(0xFFFF003F), RoundedCornerShape(8.dp))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = "Takedown Media",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                "payouts" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("SECURED WALLET WITHDRAWALS LIST", color = UmairaWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        
                        if (adminWithdrawals.isEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = adminCardBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "All requested withdrawals fully verified & dispatched.",
                                    color = UmairaMutedText,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(20.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            adminWithdrawals.forEach { wq ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = adminCardBg),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 1.dp,
                                            color = when (wq.status) {
                                                "APPROVED" -> Color.Green.copy(alpha = 0.2f)
                                                "REJECTED" -> UmairaRed.copy(alpha = 0.2f)
                                                else -> goldColor.copy(alpha = 0.2f)
                                            },
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(wq.name, color = UmairaWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text(wq.email, color = UmairaMutedText, fontSize = 10.sp)
                                            }
                                            
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        color = when (wq.status) {
                                                            "APPROVED" -> Color(0xFF2E7D32)
                                                            "REJECTED" -> Color(0xFFC62828)
                                                            else -> Color(0xFFFF8F00)
                                                        },
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(wq.status, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("Method: ${wq.method}", color = UmairaWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Text("Details: ${wq.details}", color = UmairaMutedText, fontSize = 10.sp)
                                            }
                                            
                                            Text(
                                                text = String.format("$%.2f", wq.amount),
                                                color = goldColor,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        
                                        if (wq.status == "PENDING") {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Button(
                                                    onClick = { viewModel.adminRejectWithdrawal(wq.id) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                                    modifier = Modifier.weight(1f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Reject/Hold", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                                
                                                Button(
                                                    onClick = { viewModel.adminApproveWithdrawal(wq.id) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                                    modifier = Modifier.weight(1f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Approve Payout", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                "simulation" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = adminCardBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, goldColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("UMAIRA POLICY SAFETY TESTING SIMULATOR", color = goldColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text(
                                "Easily trigger mock creator channels uploading contents to verify the automatic moderation filters of UMAIRA TUBE in real-time.",
                                color = UmairaMutedText,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = if (simulationMessage.isNotEmpty()) simulationMessage else "No test activity currently recorded.",
                                    color = if (simulationMessage.contains("AUTOMATED BAN")) UmairaRed else UmairaWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    isSimulationRunning = true
                                    simulationMessage = "Test User 'vanguard_vids@gmail.com' is publishing normal video 'Making Android Apps with AI'..."
                                    viewModel.createChannel("vanguard_vids@gmail.com", "Vanguard Vids")
                                    viewModel.uploadUserVideo("Making Android Apps with AI", "VIDEO", "AI")
                                    simulationMessage += "\nResult: SUCCESS! Video added to feed. Channel active."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Text("Simulate Standard Video Upload", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            
                            Button(
                                onClick = {
                                    isSimulationRunning = true
                                    simulationMessage = "Test User 'bad_actor@gmail.com' tries uploading illegal video 'XXX Mature Movie Clips'..."
                                    viewModel.createChannel("bad_actor@gmail.com", "Shadow Actor")
                                    viewModel.uploadUserVideo("XXX Mature Movie Clips", "VIDEO", "Trending")
                                    simulationMessage += "\nResult: AUTOMATED BAN INFRASTRUCTURE TRIGGERED! Shadow Actor channel has been instantly Suspended/Banned on violation check! Adult keyword 'xxx' matched."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = UmairaRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Simulate Policy Violation (xxx) Ban Click", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
        
        if (adminVideoToEdit != null) {
            AlertDialog(
                onDismissRequest = { adminVideoToEdit = null },
                title = { Text("Edit Video Title (Admin Control)", color = UmairaWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                text = {
                    Column {
                        Text("You are updating the title of this broadcasted content as the platform Owner.", color = UmairaMutedText, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = adminEditTitleQuery,
                            onValueChange = { adminEditTitleQuery = it },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = UmairaWhite,
                                unfocusedTextColor = UmairaWhite,
                                focusedBorderColor = goldColor,
                                unfocusedBorderColor = Color.LightGray
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val targetId = adminVideoToEdit?.id
                            if (targetId != null && adminEditTitleQuery.isNotBlank()) {
                                viewModel.updateVideoTitle(targetId, adminEditTitleQuery)
                            }
                            adminVideoToEdit = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = goldColor)
                    ) {
                        Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { adminVideoToEdit = null }) {
                        Text("Cancel", color = UmairaWhite)
                    }
                },
                containerColor = adminCardBg,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}
