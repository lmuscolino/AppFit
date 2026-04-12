package com.appfit.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDate;
import java.time.LocalTime;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004J\u0006\u0010\u0005\u001a\u00020\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n\u00a8\u0006\u000b"}, d2 = {"Lcom/appfit/data/model/MealType;", "", "(Ljava/lang/String;I)V", "displayName", "", "order", "", "BREAKFAST", "LUNCH", "DINNER", "SNACK", "app_debug"})
public enum MealType {
    /*public static final*/ BREAKFAST /* = new BREAKFAST() */,
    /*public static final*/ LUNCH /* = new LUNCH() */,
    /*public static final*/ DINNER /* = new DINNER() */,
    /*public static final*/ SNACK /* = new SNACK() */;
    
    MealType() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String displayName() {
        return null;
    }
    
    public final int order() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.appfit.data.model.MealType> getEntries() {
        return null;
    }
}