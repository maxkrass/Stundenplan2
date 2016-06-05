package com.maxkrass.stundenplan.activities;

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toolbar;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.adapter.TeachersAdapter;
import com.maxkrass.stundenplan.objects.Teacher;
import com.maxkrass.stundenplan.tools.Tools;
import com.orm.SugarRecord;

import java.util.Locale;

public class CreateTeacherActivity extends BaseActivity {

    EditText teacherName, teacherPhone, teacherEmail;
    Toolbar toolbar;
    boolean edit;
    Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_teacher);
        teacherName = (EditText) findViewById(R.id.teacher_name_field);
        teacherPhone = (EditText) findViewById(R.id.teacher_phone_field);
        teacherEmail = (EditText) findViewById(R.id.teacher_email_field);
        toolbar = (Toolbar) findViewById(R.id.create_teacher_toolbar);
        //setActionBar(toolbar);
        edit = getIntent().getBooleanExtra("edit", false);
        if (edit) {
            teacher = Teacher.findById(Teacher.class, getIntent().getLongExtra("teacherId", 0));
            if (teacher == null)
                finish();
            teacherName.setText(teacher.getName());
            teacherEmail.setText(teacher.getEmail());
            teacherPhone.setText(teacher.getPhone());
        }
        /*TransitionSet set = new TransitionSet();
        set.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        Slide slide = new Slide(Gravity.BOTTOM);
        slide.excludeTarget((View) teacherName.getParent(), true);
        slide.excludeTarget((View) teacherPhone.getParent(), true);
        slide.excludeTarget((View) teacherEmail.getParent(), true);
        set.addTransition(slide);
        Fade fade = new Fade();
        fade.addTarget((View) teacherName.getParent());
        fade.addTarget((View) teacherPhone.getParent());
        fade.addTarget((View) teacherEmail.getParent());
        fade.setStartDelay(500);
        //set.addTransition(fade);
        getWindow().setEnterTransition(set);
        getWindow().setExitTransition(new Slide(Gravity.TOP));*/
        findViewById(R.id.save_teacher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;
                if (TextUtils.isEmpty(teacherName.getText())) {
                    ((android.support.design.widget.TextInputLayout) teacherName.getParent()).setError("Dein Lehrer braucht einen Namen");
                    error = true;
                } else {
                    ((android.support.design.widget.TextInputLayout) teacherName.getParent()).setError("");
                }
                if (!TextUtils.isEmpty(teacherEmail.getText()) && !isValidEmail(teacherEmail.getText())) {
                    ((android.support.design.widget.TextInputLayout) teacherEmail.getParent()).setError("Diese E-Mail-Addresse ist ungültig");
                    error = true;
                } else {
                    ((android.support.design.widget.TextInputLayout) teacherEmail.getParent()).setError("");
                }
                if (!error) {
                    if (!edit) {
                        teacher = new Teacher(teacherName.getText().toString(), TextUtils.isEmpty(teacherPhone.getText()) ? "" : PhoneNumberUtils.formatNumber(teacherPhone.getText().toString(), Locale.getDefault().getCountry()), teacherEmail.getText().toString());
                        teacher.save();
                        ((TeachersAdapter) ManageTeacherActivity.teacherRecyclerView.getAdapter()).add(teacher);
                        ManageTeacherActivity.teacherRecyclerView.getAdapter().notifyItemInserted(SugarRecord.listAll(Teacher.class, "name").size() - 1);
                    } else {
                        teacher = Teacher.findById(Teacher.class, teacher.getId());
                        teacher.setName(teacherName.getText().toString());
                        teacher.setEmail(teacherEmail.getText().toString());
                        teacher.setPhone(TextUtils.isEmpty(teacherPhone.getText()) ? "" : PhoneNumberUtils.formatNumber(teacherPhone.getText().toString(), Locale.getDefault().getCountry()));
                        teacher.save();
                        ((TeachersAdapter) ManageTeacherActivity.teacherRecyclerView.getAdapter()).updateData();
                        ManageTeacherActivity.teacherRecyclerView.getAdapter().notifyItemChanged(Tools.getTeacherPosition(teacher));
                    }
                    //teachersAdapter.add(teacher);
                    //teachersAdapter.notifyItemInserted(Stundenplan.getInstance().getTeachers().size() - 1);
                    finish();
                }
            }
        });
        findViewById(R.id.cancel_teacher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
