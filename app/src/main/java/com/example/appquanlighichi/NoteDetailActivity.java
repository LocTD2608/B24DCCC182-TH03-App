package com.example.appquanlighichi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NoteDetailActivity extends AppCompatActivity {

    private TextView tvDetailTitle, tvDetailContent, tvCreatedAt, tvUpdatedAt;
    private Button btnEdit, btnDelete, btnBack;
    private Note currentNote;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailContent = findViewById(R.id.tvDetailContent);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvUpdatedAt = findViewById(R.id.tvUpdatedAt);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DatabaseHelper(this);

        // Nhận dữ liệu ghi chú được truyền sang
        if (getIntent().hasExtra("note")) {
            currentNote = (Note) getIntent().getSerializableExtra("note");
        }

        if (currentNote == null) {
            Toast.makeText(this, "Không tìm thấy ghi chú", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị thông tin ghi chú
        displayNote();

        // Nút chỉnh sửa: mở NoteActivity ở chế độ sửa
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteDetailActivity.this, NoteActivity.class);
                intent.putExtra("note", currentNote);
                startActivity(intent);
            }
        });

        // Nút xóa: hiện dialog xác nhận trước khi xóa
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(NoteDetailActivity.this)
                        .setTitle("Xóa ghi chú")
                        .setMessage("Bạn có chắc chắn muốn xóa ghi chú \"" + currentNote.getTitle() + "\" không?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.deleteNote(currentNote.getId());
                                Toast.makeText(NoteDetailActivity.this, "Đã xóa ghi chú", Toast.LENGTH_SHORT).show();
                                finish(); // Quay lại danh sách
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        // Nút quay lại: đóng màn hình chi tiết, trở về danh sách
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại dữ liệu khi quay lại từ màn hình chỉnh sửa
        if (currentNote != null) {
            Note updatedNote = dbHelper.getNoteById(currentNote.getId());
            if (updatedNote != null) {
                currentNote = updatedNote;
                displayNote();
            } else {
                // Ghi chú đã bị xóa
                finish();
            }
        }
    }

    // Hiển thị thông tin ghi chú lên giao diện
    private void displayNote() {
        tvDetailTitle.setText(currentNote.getTitle());
        tvDetailContent.setText(currentNote.getContent());
        tvCreatedAt.setText("Tạo lúc: " + currentNote.getCreatedAt());
        tvUpdatedAt.setText("Cập nhật: " + currentNote.getUpdatedAt());
    }
}
