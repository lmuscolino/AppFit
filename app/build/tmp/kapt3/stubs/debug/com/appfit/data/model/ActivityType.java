package com.appfit.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n\u00a8\u0006\u000b"}, d2 = {"Lcom/appfit/data/model/ActivityType;", "", "(Ljava/lang/String;I)V", "displayName", "", "CARDIO", "STRENGTH", "FLEXIBILITY", "YOGA", "REST", "CUSTOM", "app_debug"})
public enum ActivityType {
    /*public static final*/ CARDIO /* = new CARDIO() */,
    /*public static final*/ STRENGTH /* = new STRENGTH() */,
    /*public static final*/ FLEXIBILITY /* = new FLEXIBILITY() */,
    /*public static final*/ YOGA /* = new YOGA() */,
    /*public static final*/ REST /* = new REST() */,
    /*public static final*/ CUSTOM /* = new CUSTOM() */;
    
    ActivityType() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String displayName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.appfit.data.model.ActivityType> getEntries() {
        return null;
    }
}