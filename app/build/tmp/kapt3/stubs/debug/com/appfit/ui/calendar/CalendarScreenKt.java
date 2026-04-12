package com.appfit.ui.calendar;

import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextDecoration;
import com.appfit.data.model.Activity;
import com.appfit.data.model.ActivityType;
import com.appfit.data.model.Meal;
import com.appfit.data.model.MealType;
import com.appfit.ui.common.UiState;
import com.appfit.ui.theme.*;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.DayPosition;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000T\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\u001a,\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a.\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a&\u0010\r\u001a\u00020\u00012\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00010\u000f2\b\b\u0002\u0010\u0011\u001a\u00020\u0012H\u0007\u001a \u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u0015H\u0003\u001a\u0010\u0010\u0018\u001a\u00020\u00012\u0006\u0010\u0019\u001a\u00020\u001aH\u0003\u001a\u0010\u0010\u001b\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020\u0015H\u0003\u001a\u001e\u0010\u001d\u001a\u00020\u00012\u0006\u0010\u001e\u001a\u00020\u001f2\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a\u0018\u0010!\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020\u00152\u0006\u0010#\u001a\u00020$H\u0003\u00a8\u0006%"}, d2 = {"ActivityCard", "", "activity", "Lcom/appfit/data/model/Activity;", "onClick", "Lkotlin/Function0;", "onToggleComplete", "CalendarDay", "day", "Lcom/kizitonwose/calendar/core/CalendarDay;", "isSelected", "", "hasContent", "CalendarScreen", "onActivityClick", "Lkotlin/Function1;", "", "viewModel", "Lcom/appfit/ui/calendar/CalendarViewModel;", "CalorieItem", "label", "", "value", "unit", "CalorieSummaryCard", "plan", "Lcom/appfit/data/model/DailyPlan;", "EmptyStateCard", "text", "MealCard", "meal", "Lcom/appfit/data/model/Meal;", "onToggleConsumed", "SectionHeader", "title", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "app_debug"})
public final class CalendarScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void CalendarScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onActivityClick, @org.jetbrains.annotations.NotNull()
    com.appfit.ui.calendar.CalendarViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CalendarDay(com.kizitonwose.calendar.core.CalendarDay day, boolean isSelected, boolean hasContent, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ActivityCard(@org.jetbrains.annotations.NotNull()
    com.appfit.data.model.Activity activity, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onToggleComplete) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void MealCard(@org.jetbrains.annotations.NotNull()
    com.appfit.data.model.Meal meal, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onToggleConsumed) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CalorieSummaryCard(com.appfit.data.model.DailyPlan plan) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CalorieItem(java.lang.String label, java.lang.String value, java.lang.String unit) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SectionHeader(java.lang.String title, androidx.compose.ui.graphics.vector.ImageVector icon) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void EmptyStateCard(java.lang.String text) {
    }
}