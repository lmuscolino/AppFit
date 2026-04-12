package com.appfit;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;
import com.appfit.ai.AiDebugLogger;
import com.appfit.ai.AiService;
import com.appfit.ai.AnthropicService;
import com.appfit.ai.ApiKeyProvider;
import com.appfit.ai.ClaudeToolExecutor;
import com.appfit.ai.GeminiService;
import com.appfit.ai.ToolApprovalManager;
import com.appfit.ai.UserProfileProvider;
import com.appfit.data.local.AppDatabase;
import com.appfit.data.local.dao.ActivityDao;
import com.appfit.data.local.dao.ChatMessageDao;
import com.appfit.data.local.dao.DietPlanDao;
import com.appfit.data.local.dao.MealDao;
import com.appfit.data.local.dao.ShoppingItemDao;
import com.appfit.data.repository.ActivityRepository;
import com.appfit.data.repository.ChatRepository;
import com.appfit.data.repository.DietRepository;
import com.appfit.data.repository.ShoppingRepository;
import com.appfit.di.AiModule_ProvideAiServiceFactory;
import com.appfit.di.AiModule_ProvideAnthropicServiceFactory;
import com.appfit.di.AiModule_ProvideGeminiServiceFactory;
import com.appfit.di.AppModule_ProvideDataStoreFactory;
import com.appfit.di.DatabaseModule_ProvideActivityDaoFactory;
import com.appfit.di.DatabaseModule_ProvideChatMessageDaoFactory;
import com.appfit.di.DatabaseModule_ProvideDatabaseFactory;
import com.appfit.di.DatabaseModule_ProvideDietPlanDaoFactory;
import com.appfit.di.DatabaseModule_ProvideMealDaoFactory;
import com.appfit.di.DatabaseModule_ProvideShoppingItemDaoFactory;
import com.appfit.di.WorkManagerModule_ProvideWorkManagerFactory;
import com.appfit.domain.usecase.GenerateShoppingListUseCase;
import com.appfit.domain.usecase.GetDailyPlanUseCase;
import com.appfit.domain.usecase.SendChatMessageUseCase;
import com.appfit.notification.ActivityReminderWorker;
import com.appfit.notification.ActivityReminderWorker_AssistedFactory;
import com.appfit.notification.DailyCheckWorker;
import com.appfit.notification.DailyCheckWorker_AssistedFactory;
import com.appfit.notification.MonthlyUpdateWorker;
import com.appfit.notification.MonthlyUpdateWorker_AssistedFactory;
import com.appfit.notification.NotificationChannels;
import com.appfit.notification.NotificationScheduler;
import com.appfit.ui.activity.ActivityDetailViewModel;
import com.appfit.ui.activity.ActivityDetailViewModel_HiltModules;
import com.appfit.ui.calendar.CalendarViewModel;
import com.appfit.ui.calendar.CalendarViewModel_HiltModules;
import com.appfit.ui.chat.ChatViewModel;
import com.appfit.ui.chat.ChatViewModel_HiltModules;
import com.appfit.ui.debug.DbViewerViewModel;
import com.appfit.ui.debug.DbViewerViewModel_HiltModules;
import com.appfit.ui.diet.DietViewModel;
import com.appfit.ui.diet.DietViewModel_HiltModules;
import com.appfit.ui.diet.MealDetailViewModel;
import com.appfit.ui.diet.MealDetailViewModel_HiltModules;
import com.appfit.ui.onboarding.OnboardingViewModel;
import com.appfit.ui.onboarding.OnboardingViewModel_HiltModules;
import com.appfit.ui.profile.ProfileViewModel;
import com.appfit.ui.profile.ProfileViewModel_HiltModules;
import com.appfit.ui.shopping.ShoppingViewModel;
import com.appfit.ui.shopping.ShoppingViewModel_HiltModules;
import com.appfit.ui.workouts.WorkoutsViewModel;
import com.appfit.ui.workouts.WorkoutsViewModel_HiltModules;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class DaggerAppFitApplication_HiltComponents_SingletonC {
  private DaggerAppFitApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public AppFitApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements AppFitApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public AppFitApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements AppFitApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public AppFitApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements AppFitApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public AppFitApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements AppFitApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public AppFitApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements AppFitApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public AppFitApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements AppFitApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public AppFitApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements AppFitApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public AppFitApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends AppFitApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends AppFitApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends AppFitApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends AppFitApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(10).put(LazyClassKeyProvider.com_appfit_ui_activity_ActivityDetailViewModel, ActivityDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_appfit_ui_calendar_CalendarViewModel, CalendarViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_appfit_ui_chat_ChatViewModel, ChatViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_appfit_ui_debug_DbViewerViewModel, DbViewerViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_appfit_ui_diet_DietViewModel, DietViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_appfit_ui_diet_MealDetailViewModel, MealDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_appfit_ui_onboarding_OnboardingViewModel, OnboardingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_appfit_ui_profile_ProfileViewModel, ProfileViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_appfit_ui_shopping_ShoppingViewModel, ShoppingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_appfit_ui_workouts_WorkoutsViewModel, WorkoutsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_appfit_ui_chat_ChatViewModel = "com.appfit.ui.chat.ChatViewModel";

      static String com_appfit_ui_diet_DietViewModel = "com.appfit.ui.diet.DietViewModel";

      static String com_appfit_ui_onboarding_OnboardingViewModel = "com.appfit.ui.onboarding.OnboardingViewModel";

      static String com_appfit_ui_profile_ProfileViewModel = "com.appfit.ui.profile.ProfileViewModel";

      static String com_appfit_ui_workouts_WorkoutsViewModel = "com.appfit.ui.workouts.WorkoutsViewModel";

      static String com_appfit_ui_activity_ActivityDetailViewModel = "com.appfit.ui.activity.ActivityDetailViewModel";

      static String com_appfit_ui_calendar_CalendarViewModel = "com.appfit.ui.calendar.CalendarViewModel";

      static String com_appfit_ui_diet_MealDetailViewModel = "com.appfit.ui.diet.MealDetailViewModel";

      static String com_appfit_ui_shopping_ShoppingViewModel = "com.appfit.ui.shopping.ShoppingViewModel";

      static String com_appfit_ui_debug_DbViewerViewModel = "com.appfit.ui.debug.DbViewerViewModel";

      @KeepFieldType
      ChatViewModel com_appfit_ui_chat_ChatViewModel2;

      @KeepFieldType
      DietViewModel com_appfit_ui_diet_DietViewModel2;

      @KeepFieldType
      OnboardingViewModel com_appfit_ui_onboarding_OnboardingViewModel2;

      @KeepFieldType
      ProfileViewModel com_appfit_ui_profile_ProfileViewModel2;

      @KeepFieldType
      WorkoutsViewModel com_appfit_ui_workouts_WorkoutsViewModel2;

      @KeepFieldType
      ActivityDetailViewModel com_appfit_ui_activity_ActivityDetailViewModel2;

      @KeepFieldType
      CalendarViewModel com_appfit_ui_calendar_CalendarViewModel2;

      @KeepFieldType
      MealDetailViewModel com_appfit_ui_diet_MealDetailViewModel2;

      @KeepFieldType
      ShoppingViewModel com_appfit_ui_shopping_ShoppingViewModel2;

      @KeepFieldType
      DbViewerViewModel com_appfit_ui_debug_DbViewerViewModel2;
    }
  }

  private static final class ViewModelCImpl extends AppFitApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<ActivityDetailViewModel> activityDetailViewModelProvider;

    private Provider<CalendarViewModel> calendarViewModelProvider;

    private Provider<ChatViewModel> chatViewModelProvider;

    private Provider<DbViewerViewModel> dbViewerViewModelProvider;

    private Provider<DietViewModel> dietViewModelProvider;

    private Provider<MealDetailViewModel> mealDetailViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<ProfileViewModel> profileViewModelProvider;

    private Provider<ShoppingViewModel> shoppingViewModelProvider;

    private Provider<WorkoutsViewModel> workoutsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private GetDailyPlanUseCase getDailyPlanUseCase() {
      return new GetDailyPlanUseCase(singletonCImpl.activityRepositoryProvider.get(), singletonCImpl.dietRepositoryProvider.get());
    }

    private SendChatMessageUseCase sendChatMessageUseCase() {
      return new SendChatMessageUseCase(singletonCImpl.chatRepositoryProvider.get(), singletonCImpl.provideAiServiceProvider.get(), singletonCImpl.claudeToolExecutorProvider.get(), singletonCImpl.notificationSchedulerProvider.get());
    }

    private GenerateShoppingListUseCase generateShoppingListUseCase() {
      return new GenerateShoppingListUseCase(singletonCImpl.dietRepositoryProvider.get(), singletonCImpl.shoppingRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.activityDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.calendarViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.chatViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.dbViewerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.dietViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.mealDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.profileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.shoppingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.workoutsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(10).put(LazyClassKeyProvider.com_appfit_ui_activity_ActivityDetailViewModel, ((Provider) activityDetailViewModelProvider)).put(LazyClassKeyProvider.com_appfit_ui_calendar_CalendarViewModel, ((Provider) calendarViewModelProvider)).put(LazyClassKeyProvider.com_appfit_ui_chat_ChatViewModel, ((Provider) chatViewModelProvider)).put(LazyClassKeyProvider.com_appfit_ui_debug_DbViewerViewModel, ((Provider) dbViewerViewModelProvider)).put(LazyClassKeyProvider.com_appfit_ui_diet_DietViewModel, ((Provider) dietViewModelProvider)).put(LazyClassKeyProvider.com_appfit_ui_diet_MealDetailViewModel, ((Provider) mealDetailViewModelProvider)).put(LazyClassKeyProvider.com_appfit_ui_onboarding_OnboardingViewModel, ((Provider) onboardingViewModelProvider)).put(LazyClassKeyProvider.com_appfit_ui_profile_ProfileViewModel, ((Provider) profileViewModelProvider)).put(LazyClassKeyProvider.com_appfit_ui_shopping_ShoppingViewModel, ((Provider) shoppingViewModelProvider)).put(LazyClassKeyProvider.com_appfit_ui_workouts_WorkoutsViewModel, ((Provider) workoutsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_appfit_ui_debug_DbViewerViewModel = "com.appfit.ui.debug.DbViewerViewModel";

      static String com_appfit_ui_activity_ActivityDetailViewModel = "com.appfit.ui.activity.ActivityDetailViewModel";

      static String com_appfit_ui_calendar_CalendarViewModel = "com.appfit.ui.calendar.CalendarViewModel";

      static String com_appfit_ui_workouts_WorkoutsViewModel = "com.appfit.ui.workouts.WorkoutsViewModel";

      static String com_appfit_ui_diet_DietViewModel = "com.appfit.ui.diet.DietViewModel";

      static String com_appfit_ui_diet_MealDetailViewModel = "com.appfit.ui.diet.MealDetailViewModel";

      static String com_appfit_ui_shopping_ShoppingViewModel = "com.appfit.ui.shopping.ShoppingViewModel";

      static String com_appfit_ui_onboarding_OnboardingViewModel = "com.appfit.ui.onboarding.OnboardingViewModel";

      static String com_appfit_ui_profile_ProfileViewModel = "com.appfit.ui.profile.ProfileViewModel";

      static String com_appfit_ui_chat_ChatViewModel = "com.appfit.ui.chat.ChatViewModel";

      @KeepFieldType
      DbViewerViewModel com_appfit_ui_debug_DbViewerViewModel2;

      @KeepFieldType
      ActivityDetailViewModel com_appfit_ui_activity_ActivityDetailViewModel2;

      @KeepFieldType
      CalendarViewModel com_appfit_ui_calendar_CalendarViewModel2;

      @KeepFieldType
      WorkoutsViewModel com_appfit_ui_workouts_WorkoutsViewModel2;

      @KeepFieldType
      DietViewModel com_appfit_ui_diet_DietViewModel2;

      @KeepFieldType
      MealDetailViewModel com_appfit_ui_diet_MealDetailViewModel2;

      @KeepFieldType
      ShoppingViewModel com_appfit_ui_shopping_ShoppingViewModel2;

      @KeepFieldType
      OnboardingViewModel com_appfit_ui_onboarding_OnboardingViewModel2;

      @KeepFieldType
      ProfileViewModel com_appfit_ui_profile_ProfileViewModel2;

      @KeepFieldType
      ChatViewModel com_appfit_ui_chat_ChatViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.appfit.ui.activity.ActivityDetailViewModel 
          return (T) new ActivityDetailViewModel(singletonCImpl.activityRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          case 1: // com.appfit.ui.calendar.CalendarViewModel 
          return (T) new CalendarViewModel(viewModelCImpl.getDailyPlanUseCase(), singletonCImpl.activityRepositoryProvider.get(), singletonCImpl.dietRepositoryProvider.get());

          case 2: // com.appfit.ui.chat.ChatViewModel 
          return (T) new ChatViewModel(singletonCImpl.chatRepositoryProvider.get(), viewModelCImpl.sendChatMessageUseCase(), viewModelCImpl.getDailyPlanUseCase(), singletonCImpl.aiDebugLoggerProvider.get(), singletonCImpl.toolApprovalManagerProvider.get());

          case 3: // com.appfit.ui.debug.DbViewerViewModel 
          return (T) new DbViewerViewModel(singletonCImpl.provideDatabaseProvider.get(), singletonCImpl.aiDebugLoggerProvider.get());

          case 4: // com.appfit.ui.diet.DietViewModel 
          return (T) new DietViewModel(viewModelCImpl.getDailyPlanUseCase());

          case 5: // com.appfit.ui.diet.MealDetailViewModel 
          return (T) new MealDetailViewModel(singletonCImpl.dietRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          case 6: // com.appfit.ui.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.apiKeyProvider.get(), singletonCImpl.userProfileProvider.get(), singletonCImpl.provideWorkManagerProvider.get());

          case 7: // com.appfit.ui.profile.ProfileViewModel 
          return (T) new ProfileViewModel(singletonCImpl.userProfileProvider.get(), singletonCImpl.apiKeyProvider.get(), singletonCImpl.provideWorkManagerProvider.get());

          case 8: // com.appfit.ui.shopping.ShoppingViewModel 
          return (T) new ShoppingViewModel(singletonCImpl.shoppingRepositoryProvider.get(), viewModelCImpl.generateShoppingListUseCase());

          case 9: // com.appfit.ui.workouts.WorkoutsViewModel 
          return (T) new WorkoutsViewModel(singletonCImpl.activityRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends AppFitApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends AppFitApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends AppFitApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<ActivityReminderWorker_AssistedFactory> activityReminderWorker_AssistedFactoryProvider;

    private Provider<WorkManager> provideWorkManagerProvider;

    private Provider<AppDatabase> provideDatabaseProvider;

    private Provider<ActivityRepository> activityRepositoryProvider;

    private Provider<NotificationScheduler> notificationSchedulerProvider;

    private Provider<DailyCheckWorker_AssistedFactory> dailyCheckWorker_AssistedFactoryProvider;

    private Provider<DataStore<Preferences>> provideDataStoreProvider;

    private Provider<UserProfileProvider> userProfileProvider;

    private Provider<MonthlyUpdateWorker_AssistedFactory> monthlyUpdateWorker_AssistedFactoryProvider;

    private Provider<NotificationChannels> notificationChannelsProvider;

    private Provider<DietRepository> dietRepositoryProvider;

    private Provider<ChatRepository> chatRepositoryProvider;

    private Provider<ApiKeyProvider> apiKeyProvider;

    private Provider<AiDebugLogger> aiDebugLoggerProvider;

    private Provider<ToolApprovalManager> toolApprovalManagerProvider;

    private Provider<AnthropicService> provideAnthropicServiceProvider;

    private Provider<GeminiService> provideGeminiServiceProvider;

    private Provider<AiService> provideAiServiceProvider;

    private Provider<ClaudeToolExecutor> claudeToolExecutorProvider;

    private Provider<ShoppingRepository> shoppingRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private ActivityDao activityDao() {
      return DatabaseModule_ProvideActivityDaoFactory.provideActivityDao(provideDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return MapBuilder.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>newMapBuilder(3).put("com.appfit.notification.ActivityReminderWorker", ((Provider) activityReminderWorker_AssistedFactoryProvider)).put("com.appfit.notification.DailyCheckWorker", ((Provider) dailyCheckWorker_AssistedFactoryProvider)).put("com.appfit.notification.MonthlyUpdateWorker", ((Provider) monthlyUpdateWorker_AssistedFactoryProvider)).build();
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    private MealDao mealDao() {
      return DatabaseModule_ProvideMealDaoFactory.provideMealDao(provideDatabaseProvider.get());
    }

    private DietPlanDao dietPlanDao() {
      return DatabaseModule_ProvideDietPlanDaoFactory.provideDietPlanDao(provideDatabaseProvider.get());
    }

    private ChatMessageDao chatMessageDao() {
      return DatabaseModule_ProvideChatMessageDaoFactory.provideChatMessageDao(provideDatabaseProvider.get());
    }

    private ShoppingItemDao shoppingItemDao() {
      return DatabaseModule_ProvideShoppingItemDaoFactory.provideShoppingItemDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.activityReminderWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<ActivityReminderWorker_AssistedFactory>(singletonCImpl, 0));
      this.provideWorkManagerProvider = DoubleCheck.provider(new SwitchingProvider<WorkManager>(singletonCImpl, 3));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 5));
      this.activityRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRepository>(singletonCImpl, 4));
      this.notificationSchedulerProvider = DoubleCheck.provider(new SwitchingProvider<NotificationScheduler>(singletonCImpl, 2));
      this.dailyCheckWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<DailyCheckWorker_AssistedFactory>(singletonCImpl, 1));
      this.provideDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<DataStore<Preferences>>(singletonCImpl, 8));
      this.userProfileProvider = DoubleCheck.provider(new SwitchingProvider<UserProfileProvider>(singletonCImpl, 7));
      this.monthlyUpdateWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<MonthlyUpdateWorker_AssistedFactory>(singletonCImpl, 6));
      this.notificationChannelsProvider = DoubleCheck.provider(new SwitchingProvider<NotificationChannels>(singletonCImpl, 9));
      this.dietRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<DietRepository>(singletonCImpl, 10));
      this.chatRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ChatRepository>(singletonCImpl, 11));
      this.apiKeyProvider = DoubleCheck.provider(new SwitchingProvider<ApiKeyProvider>(singletonCImpl, 13));
      this.aiDebugLoggerProvider = DoubleCheck.provider(new SwitchingProvider<AiDebugLogger>(singletonCImpl, 15));
      this.toolApprovalManagerProvider = DoubleCheck.provider(new SwitchingProvider<ToolApprovalManager>(singletonCImpl, 16));
      this.provideAnthropicServiceProvider = DoubleCheck.provider(new SwitchingProvider<AnthropicService>(singletonCImpl, 14));
      this.provideGeminiServiceProvider = DoubleCheck.provider(new SwitchingProvider<GeminiService>(singletonCImpl, 17));
      this.provideAiServiceProvider = DoubleCheck.provider(new SwitchingProvider<AiService>(singletonCImpl, 12));
      this.claudeToolExecutorProvider = DoubleCheck.provider(new SwitchingProvider<ClaudeToolExecutor>(singletonCImpl, 18));
      this.shoppingRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ShoppingRepository>(singletonCImpl, 19));
    }

    @Override
    public void injectAppFitApplication(AppFitApplication appFitApplication) {
      injectAppFitApplication2(appFitApplication);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private AppFitApplication injectAppFitApplication2(AppFitApplication instance) {
      AppFitApplication_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      AppFitApplication_MembersInjector.injectNotificationChannels(instance, notificationChannelsProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.appfit.notification.ActivityReminderWorker_AssistedFactory 
          return (T) new ActivityReminderWorker_AssistedFactory() {
            @Override
            public ActivityReminderWorker create(Context appContext,
                WorkerParameters workerParams) {
              return new ActivityReminderWorker(appContext, workerParams);
            }
          };

          case 1: // com.appfit.notification.DailyCheckWorker_AssistedFactory 
          return (T) new DailyCheckWorker_AssistedFactory() {
            @Override
            public DailyCheckWorker create(Context appContext2, WorkerParameters workerParams2) {
              return new DailyCheckWorker(appContext2, workerParams2, singletonCImpl.notificationSchedulerProvider.get());
            }
          };

          case 2: // com.appfit.notification.NotificationScheduler 
          return (T) new NotificationScheduler(singletonCImpl.provideWorkManagerProvider.get(), singletonCImpl.activityRepositoryProvider.get());

          case 3: // androidx.work.WorkManager 
          return (T) WorkManagerModule_ProvideWorkManagerFactory.provideWorkManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.appfit.data.repository.ActivityRepository 
          return (T) new ActivityRepository(singletonCImpl.activityDao());

          case 5: // com.appfit.data.local.AppDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // com.appfit.notification.MonthlyUpdateWorker_AssistedFactory 
          return (T) new MonthlyUpdateWorker_AssistedFactory() {
            @Override
            public MonthlyUpdateWorker create(Context appContext3, WorkerParameters workerParams3) {
              return new MonthlyUpdateWorker(appContext3, workerParams3, singletonCImpl.userProfileProvider.get());
            }
          };

          case 7: // com.appfit.ai.UserProfileProvider 
          return (T) new UserProfileProvider(singletonCImpl.provideDataStoreProvider.get());

          case 8: // androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> 
          return (T) AppModule_ProvideDataStoreFactory.provideDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 9: // com.appfit.notification.NotificationChannels 
          return (T) new NotificationChannels(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // com.appfit.data.repository.DietRepository 
          return (T) new DietRepository(singletonCImpl.mealDao(), singletonCImpl.dietPlanDao());

          case 11: // com.appfit.data.repository.ChatRepository 
          return (T) new ChatRepository(singletonCImpl.chatMessageDao());

          case 12: // com.appfit.ai.AiService 
          return (T) AiModule_ProvideAiServiceFactory.provideAiService(singletonCImpl.apiKeyProvider.get(), singletonCImpl.provideAnthropicServiceProvider.get(), singletonCImpl.provideGeminiServiceProvider.get());

          case 13: // com.appfit.ai.ApiKeyProvider 
          return (T) new ApiKeyProvider(singletonCImpl.provideDataStoreProvider.get());

          case 14: // com.appfit.ai.AnthropicService 
          return (T) AiModule_ProvideAnthropicServiceFactory.provideAnthropicService(singletonCImpl.apiKeyProvider.get(), singletonCImpl.aiDebugLoggerProvider.get(), singletonCImpl.toolApprovalManagerProvider.get(), singletonCImpl.activityRepositoryProvider.get(), singletonCImpl.dietRepositoryProvider.get(), singletonCImpl.userProfileProvider.get());

          case 15: // com.appfit.ai.AiDebugLogger 
          return (T) new AiDebugLogger();

          case 16: // com.appfit.ai.ToolApprovalManager 
          return (T) new ToolApprovalManager();

          case 17: // com.appfit.ai.GeminiService 
          return (T) AiModule_ProvideGeminiServiceFactory.provideGeminiService(singletonCImpl.apiKeyProvider.get(), singletonCImpl.aiDebugLoggerProvider.get(), singletonCImpl.toolApprovalManagerProvider.get(), singletonCImpl.activityRepositoryProvider.get(), singletonCImpl.dietRepositoryProvider.get(), singletonCImpl.userProfileProvider.get());

          case 18: // com.appfit.ai.ClaudeToolExecutor 
          return (T) new ClaudeToolExecutor(singletonCImpl.activityRepositoryProvider.get(), singletonCImpl.dietRepositoryProvider.get(), singletonCImpl.userProfileProvider.get());

          case 19: // com.appfit.data.repository.ShoppingRepository 
          return (T) new ShoppingRepository(singletonCImpl.shoppingItemDao());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
