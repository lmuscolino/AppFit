package com.appfit.ui.navigation;

import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.ui.graphics.vector.ImageVector;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0007\u0007\b\t\n\u000b\f\rB\u000f\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u0082\u0001\u0007\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u00a8\u0006\u0015"}, d2 = {"Lcom/appfit/ui/navigation/Screen;", "", "route", "", "(Ljava/lang/String;)V", "getRoute", "()Ljava/lang/String;", "ActivityDetail", "Calendar", "Chat", "Diet", "MealDetail", "Onboarding", "Shopping", "Lcom/appfit/ui/navigation/Screen$ActivityDetail;", "Lcom/appfit/ui/navigation/Screen$Calendar;", "Lcom/appfit/ui/navigation/Screen$Chat;", "Lcom/appfit/ui/navigation/Screen$Diet;", "Lcom/appfit/ui/navigation/Screen$MealDetail;", "Lcom/appfit/ui/navigation/Screen$Onboarding;", "Lcom/appfit/ui/navigation/Screen$Shopping;", "app_debug"})
public abstract class Screen {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String route = null;
    
    private Screen(java.lang.String route) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRoute() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/appfit/ui/navigation/Screen$ActivityDetail;", "Lcom/appfit/ui/navigation/Screen;", "()V", "createRoute", "", "id", "", "app_debug"})
    public static final class ActivityDetail extends com.appfit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.appfit.ui.navigation.Screen.ActivityDetail INSTANCE = null;
        
        private ActivityDetail() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String createRoute(long id) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/appfit/ui/navigation/Screen$Calendar;", "Lcom/appfit/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Calendar extends com.appfit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.appfit.ui.navigation.Screen.Calendar INSTANCE = null;
        
        private Calendar() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/appfit/ui/navigation/Screen$Chat;", "Lcom/appfit/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Chat extends com.appfit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.appfit.ui.navigation.Screen.Chat INSTANCE = null;
        
        private Chat() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/appfit/ui/navigation/Screen$Diet;", "Lcom/appfit/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Diet extends com.appfit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.appfit.ui.navigation.Screen.Diet INSTANCE = null;
        
        private Diet() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/appfit/ui/navigation/Screen$MealDetail;", "Lcom/appfit/ui/navigation/Screen;", "()V", "createRoute", "", "id", "", "app_debug"})
    public static final class MealDetail extends com.appfit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.appfit.ui.navigation.Screen.MealDetail INSTANCE = null;
        
        private MealDetail() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String createRoute(long id) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/appfit/ui/navigation/Screen$Onboarding;", "Lcom/appfit/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Onboarding extends com.appfit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.appfit.ui.navigation.Screen.Onboarding INSTANCE = null;
        
        private Onboarding() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/appfit/ui/navigation/Screen$Shopping;", "Lcom/appfit/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Shopping extends com.appfit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.appfit.ui.navigation.Screen.Shopping INSTANCE = null;
        
        private Shopping() {
        }
    }
}