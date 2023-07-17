package com.example.python_compiler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorKt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;


import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    EditText edt_txt_code;
    Button btn_run;
    String str;
String D_Q="\"";
String s_q= "'";
    private TextView lineNumberText;
   public boolean isDeleting=false;
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "def", "if", "else", "elif", "while", "for", "in", "return", "print", "import",
            "break", "continue", "true", "false", "and", "or", "not", "for", "while",  "class",
           "from", "except", "exec", "print", "return", "yield", "lambda", "global"



            // Add more Python keywords as needed
    ));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        edt_txt_code=(EditText) findViewById(R.id.edt_txt_code);
        edt_txt_code.setGravity(Gravity.TOP);
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }



       btn_run=(Button) findViewById(R.id.btn_run);

        lineNumberText = findViewById(R.id.lineNumberText);
        edt_txt_code = findViewById(R.id.edt_txt_code);
        //   formatButton = findViewById(R.id.formatButton);

        // Add listener to format button


        // Add listener to code editor for line number updates

//        edt_txt_code.setOnKeyListener(new View.OnKeyListener(){
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                edt_txt_code.setText("get");
////                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
//                        // Double quote (") key pressed
//                        // Insert double quote at the cursor position
//                        int cursorPosition = edt_txt_code.getSelectionStart();
//                        edt_txt_code.getText().insert(cursorPosition, "\"");
//                        return true; // indicate that the event has been handled
//                    } else if (keyCode == KeyEvent.KEYCODE_APOSTROPHE) {
//                        // Single quote (') key pressed
//                        // Insert single quote at the cursor position
//                        int cursorPosition = edt_txt_code.getSelectionStart();
//                        edt_txt_code.getText().insert(cursorPosition, "'");
//                        return true; // indicate that the event has been handled
//                    }
////                }
//
// return false;
//            }
//
//        });
        edt_txt_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (count <= 0) {
                    isDeleting=false;
                    return;
                }

                    // User is deleting text
                    // Perform necessary actions
                 int cur_pos = edt_txt_code.getSelectionStart();
                String s_temp = edt_txt_code.getText().toString();
                if (cur_pos > 0 && cur_pos <= s_temp.length()) {
                    char previousChar = s_temp.charAt(cur_pos - 1);
                    switch(previousChar){
                        case'(':
                        case'{':
                        case'[':
                        case'<':
                            isDeleting=true;
                            return;
                        default:
                            isDeleting=false;

                    }
                    isDeleting=false;


                }
                }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String s_temp = edt_txt_code.getText().toString();
//               char ch=s_temp.charAt(s_temp.length()-1);
//               if(ch=='('){
//                   edt_txt_code.setText(s_temp+")");
//                  String s_1 = edt_txt_code.getText().toString();
//                   edt_txt_code.setSelection(s_1.length()-1);
//               }
            }
            public void afterTextChanged(Editable s) {
                if(isDeleting==true)
                               return;
                int cur_pos = edt_txt_code.getSelectionStart();
                int cur_end_pos =edt_txt_code.getSelectionEnd();
                String s_temp = edt_txt_code.getText().toString();
                if (cur_pos > 0 && cur_pos <= s_temp.length()) {
                    char previousChar = s_temp.charAt(cur_pos - 1);

                    switch(previousChar){
                        case'(':
                            s.insert(cur_pos, ")");
                            edt_txt_code.setSelection(cur_pos);
                            break;
                        case'{':
                            s.insert(cur_pos, "}");
                            edt_txt_code.setSelection(cur_pos);
                            break;
                        case'[':
                            s.insert(cur_pos, "]");
                            edt_txt_code.setSelection(cur_pos);
                            break;
                        case'<':
                            s.insert(cur_pos, ">");
                            edt_txt_code.setSelection(cur_pos);
                            break;

                        default:
                          edt_txt_code.setSelection(cur_end_pos);
                         //   break;

                    }

                    updateLineNumbers();
                    highlightKeywords();
                }
            }
        });

        // Update initial line numbers and highlight keywords
        updateLineNumbers();
        highlightKeywords();

      //  tv_text=(TextView) findViewById(R.id.tv_text);

        //now on cliking run btn we extract data from input and send to py script
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Python py=Python.getInstance();
                //here we call our script with name myscript
                PyObject pyobj=py.getModule("myscript");
                PyObject obj=pyobj.callAttr("main",edt_txt_code.getText().toString());
              str  =obj.toString();
                Intent intent=new Intent(MainActivity.this, OutputActivity.class);
                intent.putExtra("op",str);
                startActivity(intent);


            }
        });



    }


    private void updateLineNumbers() {
        String code = edt_txt_code.getText().toString();
        int lineCount = code.split("\n").length;

        StringBuilder lineNumbers = new StringBuilder();
        for (int i = 1; i <= lineCount; i++) {
            lineNumbers.append(i).append("\n");
        }

        lineNumberText.setText(lineNumbers.toString());
    }

    private void highlightKeywords() {
        Editable code = edt_txt_code.getText();
        String[] words = code.toString().split("\\W");

        int startPos = 0;
        for (String word : words) {
            int len = word.length();
            int endPos = startPos + len;

            if (KEYWORDS.contains(word)) {
                code.setSpan(new ForegroundColorSpan(Color.YELLOW), startPos, endPos,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                code.setSpan(new ForegroundColorSpan(Color.LTGRAY), startPos, endPos,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            startPos = endPos + 1;
        }
    }

    private void formatCode() {
        // Implement your code formatting logic here
    }
}