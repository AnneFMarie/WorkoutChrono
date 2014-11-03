package com.annef.workoutchrono.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.annef.workoutchrono.R;
import com.annef.workoutchrono.dao.WorkoutDAO;
import com.annef.workoutchrono.model.Exercise;
import com.annef.workoutchrono.model.ExerciseType;

import java.io.File;
import java.io.IOException;

public class ExerciseActivity extends Activity {

    private static final int SELECT_PHOTO = 100;
    public static final int EDIT_NBREPET = 101;
    public static final int EDIT_NBSERIE = 102;
    private static final int EDIT_REPOS = 103;
    private static final int EDIT_TYPE = 104;
    private static final int EDIT_TEMPS = 105;
    private ImageView exoImage;

    private Bitmap resizePicture(Bitmap bitmap){

        int width;
        int height;
        if (bitmap.getHeight() > bitmap.getWidth() ) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }
        else {
            width = bitmap.getHeight();
            height = bitmap.getWidth();
        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int newWidth;
        int newHeight;
        if (size.x > size.y){
            newHeight = size.x /2;
            newWidth = size.y / 2;
        } else {
            newWidth = size.x /2;
            newHeight = size.y /2;
        }

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        if (scaleHeight > 1 && scaleWidth > 1) {
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            // create the new Bitmap object
            return Bitmap.createBitmap(bitmap, 0, 0, width,
                    height, matrix, true);
        }
        return bitmap;
    }

    private OnItemSelectedListener onSpinnerItemListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (id == 0) {
                type = ExerciseType.Serie;
                TextView repetitionLabel = (TextView) findViewById(R.id.RepetitionText);
                repetitionLabel.setText("Repetition");
                TextView reposLabel = (TextView) findViewById(R.id.ReposText);
                reposLabel.setVisibility(View.VISIBLE);
            }
            if (id == 1) {
                type = ExerciseType.Duree;
                TextView repetitionLabel = (TextView) findViewById(R.id.RepetitionText);
                repetitionLabel.setText("Temps");
                TextView reposLabel = (TextView) findViewById(R.id.ReposText);
                reposLabel.setVisibility(View.VISIBLE);
            }
            if (id == 2) {
                type = ExerciseType.Repos;
                TextView repetitionLabel = (TextView) findViewById(R.id.RepetitionText);
                repetitionLabel.setText("Temps de repos");
                repos.setVisibility(View.INVISIBLE);
                TextView reposLabel = (TextView) findViewById(R.id.ReposText);
                reposLabel.setVisibility(View.INVISIBLE);

            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    //Uri selectedImage = data.getData();
                    //Bundle extras = data.getExtras();
                    //Bitmap imageBitmap = resizePicture((Bitmap) extras.get("data"));
                    //exoImage.setImageBitmap(imageBitmap);

                   // exo.setPathToPic(selectedImage.getPath());

                    exoImage.setImageURI(Uri.fromFile(new File(exo.getPathToPic())));
                    exoImage.setVisibility(View.VISIBLE);
                }
                break;
            case EDIT_NBREPET:
                nbRepetition.setText(""+data.getIntExtra("com.annef.workoutchrono.number", 0));
                break;
            case EDIT_TEMPS:
                nbRepetition.setText(""+data.getIntExtra("com.annef.workoutchrono.time", 0));
                break;
            case EDIT_NBSERIE:
                nbSerie.setText(""+data.getIntExtra("com.annef.workoutchrono.number", 0));
                break;
            case EDIT_REPOS:
                repos.setText(""+data.getIntExtra("com.annef.workoutchrono.time", 0));
                break;
            case EDIT_TYPE:
                String newType = data.getStringExtra("com.annef.workoutchrono.type");
                if (newType.equals(ExerciseType.Serie.toString())) {
                    type = ExerciseType.Serie;
                    TextView repetitionLabel = (TextView) findViewById(R.id.RepetitionText);
                    repetitionLabel.setText("Repetition");
                    TextView reposLabel = (TextView) findViewById(R.id.ReposText);
                    reposLabel.setVisibility(View.VISIBLE);
                }
                if (newType.equals(ExerciseType.Duree.toString())) {
                    type = ExerciseType.Duree;
                    TextView repetitionLabel = (TextView) findViewById(R.id.RepetitionText);
                    repetitionLabel.setText("Temps");
                    TextView reposLabel = (TextView) findViewById(R.id.ReposText);
                    reposLabel.setVisibility(View.VISIBLE);
                }
                if (newType.equals(ExerciseType.Repos.toString())) {
                    type = ExerciseType.Repos;
                    TextView repetitionLabel = (TextView) findViewById(R.id.RepetitionText);
                    repetitionLabel.setText("Temps de repos");
                    repos.setVisibility(View.INVISIBLE);
                    TextView reposLabel = (TextView) findViewById(R.id.ReposText);
                    reposLabel.setVisibility(View.INVISIBLE);

                }

                break;
        }
    };

    private WorkoutDAO db;
    private Exercise exo;
    private EditText name;
    private TextView nbRepetition;
    private TextView nbSerie;
    private TextView repos;
    private TextView typetext;
    private ExerciseType type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        db = new WorkoutDAO(this);

        long exoId = getIntent().getExtras().getLong(
                "com.annef.workoutchrono.exercise");
        Log.d("annef", "started with exo id=" + exoId);

        exo = db.readExo(exoId);

        name = (EditText) findViewById(R.id.exerciseName);
        name.setText(exo.getName());

        /*typetext = (TextView) findViewById(R.id.exoType);
        typetext.setText(exo.getType().toString());
        typetext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent editIntent= new Intent(ExerciseActivity.this,PickTypeActivity.class);
                editIntent.putExtra("com.annef.workoutchrono.type", exo.getType().toString());
                // Puis on lance l'intent !
                startActivityForResult(editIntent, EDIT_TYPE);
            }
        });*/
        Spinner spinner = (Spinner) findViewById(R.id.exoTypeSpinner);
        //  Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this, R.array.typesArray, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(onSpinnerItemListener );
        spinner.setSelection((exo.getType() == ExerciseType.Serie) ? 0 : (exo.getType() == ExerciseType.Duree ? 1 : 2));

        nbRepetition = (TextView) findViewById(R.id.repetiton);
        nbRepetition.setText(exo.getRepeat() + "");
        nbRepetition.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent editIntent;
                if (exo.getType() == ExerciseType.Serie){
                    editIntent= new Intent(ExerciseActivity.this,
                            PickNumberActivity.class);
                    editIntent.putExtra("com.annef.workoutchrono.number", Integer.valueOf(nbRepetition.getText().toString()));
                    // Puis on lance l'intent !
                    startActivityForResult(editIntent, EDIT_NBREPET);
                }
                else {
                    editIntent= new Intent(ExerciseActivity.this,
                            PickTimeActivity.class);
                    editIntent.putExtra("com.annef.workoutchrono.time", Integer.valueOf(nbRepetition.getText().toString()));
                    // Puis on lance l'intent !
                    startActivityForResult(editIntent, EDIT_TEMPS);

                }

            }
        });

        nbSerie = (TextView) findViewById(R.id.nbSeries);
        nbSerie.setText(exo.getNbSerie() + "");
        nbSerie.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent editIntent = new Intent(ExerciseActivity.this,
                        PickNumberActivity.class);

                editIntent.putExtra("com.annef.workoutchrono.number", Integer.valueOf(nbSerie.getText().toString()));
                // Puis on lance l'intent !
                startActivityForResult(editIntent, EDIT_NBSERIE);
            }
        });

        repos = (TextView) findViewById(R.id.repos);
        repos.setText(exo.getRest() + "");
        repos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent editIntent = new Intent(ExerciseActivity.this,
                        PickTimeActivity.class);

                editIntent.putExtra("com.annef.workoutchrono.time", Integer.valueOf(repos.getText().toString()));
                // Puis on lance l'intent !
                startActivityForResult(editIntent, EDIT_REPOS);
            }
        });

        exoImage = (ImageView) findViewById(R.id.imageView);
        if (exo.getPathToPic().equals("")) {
            // image set, add a button to pick an image
            exoImage.setVisibility(View.INVISIBLE);
            ImageButton addImageButton = (ImageButton) findViewById(R.id.addImage);
            addImageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                //    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                //    photoPickerIntent.setType("image/*");
                //    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                        String imageFileName = exo.getName();
                        File storageDir = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES);

                        File image = new File(storageDir+File.separator+imageFileName+".jpg");

                        if (image != null && image.getAbsolutePath() != null) {
                            exo.setPathToPic(image.getAbsolutePath());
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(image.getAbsoluteFile()));
                            takePictureIntent.putExtra("return-data", true);
                            startActivityForResult(takePictureIntent, SELECT_PHOTO);
                        }
                    }
                }
            });

            addImageButton.setVisibility(View.VISIBLE);
        } else {
            exoImage.setImageURI(Uri.parse(new File(exo.getPathToPic()).toString()));
        }



        // Bouton Annuler
        Button dismiss = (Button) findViewById(R.id.buttonDismissExercise);
        dismiss.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent result = new Intent();
                setResult(RESULT_OK, result);
                finish();

            }
        });
        // Bouton Sauver
        Button save = (Button) findViewById(R.id.buttonSaveExercise);
        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (updateExerciseData()) {

                    db.update(exo);

                    Intent result = new Intent();
                    setResult(RESULT_OK, result);
                    //result.putExtra("com.annef.workoutchrono.exercise", exo);
                    finish();
                }

            }
        });

        //Bouton Supprimer
        Button delete = (Button) findViewById(R.id.buttonDeleteExercise);
        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                db.delete(exo);

                Intent result = new Intent();
                setResult(RESULT_OK, result);
                finish();
            }
        });

    }

    public boolean updateExerciseData() {

        exo.setName(name.getText().toString());
        //exo.setType(type.getText().toString());
        exo.setType(type.toString());
        int repeat = parse("The number of repetitions", nbRepetition);
        if (repeat == -1) {
            return false;
        }
        exo.setRepeat(repeat);

        int serie = parse("The number of series", nbSerie);
        if (serie == -1) {
            return false;
        }
        exo.setNbSerie(serie);

        int rest = parse("The rest duration", repos);
        if (rest == -1) {
            return false;
        }
        exo.setRepos(rest);


        return true;

    }
    @Override
    public void onBackPressed() {
        if (updateExerciseData()) {

            db.update(exo);

            Intent result = new Intent();
            setResult(RESULT_OK, result);
            //result.putExtra("com.annef.workoutchrono.exercise", exo);
            finish();
        }
    }

    /**
     * Parse EditText to int
     *
     * @param label
     *            label of the current editText, in order to toast an error
     *            message
     * @param editText
     * @return value of the editTet, -1 if the value is not a number
     */
    public int parse(String label, TextView editText) {
        int repeat = 0;
        try {

            repeat = Integer.parseInt(editText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    label + " is not a valid number, please correct",
                    Toast.LENGTH_SHORT).show();
        }
        return repeat;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.exercise_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_delete) {
            db.delete(exo);

            Intent result = new Intent();
            setResult(RESULT_OK, result);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.workout_fragment,
                    container, false);
            return rootView;
        }
    }
}
