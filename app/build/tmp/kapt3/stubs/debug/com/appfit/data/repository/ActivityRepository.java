package com.appfit.data.repository;

import com.appfit.data.local.dao.ActivityDao;
import com.appfit.data.model.Activity;
import kotlinx.coroutines.flow.Flow;
import java.time.LocalDate;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\u001a\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f0\u000b2\u0006\u0010\u000e\u001a\u00020\u000fJ\"\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f0\u000b2\u0006\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u000fJ\u0018\u0010\u0013\u001a\u0004\u0018\u00010\r2\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\"\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\f0\u000b2\u0006\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u000fJ\u001a\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f0\u000b2\u0006\u0010\u0016\u001a\u00020\u000fJ\u0016\u0010\u0017\u001a\u00020\b2\u0006\u0010\u0018\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010\u0019J\u001e\u0010\u001a\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u001b\u001a\u00020\u001cH\u0086@\u00a2\u0006\u0002\u0010\u001dJ\u0016\u0010\u001e\u001a\u00020\u00062\u0006\u0010\u0018\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010\u0019R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2 = {"Lcom/appfit/data/repository/ActivityRepository;", "", "dao", "Lcom/appfit/data/local/dao/ActivityDao;", "(Lcom/appfit/data/local/dao/ActivityDao;)V", "deleteActivity", "", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getActivitiesForDate", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/appfit/data/model/Activity;", "date", "Ljava/time/LocalDate;", "getActivitiesForRange", "start", "end", "getActivityById", "getDatesWithActivities", "getUpcomingActivitiesWithTime", "from", "insertActivity", "activity", "(Lcom/appfit/data/model/Activity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setCompleted", "completed", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateActivity", "app_debug"})
public final class ActivityRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.data.local.dao.ActivityDao dao = null;
    
    @javax.inject.Inject()
    public ActivityRepository(@org.jetbrains.annotations.NotNull()
    com.appfit.data.local.dao.ActivityDao dao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.appfit.data.model.Activity>> getActivitiesForDate(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate date) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.appfit.data.model.Activity>> getActivitiesForRange(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate start, @org.jetbrains.annotations.NotNull()
    java.time.LocalDate end) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getActivityById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.appfit.data.model.Activity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.appfit.data.model.Activity>> getUpcomingActivitiesWithTime(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate from) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertActivity(@org.jetbrains.annotations.NotNull()
    com.appfit.data.model.Activity activity, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateActivity(@org.jetbrains.annotations.NotNull()
    com.appfit.data.model.Activity activity, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setCompleted(long id, boolean completed, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteActivity(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<java.time.LocalDate>> getDatesWithActivities(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate start, @org.jetbrains.annotations.NotNull()
    java.time.LocalDate end) {
        return null;
    }
}