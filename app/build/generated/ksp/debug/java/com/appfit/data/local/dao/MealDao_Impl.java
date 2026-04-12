package com.appfit.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
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
import com.appfit.data.model.Meal;
import com.appfit.data.model.MealType;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.time.LocalDate;
import java.time.LocalTime;
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
public final class MealDao_Impl implements MealDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Meal> __insertionAdapterOfMeal;

  private final DateTimeConverters __dateTimeConverters = new DateTimeConverters();

  private final EntityDeletionOrUpdateAdapter<Meal> __updateAdapterOfMeal;

  private final SharedSQLiteStatement __preparedStmtOfSetConsumed;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMeal;

  public MealDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMeal = new EntityInsertionAdapter<Meal>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `meals` (`id`,`name`,`description`,`type`,`scheduledDate`,`scheduledTime`,`ingredients`,`caloriesKcal`,`proteinG`,`carbsG`,`fatG`,`isConsumed`,`aiGenerated`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Meal entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, __MealType_enumToString(entity.getType()));
        final String _tmp = __dateTimeConverters.fromLocalDate(entity.getScheduledDate());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp);
        }
        final String _tmp_1 = __dateTimeConverters.fromLocalTime(entity.getScheduledTime());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_1);
        }
        final String _tmp_2 = __dateTimeConverters.fromStringList(entity.getIngredients());
        if (_tmp_2 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_2);
        }
        statement.bindLong(8, entity.getCaloriesKcal());
        statement.bindLong(9, entity.getProteinG());
        statement.bindLong(10, entity.getCarbsG());
        statement.bindLong(11, entity.getFatG());
        final int _tmp_3 = entity.isConsumed() ? 1 : 0;
        statement.bindLong(12, _tmp_3);
        final int _tmp_4 = entity.getAiGenerated() ? 1 : 0;
        statement.bindLong(13, _tmp_4);
      }
    };
    this.__updateAdapterOfMeal = new EntityDeletionOrUpdateAdapter<Meal>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `meals` SET `id` = ?,`name` = ?,`description` = ?,`type` = ?,`scheduledDate` = ?,`scheduledTime` = ?,`ingredients` = ?,`caloriesKcal` = ?,`proteinG` = ?,`carbsG` = ?,`fatG` = ?,`isConsumed` = ?,`aiGenerated` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Meal entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, __MealType_enumToString(entity.getType()));
        final String _tmp = __dateTimeConverters.fromLocalDate(entity.getScheduledDate());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp);
        }
        final String _tmp_1 = __dateTimeConverters.fromLocalTime(entity.getScheduledTime());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_1);
        }
        final String _tmp_2 = __dateTimeConverters.fromStringList(entity.getIngredients());
        if (_tmp_2 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_2);
        }
        statement.bindLong(8, entity.getCaloriesKcal());
        statement.bindLong(9, entity.getProteinG());
        statement.bindLong(10, entity.getCarbsG());
        statement.bindLong(11, entity.getFatG());
        final int _tmp_3 = entity.isConsumed() ? 1 : 0;
        statement.bindLong(12, _tmp_3);
        final int _tmp_4 = entity.getAiGenerated() ? 1 : 0;
        statement.bindLong(13, _tmp_4);
        statement.bindLong(14, entity.getId());
      }
    };
    this.__preparedStmtOfSetConsumed = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE meals SET isConsumed = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteMeal = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM meals WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertMeal(final Meal meal, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMeal.insertAndReturnId(meal);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMeal(final Meal meal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMeal.handle(meal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object setConsumed(final long id, final boolean consumed,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetConsumed.acquire();
        int _argIndex = 1;
        final int _tmp = consumed ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
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
          __preparedStmtOfSetConsumed.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMeal(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMeal.acquire();
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
          __preparedStmtOfDeleteMeal.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Meal>> getMealsForDate(final LocalDate date) {
    final String _sql = "SELECT * FROM meals WHERE scheduledDate = ? ORDER BY type ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __dateTimeConverters.fromLocalDate(date);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"meals"}, new Callable<List<Meal>>() {
      @Override
      @NonNull
      public List<Meal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfScheduledDate = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledDate");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfIngredients = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredients");
          final int _cursorIndexOfCaloriesKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesKcal");
          final int _cursorIndexOfProteinG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinG");
          final int _cursorIndexOfCarbsG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsG");
          final int _cursorIndexOfFatG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatG");
          final int _cursorIndexOfIsConsumed = CursorUtil.getColumnIndexOrThrow(_cursor, "isConsumed");
          final int _cursorIndexOfAiGenerated = CursorUtil.getColumnIndexOrThrow(_cursor, "aiGenerated");
          final List<Meal> _result = new ArrayList<Meal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Meal _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final MealType _tmpType;
            _tmpType = __MealType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final LocalDate _tmpScheduledDate;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfScheduledDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfScheduledDate);
            }
            final LocalDate _tmp_2 = __dateTimeConverters.toLocalDate(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpScheduledDate = _tmp_2;
            }
            final LocalTime _tmpScheduledTime;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfScheduledTime)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfScheduledTime);
            }
            _tmpScheduledTime = __dateTimeConverters.toLocalTime(_tmp_3);
            final List<String> _tmpIngredients;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfIngredients)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfIngredients);
            }
            _tmpIngredients = __dateTimeConverters.toStringList(_tmp_4);
            final int _tmpCaloriesKcal;
            _tmpCaloriesKcal = _cursor.getInt(_cursorIndexOfCaloriesKcal);
            final int _tmpProteinG;
            _tmpProteinG = _cursor.getInt(_cursorIndexOfProteinG);
            final int _tmpCarbsG;
            _tmpCarbsG = _cursor.getInt(_cursorIndexOfCarbsG);
            final int _tmpFatG;
            _tmpFatG = _cursor.getInt(_cursorIndexOfFatG);
            final boolean _tmpIsConsumed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIsConsumed);
            _tmpIsConsumed = _tmp_5 != 0;
            final boolean _tmpAiGenerated;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfAiGenerated);
            _tmpAiGenerated = _tmp_6 != 0;
            _item = new Meal(_tmpId,_tmpName,_tmpDescription,_tmpType,_tmpScheduledDate,_tmpScheduledTime,_tmpIngredients,_tmpCaloriesKcal,_tmpProteinG,_tmpCarbsG,_tmpFatG,_tmpIsConsumed,_tmpAiGenerated);
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

  @Override
  public Flow<List<Meal>> getMealsForRange(final LocalDate start, final LocalDate end) {
    final String _sql = "SELECT * FROM meals WHERE scheduledDate BETWEEN ? AND ? ORDER BY scheduledDate ASC, type ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __dateTimeConverters.fromLocalDate(start);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    final String _tmp_1 = __dateTimeConverters.fromLocalDate(end);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"meals"}, new Callable<List<Meal>>() {
      @Override
      @NonNull
      public List<Meal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfScheduledDate = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledDate");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfIngredients = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredients");
          final int _cursorIndexOfCaloriesKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesKcal");
          final int _cursorIndexOfProteinG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinG");
          final int _cursorIndexOfCarbsG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsG");
          final int _cursorIndexOfFatG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatG");
          final int _cursorIndexOfIsConsumed = CursorUtil.getColumnIndexOrThrow(_cursor, "isConsumed");
          final int _cursorIndexOfAiGenerated = CursorUtil.getColumnIndexOrThrow(_cursor, "aiGenerated");
          final List<Meal> _result = new ArrayList<Meal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Meal _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final MealType _tmpType;
            _tmpType = __MealType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final LocalDate _tmpScheduledDate;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfScheduledDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfScheduledDate);
            }
            final LocalDate _tmp_3 = __dateTimeConverters.toLocalDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpScheduledDate = _tmp_3;
            }
            final LocalTime _tmpScheduledTime;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfScheduledTime)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfScheduledTime);
            }
            _tmpScheduledTime = __dateTimeConverters.toLocalTime(_tmp_4);
            final List<String> _tmpIngredients;
            final String _tmp_5;
            if (_cursor.isNull(_cursorIndexOfIngredients)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getString(_cursorIndexOfIngredients);
            }
            _tmpIngredients = __dateTimeConverters.toStringList(_tmp_5);
            final int _tmpCaloriesKcal;
            _tmpCaloriesKcal = _cursor.getInt(_cursorIndexOfCaloriesKcal);
            final int _tmpProteinG;
            _tmpProteinG = _cursor.getInt(_cursorIndexOfProteinG);
            final int _tmpCarbsG;
            _tmpCarbsG = _cursor.getInt(_cursorIndexOfCarbsG);
            final int _tmpFatG;
            _tmpFatG = _cursor.getInt(_cursorIndexOfFatG);
            final boolean _tmpIsConsumed;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIsConsumed);
            _tmpIsConsumed = _tmp_6 != 0;
            final boolean _tmpAiGenerated;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfAiGenerated);
            _tmpAiGenerated = _tmp_7 != 0;
            _item = new Meal(_tmpId,_tmpName,_tmpDescription,_tmpType,_tmpScheduledDate,_tmpScheduledTime,_tmpIngredients,_tmpCaloriesKcal,_tmpProteinG,_tmpCarbsG,_tmpFatG,_tmpIsConsumed,_tmpAiGenerated);
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

  @Override
  public Object getMealById(final long id, final Continuation<? super Meal> $completion) {
    final String _sql = "SELECT * FROM meals WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Meal>() {
      @Override
      @Nullable
      public Meal call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfScheduledDate = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledDate");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfIngredients = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredients");
          final int _cursorIndexOfCaloriesKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesKcal");
          final int _cursorIndexOfProteinG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinG");
          final int _cursorIndexOfCarbsG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsG");
          final int _cursorIndexOfFatG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatG");
          final int _cursorIndexOfIsConsumed = CursorUtil.getColumnIndexOrThrow(_cursor, "isConsumed");
          final int _cursorIndexOfAiGenerated = CursorUtil.getColumnIndexOrThrow(_cursor, "aiGenerated");
          final Meal _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final MealType _tmpType;
            _tmpType = __MealType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final LocalDate _tmpScheduledDate;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfScheduledDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfScheduledDate);
            }
            final LocalDate _tmp_1 = __dateTimeConverters.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpScheduledDate = _tmp_1;
            }
            final LocalTime _tmpScheduledTime;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfScheduledTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfScheduledTime);
            }
            _tmpScheduledTime = __dateTimeConverters.toLocalTime(_tmp_2);
            final List<String> _tmpIngredients;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfIngredients)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfIngredients);
            }
            _tmpIngredients = __dateTimeConverters.toStringList(_tmp_3);
            final int _tmpCaloriesKcal;
            _tmpCaloriesKcal = _cursor.getInt(_cursorIndexOfCaloriesKcal);
            final int _tmpProteinG;
            _tmpProteinG = _cursor.getInt(_cursorIndexOfProteinG);
            final int _tmpCarbsG;
            _tmpCarbsG = _cursor.getInt(_cursorIndexOfCarbsG);
            final int _tmpFatG;
            _tmpFatG = _cursor.getInt(_cursorIndexOfFatG);
            final boolean _tmpIsConsumed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsConsumed);
            _tmpIsConsumed = _tmp_4 != 0;
            final boolean _tmpAiGenerated;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfAiGenerated);
            _tmpAiGenerated = _tmp_5 != 0;
            _result = new Meal(_tmpId,_tmpName,_tmpDescription,_tmpType,_tmpScheduledDate,_tmpScheduledTime,_tmpIngredients,_tmpCaloriesKcal,_tmpProteinG,_tmpCarbsG,_tmpFatG,_tmpIsConsumed,_tmpAiGenerated);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<LocalDate>> getDatesWithMeals(final LocalDate start, final LocalDate end) {
    final String _sql = "SELECT DISTINCT scheduledDate FROM meals WHERE scheduledDate BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __dateTimeConverters.fromLocalDate(start);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    final String _tmp_1 = __dateTimeConverters.fromLocalDate(end);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"meals"}, new Callable<List<LocalDate>>() {
      @Override
      @NonNull
      public List<LocalDate> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<LocalDate> _result = new ArrayList<LocalDate>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LocalDate _item;
            final String _tmp_2;
            if (_cursor.isNull(0)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(0);
            }
            final LocalDate _tmp_3 = __dateTimeConverters.toLocalDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _item = _tmp_3;
            }
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

  private String __MealType_enumToString(@NonNull final MealType _value) {
    switch (_value) {
      case BREAKFAST: return "BREAKFAST";
      case LUNCH: return "LUNCH";
      case DINNER: return "DINNER";
      case SNACK: return "SNACK";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private MealType __MealType_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "BREAKFAST": return MealType.BREAKFAST;
      case "LUNCH": return MealType.LUNCH;
      case "DINNER": return MealType.DINNER;
      case "SNACK": return MealType.SNACK;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
