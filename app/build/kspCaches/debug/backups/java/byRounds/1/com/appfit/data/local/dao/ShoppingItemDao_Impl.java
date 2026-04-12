package com.appfit.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.appfit.data.local.converters.DateTimeConverters;
import com.appfit.data.model.ShoppingCategory;
import com.appfit.data.model.ShoppingItem;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
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
public final class ShoppingItemDao_Impl implements ShoppingItemDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ShoppingItem> __insertionAdapterOfShoppingItem;

  private final DateTimeConverters __dateTimeConverters = new DateTimeConverters();

  private final SharedSQLiteStatement __preparedStmtOfSetChecked;

  private final SharedSQLiteStatement __preparedStmtOfDeleteItemsForWeek;

  private final SharedSQLiteStatement __preparedStmtOfDeleteItem;

  public ShoppingItemDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfShoppingItem = new EntityInsertionAdapter<ShoppingItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `shopping_items` (`id`,`name`,`quantity`,`unit`,`category`,`isChecked`,`weekStartDate`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ShoppingItem entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getQuantity());
        statement.bindString(4, entity.getUnit());
        statement.bindString(5, __ShoppingCategory_enumToString(entity.getCategory()));
        final int _tmp = entity.isChecked() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final String _tmp_1 = __dateTimeConverters.fromLocalDate(entity.getWeekStartDate());
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_1);
        }
      }
    };
    this.__preparedStmtOfSetChecked = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE shopping_items SET isChecked = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteItemsForWeek = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM shopping_items WHERE weekStartDate = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteItem = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM shopping_items WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertItems(final List<ShoppingItem> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfShoppingItem.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object replaceShoppingListForWeek(final LocalDate weekStart,
      final List<ShoppingItem> items, final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> ShoppingItemDao.DefaultImpls.replaceShoppingListForWeek(ShoppingItemDao_Impl.this, weekStart, items, __cont), $completion);
  }

  @Override
  public Object setChecked(final long id, final boolean checked,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetChecked.acquire();
        int _argIndex = 1;
        final int _tmp = checked ? 1 : 0;
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
          __preparedStmtOfSetChecked.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteItemsForWeek(final LocalDate weekStart,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteItemsForWeek.acquire();
        int _argIndex = 1;
        final String _tmp = __dateTimeConverters.fromLocalDate(weekStart);
        if (_tmp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, _tmp);
        }
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
          __preparedStmtOfDeleteItemsForWeek.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteItem(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteItem.acquire();
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
          __preparedStmtOfDeleteItem.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ShoppingItem>> getShoppingListForWeek(final LocalDate weekStart) {
    final String _sql = "SELECT * FROM shopping_items WHERE weekStartDate = ? ORDER BY category ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __dateTimeConverters.fromLocalDate(weekStart);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"shopping_items"}, new Callable<List<ShoppingItem>>() {
      @Override
      @NonNull
      public List<ShoppingItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsChecked = CursorUtil.getColumnIndexOrThrow(_cursor, "isChecked");
          final int _cursorIndexOfWeekStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "weekStartDate");
          final List<ShoppingItem> _result = new ArrayList<ShoppingItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ShoppingItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpQuantity;
            _tmpQuantity = _cursor.getString(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final ShoppingCategory _tmpCategory;
            _tmpCategory = __ShoppingCategory_stringToEnum(_cursor.getString(_cursorIndexOfCategory));
            final boolean _tmpIsChecked;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsChecked);
            _tmpIsChecked = _tmp_1 != 0;
            final LocalDate _tmpWeekStartDate;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfWeekStartDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfWeekStartDate);
            }
            final LocalDate _tmp_3 = __dateTimeConverters.toLocalDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpWeekStartDate = _tmp_3;
            }
            _item = new ShoppingItem(_tmpId,_tmpName,_tmpQuantity,_tmpUnit,_tmpCategory,_tmpIsChecked,_tmpWeekStartDate);
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

  private String __ShoppingCategory_enumToString(@NonNull final ShoppingCategory _value) {
    switch (_value) {
      case PRODUCE: return "PRODUCE";
      case PROTEIN: return "PROTEIN";
      case DAIRY: return "DAIRY";
      case GRAINS: return "GRAINS";
      case PANTRY: return "PANTRY";
      case OTHER: return "OTHER";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private ShoppingCategory __ShoppingCategory_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "PRODUCE": return ShoppingCategory.PRODUCE;
      case "PROTEIN": return ShoppingCategory.PROTEIN;
      case "DAIRY": return ShoppingCategory.DAIRY;
      case "GRAINS": return ShoppingCategory.GRAINS;
      case "PANTRY": return ShoppingCategory.PANTRY;
      case "OTHER": return ShoppingCategory.OTHER;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
