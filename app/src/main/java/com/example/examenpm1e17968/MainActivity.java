package com.example.examenpm1e17968;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.examenpm1e17968.Tablas.Paises;
import com.example.examenpm1e17968.Transacciones.Transacciones;


import java.util.ArrayList;




public class MainActivity extends AppCompatActivity {

    SQLiteConexion conexion;
    Spinner combopais;
    EditText txtnombre, txtTelefono, txtNota;
    ArrayList<String> listaPaises;
    ArrayList<Paises> paises;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);
        combopais = (Spinner) findViewById(R.id.cmbPaisesBuscado);


        ObtenerListaPaises();
        ArrayAdapter<CharSequence> adp = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaPaises);
        combopais.setAdapter(adp);


        Button btnAddPais = (Button) findViewById(R.id.btnanadir);
        btnAddPais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityAddPais.class);
                startActivity(intent);
            }
        });


        Button btnSalvarContacto = (Button) findViewById(R.id.btnGuardar);
        txtnombre = (EditText) findViewById(R.id.txtNombreModificar);
        txtTelefono = (EditText) findViewById(R.id.txtTelefonoModificar);
        txtNota = (EditText) findViewById(R.id.txtInformacionModificar);
        btnSalvarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgregarContacto();
            }
        });


        Button btnListaContactos = (Button) findViewById(R.id.btnContactos);
        btnListaContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityListContactos.class);
                startActivity(intent);
            }
        });

    }

    private void AgregarContacto() {
        int numeros = 0;
        if(txtnombre.getText().toString().isEmpty() || txtTelefono.getText().toString().isEmpty()) {
            mostrarDialogoVacios();
        } else {
            for (int i = 0; i < txtnombre.getText().toString().length(); i++) {
                if (Character.isDigit(txtnombre.getText().toString().charAt(i))) {
                    mostrarDialogoNumeros();
                    numeros = 1;
                    break;
                }
            }

            if (numeros == 0) {
                SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);
                SQLiteDatabase db = conexion.getWritableDatabase();

                ContentValues valores = new ContentValues();
                valores.put(Transacciones.nombreContacto, txtnombre.getText().toString());
                valores.put(Transacciones.telefonoContacto, txtTelefono.getText().toString());
                valores.put(Transacciones.pais, (combopais.getSelectedItemId() + 1));
                valores.put(Transacciones.nota, txtNota.getText().toString());

                Long resultado = db.insert(Transacciones.tablaContactos, Transacciones.idContacto, valores);
                Toast.makeText(getApplicationContext(), "Contacto Guardado: " + resultado.toString(), Toast.LENGTH_LONG).show();
                db.close();
                ClearScreen();
            }
        }
    }

    private void ClearScreen() {
        txtnombre.setText("");
        txtTelefono.setText("");
        txtNota.setText("");
    }

    private void mostrarDialogoVacios() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta de Vacíos")
                .setMessage("No puede dejar ningún campo vacío")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void mostrarDialogoNumeros() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta de Números")
                .setMessage("No puede ingresar números en el campo de Nombre")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void ObtenerListaPaises() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Paises pais = null;

        paises = new ArrayList<Paises>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Transacciones.tablaPaises, null);

        while (cursor.moveToNext()){
            pais = new Paises();
            pais.setIdPais(cursor.getInt(0));
            pais.setNombrePais(cursor.getString(1));
            pais.setCodigoMarcado(cursor.getInt(2));

            paises.add(pais);
        }

        fillComb();
        cursor.close();
    }

    private void fillComb() {
        listaPaises = new ArrayList<String>();
        for(int i = 0; i < paises.size(); i++){
            listaPaises.add(paises.get(i).getNombrePais() + "  ("
                    + paises.get(i).getCodigoMarcado() + ")");
        }
    }
}