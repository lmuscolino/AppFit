package com.appfit.ui.diet;

import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import com.appfit.data.model.DailyPlan;
import com.appfit.data.model.Meal;
import com.appfit.data.model.MealType;
import com.appfit.ui.common.UiState;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000&\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0007\u001a\u0010\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0006H\u0003\u001a\"\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\u00062\b\u0010\n\u001a\u0004\u0018\u00010\u0006H\u0003\u001a\u0010\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\rH\u0003\u001a\u0010\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u0010H\u0003\u00a8\u0006\u0011"}, d2 = {"DietScreen", "", "viewModel", "Lcom/appfit/ui/diet/DietViewModel;", "MacroChip", "text", "", "MacroItem", "name", "value", "goal", "MacroSummaryCard", "plan", "Lcom/appfit/data/model/DailyPlan;", "MealDetailCard", "meal", "Lcom/appfit/data/model/Meal;", "app_debug"})
public final class DietScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void DietScreen(@org.jetbrains.annotations.NotNull()
    com.appfit.ui.diet.DietViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MacroSummaryCard(com.appfit.data.model.DailyPlan plan) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MacroItem(java.lang.String name, java.lang.String value, java.lang.String goal) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MealDetailCard(com.appfit.data.model.Meal meal) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MacroChip(java.lang.String text) {
    }
}