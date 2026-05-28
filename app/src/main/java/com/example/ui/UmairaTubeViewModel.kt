package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.UUID

class UmairaTubeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val videoDao = database.videoDao()
    private val commentDao = database.commentDao()
    private val battleDao = database.battleDao()
    private val geminiRepository = GeminiRepository()

    private val prefs = application.getSharedPreferences("umaira_tube_prefs", android.content.Context.MODE_PRIVATE)

    // Flow for Channel Name (null means not created yet!)
    private val _channelName = MutableStateFlow<String?>(null)
    val channelName: StateFlow<String?> = _channelName.asStateFlow()

    // Flow for channel suspension (due to explicit keyword)
    private val _channelSuspended = MutableStateFlow(false)
    val channelSuspended: StateFlow<Boolean> = _channelSuspended.asStateFlow()

    // Flow for withdrawal message
    private val _withdrawalMessage = MutableStateFlow<String?>(null)
    val withdrawalMessage: StateFlow<String?> = _withdrawalMessage.asStateFlow()

    // Flow for pending withdrawal amount
    private val _pendingWithdrawalAmount = MutableStateFlow(0f)
    val pendingWithdrawalAmount: StateFlow<Float> = _pendingWithdrawalAmount.asStateFlow()

    // --- OWNER / ADMIN CONTROL STATE ---
    val ownerEmails = listOf("umairkhanseo22400@gmail.com", "umaircontent2240@gmail.com")

    private val _adminCpmRate = MutableStateFlow(prefs.getFloat("admin_cpm_rate", 0.50f))
    val adminCpmRate: StateFlow<Float> = _adminCpmRate.asStateFlow()

    val adminVerificationSent = MutableStateFlow(false)
    val adminVerificationCode = MutableStateFlow("")
    val isOwnerVerified = MutableStateFlow(false)
    val verificationProgressMessage = MutableStateFlow("")

    data class CreatorInfo(
        val email: String,
        val name: String,
        val isSuspended: Boolean,
        val totalViews: Int,
        val totalVideos: Int,
        val pendingBalance: Float
    )

    data class AdminWithdrawalRequest(
        val id: String,
        val email: String,
        val name: String,
        val amount: Float,
        val method: String,
        val details: String,
        val timestamp: Long,
        val status: String // "PENDING", "APPROVED", "REJECTED"
    )

    private val _adminCreators = MutableStateFlow<List<CreatorInfo>>(emptyList())
    val adminCreators: StateFlow<List<CreatorInfo>> = _adminCreators.asStateFlow()

    private val _adminWithdrawalRequests = MutableStateFlow<List<AdminWithdrawalRequest>>(emptyList())
    val adminWithdrawalRequests: StateFlow<List<AdminWithdrawalRequest>> = _adminWithdrawalRequests.asStateFlow()

    // --- MONETIZATION & ADSENSE STATES ---
    private val _channelSubscribers = MutableStateFlow(840)
    val channelSubscribers: StateFlow<Int> = _channelSubscribers.asStateFlow()

    private val _channelWatchHours = MutableStateFlow(314)
    val channelWatchHours: StateFlow<Int> = _channelWatchHours.asStateFlow()

    private val _adsenseStatus = MutableStateFlow("NOT_ELIGIBLE") // NOT_ELIGIBLE, ELIGIBLE_TO_APPLY, ADSENSE_PENDING, FULLY_MONETIZED
    val adsenseStatus: StateFlow<String> = _adsenseStatus.asStateFlow()

    data class AdSenseRequest(
        val email: String,
        val channelName: String,
        val subscribers: Int,
        val watchHours: Int
    )

    private val _adminAdSenseRequests = MutableStateFlow<List<AdSenseRequest>>(emptyList())
    val adminAdSenseRequests: StateFlow<List<AdSenseRequest>> = _adminAdSenseRequests.asStateFlow()

    // --- SCREEN CONFIG & NAVIGATION TAB ---
    private val _activeTab = MutableStateFlow("home")
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    fun setActiveTab(tab: String) {
        _activeTab.value = tab
    }

    fun simulateAddSubscribers(amount: Int) {
        val email = _currentUser.value ?: "king2240ff@gmail.com"
        val current = _channelSubscribers.value
        val next = current + amount
        prefs.edit().putInt("channel_subscribers_$email", next).apply()
        _channelSubscribers.value = next
        checkMonetizationEligibility(email)
    }

    fun simulateAddWatchHours(amount: Int) {
        val email = _currentUser.value ?: "king2240ff@gmail.com"
        val current = _channelWatchHours.value
        val next = current + amount
        prefs.edit().putInt("channel_watch_hours_$email", next).apply()
        _channelWatchHours.value = next
        checkMonetizationEligibility(email)
    }

    private fun checkMonetizationEligibility(email: String) {
        val subs = prefs.getInt("channel_subscribers_$email", 840)
        val watch = prefs.getInt("channel_watch_hours_$email", 314)
        val status = prefs.getString("adsense_status_$email", "NOT_ELIGIBLE") ?: "NOT_ELIGIBLE"
        if (status == "NOT_ELIGIBLE" && subs >= 1000 && watch >= 500) {
            prefs.edit().putString("adsense_status_$email", "ELIGIBLE_TO_APPLY").apply()
            _adsenseStatus.value = "ELIGIBLE_TO_APPLY"
        } else if (status == "ELIGIBLE_TO_APPLY" && (subs < 1000 || watch < 500)) {
            prefs.edit().putString("adsense_status_$email", "NOT_ELIGIBLE").apply()
            _adsenseStatus.value = "NOT_ELIGIBLE"
        }
    }

    fun applyForAdSense() {
        val email = _currentUser.value ?: "king2240ff@gmail.com"
        prefs.edit().putString("adsense_status_$email", "ADSENSE_PENDING").apply()
        _adsenseStatus.value = "ADSENSE_PENDING"

        val set = prefs.getStringSet("registered_creators_set", emptySet())?.toMutableSet() ?: mutableSetOf()
        set.add(email)
        prefs.edit().putStringSet("registered_creators_set", set).apply()

        refreshAdminDataLists()
    }

    fun adminApproveAdSense(email: String) {
        prefs.edit().putString("adsense_status_$email", "FULLY_MONETIZED").apply()
        if (_currentUser.value == email) {
            _adsenseStatus.value = "FULLY_MONETIZED"
        }
        refreshAdminDataLists()
    }

    fun adminRejectAdSense(email: String) {
        prefs.edit().putString("adsense_status_$email", "ELIGIBLE_TO_APPLY").apply()
        if (_currentUser.value == email) {
            _adsenseStatus.value = "ELIGIBLE_TO_APPLY"
        }
        refreshAdminDataLists()
    }

    fun updateVideoTitle(id: Int, newTitle: String) {
        viewModelScope.launch {
            val video = videoDao.getVideoById(id)
            if (video != null) {
                videoDao.updateVideo(video.copy(title = newTitle))
            }
        }
    }

    fun isCurrentUserAdmin(): Boolean {
        val email = _currentUser.value ?: return false
        return ownerEmails.contains(email.lowercase().trim())
    }

    fun requestAdminOtp(email: String) {
        viewModelScope.launch {
            verificationProgressMessage.value = "Sending Secure Authorization Link and OTP code to $email..."
            delay(1200)
            val generatedCode = (1000..9999).random().toString()
            adminVerificationCode.value = generatedCode
            adminVerificationSent.value = true
            verificationProgressMessage.value = "Verification code securely dispatched to $email! (Hint: Code is $generatedCode)"
        }
    }

    fun verifyAdminOtp(inputCode: String): Boolean {
        if (inputCode == adminVerificationCode.value && adminVerificationCode.value.isNotEmpty()) {
            isOwnerVerified.value = true
            verificationProgressMessage.value = "Verification Successful! Welcome Owner."
            refreshAdminDataLists()
            return true
        }
        verificationProgressMessage.value = "Error: Invalid/Expired Verification Code!"
        return false
    }

    fun resetOwnerVerification() {
        adminVerificationSent.value = false
        adminVerificationCode.value = ""
        isOwnerVerified.value = false
        verificationProgressMessage.value = ""
    }

    fun adminSetCpmRate(rate: Float) {
        prefs.edit().putFloat("admin_cpm_rate", rate).apply()
        _adminCpmRate.value = rate
    }

    fun checkChannelStateForUser(email: String) {
        val name = prefs.getString("channel_name_$email", null)
        val isSuspended = prefs.getBoolean("channel_suspended_$email", false)
        val pending = prefs.getFloat("pending_withdrawal_$email", 0f)
        _channelName.value = name
        _channelSuspended.value = isSuspended
        _pendingWithdrawalAmount.value = pending

        val subs = prefs.getInt("channel_subscribers_$email", 840)
        val watch = prefs.getInt("channel_watch_hours_$email", 314)
        var status = prefs.getString("adsense_status_$email", "NOT_ELIGIBLE") ?: "NOT_ELIGIBLE"

        if (status == "NOT_ELIGIBLE" && subs >= 1000 && watch >= 500) {
            status = "ELIGIBLE_TO_APPLY"
            prefs.edit().putString("adsense_status_$email", "ELIGIBLE_TO_APPLY").apply()
        } else if (status == "ELIGIBLE_TO_APPLY" && (subs < 1000 || watch < 500)) {
            status = "NOT_ELIGIBLE"
            prefs.edit().putString("adsense_status_$email", "NOT_ELIGIBLE").apply()
        }

        _channelSubscribers.value = subs
        _channelWatchHours.value = watch
        _adsenseStatus.value = status

        if (ownerEmails.contains(email.lowercase().trim())) {
            refreshAdminDataLists()
        }
    }

    fun createChannel(email: String, name: String) {
        prefs.edit()
            .putString("channel_name_$email", name)
            .putBoolean("channel_suspended_$email", false)
            .apply()
        _channelName.value = name
        _channelSuspended.value = false

        val set = prefs.getStringSet("registered_creators_set", emptySet())?.toMutableSet() ?: mutableSetOf()
        set.add(email)
        prefs.edit().putStringSet("registered_creators_set", set).apply()

        refreshAdminDataLists()
    }

    fun deleteAndSuspendChannel(email: String) {
        prefs.edit()
            .putBoolean("channel_suspended_$email", true)
            .apply()
        _channelSuspended.value = true
        refreshAdminDataLists()
    }

    fun requestWithdrawal(amount: Double, method: String, details: String) {
        val email = _currentUser.value ?: "king2240ff@gmail.com"
        val currentPending = prefs.getFloat("pending_withdrawal_$email", 0f)
        val newPending = currentPending + amount.toFloat()
        prefs.edit()
            .putFloat("pending_withdrawal_$email", newPending)
            .apply()
        _pendingWithdrawalAmount.value = newPending

        val requests = prefs.getStringSet("withdrawal_requests_list", emptySet())?.toMutableSet() ?: mutableSetOf()
        val requestId = UUID.randomUUID().toString().take(6)
        requests.add("$requestId|$email|$amount|$method|$details|${System.currentTimeMillis()}|PENDING")
        prefs.edit().putStringSet("withdrawal_requests_list", requests).apply()

        val set = prefs.getStringSet("registered_creators_set", emptySet())?.toMutableSet() ?: mutableSetOf()
        set.add(email)
        prefs.edit().putStringSet("registered_creators_set", set).apply()

        _withdrawalMessage.value = "Successfully requested withdrawal of $$amount to $method ($details). Pending transfer."
        refreshAdminDataLists()
    }

    fun clearWithdrawalMessage() {
        _withdrawalMessage.value = null
    }

    fun resetChannelAfterViolation(email: String) {
        prefs.edit()
            .remove("channel_name_$email")
            .putBoolean("channel_suspended_$email", false)
            .apply()
        _channelName.value = null
        _channelSuspended.value = false
        refreshAdminDataLists()
    }

    fun adminToggleCreatorSuspension(email: String, suspend: Boolean) {
        prefs.edit().putBoolean("channel_suspended_$email", suspend).apply()
        if (_currentUser.value == email) {
            _channelSuspended.value = suspend
        }
        refreshAdminDataLists()
    }

    fun adminDeleteChannel(email: String) {
        prefs.edit()
            .remove("channel_name_$email")
            .putBoolean("channel_suspended_$email", true)
            .apply()
        if (_currentUser.value == email) {
            _channelName.value = null
            _channelSuspended.value = true
        }
        refreshAdminDataLists()
    }

    fun adminApproveWithdrawal(requestId: String) {
        val reqSet = prefs.getStringSet("withdrawal_requests_list", emptySet())?.toMutableSet() ?: mutableSetOf()
        val matchStr = reqSet.find { it.startsWith("$requestId|") }
        if (matchStr != null) {
            val parts = matchStr.split("|").toMutableList()
            if (parts.size >= 7) {
                val email = parts[1]
                val amount = parts[2].toFloatOrNull() ?: 0f
                parts[6] = "APPROVED"
                reqSet.remove(matchStr)
                reqSet.add(parts.joinToString("|"))

                val currentPending = prefs.getFloat("pending_withdrawal_$email", 0f)
                val newPending = (currentPending - amount).coerceAtLeast(0f)
                prefs.edit()
                    .putFloat("pending_withdrawal_$email", newPending)
                    .putStringSet("withdrawal_requests_list", reqSet)
                    .apply()

                if (_currentUser.value == email) {
                    _pendingWithdrawalAmount.value = newPending
                    _withdrawalMessage.value = "Your requested transfer of $$amount has been APPROVED & DISPATCHED by the Admin Owner!"
                }
            }
        }
        refreshAdminDataLists()
    }

    fun adminRejectWithdrawal(requestId: String) {
        val reqSet = prefs.getStringSet("withdrawal_requests_list", emptySet())?.toMutableSet() ?: mutableSetOf()
        val matchStr = reqSet.find { it.startsWith("$requestId|") }
        if (matchStr != null) {
            val parts = matchStr.split("|").toMutableList()
            if (parts.size >= 7) {
                val email = parts[1]
                val amount = parts[2].toFloatOrNull() ?: 0f
                parts[6] = "REJECTED"
                reqSet.remove(matchStr)
                reqSet.add(parts.joinToString("|"))

                val currentPending = prefs.getFloat("pending_withdrawal_$email", 0f)
                val newPending = (currentPending - amount).coerceAtLeast(0f)
                prefs.edit()
                    .putFloat("pending_withdrawal_$email", newPending)
                    .putStringSet("withdrawal_requests_list", reqSet)
                    .apply()

                if (_currentUser.value == email) {
                    _pendingWithdrawalAmount.value = newPending
                    _withdrawalMessage.value = "Your requested transfer of $$amount has been REJECTED by the Admin. Please appeal."
                }
            }
        }
        refreshAdminDataLists()
    }

    fun refreshAdminDataLists() {
        val registeredEmails = prefs.getStringSet("registered_creators_set", emptySet())?.toMutableSet() ?: mutableSetOf()
        registeredEmails.add("king2240ff@gmail.com")

        val list = registeredEmails.map { email ->
            val defaultHandle = email.substringBefore("@")
            val name = prefs.getString("channel_name_$email", null) ?: "$defaultHandle Official"
            val isSuspended = prefs.getBoolean("channel_suspended_$email", false)
            val pending = prefs.getFloat("pending_withdrawal_$email", 0f)

            val matches = (videosList.value + shortsList.value).filter {
                it.creatorName.equals(defaultHandle, ignoreCase = true) || it.creatorName.equals(name, ignoreCase = true)
            }
            CreatorInfo(
                email = email,
                name = name,
                isSuspended = isSuspended,
                totalViews = matches.sumOf { it.views },
                totalVideos = matches.size,
                pendingBalance = pending
            )
        }.toMutableList()

        if (list.none { it.email == "vashmax_vfx@gmail.com" }) {
            list.add(CreatorInfo(
                email = "vashmax_vfx@gmail.com",
                name = "VashMax Creator",
                isSuspended = prefs.getBoolean("channel_suspended_vashmax_vfx@gmail.com", false),
                totalViews = 15400,
                totalVideos = 2,
                pendingBalance = prefs.getFloat("pending_withdrawal_vashmax_vfx@gmail.com", 85.50f)
            ))
        }
        if (list.none { it.email == "gemini_studio@gmail.com" }) {
            list.add(CreatorInfo(
                email = "gemini_studio@gmail.com",
                name = "Gemini Studio Pro",
                isSuspended = prefs.getBoolean("channel_suspended_gemini_studio@gmail.com", false),
                totalViews = 8900,
                totalVideos = 1,
                pendingBalance = prefs.getFloat("pending_withdrawal_gemini_studio@gmail.com", 29.80f)
            ))
        }

        _adminCreators.value = list

        val reqSet = prefs.getStringSet("withdrawal_requests_list", emptySet()) ?: emptySet()
        val parsedReqs = reqSet.mapNotNull { str ->
            val parts = str.split("|")
            if (parts.size >= 7) {
                val e = parts[1]
                val handle = e.substringBefore("@")
                val n = prefs.getString("channel_name_$e", null) ?: "$handle Official"
                AdminWithdrawalRequest(
                    id = parts[0],
                    email = e,
                    name = n,
                    amount = parts[2].toFloatOrNull() ?: 0f,
                    method = parts[3],
                    details = parts[4],
                    timestamp = parts[5].toLongOrNull() ?: System.currentTimeMillis(),
                    status = parts[6]
                )
            } else null
        }.sortedByDescending { it.timestamp }

        if (parsedReqs.isEmpty()) {
            val fakeReqSet = mutableSetOf(
                "w102|vashmax_vfx@gmail.com|85.50|JazzCash|03001234567|${System.currentTimeMillis() - 7200000}|PENDING",
                "w103|gemini_studio@gmail.com|29.80|CreditCard|4242-4242-4242-4242|${System.currentTimeMillis() - 18000000}|PENDING"
            )
            prefs.edit().putStringSet("withdrawal_requests_list", fakeReqSet).apply()
            _adminWithdrawalRequests.value = fakeReqSet.mapNotNull { str ->
                val parts = str.split("|")
                AdminWithdrawalRequest(parts[0], parts[1], "VashMax Creator", parts[2].toFloat(), parts[3], parts[4], parts[5].toLong(), parts[6])
            }
        } else {
            _adminWithdrawalRequests.value = parsedReqs
        }

        // Parse AdSense requests
        val adsenseRequests = registeredEmails.mapNotNull { email ->
            val status = prefs.getString("adsense_status_$email", "NOT_ELIGIBLE") ?: "NOT_ELIGIBLE"
            if (status == "ADSENSE_PENDING") {
                val defaultHandle = email.substringBefore("@")
                val name = prefs.getString("channel_name_$email", null) ?: "$defaultHandle Official"
                val subs = prefs.getInt("channel_subscribers_$email", 840)
                val watch = prefs.getInt("channel_watch_hours_$email", 314)
                AdSenseRequest(email, name, subs, watch)
            } else null
        }
        _adminAdSenseRequests.value = adsenseRequests
    }

    fun adminTakedownVideo(id: Int) {
        viewModelScope.launch {
            deleteVideoById(id)
        }
    }

    fun deleteVideoById(id: Int) {
        viewModelScope.launch {
            videoDao.deleteVideoById(id)
        }
    }

    // Screen navigation state
    private val _currentScreen = MutableStateFlow("onboarding")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Categories filter
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Active Video play state
    private val _activeVideo = MutableStateFlow<VideoEntity?>(null)
    val activeVideo: StateFlow<VideoEntity?> = _activeVideo.asStateFlow()

    // User Session
    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser.asStateFlow()

    // Liked status local state
    val videosList: StateFlow<List<VideoEntity>> = videoDao.getAllVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shortsList: StateFlow<List<VideoEntity>> = videoDao.getShorts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ghostUploads: StateFlow<List<VideoEntity>> = videoDao.getGhostUploads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val battlesList: StateFlow<List<BattleEntity>> = battleDao.getAllBattles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Comments for active video
    private val _activeComments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val activeComments: StateFlow<List<CommentEntity>> = _activeComments.asStateFlow()

    // AI Operation states
    private val _aiSeoResult = MutableStateFlow("")
    val aiSeoResult: StateFlow<String> = _aiSeoResult.asStateFlow()

    private val _aiThumbnailResult = MutableStateFlow<String?>(null)
    val aiThumbnailResult: StateFlow<String?> = _aiThumbnailResult.asStateFlow()

    private val _aiCaptionsResult = MutableStateFlow("")
    val aiCaptionsResult: StateFlow<String> = _aiCaptionsResult.asStateFlow()

    private val _voiceSearchRecognized = MutableStateFlow("")
    val voiceSearchRecognized: StateFlow<String> = _voiceSearchRecognized.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    init {
        // Collect current user changes to dynamically adjust channel info
        viewModelScope.launch {
            _currentUser.collect { email ->
                if (email != null) {
                    checkChannelStateForUser(email)
                }
            }
        }

        // Pre-populate Database if empty
        viewModelScope.launch {
            videosList.collectIndexed { index, list ->
                if (index == 0 && list.isEmpty()) {
                    populateInitialData()
                }
            }
        }

        // Dynamic views and earnings simulation ticker for interactive real-time earning showcase
        viewModelScope.launch {
            while (true) {
                delay(3500) // Updates every 3.5 seconds
                val user = _currentUser.value ?: "king2240ff@gmail.com"
                val handle = user.substringBefore("@")
                val cName = _channelName.value ?: handle
                
                // Fetch and update user's own videos to simulated active organic traffic
                videosList.value.forEach { video ->
                    if (video.creatorName.equals(handle, ignoreCase = true) || video.creatorName.equals(cName, ignoreCase = true)) {
                        val viewIncrease = (25..160).random()
                        val updatedVideo = video.copy(
                            views = video.views + viewIncrease,
                            likes = video.likes + (viewIncrease / 12).coerceAtLeast(1)
                        )
                        videoDao.updateVideo(updatedVideo)
                    }
                }

                // Fetch and update user's own reels/shorts
                shortsList.value.forEach { short ->
                    if (short.creatorName.equals(handle, ignoreCase = true) || short.creatorName.equals(cName, ignoreCase = true)) {
                        val viewIncrease = (60..280).random()
                        val updatedVideo = short.copy(
                            views = short.views + viewIncrease,
                            likes = short.likes + (viewIncrease / 10).coerceAtLeast(1)
                        )
                        videoDao.updateVideo(updatedVideo)
                    }
                }
            }
        }
    }

    private suspend fun populateInitialData() {
        val initialVideos = listOf(
            VideoEntity(
                title = "UMAIRA Ultra Cinematic Trailer VFX (8K)",
                creatorName = "VashMax Creator",
                creatorAvatar = "V",
                views = 15400,
                likes = 1240,
                type = "VIDEO",
                duration = "02:45",
                category = "Trending",
                auraScore = 250,
                viralityChance = 94,
                captions = "The future has arrived. Experience ultimate entertainment...",
                videoResUrl = "trailer_1"
            ),
            VideoEntity(
                title = "How AI is changing Android Codebases Forever",
                creatorName = "Gemini Studio Pro",
                creatorAvatar = "G",
                views = 8900,
                likes = 612,
                type = "VIDEO",
                duration = "14:10",
                category = "AI",
                auraScore = 180,
                viralityChance = 82,
                captions = "Welcome back, today we are using cutting edge generative AI models inside mobile SDKs.",
                videoResUrl = "tech_1"
            ),
            VideoEntity(
                title = "Battle Mode: Final Clash highlights of the season",
                creatorName = "Arena Network",
                creatorAvatar = "A",
                views = 23900,
                likes = 3450,
                type = "VIDEO",
                duration = "11:55",
                category = "Gaming",
                auraScore = 320,
                viralityChance = 88,
                videoResUrl = "gaming_1"
            ),
            VideoEntity(
                title = "UMAIRA TUBE Anthem - Pure Bass Beats (Dolby Atmos)",
                creatorName = "Cyber Beats UK",
                creatorAvatar = "C",
                views = 45000,
                likes = 8900,
                type = "VIDEO",
                duration = "03:15",
                category = "Music",
                auraScore = 410,
                viralityChance = 97,
                videoResUrl = "music_1"
            ),
            // Shorts/Reels
            VideoEntity(
                title = "Unbelievable VFX Transition! 🤯",
                creatorName = "VashMax Creator",
                creatorAvatar = "V",
                views = 85200,
                likes = 14200,
                type = "SHORT",
                duration = "0:15",
                category = "Trending",
                auraScore = 150,
                viralityChance = 91,
                videoResUrl = "short_1"
            ),
            VideoEntity(
                title = "AI generated high-fidelity asset models",
                creatorName = "Gemini Studio Pro",
                creatorAvatar = "G",
                views = 61100,
                likes = 9420,
                type = "SHORT",
                duration = "0:25",
                category = "AI",
                auraScore = 110,
                viralityChance = 74,
                videoResUrl = "short_2"
            ),
            VideoEntity(
                title = "Epic Fortnite 360 Headshot No Scope!",
                creatorName = "NoobHunter",
                creatorAvatar = "N",
                views = 124000,
                likes = 23100,
                type = "SHORT",
                duration = "0:18",
                category = "Gaming",
                auraScore = 550,
                viralityChance = 99,
                videoResUrl = "short_3"
            )
        )

        for (v in initialVideos) {
            videoDao.insertVideo(v)
        }

        // Populating sample comment entries
        commentDao.insertComment(CommentEntity(videoId = 1, author = "KingCoder", content = "This VFX is absolutely breathtaking! Best video platform ever."))
        commentDao.insertComment(CommentEntity(videoId = 1, author = "SuperAura", content = "Unbelievable response speed. The transition logic is flawless."))
        commentDao.insertComment(CommentEntity(videoId = 2, author = "AndroidDev", content = "So glad to see native Kotlin tutorials detailing true client integrations."))

        // Prepopulate Battle match-ups
        battleDao.insertBattle(
            BattleEntity(
                creatorAName = "Cyber Beats UK",
                creatorAAvatar = "C",
                creatorAVideoTitle = "Tech Beats Live",
                creatorAVotes = 148,
                creatorBName = "Studio Synth",
                creatorBAvatar = "S",
                creatorBVideoTitle = "Deep Synthesizer Vibe",
                creatorBVotes = 135
            )
        )
        battleDao.insertBattle(
            BattleEntity(
                creatorAName = "VashMax Creator",
                creatorAAvatar = "V",
                creatorAVideoTitle = "Extreme 3D Hologram",
                creatorAVotes = 224,
                creatorBName = "Gemini Studio Pro",
                creatorBAvatar = "G",
                creatorBVideoTitle = "Neural Interface Art",
                creatorBVotes = 241
            )
        )
    }

    fun setScreen(screenName: String) {
        _currentScreen.value = screenName
    }

    fun loginUser(email: String) {
        _currentUser.value = email
        _currentScreen.value = "dashboard"
    }

    fun logout() {
        _currentUser.value = null
        _currentScreen.value = "onboarding"
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectVideo(video: VideoEntity) {
        _activeVideo.value = video
        // Live load comments
        viewModelScope.launch {
            commentDao.getCommentsForVideo(video.id).collect { comments ->
                _activeComments.value = comments
            }
        }
    }

    fun closeActiveVideo() {
        _activeVideo.value = null
        _aiCaptionsResult.value = ""
    }

    fun toggleLike(video: VideoEntity) {
        viewModelScope.launch {
            val isCurrentlyLiked = video.isLiked
            val addition = if (!isCurrentlyLiked) 1 else -1
            videoDao.updateLike(video.id, !isCurrentlyLiked, addition)
            _activeVideo.value = videoDao.getVideoById(video.id)
        }
    }

    fun toggleSubscription(creatorName: String, isSubscribedNow: Boolean) {
        viewModelScope.launch {
            videoDao.updateSubscription(creatorName, !isSubscribedNow)
            _activeVideo.value?.let { v ->
                if (v.creatorName == creatorName) {
                    _activeVideo.value = v.copy(isSubscribed = !isSubscribedNow)
                }
            }
        }
    }

    fun addComment(videoId: Int, text: String) {
        val user = _currentUser.value ?: "GuestCreator"
        viewModelScope.launch {
            commentDao.insertComment(
                CommentEntity(videoId = videoId, author = user, content = text)
            )
            // Refresh comments manually to guarantee reactive refresh UI
            commentDao.getCommentsForVideo(videoId).firstOrNull()?.let {
                _activeComments.value = it
            }
        }
    }

    // AI Core Functions using real Gemini API
    fun runAiSeoOptimizer(title: String, description: String = "") {
        viewModelScope.launch {
            _isAiLoading.value = true
            val prompt = """
                Recommend 5 trending, high SEO rank search tags, an optimized catchy youtube title, and a professional video description for a video.
                Video Title: "$title"
                Video Description: "$description"
                Format the result with a neat Black + Crimson style design context.
            """.trimIndent()
            
            val systemPrompt = "You are an expert AI Video Growth Manager for UMAIRA Tube. Output concise, actionable, tags and viral titles."
            val result = geminiRepository.generateContent(prompt, systemPrompt)
            _aiSeoResult.value = result
            _isAiLoading.value = false
        }
    }

    fun runAiThumbnailGenerator(title: String, style: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val prompt = """
                Propose a complete graphic design composition concept for a 3D overlay thumbnail for UMAIRA Tube.
                Video Title: "$title"
                Thumbnail Visual Style requested: "$style"
                Specify overlay text, background colors (primarily black/crimson shades), and focal 3D icons to represent.
            """.trimIndent()
            
            val result = geminiRepository.generateContent(prompt, "You are a senior graphic designer specializing in 3D gaming, tech and entertainment content thumbnails.")
            _aiThumbnailResult.value = result
            _isAiLoading.value = false
        }
    }

    fun runVoiceSearch(queryText: String) {
        _voiceSearchRecognized.value = queryText
        _searchQuery.value = queryText
    }

    fun generateVideoCaptions(video: VideoEntity) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val prompt = """
                Generate a sequence of real-time audio captions (first 30 seconds) for a movie/video titled "${video.title}" by "${video.creatorName}" category "${video.category}".
                Create a realistic, engaging dialogue subtitles sheet format.
            """.trimIndent()

            val captions = geminiRepository.generateContent(prompt, "You are an automated captioning transcription engine.")
            _aiCaptionsResult.value = captions
            videoDao.updateCaptions(video.id, captions)
            _isAiLoading.value = false
        }
    }

    // Battle Mode Voting Logic
    fun castBattleVote(battle: BattleEntity, votedFor: String) {
        viewModelScope.launch {
            val voteAIncrement = if (votedFor == "A") 1 else 0
            val voteBIncrement = if (votedFor == "B") 1 else 0
            
            val updatedBattle = battle.copy(
                creatorAVotes = battle.creatorAVotes + voteAIncrement,
                creatorBVotes = battle.creatorBVotes + voteBIncrement,
                hasVoted = votedFor
            )
            battleDao.updateBattle(updatedBattle)
        }
    }

    // Creator Studio: Ghost/Scheduled Upload
    fun createGhostUpload(title: String, creatorName: String, type: String, category: String, scheduledDelayHours: Int, viralityScorePercent: Int) {
        viewModelScope.launch {
            val currentMillis = System.currentTimeMillis()
            val scheduledTime = currentMillis + (scheduledDelayHours * 3600 * 1000L)
            
            val ghostVideo = VideoEntity(
                title = title,
                creatorName = creatorName,
                creatorAvatar = if (creatorName.isNotEmpty()) creatorName.first().toString() else "U",
                views = 0,
                likes = 0,
                type = type,
                duration = if (type == "SHORT") "0:30" else "08:15",
                category = category,
                auraScore = 150,
                viralityChance = viralityScorePercent,
                isGhostUpload = true,
                scheduledTime = scheduledTime,
                videoResUrl = "ghost_${UUID.randomUUID().toString().take(4)}"
            )
            videoDao.insertVideo(ghostVideo)
        }
    }

    // Release/Trigger hidden upload for live view immediately
    fun releaseGhostUpload(video: VideoEntity) {
        viewModelScope.launch {
            val releasedVideo = video.copy(
                isGhostUpload = false,
                createdAt = System.currentTimeMillis() // Bump to top of feed
            )
            videoDao.updateVideo(releasedVideo)
        }
    }

    // Direct channel publish for real-time video upload
    fun uploadUserVideo(title: String, type: String, category: String) {
        viewModelScope.launch {
            val email = _currentUser.value ?: "king2240ff@gmail.com"
            val handle = email.substringBefore("@")
            val cName = _channelName.value ?: handle

            // Moderation check: No adult keywords
            val lowercaseTitle = title.lowercase()
            val lowercaseCategory = category.lowercase()
            val banKeywords = listOf("xxx", "sexi", "sexy", "porn", "naked", "erotic", "adult", "unclothed", "sex")
            val hasViolation = banKeywords.any { lowercaseTitle.contains(it) || lowercaseCategory.contains(it) }

            if (hasViolation) {
                // Instantly suspend/delete channel!
                deleteAndSuspendChannel(email)
                return@launch
            }

            val startViews = (250..950).random()
            val newVideo = VideoEntity(
                title = title,
                creatorName = cName,
                creatorAvatar = if (cName.isNotEmpty()) cName.first().toString().uppercase() else "K",
                views = startViews,
                likes = (startViews / 15).coerceAtLeast(1),
                type = type,
                duration = if (type == "SHORT") "0:35" else "07:45",
                category = category,
                auraScore = 120,
                viralityChance = (65..98).random(),
                isGhostUpload = false,
                videoResUrl = "user_${UUID.randomUUID().toString().take(4)}"
            )
            videoDao.insertVideo(newVideo)
        }
    }
}
