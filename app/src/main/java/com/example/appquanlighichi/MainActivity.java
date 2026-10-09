package com.example.appquanlighichi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private SearchView searchView;
    private TextView tvEmpty;
    private NoteAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Chỉnh padding để không bị che bởi thanh trạng thái và thanh điều hướng
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        searchView = findViewById(R.id.searchView);
        tvEmpty = findViewById(R.id.tvEmpty);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Hiển thị danh sách dạng LinearLayout (từ trên xuống)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Nhấn nút thêm ghi chú
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý tìm kiếm ghi chú
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Tìm kiếm khi nhấn Enter
                searchNotes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Tìm kiếm theo thời gian thực khi gõ
                searchNotes(newText);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại danh sách ghi chú từ SQLite mỗi khi màn hình hiển thị lại
        loadNotes();

        // Xóa text tìm kiếm khi quay lại
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.clearFocus();
        }
    }

    // Tải danh sách ghi chú từ SQLite
    private void loadNotes() {
        List<Note> noteList = dbHelper.getAllNotes();
        displayNotes(noteList);
    }

    // Tìm kiếm ghi chú theo từ khóa
    private void searchNotes(String keyword) {
        List<Note> noteList;
        if (keyword.isEmpty()) {
            noteList = dbHelper.getAllNotes();
        } else {
            noteList = dbHelper.searchNotes(keyword);
        }
        displayNotes(noteList);
    }

    // Hiển thị danh sách ghi chú lên RecyclerView
    private void displayNotes(List<Note> noteList) {
        if (adapter == null) {
            adapter = new NoteAdapter(this, noteList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(noteList);
        }

        // Hiển thị thông báo khi danh sách trống
        if (noteList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
