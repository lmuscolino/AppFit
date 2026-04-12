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
import com.appfit.data.model.Activity;
import com.appfit.data.model.ActivityType;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.time.Instant;
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
public final class ActivityDao_Impl implements ActivityDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Activity> __insertionAdapterOfActivity;

  private final DateTimeConverters __dateTimeConverters = new DateTimeConverters();

  private final EntityDeletionOrUpdateAdapter<Activity> __updateAdapterOfActivity;

  private final SharedSQLiteStatement __preparedStmtOfSetCompleted;

  private final SharedSQLiteStatement __preparedStmtOfDeleteActivity;

  public ActivityDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfActivity = new EntityInsertionAdapter<Activity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `activities` (`id`,`title`,`description`,`type`,`durationMinutes`,`scheduledDate`,`scheduledTime`,`isCompleted`,`caloriesBurned`,`aiGenerated`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Activity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, __ActivityType_enumToString(entity.getType()));
        statement.bindLong(5, entity.getDurationMinutes());
        final String _tmp = __dateTimeConverters.fromLocalDate(entity.getScheduledDate());
        if (_tmp == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp);
        }
        final String _tmp_1 = __dateTimeConverters.fromLocalTime(entity.getScheduledTime());
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_1);
        }
        final int _tmp_2 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
        statement.bindLong(9, entity.getCaloriesBurned());
        final int _tmp_3 = entity.getAiGenerated() ? 1 : 0;
        statement.bindLong(10, _tmp_3);
        final Long _tmp_4 = __dateTimeConverters.fromInstant(entity.getCreatedAt());
        if (_tmp_4 == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, _tmp_4);
        }
      }
    };
    this.__updateAdapterOfActivity = new EntityDeletionOrUpdateAdapter<Activity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `activities` SET `id` = ?,`title` = ?,`description` = ?,`type` = ?,`durationMinutes` = ?,`scheduledDate` = ?,`scheduledTime` = ?,`isCompleted` = ?,`caloriesBurned` = ?,`aiGenerated` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Activity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, __ActivityType_enumToString(entity.getType()));
        statement.bindLong(5, entity.getDurationMinutes());
        final String _tmp = __dateTimeConverters.fromLocalDate(entity.getScheduledDate());
        if (_tmp == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp);
        }
        final String _tmp_1 = __dateTimeConverters.fromLocalTime(entity.getScheduledTime());
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_1);
        }
        final int _tmp_2 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
        statement.bindLong(9, entity.getCaloriesBurned());
        final int _tmp_3 = entity.getAiGenerated() ? 1 : 0;
        statement.bindLong(10, _tmp_3);
        final Long _tmp_4 = __dateTimeConverters.fromInstant(entity.getCreatedAt());
        if (_tmp_4 == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, _tmp_4);
        }
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfSetCompleted = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE activities SET isCompleted = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteActivity = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM activities WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertActivity(final Activity activity,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfActivity.insertAndReturnId(activity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateActivity(final Activity activity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfActivity.handle(activity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object setCompleted(final long id, final boolean completed,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetCompleted.acquire();
        int _argIndex = 1;
        final int _tmp = completed ? 1 : 0;
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
          __preparedStmtOfSetCompleted.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteActivity(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteActivity.acquire();
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
          __preparedStmtOfDeleteActivity.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Activity>> getActivitiesForDate(final LocalDate date) {
    final String _sql = "SELECT * FROM activities WHERE scheduledDate = ? ORDER BY scheduledTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __dateTimeConverters.fromLocalDate(date);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"activities"}, new Callable<List<Activity>>() {
      @Override
      @NonNull
      public List<Activity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfScheduledDate = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledDate");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfAiGenerated = CursorUtil.getColumnIndexOrThrow(_cursor, "aiGenerated");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Activity> _result = new ArrayList<Activity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Activity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final ActivityType _tmpType;
            _tmpType = __ActivityType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
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
            final boolean _tmpIsCompleted;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_4 != 0;
            final int _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getInt(_cursorIndexOfCaloriesBurned);
            final boolean _tmpAiGenerated;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfAiGenerated);
            _tmpAiGenerated = _tmp_5 != 0;
            final Instant _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            final Instant _tmp_7 = __dateTimeConverters.toInstant(_tmp_6);
            if (_tmp_7 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.Instant', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_7;
            }
            _item = new Activity(_tmpId,_tmpTitle,_tmpDescription,_tmpType,_tmpDurationMinutes,_tmpScheduledDate,_tmpScheduledTime,_tmpIsCompleted,_tmpCaloriesBurned,_tmpAiGenerated,_tmpCreatedAt);
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
  public Flow<List<Activity>> getActivitiesForRange(final LocalDate start, final LocalDate end) {
    final String _sql = "SELECT * FROM activities WHERE scheduledDate BETWEEN ? AND ? ORDER BY scheduledDate ASC, scheduledTime ASC";
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
    return CoroutinesRoom.createFlow(__db, false, new String[] {"activities"}, new Callable<List<Activity>>() {
      @Override
      @NonNull
      public List<Activity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfScheduledDate = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledDate");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfAiGenerated = CursorUtil.getColumnIndexOrThrow(_cursor, "aiGenerated");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Activity> _result = new ArrayList<Activity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Activity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final ActivityType _tmpType;
            _tmpType = __ActivityType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
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
            final boolean _tmpIsCompleted;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_5 != 0;
            final int _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getInt(_cursorIndexOfCaloriesBurned);
            final boolean _tmpAiGenerated;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfAiGenerated);
            _tmpAiGenerated = _tmp_6 != 0;
            final Instant _tmpCreatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            final Instant _tmp_8 = __dateTimeConverters.toInstant(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.Instant', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_8;
            }
            _item = new Activity(_tmpId,_tmpTitle,_tmpDescription,_tmpType,_tmpDurationMinutes,_tmpScheduledDate,_tmpScheduledTime,_tmpIsCompleted,_tmpCaloriesBurned,_tmpAiGenerated,_tmpCreatedAt);
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
  public Object getActivityById(final long id, final Continuation<? super Activity> $completion) {
    final String _sql = "SELECT * FROM activities WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Activity>() {
      @Override
      @Nullable
      public Activity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfScheduledDate = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledDate");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfAiGenerated = CursorUtil.getColumnIndexOrThrow(_cursor, "aiGenerated");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final Activity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final ActivityType _tmpType;
            _tmpType = __ActivityType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
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
            final boolean _tmpIsCompleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_3 != 0;
            final int _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getInt(_cursorIndexOfCaloriesBurned);
            final boolean _tmpAiGenerated;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGenerated);
            _tmpAiGenerated = _tmp_4 != 0;
            final Instant _tmpCreatedAt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            final Instant _tmp_6 = __dateTimeConverters.toInstant(_tmp_5);
            if (_tmp_6 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.Instant', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_6;
            }
            _result = new Activity(_tmpId,_tmpTitle,_tmpDescription,_tmpType,_tmpDurationMinutes,_tmpScheduledDate,_tmpScheduledTime,_tmpIsCompleted,_tmpCaloriesBurned,_tmpAiGenerated,_tmpCreatedAt);
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
  public Flow<List<Activity>> getUpcomingActivitiesWithTime(final LocalDate from) {
    final String _sql = "SELECT * FROM activities WHERE scheduledDate >= ? AND scheduledTime IS NOT NULL AND isCompleted = 0 ORDER BY scheduledDate ASC, scheduledTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __dateTimeConverters.fromLocalDate(from);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"activities"}, new Callable<List<Activity>>() {
      @Override
      @NonNull
      public List<Activity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfScheduledDate = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledDate");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfAiGenerated = CursorUtil.getColumnIndexOrThrow(_cursor, "aiGenerated");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Activity> _result = new ArrayList<Activity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Activity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final ActivityType _tmpType;
            _tmpType = __ActivityType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
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
            final boolean _tmpIsCompleted;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_4 != 0;
            final int _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getInt(_cursorIndexOfCaloriesBurned);
            final boolean _tmpAiGenerated;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfAiGenerated);
            _tmpAiGenerated = _tmp_5 != 0;
            final Instant _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            final Instant _tmp_7 = __dateTimeConverters.toInstant(_tmp_6);
            if (_tmp_7 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.Instant', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_7;
            }
            _item = new Activity(_tmpId,_tmpTitle,_tmpDescription,_tmpType,_tmpDurationMinutes,_tmpScheduledDate,_tmpScheduledTime,_tmpIsCompleted,_tmpCaloriesBurned,_tmpAiGenerated,_tmpCreatedAt);
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
  public Flow<List<LocalDate>> getDatesWithActivities(final LocalDate start, final LocalDate end) {
    final String _sql = "SELECT DISTINCT scheduledDate FROM activities WHERE scheduledDate BETWEEN ? AND ?";
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
    return CoroutinesRoom.createFlow(__db, false, new String[] {"activities"}, new Callable<List<LocalDate>>() {
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

  private String __ActivityType_enumToString(@NonNull final ActivityType _value) {
    switch (_value) {
      case CARDIO: return "CARDIO";
      case STRENGTH: return "STRENGTH";
      case FLEXIBILITY: return "FLEXIBILITY";
      case YOGA: return "YOGA";
      case REST: return "REST";
      case CUSTOM: return "CUSTOM";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private ActivityType __ActivityType_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "CARDIO": return ActivityType.CARDIO;
      case "STRENGTH": return ActivityType.STRENGTH;
      case "FLEXIBILITY": return ActivityType.FLEXIBILITY;
      case "YOGA": return ActivityType.YOGA;
      case "REST": return ActivityType.REST;
      case "CUSTOM": return ActivityType.CUSTOM;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
