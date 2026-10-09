package com.example.appquanlighichi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NoteActivity extends AppCompatActivity {

    private EditText etTitle, etContent;
    private Button btnSave;
    private Note currentNote; // Biến lưu trữ ghi chú nếu đang ở chế độ chỉnh sửa
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSave);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Nhận dữ liệu truyền sang nếu là bấm vào ghi chú có sẵn để sửa
        if (getIntent().hasExtra("note")) {
            currentNote = (Note) getIntent().getSerializableExtra("note");
            if (currentNote != null) {
                etTitle.setText(currentNote.getTitle());
                etContent.setText(currentNote.getContent());
                btnSave.setText("Cập nhật"); // Đổi text của nút
            }
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();

                // Kiểm tra dữ liệu đầu vào
                if (title.isEmpty()) {
                    etTitle.setError("Vui lòng nhập tiêu đề");
                    etTitle.requestFocus();
                    return;
                }

                if (content.isEmpty()) {
                    etContent.setError("Vui lòng nhập nội dung");
                    etContent.requestFocus();
                    return;
                }

                if (currentNote == null) {
                    // Thêm ghi chú mới vào SQLite
                    Note newNote = new Note(title, content);
                    long id = dbHelper.insertNote(newNote);
                    if (id > 0) {
                        Toast.makeText(NoteActivity.this, "Đã thêm ghi chú", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NoteActivity.this, "Lỗi khi thêm ghi chú", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Cập nhật ghi chú trong SQLite
                    currentNote.setTitle(title);
                    currentNote.setContent(content);
                    int rows = dbHelper.updateNote(currentNote);
                    if (rows > 0) {
                        Toast.makeText(NoteActivity.this, "Đã cập nhật ghi chú", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NoteActivity.this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
                    }
                }

                finish(); // Đóng màn hình này và quay lại
            }
        });
    }
}
