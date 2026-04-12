package com.appfit.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.appfit.data.local.converters.DateTimeConverters;
import com.appfit.data.model.DietPlan;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DietPlanDao_Impl implements DietPlanDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DietPlan> __insertionAdapterOfDietPlan;

  private final DateTimeConverters __dateTimeConverters = new DateTimeConverters();

  private final EntityDeletionOrUpdateAdapter<DietPlan> __updateAdapterOfDietPlan;

  private final SharedSQLiteStatement __preparedStmtOfDeactivateAllPlans;

  private final SharedSQLiteStatement __preparedStmtOfActivatePlan;

  private final SharedSQLiteStatement __preparedStmtOfDeletePlan;

  public DietPlanDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDietPlan = new EntityInsertionAdapter<DietPlan>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `diet_plans` (`id`,`name`,`description`,`startDate`,`endDate`,`dailyCalorieGoal`,`dailyProteinGoalG`,`dailyCarbsGoalG`,`dailyFatGoalG`,`isActive`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DietPlan entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        final String _tmp = __dateTimeConverters.fromLocalDate(entity.getStartDate());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        final String _tmp_1 = __dateTimeConverters.fromLocalDate(entity.getEndDate());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_1);
        }
        statement.bindLong(6, entity.getDailyCalorieGoal());
        statement.bindLong(7, entity.getDailyProteinGoalG());
        statement.bindLong(8, entity.getDailyCarbsGoalG());
        statement.bindLong(9, entity.getDailyFatGoalG());
        final int _tmp_2 = entity.isActive() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
      }
    };
    this.__updateAdapterOfDietPlan = new EntityDeletionOrUpdateAdapter<DietPlan>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `diet_plans` SET `id` = ?,`name` = ?,`description` = ?,`startDate` = ?,`endDate` = ?,`dailyCalorieGoal` = ?,`dailyProteinGoalG` = ?,`dailyCarbsGoalG` = ?,`dailyFatGoalG` = ?,`isActive` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DietPlan entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        final String _tmp = __dateTimeConverters.fromLocalDate(entity.getStartDate());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        final String _tmp_1 = __dateTimeConverters.fromLocalDate(entity.getEndDate());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_1);
        }
        statement.bindLong(6, entity.getDailyCalorieGoal());
        statement.bindLong(7, entity.getDailyProteinGoalG());
        statement.bindLong(8, entity.getDailyCarbsGoalG());
        statement.bindLong(9, entity.getDailyFatGoalG());
        final int _tmp_2 = entity.isActive() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeactivateAllPlans = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE diet_plans SET isActive = 0";
        return _query;
      }
    };
    this.__preparedStmtOfActivatePlan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE diet_plans SET isActive = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeletePlan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM diet_plans WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertPlan(final DietPlan plan, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDietPlan.insertAndReturnId(plan);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePlan(final DietPlan plan, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDietPlan.handle(plan);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deactivateAllPlans(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivateAllPlans.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeactivateAllPlans.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object activatePlan(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActivatePlan.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfActivatePlan.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePlan(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePlan.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeletePlan.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<DietPlan> getActivePlanForDate(final LocalDate date) {
    final String _sql = "SELECT * FROM diet_plans WHERE isActive = 1 AND startDate <= ? AND endDate >= ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __dateTimeConverters.fromLocalDate(date);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    final String _tmp_1 = __dateTimeConverters.fromLocalDate(date);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diet_plans"}, new Callable<DietPlan>() {
      @Override
      @Nullable
      public DietPlan call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfDailyCalorieGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyCalorieGoal");
          final int _cursorIndexOfDailyProteinGoalG = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyProteinGoalG");
          final int _cursorIndexOfDailyCarbsGoalG = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyCarbsGoalG");
          final int _cursorIndexOfDailyFatGoalG = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyFatGoalG");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final DietPlan _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final LocalDate _tmpStartDate;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfStartDate);
            }
            final LocalDate _tmp_3 = __dateTimeConverters.toLocalDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpStartDate = _tmp_3;
            }
            final LocalDate _tmpEndDate;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfEndDate);
            }
            final LocalDate _tmp_5 = __dateTimeConverters.toLocalDate(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpEndDate = _tmp_5;
            }
            final int _tmpDailyCalorieGoal;
            _tmpDailyCalorieGoal = _cursor.getInt(_cursorIndexOfDailyCalorieGoal);
            final int _tmpDailyProteinGoalG;
            _tmpDailyProteinGoalG = _cursor.getInt(_cursorIndexOfDailyProteinGoalG);
            final int _tmpDailyCarbsGoalG;
            _tmpDailyCarbsGoalG = _cursor.getInt(_cursorIndexOfDailyCarbsGoalG);
            final int _tmpDailyFatGoalG;
            _tmpDailyFatGoalG = _cursor.getInt(_cursorIndexOfDailyFatGoalG);
            final boolean _tmpIsActive;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_6 != 0;
            _result = new DietPlan(_tmpId,_tmpName,_tmpDescription,_tmpStartDate,_tmpEndDate,_tmpDailyCalorieGoal,_tmpDailyProteinGoalG,_tmpDailyCarbsGoalG,_tmpDailyFatGoalG,_tmpIsActive);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<DietPlan>> getAllPlans() {
    final String _sql = "SELECT * FROM diet_plans ORDER BY startDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diet_plans"}, new Callable<List<DietPlan>>() {
      @Override
      @NonNull
      public List<DietPlan> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfDailyCalorieGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyCalorieGoal");
          final int _cursorIndexOfDailyProteinGoalG = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyProteinGoalG");
          final int _cursorIndexOfDailyCarbsGoalG = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyCarbsGoalG");
          final int _cursorIndexOfDailyFatGoalG = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyFatGoalG");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<DietPlan> _result = new ArrayList<DietPlan>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DietPlan _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final LocalDate _tmpStartDate;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStartDate);
            }
            final LocalDate _tmp_1 = __dateTimeConverters.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpStartDate = _tmp_1;
            }
            final LocalDate _tmpEndDate;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfEndDate);
            }
            final LocalDate _tmp_3 = __dateTimeConverters.toLocalDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpEndDate = _tmp_3;
            }
            final int _tmpDailyCalorieGoal;
            _tmpDailyCalorieGoal = _cursor.getInt(_cursorIndexOfDailyCalorieGoal);
            final int _tmpDailyProteinGoalG;
            _tmpDailyProteinGoalG = _cursor.getInt(_cursorIndexOfDailyProteinGoalG);
            final int _tmpDailyCarbsGoalG;
            _tmpDailyCarbsGoalG = _cursor.getInt(_cursorIndexOfDailyCarbsGoalG);
            final int _tmpDailyFatGoalG;
            _tmpDailyFatGoalG = _cursor.getInt(_cursorIndexOfDailyFatGoalG);
            final boolean _tmpIsActive;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_4 != 0;
            _item = new DietPlan(_tmpId,_tmpName,_tmpDescription,_tmpStartDate,_tmpEndDate,_tmpDailyCalorieGoal,_tmpDailyProteinGoalG,_tmpDailyCarbsGoalG,_tmpDailyFatGoalG,_tmpIsActive);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
