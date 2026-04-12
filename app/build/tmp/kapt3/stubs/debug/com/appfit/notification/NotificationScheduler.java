package com.appfit.notification;

import androidx.work.*;
import com.appfit.data.repository.ActivityRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\u000b\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\fJ&\u0010\r\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/appfit/notification/NotificationScheduler;", "", "workManager", "Landroidx/work/WorkManager;", "activityRepository", "Lcom/appfit/data/repository/ActivityRepository;", "(Landroidx/work/WorkManager;Lcom/appfit/data/repository/ActivityRepository;)V", "cancelReminder", "", "activityId", "", "rescheduleAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "scheduleReminder", "title", "", "durationMinutes", "", "reminderAt", "Ljava/time/LocalDateTime;", "Companion", "app_debug"})
public final class NotificationScheduler {
    @org.jetbrains.annotations.NotNull()
    private final androidx.work.WorkManager workManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.data.repository.ActivityRepository activityRepository = null;
    private static final long REMINDER_ADVANCE_MINUTES = 30L;
    @org.jetbrains.annotations.NotNull()
    public static final com.appfit.notification.NotificationScheduler.Companion Companion = null;
    
    @javax.inject.Inject()
    public NotificationScheduler(@org.jetbrains.annotations.NotNull()
    androidx.work.WorkManager workManager, @org.jetbrains.annotations.NotNull()
    com.appfit.data.repository.ActivityRepository activityRepository) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object rescheduleAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void scheduleReminder(long activityId, @org.jetbrains.annotations.NotNull()
    java.lang.String title, int durationMinutes, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime reminderAt) {
    }
    
    public final void cancelReminder(long activityId) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/appfit/notification/NotificationScheduler$Companion;", "", "()V", "REMINDER_ADVANCE_MINUTES", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}