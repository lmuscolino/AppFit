package com.appfit.data.local.dao;

import androidx.room.*;
import com.appfit.data.model.Activity;
import kotlinx.coroutines.flow.Flow;
import java.time.LocalDate;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\b2\u0006\u0010\u000b\u001a\u00020\fH\'J$\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\b2\u0006\u0010\u000e\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\fH\'J\u0018\u0010\u0010\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J$\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\t0\b2\u0006\u0010\u000e\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\fH\'J\u001c\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\b2\u0006\u0010\u0013\u001a\u00020\fH\'J\u0016\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0015\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u001e\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0018\u001a\u00020\u0019H\u00a7@\u00a2\u0006\u0002\u0010\u001aJ\u0016\u0010\u001b\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u0016\u00a8\u0006\u001c"}, d2 = {"Lcom/appfit/data/local/dao/ActivityDao;", "", "deleteActivity", "", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getActivitiesForDate", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/appfit/data/model/Activity;", "date", "Ljava/time/LocalDate;", "getActivitiesForRange", "start", "end", "getActivityById", "getDatesWithActivities", "getUpcomingActivitiesWithTime", "from", "insertActivity", "activity", "(Lcom/appfit/data/model/Activity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setCompleted", "completed", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateActivity", "app_debug"})
@androidx.room.Dao()
public abstract interface ActivityDao {
    
    @androidx.room.Query(value = "SELECT * FROM activities WHERE scheduledDate = :date ORDER BY scheduledTime ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.appfit.data.model.Activity>> getActivitiesForDate(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate date);
    
    @androidx.room.Query(value = "SELECT * FROM activities WHERE scheduledDate BETWEEN :start AND :end ORDER BY scheduledDate ASC, scheduledTime ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.appfit.data.model.Activity>> getActivitiesForRange(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate start, @org.jetbrains.annotations.NotNull()
    java.time.LocalDate end);
    
    @androidx.room.Query(value = "SELECT * FROM activities WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getActivityById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.appfit.data.model.Activity> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM activities WHERE scheduledDate >= :from AND scheduledTime IS NOT NULL AND isCompleted = 0 ORDER BY scheduledDate ASC, scheduledTime ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.appfit.data.model.Activity>> getUpcomingActivitiesWithTime(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate from);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertActivity(@org.jetbrains.annotations.NotNull()
    com.appfit.data.model.Activity activity, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateActivity(@org.jetbrains.annotations.NotNull()
    com.appfit.data.model.Activity activity, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE activities SET isCompleted = :completed WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setCompleted(long id, boolean completed, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM activities WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteActivity(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT DISTINCT scheduledDate FROM activities WHERE scheduledDate BETWEEN :start AND :end")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<java.time.LocalDate>> getDatesWithActivities(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate start, @org.jetbrains.annotations.NotNull()
    java.time.LocalDate end);
}