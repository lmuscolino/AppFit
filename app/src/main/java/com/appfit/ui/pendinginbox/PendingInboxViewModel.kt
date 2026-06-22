package com.appfit.ui.pendinginbox

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import android.util.Log
import com.appfit.ai.GoogleCalendarService
import com.appfit.ai.GoogleMailProvider
import com.appfit.data.model.*
import com.appfit.data.repository.ActivityRepository
import com.appfit.data.repository.DietRepository
import com.appfit.data.repository.PendingEmailRepository
import com.appfit.data.repository.ReminderRepository
import com.appfit.notification.GmailScanWorker
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class PendingInboxViewModel @Inject constructor(
    private val pendingEmailRepository: PendingEmailRepository,
    private val activityRepository: ActivityRepository,
    private val dietRepository: DietRepository,
    private val reminderRepository: ReminderRepository,
    private val googleCalendarService: GoogleCalendarService,
    private val googleMailProvider: GoogleMailProvider,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val workManager = WorkManager.getInstance(context)

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    fun triggerScanNow() {
        viewModelScope.launch { googleMailProvider.clearLastScanTimestamp() }
        val request = OneTimeWorkRequestBuilder<GmailScanWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        workManager.enqueueUniqueWork(
            "gmail_scan_manual",
            ExistingWorkPolicy.REPLACE,
            request
        )
        _scanState.value = ScanState.Running

        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(request.id).collect { info ->
                when (info?.state) {
                    WorkInfo.State.SUCCEEDED -> _scanState.value = ScanState.Done
                    WorkInfo.State.FAILED    -> _scanState.value = ScanState.Failed
                    WorkInfo.State.CANCELLED -> _scanState.value = ScanState.Idle
                    else -> {}
                }
            }
        }
    }

    fun resetScanState() { _scanState.value = ScanState.Idle }

    sealed class ScanState {
        object Idle    : ScanState()
        object Running : ScanState()
        object Done    : ScanState()
        object Failed  : ScanState()
    }

    val pendingItems: StateFlow<List<PendingEmailItem>> = pendingEmailRepository.getPendingItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingCount: StateFlow<Int> = pendingEmailRepository.getPendingCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun approve(item: PendingEmailItem, date: LocalDate, time: LocalTime?) {
        viewModelScope.launch {
            try {
                val payload = JsonParser.parseString(item.payloadJson).asJsonObject
                when (item.itemType) {
                    PendingItemType.ACTIVITY -> {
                        val typeStr = payload.get("type")?.asString ?: "CUSTOM"
                        val type = try { ActivityType.valueOf(typeStr) } catch (e: Exception) { ActivityType.CUSTOM }
                        val activity = Activity(
                            title = payload.get("title")?.asString ?: item.title,
                            description = payload.get("description")?.asString ?: item.aiReason,
                            type = type,
                            durationMinutes = payload.get("duration_minutes")?.asInt ?: 60,
                            scheduledDate = date,
                            scheduledTime = time,
                            caloriesBurned = payload.get("calories_burned")?.asInt ?: 0,
                            aiGenerated = true
                        )
                        Log.d("PendingInbox", "approve ACTIVITY: '${activity.title}' il $date alle $time")
                        val id = activityRepository.insertActivity(activity)
                        Log.d("PendingInbox", "  insertActivity id=$id")
                        val eventId = googleCalendarService.syncActivity(activity.copy(id = id))
                        Log.d("PendingInbox", "  syncActivity eventId=$eventId")
                    }
                    PendingItemType.REMINDER, PendingItemType.TODO -> {
                        val categoryStr = payload.get("category")?.asString ?: "OTHER"
                        val category = try { ReminderCategory.valueOf(categoryStr) } catch (e: Exception) { ReminderCategory.OTHER }
                        val reminder = Reminder(
                            title = payload.get("title")?.asString ?: item.title,
                            description = payload.get("description")?.asString ?: item.aiReason,
                            category = category,
                            dueDate = date,
                            amount = payload.get("amount")?.takeIf { !it.isJsonNull }?.asFloat,
                            isImportant = payload.get("is_important")?.asBoolean ?: false,
                            sourceEmail = item.sourceEmailSubject
                        )
                        val reminderId = reminderRepository.insertReminder(reminder)
                        Log.d("PendingInbox", "  insertReminder id=$reminderId, scadenza=$date")
                        googleCalendarService.syncReminder(reminder.copy(id = reminderId))
                    }
                }
                pendingEmailRepository.approve(item.id)
            } catch (e: Exception) {
                Log.e("PendingInbox", "approve error: ${e::class.simpleName}: ${e.message}", e)
            }
        }
    }

    fun reject(item: PendingEmailItem) {
        viewModelScope.launch { pendingEmailRepository.reject(item.id) }
    }

    fun clearProcessed() {
        viewModelScope.launch { pendingEmailRepository.deleteProcessed() }
    }
}
