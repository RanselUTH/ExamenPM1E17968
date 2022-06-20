package com.example.examenpm1e17968;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.examenpm1e17968.Transacciones.Transacciones;



public class ActivityAddPais extends AppCompatActivity {

    EditText txtpais, txtPrefijo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pais);

        Button btnSalvarPais = (Button) findViewById(R.id.btnSalvarPais);
        Button btnVolver = (Button) findViewById(R.id.btnVolverInicio);
        txtpais = (EditText) findViewById(R.id.txtpais);
        txtPrefijo = (EditText) findViewById(R.id.txtprefijo);

        btnSalvarPais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgregarPais();
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void AgregarPais() {
        int numeros = 0;
        if(txtpais.getText().toString().isEmpty() || txtPrefijo.getText().toString().isEmpty()) {
            mostrarDialogoVacios();
        } else {
            for( int i = 0; i < txtpais.getText().toString().length(); i++ ) {
                if (Character.isDigit(txtpais.getText().toString().charAt(i))) {
                    mostrarDialogoNumeros();
                    numeros = 1;
                    break;
                }
            }

            if(numeros == 0) {
                SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);
                SQLiteDatabase db = conexion.getWritableDatabase();

                ContentValues valores = new ContentValues();
                valores.put(Transacciones.nombrePais, txtpais.getText().toString());
                valores.put(Transacciones.codigoMarcado, txtPrefijo.getText().toString());

                Long resultado = db.insert(Transacciones.tablaPaises, Transacciones.idPais, valores);
                Toast.makeText(getApplicationContext(), "País Agregado: " + resultado.toString(), Toast.LENGTH_LONG).show();
                db.close();
                ClearScreen();
            }
        }

    }

    private void ClearScreen() {
        txtpais.setText("");
        txtPrefijo.setText("");
    }

    private void mostrarDialogoVacios() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta Campos Vacíos")
                .setMessage("No puede dejar ningún campo vacío")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
    private void mostrarDialogoNumeros() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta Números")
                .setMessage("No puede ingresar números en el campo de Nombre")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

}